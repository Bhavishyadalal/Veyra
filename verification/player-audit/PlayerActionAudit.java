import java.nio.file.*;
import java.util.*;
import org.jetbrains.kotlin.cli.jvm.compiler.*;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer;
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil;

/** Source preservation audit against the saved preview baseline; not an integration test. */
public class PlayerActionAudit {
 static final String BASE="3bfcbcd89bca544e71fb4b17df5433d444737a82";
 static final Map<String,List<String>> CONTRACTS=Map.ofEntries(
   Map.entry("TransportRow",List.of("isPlaying","isLoading","previousEnabled","nextEnabled","onPrevious","onPlayPause","onNext")),
   Map.entry("TransportGlyph",List.of("onClick","enabled","haptic")),
   Map.entry("PlayerScrubber",List.of("shown","durationMs","loading","mixing","transitionWindow","onScrub","onScrubFinished")),
   Map.entry("VolumeRow",List.of("value","onValueChange","onValueChangeFinished")),
   Map.entry("PlayerActionRow",List.of("lyricsOpen","queueOpen","shuffleEnabled","repeatMode","autoplayEnabled","onToggleLyrics","onToggleQueue","onToggleShuffle","onCycleRepeat","onToggleAutoplay","onOpenOutput","onListenTogether","onOpenListenTogetherMembers")),
   Map.entry("CircleGlyph",List.of("onClick","active","haptic")),
   Map.entry("PillSegment",List.of("onClick","haptic","tapWindowMs")),
   Map.entry("BottomGlyph",List.of("onClick","haptic","tapWindowMs")),
   Map.entry("InlineQueueRow",List.of("onClick","onRemove","locked","onDragStart","onDrag","onDragEnd")),
   Map.entry("ThinSlider",List.of("value","onValueChange","onValueChangeFinished","enabled","accessibilityLabel")));
 static List<String> calls(KtPsiFactory f,String path,String text,String callee,List<String> args) {
  var out=new ArrayList<String>();
  for(var call:PsiTreeUtil.collectElementsOfType(f.createFile(path,text),KtCallExpression.class)) {
   if(call.getCalleeExpression()==null || !call.getCalleeExpression().getText().equals(callee))continue;
   var values=new TreeMap<String,String>();
   for(var arg:call.getValueArguments()) {
    if(arg.getArgumentName()==null || arg.getArgumentExpression()==null)continue;
    var name=arg.getArgumentName().getAsName().asString();
    if(args.contains(name))values.put(name,arg.getArgumentExpression().getText().replaceAll("\\s+"," "));
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
    merge(originalDeclarations,declarations(f,path,old));
    merge(currentDeclarations,declarations(f,path,current));
    for(var contract:CONTRACTS.entrySet()) {
     var a=calls(f,path,old,contract.getKey(),contract.getValue());
     var b=calls(f,path,current,contract.getKey(),contract.getValue());
     if(!a.equals(b))throw new Exception("Changed contract: "+path+" / "+contract.getKey()+"\nBEFORE="+a+"\nAFTER="+b);
     totals.merge(contract.getKey(),a.size(),Integer::sum);
    }
   }
   for(String extra:List.of(
     "sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/player/PlayerControlPrimitives.kt",
     "sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/player/PlayerInteractionPolicy.kt",
     "sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/player/PlayerScrubState.kt")) {
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
