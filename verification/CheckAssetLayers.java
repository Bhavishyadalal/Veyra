import app.veyra.music.data.themes.*;
import app.veyra.music.ui.themes.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

/** Runs the production merge on every published theme; no Compose UI/device harness. */
public class CheckAssetLayers {
 static long color(String s){return Long.parseLong("ff"+s.substring(1),16)<<32;}
 static ThemeVisuals base(ThemeManifest m,String scene)throws Exception {
  Constructor<?> c=Arrays.stream(ThemeVisuals.class.getDeclaredConstructors()).filter(x->x.getParameterCount()==24).findFirst().orElseThrow();c.setAccessible(true);
  var p=m.getPalette();return (ThemeVisuals)c.newInstance(m.getId(),m.getName(),color(p.getAccent()),color(p.getSurface()),color(p.getRaised()),color(p.getOutline()),m.getRowCorner(),m.getButtonCorner(),m.getNavCorner(),m.getAssets(),m.getSeekStyle(),m.getSeekHeight(),m.getVersion(),color(p.getBackground()),color(p.getText()),color(p.getMuted()),color(p.getSecondary()),scene,m.getTagline(),m.getMaterialStyle(),m.getIconStyle(),m.getTypeStyle(),null,null);
 }
 static void same(ThemeVisuals a,ThemeVisuals b)throws Exception {
  for(String n:List.of("getId","getName","getSceneStyle","getTagline","getAccent-0d7_KjU","getSurface-0d7_KjU","getRaised-0d7_KjU","getOutline-0d7_KjU","getBackground-0d7_KjU","getText-0d7_KjU","getMuted-0d7_KjU","getSecondary-0d7_KjU"))if(!Objects.equals(ThemeVisuals.class.getMethod(n).invoke(a),ThemeVisuals.class.getMethod(n).invoke(b)))throw new AssertionError("base changed "+n);
  for(String role:List.of("header","player","preview","brandIcon"))if(!Objects.equals(a.asset(role),b.asset(role)))throw new AssertionError("art changed "+role);
 }
 public static void main(String[] args)throws Exception {
  var tc=ThemePack.INSTANCE.getJson().decodeFromString(ThemeCatalog.Companion.serializer(),Files.readString(Path.of("themes/catalog.json")));
  var ac=AssetPack.INSTANCE.getJson().decodeFromString(AssetCatalog.Companion.serializer(),Files.readString(Path.of("asset-packs/catalog.json")));
  int pairs=0;
  for(var t:tc.getThemes()) {
   var tm=ThemePack.INSTANCE.getJson().decodeFromString(ThemeManifest.Companion.serializer(),Files.readString(Path.of("themes",t.getId(),"theme.json")));
   for(var listing:ac.getPacks()) {
    var a=AssetPack.INSTANCE.getJson().decodeFromString(AssetManifest.Companion.serializer(),Files.readString(Path.of("asset-packs",listing.getId(),"assets.json")));
    var v=new AssetVisuals(a.getId(),a.getName(),a.getAssets(),a.getMaterialStyle(),a.getSeekStyle(),a.getSeekHeight(),a.getRowCorner(),a.getButtonCorner(),a.getNavCorner(),a.getHeadingInk(),a.getMotionStyle());
    for(String scene:List.of(tm.getSceneStyle(),"app_editorial","app_room","app_after_dark")) {
     ThemeVisuals b=base(tm,scene);
     var over=AssetVisualsKt.over(v,b,new AssetOptions(true,true,true,true,true));same(b,over);
     if(!over.asset("play").equals(a.getAssets().get("play")) || !over.asset("headingFont").equals(a.getAssets().get("headingFont")))throw new AssertionError("override missing");
     if(!over.getMaterialStyle().equals(a.getMaterialStyle()) || !over.getMotionStyle().equals(a.getMotionStyle()))throw new AssertionError("preset missing");
     var off=AssetVisualsKt.over(v,b,new AssetOptions(false,false,false,false,false));same(b,off);
     if(!off.getAssets().equals(b.getAssets()) || !off.getMaterialStyle().equals(b.getMaterialStyle()) || !off.getMotionStyle().equals(b.getMaterialStyle()))throw new AssertionError("disabled layer did not restore base");
     var justFonts=AssetVisualsKt.over(v,b,new AssetOptions(false,true,false,false,false));
     if(!Objects.equals(justFonts.asset("play"),b.asset("play")) || !justFonts.asset("bodyFont").equals(a.getAssets().get("bodyFont")))throw new AssertionError("font toggle leaked icons");
    }
    pairs++;
   }
  }
  System.out.println(pairs+" theme/pack combinations: identity, all palette colors/artwork preserved; category restoration, font-only isolation and custom-scene preservation passed.");
 }
}
