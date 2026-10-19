import java.nio.file.*;
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer;
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement;
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.kotlin.cli.jvm.compiler.*;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.psi.KtPsiFactory;

public class VerifyKotlin {
    public static void main(String[] paths) throws Exception {
        var disposable = Disposer.newDisposable();
        try {
            var env = KotlinCoreEnvironment.createForProduction(disposable, new CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES);
            var factory = new KtPsiFactory(env.getProject(), false);
            int errors = 0;
            for (String path : paths) {
                var file = factory.createFile(Path.of(path).getFileName().toString(), Files.readString(Path.of(path)));
                for (var error : PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class)) {
                    System.err.println(path + ":" + error.getTextOffset() + ": " + error.getErrorDescription()); errors++;
                }
            }
            if (errors > 0) throw new IllegalStateException(errors + " syntax errors");
            System.out.println("Kotlin PSI: " + paths.length + " production files parsed without syntax errors (not Android/Compose type checking).");
        } finally { Disposer.dispose(disposable); }
    }
}
