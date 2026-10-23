import app.veyra.music.AppIcon;
import app.veyra.music.data.themes.*;
import java.nio.file.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;

/** Focused launcher binding check using the real enum/picker and actual catalogs/manifest. */
public class CheckIconCompletion {
 public static void main(String[] args)throws Exception {
  if(AppIcon.values().length!=95)throw new AssertionError("choice count");
  var doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("app/src/main/AndroidManifest.xml");
  var aliases=doc.getElementsByTagName("activity-alias");Set<String> names=new HashSet<>();int enabled=0;
  for(int i=0;i<aliases.getLength();i++) {var a=aliases.item(i).getAttributes();String name=a.getNamedItem("android:name").getNodeValue().replaceFirst("^\\.","");if(!names.add(name))throw new AssertionError("duplicate alias");if(a.getNamedItem("android:enabled").getNodeValue().equals("true")){enabled++;if(!name.equals("LauncherDefault"))throw new AssertionError("unexpected default");}}
  if(enabled!=1 || names.size()!=95)throw new AssertionError("manifest defaults/count");
  for(var icon:AppIcon.values())if(!names.contains(icon.getAlias()))throw new AssertionError("unregistered "+icon);
  var themes=ThemePack.INSTANCE.getJson().decodeFromString(ThemeCatalog.Companion.serializer(),Files.readString(Path.of("themes/catalog.json")));
  for(var t:themes.getThemes())if(AppIcon.Companion.forTheme(t.getId())==null)throw new AssertionError("unmapped theme "+t.getId());
  var assets=AssetPack.INSTANCE.getJson().decodeFromString(AssetCatalog.Companion.serializer(),Files.readString(Path.of("asset-packs/catalog.json")));
  for(var a:assets.getPacks())if(AppIcon.Companion.forAssetPack(a.getId())==null || AppIcon.Companion.forTheme(a.getId())!=null)throw new AssertionError("unmapped/mixed asset binding");
  if(AppIcon.Companion.forTheme("external.component")!=null || AppIcon.Companion.forAssetPack("external.component")!=null)throw new AssertionError("untrusted component");
  for(var f:GlassFamily.values())if(IconPickerPolicy.INSTANCE.options(IconCollection.ALL,f).size()!=95)throw new AssertionError("All filter");
  if(IconPickerPolicy.INSTANCE.options(IconCollection.THEMES,GlassFamily.LIQUID).size()!=62 || IconPickerPolicy.INSTANCE.options(IconCollection.ASSETS,GlassFamily.LIQUID).size()!=5)throw new AssertionError("collection filters");
  System.out.println("95 real enum/manifest choices; 62 theme and five asset mappings; All/Theme/Asset filters, default uniqueness and unknown-ID rejection passed.");
 }
}
