package space.webkombinat.feg2.View.Module

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.Model.Constants.CANVAS_WIDTH
import space.webkombinat.feg2.Model.Constants.CHART_MINUTE
import space.webkombinat.feg2.Model.Constants.ONE_MINUTE_WIDTH

@Composable
fun TimeMemoryCanvas(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
) {
    val textMeasure = rememberTextMeasurer()
    val screenWidth = with(LocalDensity.current) {CANVAS_WIDTH.toDp()}
    val color = MaterialTheme.colorScheme.primary
    Canvas(
        modifier = modifier
            .horizontalScroll(scrollState)
            .height(24.dp)
            .width(screenWidth)
    ) {
        TimeMemoryAndText(
            textMeasure = textMeasure,
            color = color,
            font_size = 10
        )
    }
}

fun DrawScope.TimeMemoryAndText(
    textMeasure: TextMeasurer,
    color: Color,
    font_size: Int,
){
    val height = size.height

    for(i in 1..CHART_MINUTE){
        val xPosition = ONE_MINUTE_WIDTH * i
        val startPoint = Offset(x = xPosition, y = height)
        val endPoint = Offset(x = xPosition, y = height - 10.dp.toPx())
        val minutesToString = (i).toString().padStart(2, '0')
        //          時間メモリ線描画
        drawLine(
            color = color,
            start = startPoint,
            end = endPoint,
            strokeWidth = 2.5f
        )

        //          時間テキスト描画
        drawText(
            textMeasurer = textMeasure,
            text = minutesToString,
            style = TextStyle(
                fontSize = font_size.sp,
                textAlign = TextAlign.Center,
                color = color
            ),
            topLeft = Offset(x = xPosition - 5.dp.toPx(), y = height - 23.dp.toPx())
        )
    }
}