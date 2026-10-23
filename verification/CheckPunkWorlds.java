import app.veyra.music.data.themes.*;
import app.veyra.music.ui.themes.ThemeGlyphs;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import kotlin.Unit;

/** Focused runtime installer probe; run from repository root with production Kotlin classes. */
public class CheckPunkWorlds {
 public static void main(String[] args) throws Exception {
  Set<String> ids=Set.of("riot-candy","chrome-venom","bubble-riot","xerox-afterparty","cosmic-vandal");
  ThemeCatalog catalog=ThemePack.INSTANCE.getJson().decodeFromString(ThemeCatalog.Companion.serializer(),Files.readString(Path.of("themes/catalog.json")));
  if(catalog.getThemes().size()!=62)throw new AssertionError("catalog count");
  for(ThemeListing e:catalog.getThemes()){
   ThemeManifest m=ThemePack.INSTANCE.getJson().decodeFromString(ThemeManifest.Companion.serializer(),Files.readString(Path.of("themes",e.getId(),"theme.json")));
   ThemePack.INSTANCE.validate(m,32);
   if(!ids.contains(e.getId()))continue;
   try {ThemePack.INSTANCE.validate(m,31);throw new AssertionError("old build accepted");}catch(IllegalArgumentException expected){}
   if(m.getSchema()!=4 || m.getAssets().size()!=123 || !m.getAssets().keySet().containsAll(ThemeGlyphs.INSTANCE.getAdvancedKeys()))throw new AssertionError("incomplete roles");
   String raw=Files.readString(Path.of("themes",e.getId(),"theme.json"));
   for(String bad:new String[]{raw.replace("\"schema\": 4","\"schema\": 5"),raw.replace("\"minAppCode\": 32","\"minAppCode\": 31"),raw.replace("\"materialStyle\": \""+m.getMaterialStyle()+"\"","\"materialStyle\": \"script\""),raw.replace("\"play\": ","\"executable\": ")}){
    ThemeManifest invalid=ThemePack.INSTANCE.getJson().decodeFromString(ThemeManifest.Companion.serializer(),bad);
    try{ThemePack.INSTANCE.validate(invalid,32);throw new AssertionError("invalid schema/version/preset/role accepted");}catch(IllegalArgumentException expected){}
   }
   if(!m.getAssets().keySet().containsAll(ThemePack.INSTANCE.getPressedKeys()))throw new AssertionError("missing pressed states");
   for(String key:new String[]{"play","pause","previous","next"})if(m.getFiles().get(m.getAssets().get(key)).equals(m.getFiles().get(m.getAssets().get(key+"_pressed"))))throw new AssertionError("identical pressed state");
   File zip=new File("themes/packs/"+e.getId()+"-v1.zip");
   ThemePack.INSTANCE.install(zip,new File(args[0],e.getId()),e,32,(definition,dir)->{
    for(var a:definition.getAssets().entrySet()){
     File f=new File(dir,a.getValue());if(!f.isFile())throw new AssertionError("missing "+a.getKey());
     if(a.getKey().endsWith("Font"))continue;
     try{
      int w,h;
      if(a.getValue().endsWith(".png")){var image=ImageIO.read(f);if(image==null)throw new AssertionError("bad image");w=image.getWidth();h=image.getHeight();}
      else{byte[] data=Files.readAllBytes(f.toPath());if(!new String(data,8,8).equals("WEBPVP8 "))throw new AssertionError("WEBP header");w=((data[26]&255)|((data[27]&255)<<8))&16383;h=((data[28]&255)|((data[29]&255)<<8))&16383;}
      ThemePack.INSTANCE.validateImageBounds(a.getKey(),w,h);
     }catch(IOException error){throw new RuntimeException(error);}
    }
    return Unit.INSTANCE;
   });
   System.out.println(e.getId()+": real installer, archive checksums, 123 asset roles/image bounds and build-31 rejection passed");
  }
  System.out.println("All 57 earlier manifests still validate with build 32.");
 }
}
