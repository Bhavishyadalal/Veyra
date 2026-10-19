import java.nio.file.*;
import java.util.*;
import org.jetbrains.kotlin.cli.jvm.compiler.*;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer;
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil;

/** Source preservation audit against the saved stable baseline; not an integration test. */
public class SettingsActionAudit {
 static final String BASE="8a03bb769aeeea5b76ed36dc44db28aa950ca5e9";
 static final Map<String,List<String>> CONTRACTS=Map.of(
   "SettingsRow",List.of("title","onClick"),
   "SettingsSubRow",List.of("title","onCheckedChange"),
   "SliderRow",List.of("title","onSliderValue","valueRange","steps"),
   "SegmentedControl",List.of("options","selectedIndex","onSelect","enabled"),
   "AlertAction",List.of("label","onClick","enabled"),
   "TextButton",List.of("onClick","enabled"),
   "DestructiveRow",List.of("label","onClick","enabled"),
   "AccountCard",List.of("signedIn","account","onSignIn","onClick","grouped"));
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
     "app/src/main/java/app/veyra/music/ui/screens/SettingsControls.kt",
     "app/src/main/java/app/veyra/music/ui/components/SettingsDialogControls.kt",
     "sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/components/PillTextField.kt")) {
    merge(currentDeclarations,declarations(f,extra,Files.readString(Path.of(extra))));
   }
   int retained=0;
   for(var entry:originalDeclarations.entrySet()) for(var oldParams:entry.getValue()) {
    boolean found=currentDeclarations.getOrDefault(entry.getKey(),List.of()).stream()
      .anyMatch(now->now.size()>=oldParams.size() && now.subList(0,oldParams.size()).equals(oldParams));
    if(!found)throw new Exception("Removed/changed function: "+entry.getKey()+oldParams);
    retained++;
   }
   System.out.println("PASS: "+retained+" original named functions retain their original parameter order (including extracted controls).");
  }finally{Disposer.dispose(disposable);}
  System.out.println("PASS: existing named UI action/value contracts match stable "+BASE+": "+totals);
 }
}
