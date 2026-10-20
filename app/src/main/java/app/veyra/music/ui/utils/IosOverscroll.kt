/*
 * Adapted from Convx's IosOverscroll.kt (GPL-3.0):
 * https://github.com/cosmictaserdev-creator/Convx
 */
package app.veyra.music.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

private const val rubberBandConstant = 0.55f
private const val fallbackContainerPx = 2_000f
private const val maxBounceVelocity = 10_000f
private const val highVelocityThreshold = 5_000f
private const val normalBounceStiffness = 247f
private const val fastBounceStiffness = 130f

/**
 * Converts raw pull distance to UIKit's self-limiting rubber-band curve.
 */
private fun rubberBand(rawDistance: Float, containerPx: Float): Float {
    val dimension = containerPx.takeIf { it > 0f } ?: fallbackContainerPx
    val distance = abs(rawDistance)
    val banded = (1f - 1f / (distance * rubberBandConstant / dimension + 1f)) *
        dimension
    return banded * sign(rawDistance)
}

/**
 * An iOS-style overscroll effect for Compose scroll containers.
 *
 * It replaces Android's stretch/glow with a UIScrollView-like rubber band during a
 * drag, then critically damps the content back to its resting position on release.
 */
private class IosOverscrollEffect : OverscrollEffect {
    private val rawPullX = mutableFloatStateOf(0f)
    private val rawPullY = mutableFloatStateOf(0f)
    private var settleX: Job? = null
    private var settleY: Job? = null
    private var containerWidthPx = 0f
    private var containerHeightPx = 0f

    override val isInProgress: Boolean
        get() = rawPullX.floatValue != 0f || rawPullY.floatValue != 0f

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        // A new touch owns the offset immediately; an old spring must not write
        // over the drag on the next frame. Preserve its current position.
        if (source == NestedScrollSource.UserInput) {
            settleX?.cancel()
            settleY?.cancel()
        }
        // When pulling back from an existing stretch, settle the stretch before
        // allowing the list itself to scroll. This keeps the motion continuous.
        val paidX = payDown(rawPullX, delta.x)
        val paidY = payDown(rawPullY, delta.y)
        val remaining = Offset(delta.x - paidX, delta.y - paidY)
        val consumed = performScroll(remaining)
        val leftover = remaining - consumed

        // Absorb only touch deltas. Fling leftovers must be returned so Compose
        // cancels its decay and gives their velocity to applyToFling below.
        var stretchedX = 0f
        var stretchedY = 0f
        if (source == NestedScrollSource.UserInput) {
            if (leftover.x != 0f) {
                rawPullX.floatValue += leftover.x
                stretchedX = leftover.x
            }
            if (leftover.y != 0f) {
                rawPullY.floatValue += leftover.y
                stretchedY = leftover.y
            }
        }
        return Offset(consumed.x + paidX + stretchedX, consumed.y + paidY + stretchedY)
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity,
    ) {
        coroutineScope {
            val stretchedX = rawPullX.floatValue != 0f
            val stretchedY = rawPullY.floatValue != 0f
            settleX?.cancel()
            settleY?.cancel()
            // Start the return on release, not after the list's decay finishes.
            // A stretched axis belongs to the spring, so it cannot also fling
            // the list beneath it and leave the content apparently stuck.
            if (stretchedX) settleX = launch { settle(rawPullX, velocity.x) }
            if (stretchedY) settleY = launch { settle(rawPullY, velocity.y) }
            val scrollVelocity = Velocity(
                if (stretchedX) 0f else velocity.x,
                if (stretchedY) 0f else velocity.y,
            )
            val leftover = scrollVelocity - performFling(scrollVelocity)
            if (!stretchedX) settleX = launch { settle(rawPullX, leftover.x) }
            if (!stretchedY) settleY = launch { settle(rawPullY, leftover.y) }
        }
    }

    private fun payDown(pull: MutableFloatState, delta: Float): Float {
        val current = pull.floatValue
        if (current == 0f || delta == 0f || sign(current) == sign(delta)) return 0f
        val target = current + delta
        val next = if (sign(target) != sign(current)) 0f else target
        pull.floatValue = next
        return next - current
    }

    private suspend fun settle(pull: MutableFloatState, leftoverVelocity: Float) {
        if (pull.floatValue == 0f && leftoverVelocity == 0f) return
        val velocity = leftoverVelocity.coerceIn(-maxBounceVelocity, maxBounceVelocity)
        animate(
            initialValue = pull.floatValue,
            targetValue = 0f,
            initialVelocity = velocity,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = if (abs(velocity) > highVelocityThreshold) {
                    fastBounceStiffness
                } else {
                    normalBounceStiffness
                },
            ),
        ) { value, _ -> pull.floatValue = value }
        pull.floatValue = 0f
    }

    override val node: DelegatableNode = IosOverscrollNode(
        offsetX = { rubberBand(rawPullX.floatValue, containerWidthPx) },
        offsetY = { rubberBand(rawPullY.floatValue, containerHeightPx) },
        onMeasured = { width, height ->
            containerWidthPx = width
            containerHeightPx = height
        },
    )
}

private class IosOverscrollNode(
    private val offsetX: () -> Float,
    private val offsetY: () -> Float,
    private val onMeasured: (Float, Float) -> Unit,
) : Modifier.Node(), LayoutModifierNode {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        onMeasured(
            (if (constraints.hasBoundedWidth) constraints.maxWidth else placeable.width).toFloat(),
            (if (constraints.hasBoundedHeight) constraints.maxHeight else placeable.height).toFloat(),
        )
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                translationX = offsetX()
                translationY = offsetY()
            }
        }
    }
}

private class IosOverscrollFactory(
    private val density: Density,
    private val scope: CoroutineScope,
) : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect = IosOverscrollEffect()

    override fun equals(other: Any?): Boolean =
        other is IosOverscrollFactory && other.density == density && other.scope === scope

    override fun hashCode(): Int = 31 * density.hashCode() + scope.hashCode()
}

/** Provides iOS-style rubber-band overscroll to every descendant scroll container. */
@Composable
fun rememberIosOverscrollFactory(): OverscrollFactory {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    return remember(density, scope) { IosOverscrollFactory(density, scope) }
}
