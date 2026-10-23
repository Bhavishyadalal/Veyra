package app.veyra.music.ui.themes

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

@Immutable
data class AssetOptions(val icons:Boolean=true,val fonts:Boolean=true,val controls:Boolean=true,
    val motion:Boolean=true,val colors:Boolean=true)

@Immutable
data class AssetVisuals(val id:String,val name:String,val assets:Map<String,String>,
    val materialStyle:String,val seekStyle:String,val seekHeight:Int,
    val rowCorner:Int,val buttonCorner:Int,val navCorner:Int,val headingInk:String,val motionStyle:String)

/** Layer only functional roles. The selected theme/style still owns identity, artwork and palette. */
fun AssetVisuals.over(base:ThemeVisuals, options:AssetOptions):ThemeVisuals {
    val replacements=assets.filterKeys {key -> if(key.endsWith("Font")) options.fonts else options.icons}
    val ink=Color(0xFF000000L or headingInk.removePrefix("#").toLong(16))
    fun contrast(a:Color,b:Color):Float = (max(a.luminance(),b.luminance())+.05f)/(min(a.luminance(),b.luminance())+.05f)
    val readableInk=if(contrast(ink,base.surface)>=4.5f && contrast(ink,base.raised)>=4.5f) ink else base.text
    return base.copy(assets=base.assets+replacements,
        iconStyle=if(options.icons) "original" else base.iconStyle,
        materialStyle=if(options.controls) materialStyle else base.materialStyle,
        seekStyle=if(options.controls) seekStyle else base.seekStyle,
        seekHeight=if(options.controls) seekHeight else base.seekHeight,
        rowCorner=if(options.controls) rowCorner else base.rowCorner,
        buttonCorner=if(options.controls) buttonCorner else base.buttonCorner,
        navCorner=if(options.controls) navCorner else base.navCorner,
        headingColor=if(options.colors) readableInk else base.headingColor,
        motionStyle=if(options.motion) motionStyle else base.motionStyle ?: base.materialStyle)
}
