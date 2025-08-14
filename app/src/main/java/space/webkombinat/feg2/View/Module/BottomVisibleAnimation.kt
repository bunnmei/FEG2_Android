package space.webkombinat.feg2.View.Module

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun BottomVisibleAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val pxToMove = with(LocalDensity.current) {
        60.dp.toPx().roundToInt()
    }
    val pxToMoveZero = with(LocalDensity.current) {
        0.dp.toPx().roundToInt()
    }
    val offset by animateIntOffsetAsState(
        animationSpec =  spring(
            stiffness = Spring.StiffnessMediumLow
        ),
        targetValue = if (visible) {
            IntOffset(pxToMoveZero, pxToMoveZero)
        } else {
            IntOffset(pxToMoveZero, pxToMove)
        },
        label = "offset"
    )

    Box(
        modifier = modifier
            .offset {
                offset
            }
    ) {
        content()
    }
}