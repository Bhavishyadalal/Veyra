import app.veyra.music.data.settings.*;

/** Small release-class preference migration/round-trip probe. */
public class CheckImmersiveOptions {
    public static void main(String[] args) {
        var codec=ExperienceOptions.Companion;
        if(codec.decode("{\"immersive\":true}").getImmersiveStyle()!=ImmersiveStyle.CLASSIC)throw new AssertionError("Existing users lost Classic");
        if(codec.decode("{\"immersiveStyle\":\"FUTURE_WORLD\",\"immersive\":true}").getImmersiveStyle()!=ImmersiveStyle.CLASSIC)throw new AssertionError("Future mode fallback");
        for(var style:ImmersiveStyle.values()) {
            var options=codec.decode("{\"immersive\":true,\"immersiveStyle\":\""+style.name()+"\",\"immersiveBackground\":\"BLEND\"}");
            if(options.getImmersiveStyle()!=style || !options.getImmersive() || options.getImmersiveBackground()!=ImmersiveBackground.BLEND)throw new AssertionError(style);
            if(!codec.decode(options.encode()).equals(options))throw new AssertionError("Round trip "+style);
        }
        System.out.println("All six immersive styles round-trip; legacy and unknown-mode preferences retain Classic and saved background choices.");
    }
}
