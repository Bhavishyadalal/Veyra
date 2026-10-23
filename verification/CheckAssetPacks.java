import app.veyra.music.data.themes.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import kotlin.Unit;

/** Focused production installer check; not a broad test suite or device benchmark. */
public class CheckAssetPacks {
 public static void main(String[] args) throws Exception {
  AssetCatalog c=AssetPack.INSTANCE.getJson().decodeFromString(AssetCatalog.Companion.serializer(),Files.readString(Path.of("asset-packs/catalog.json")));
  if(c.getPacks().size()!=15)throw new AssertionError("pack count");
  for(AssetListing e:c.getPacks()) {
   String raw=Files.readString(Path.of("asset-packs",e.getId(),"assets.json"));
   AssetManifest m=AssetPack.INSTANCE.getJson().decodeFromString(AssetManifest.Companion.serializer(),raw);
   AssetPack.INSTANCE.validate(m,33);
   if(m.getAssets().size()!=119)throw new AssertionError("role count");
   for(String bad:new String[]{raw.replace("\"minAppCode\": 33","\"minAppCode\": 32"),raw.replace("\"play\": ","\"header\": "),raw.replace("\"motionStyle\": \""+m.getMotionStyle()+"\"","\"motionStyle\": \"script\"")}) {
    AssetManifest b=AssetPack.INSTANCE.getJson().decodeFromString(AssetManifest.Companion.serializer(),bad);
    try {AssetPack.INSTANCE.validate(b,33);throw new AssertionError("bad role/build/preset accepted");}catch(IllegalArgumentException expected){}
   }
   try {AssetPack.INSTANCE.validate(m,32);throw new AssertionError("old build accepted");}catch(IllegalArgumentException expected){}
   File pack=new File("asset-packs/packs/"+e.getId()+"-v1.zip");
   var result=AssetPack.INSTANCE.install(pack,new File(args[0],e.getId()),e,33,(definition,dir)-> {
    for(var a:definition.getAssets().entrySet())try {
     File f=new File(dir,a.getValue());
     if(a.getKey().endsWith("Font"))java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,f);
     else {var image=ImageIO.read(f);if(image==null)throw new AssertionError("decode");ThemePack.INSTANCE.validateImageBounds(a.getKey(),image.getWidth(),image.getHeight());}
    }catch(Exception error){throw new RuntimeException(error);}
    if(!new File(dir,"previews/specimen.webp").isFile())throw new AssertionError("offline preview missing");
    return Unit.INSTANCE;
   });
   for(String key:List.of("play","pause","previous","next"))if(m.getFiles().get(m.getAssets().get(key)).equals(m.getFiles().get(m.getAssets().get(key+"_pressed"))))throw new AssertionError("identical press");
   System.out.println(e.getName()+": checksum/exact archive installer, 119 roles, fonts/images/offline preview, old-build/role/preset rejection passed");
  }
 }
}
