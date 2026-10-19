import java.nio.file.*;
import java.util.*;
import org.jetbrains.kotlin.cli.jvm.compiler.*;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer;
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil;

/** Source preservation audit against the saved preview baseline; not an integration test. */
public class HomeActionAudit {
 static final String BASE="0bac58fdbe77892bcf144deac97ac83d17cf3606";
 static final Map<String,List<String>> CONTRACTS=Map.ofEntries(
   Map.entry("MessageState",List.of("onAction","actionLabel")),
   Map.entry("TextButton",List.of("onClick")),
   Map.entry("PullToRefresh",List.of("refreshing","onRefresh","state")),
   Map.entry("HomeCollection",List.of("shelf","featured","onItemClick","onItemLongPress","currentSong","isPlaying")),
   Map.entry("RecentShelf",List.of("shelf","viewType","onItemClick","onItemLongPress","currentSong","isPlaying","onViewTypeToggle")),
   Map.entry("Shelf",List.of("shelf","onItemClick","onItemLongPress","currentSong","isPlaying")),
   Map.entry("RecentTrackRow",List.of("item","onClick","onLongPress","isCurrent","isPlaying")),
   Map.entry("CompactTrackRow",List.of("title","subtitle","thumbnailUrl","onClick","onLongPress","isCurrent","isPlaying")),
   Map.entry("HomeArtCard",List.of("item","onClick","onHold","isCurrent","isPlaying","compact")),
   Map.entry("longPressMenuClickable",List.of("onClick","onLongClick")),
   Map.entry("RowMoreButton",List.of("onClick")),
   Map.entry("SignInBanner",List.of("onSignIn")));
 static final Map<String,List<String>> POSITIONAL=Map.of(
   "HomeArtCard",List.of("item","onClick","onHold","isCurrent","isPlaying","compact","modifier"),
   "CompactTrackRow",List.of("title","subtitle","thumbnailUrl","onClick","onLongPress","isCurrent","isPlaying","titleMaxLines"),
   "longPressMenuClickable",List.of("onClick","onLongClick","enabled"));
 static List<String> calls(KtPsiFactory f,String path,String text,String callee,List<String> args) {
  var out=new ArrayList<String>();
  for(var call:PsiTreeUtil.collectElementsOfType(f.createFile(path,text),KtCallExpression.class)) {
   if(call.getCalleeExpression()==null)continue;
   String name=call.getCalleeExpression().getText();
   if(name.equals("HomeActionButton") && call.getValueArguments().stream().anyMatch(arg->arg.getArgumentExpression()!=null && arg.getArgumentExpression().getText().equals("Icons.Rounded.MoreVert")))name="RowMoreButton";
   if(name.equals("HomeAccountPrompt"))name="SignInBanner";
   if(!name.equals(callee))continue;
   var values=new TreeMap<String,String>();
   int index=0;
   for(var arg:call.getValueArguments()) {
    var params=POSITIONAL.getOrDefault(callee,List.of());
    String param=arg.getArgumentName()!=null?arg.getArgumentName().getAsName().asString():null;
    if(param==null && index<params.size())param=params.get(index);
    index++;
    if(arg.getArgumentExpression()!=null && param!=null && args.contains(param))values.put(param,arg.getArgumentExpression().getText().replaceAll("\\s+"," "));
   }
   out.add(values.toString());
  }
  return out;
 }
 static Map<String,List<List<String>>> declarations(KtPsiFactory f, String path, String text) {
  var result=new HashMap<String,List<List<String>>>();
  for(var fn:PsiTreeUtil.collectElementsOfType(f.createFile(path,text),KtNamedFunction.class)) {
   if(fn.getName()==null)continue;
   String receiver=fn.getReceiverTypeReference()==null ? "" : fn.getReceiverTypeReference().getText();
   String key=receiver+"::"+fn.getName();
   var names=new ArrayList<String>();for(var p:fn.getValueParameters())names.add(p.getName());
   result.computeIfAbsent(key,k->new ArrayList<>()).add(names);
  }
  return result;
 }
 static void merge(Map<String,List<List<String>>> target,Map<String,List<List<String>>> other) {
  other.forEach((k,v)->target.computeIfAbsent(k,n->new ArrayList<>()).addAll(v));
 }
 static boolean containsInOrder(List<String> now,List<String> before) {
   int i=0; for(String item:now)if(i<before.size() && item.equals(before.get(i)))i++;
   return i==before.size();
 }
 public static void main(String[] paths) throws Exception {
  var disposable=Disposer.newDisposable();var totals=new TreeMap<String,Integer>();
  try {
   var env=KotlinCoreEnvironment.createForProduction(disposable,new CompilerConfiguration(),EnvironmentConfigFiles.JVM_CONFIG_FILES);
   var f=new KtPsiFactory(env.getProject(),false);
   var originalDeclarations=new HashMap<String,List<List<String>>>();
   var currentDeclarations=new HashMap<String,List<List<String>>>();
   for(String path:paths) {
    var process=new ProcessBuilder("git","show",BASE+":"+path).start();
    var old=new String(process.getInputStream().readAllBytes());
    if(process.waitFor()!=0)throw new Exception("Missing baseline: "+path);
    var current=Files.readString(Path.of(path));
    var allowed=Set.of("HomeScreen","itemsIndexedShelves","RecentSectionHeader","CompactTrackRow","HomeTitle",
      "homeFeedSkeleton","HomeCollection","TilePage","HomeArtCard","HomePagerControls","HomePageArrow");
    var beforeFile=f.createFile(path,old); var afterFile=f.createFile(path,current);
    if(!PsiTreeUtil.collectElementsOfType(afterFile,org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement.class).isEmpty())
      throw new Exception("Kotlin syntax errors: "+path);
    var afterFunctions=PsiTreeUtil.collectElementsOfType(afterFile,KtNamedFunction.class);
    for(var fn:PsiTreeUtil.collectElementsOfType(beforeFile,KtNamedFunction.class)) {
      if(fn.getName()==null || allowed.contains(fn.getName()))continue;
      var candidate=afterFunctions.stream().filter(now->Objects.equals(now.getName(),fn.getName()) && now.getValueParameters().size()==fn.getValueParameters().size()).findFirst();
      if(candidate.isEmpty() || !candidate.get().getText().replaceAll("\\s+"," ").equals(fn.getText().replaceAll("\\s+"," ")))
        throw new Exception("Unexpected change outside Home refinement: "+fn.getName());
    }
    merge(originalDeclarations,declarations(f,path,old));
    merge(currentDeclarations,declarations(f,path,current));
    for(var contract:CONTRACTS.entrySet()) {
     var a=calls(f,path,old,contract.getKey(),contract.getValue());
     var b=calls(f,path,current,contract.getKey(),contract.getValue());
     if(!a.equals(b))throw new Exception("Changed contract: "+path+" / "+contract.getKey()+"\nBEFORE="+a+"\nAFTER="+b);
     totals.merge(contract.getKey(),a.size(),Integer::sum);
    }
   }
   for(String extra:List.of("sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/screens/HomeComponents.kt",
       "sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/screens/HomeFeedIdentity.kt")) {
    merge(currentDeclarations,declarations(f,extra,Files.readString(Path.of(extra))));
   }
   int retained=0;
   for(var entry:originalDeclarations.entrySet()) for(var oldParams:entry.getValue()) {
    boolean found=currentDeclarations.getOrDefault(entry.getKey(),List.of()).stream()
      .anyMatch(now->containsInOrder(now,oldParams));
    if(!found)throw new Exception("Removed/changed function: "+entry.getKey()+oldParams);
    retained++;
   }
   System.out.println("PASS: "+retained+" original named functions retain their original parameter order (new optional parameters allowed) (including extracted controls).");
  }finally{Disposer.dispose(disposable);}
  System.out.println("PASS: existing named UI action/value contracts match preview baseline "+BASE+": "+totals);
 }
}
