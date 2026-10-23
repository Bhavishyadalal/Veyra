import app.veyra.music.data.themes.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;

/** Focused release routing regression probe against the compiled repository parsers. */
public class CheckStableCatalogs {
    public static void main(String[] args) throws Exception {
        check(ThemeRepository.INSTANCE, "themes/catalog.json", "app/src/main/assets/theme-catalog.json", 62);
        check(AssetRepository.INSTANCE, "asset-packs/catalog.json", "app/src/main/assets/asset-catalog.json", 5);
    }
    private static void check(Object repository, String remote, String bundled, int expected) throws Exception {
        byte[] bytes = Files.readAllBytes(Path.of(remote));
        if (!Arrays.equals(bytes, Files.readAllBytes(Path.of(bundled)))) throw new AssertionError("Bundled catalog mismatch");
        Method parser = Arrays.stream(repository.getClass().getDeclaredMethods())
            .filter(m -> m.getName().startsWith("parseCatalog") && m.getParameterCount() == 1).findFirst().orElseThrow();
        List<?> entries = (List<?>) parser.invoke(repository, (Object) bytes);
        if (entries.size() != expected) throw new AssertionError("Missing packs");
        String beta = new String(bytes, java.nio.charset.StandardCharsets.UTF_8)
            .replace("/refs/heads/main/", "/refs/heads/preview/0.1.9-beta1/");
        try {
            parser.invoke(repository, (Object) beta.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            throw new AssertionError("Stable parser accepted beta URL roots");
        } catch (InvocationTargetException e) {
            if (!(e.getCause() instanceof IllegalArgumentException)) throw e;
        }
        System.out.println(remote + ": " + expected + " stable listings and bundled fallback accepted; beta URLs rejected");
    }
}
