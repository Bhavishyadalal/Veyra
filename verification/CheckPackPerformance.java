import app.veyra.music.ui.themes.PackResourcePolicyKt;
import java.util.*;

/** Focused warmup budget/key regression check; this is not a device FPS benchmark. */
public class CheckPackPerformance {
 public static void main(String[] args) {
  Map<String,String> assets=new LinkedHashMap<>();
  for(String key:List.of("nav_home","nav_search","nav_explore","nav_library","settings","back","queue","lyrics","account","history","download","shuffle","repeat","favorite","play","pause","previous","next","play_pressed","pause_pressed","previous_pressed","next_pressed"))assets.put(key,"/packs/id-v1/icons/"+key+".png");
  for(int i=0;i<150;i++)assets.put("other_"+i,"/packs/id-v1/icons/other_"+i+".png");
  assets.put("header","wallpaper.webp");assets.put("headingFont","font.ttf");assets.put("preview","preview.webp");
  var plan=PackResourcePolicyKt.themeGlyphWarmupPlan(assets);
  if(plan.size()>24 || plan.size()!=new HashSet<>(plan).size())throw new AssertionError("unbounded/duplicate warmup");
  long bytes=0;
  for(var p:plan) {
   if(!p.getFirst().endsWith(".png") || (p.getSecond()!=128 && p.getSecond()!=256))throw new AssertionError("non-glyph or unbounded size");
   bytes+=4L*p.getSecond()*p.getSecond();
   if(p.getFirst().contains("_pressed") && p.getSecond()!=256)throw new AssertionError("pressed skin sizing");
  }
  if(bytes>4L*1024*1024)throw new AssertionError("excessive prefetch memory");
  String key=PackResourcePolicyKt.themeGlyphCacheKey("/packs/id-v1/icons/play.png",256);
  if(key.equals(PackResourcePolicyKt.themeGlyphCacheKey("/packs/id-v2/icons/play.png",256)) || key.equals(PackResourcePolicyKt.themeGlyphCacheKey("/packs/id-v1/icons/play.png",128)))throw new AssertionError("cache collision");
  try{PackResourcePolicyKt.themeGlyphCacheKey("p",2048);throw new AssertionError("large glyph accepted");}catch(IllegalArgumentException expected){}
  System.out.println("Glyph warmup: "+plan.size()+" requests, "+bytes+" decoded RGBA bytes maximum; revision/size cache isolation and non-glyph exclusion passed.");
 }
}
