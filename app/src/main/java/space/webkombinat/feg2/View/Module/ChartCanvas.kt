package space.webkombinat.feg2.View.Module

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Model.Constants.CANVAS_WIDTH
import space.webkombinat.feg2.Model.Constants.ONE_SECOND_WIDTH
import space.webkombinat.feg2.getScreenSize

@Composable
fun ChartCanvas(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    temp_f: SnapshotStateList<Float>,
    temp_s: SnapshotStateList<Float>,
    temp_f_watermark: SnapshotStateList<Float>? = null,
    temp_s_watermark: SnapshotStateList<Float>? = null,
    topRange: Int = 300,
    bottomRange: Int = 0
) {
    val ctx = LocalContext.current
    val screenWidth = with(LocalDensity.current) {CANVAS_WIDTH.toDp()}
    Canvas(
        modifier = modifier
            .horizontalScroll(scrollState)
            .height(ctx.getScreenSize().second.dp)
            .width(screenWidth)
    ) {
        println("Chart size: ${temp_f.size}")
        if (temp_f.isNotEmpty() && temp_s.isNotEmpty()) {
            LineChart(tempList = temp_f, color = Color(0xFFDC5785), topRange = topRange, bottomRange = bottomRange)
            LineChart(tempList = temp_s, color = Color(0xFF548DB1), topRange = topRange, bottomRange = bottomRange)
        }
        if (temp_f_watermark != null && temp_s_watermark != null) {
            println("temp_f_watermark ${temp_s_watermark.size}")
            LineChart(tempList = temp_f_watermark, color = Color(0x77DC5785), topRange = topRange, bottomRange = bottomRange)
            LineChart(tempList = temp_s_watermark, color = Color(0x77548DB1), topRange = topRange, bottomRange = bottomRange)
        }
    }
}

fun DrawScope.LineChart(
    tempList: SnapshotStateList<Float>,
    color: Color = Color.Blue,
    topRange: Int = 300,
    bottomRange: Int = 0
){
    val height = topRange - bottomRange
    val one_temp_height = size.height / height
    //java.util.ConcurrentModificationException 対策で配列のコピーを取る。
    val snapshot = tempList.toList()
    snapshot.forEachIndexed{ index, temp ->
        if (index == 0) return@forEachIndexed

        val temp_y = temp - bottomRange
        val temp_y_prev = tempList[index-1] - bottomRange
        val y = size.height - (temp_y * one_temp_height)
        val y_b = size.height - (temp_y_prev * one_temp_height)
        val start = Offset((index-1) * ONE_SECOND_WIDTH, y_b)
        val end = Offset(index * ONE_SECOND_WIDTH, y)

        drawLine(
            color = color,
            start = start,
            end = end,
            strokeWidth = 1.dp.toPx()
        )
    }
}