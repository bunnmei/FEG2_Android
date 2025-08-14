package space.webkombinat.feg2.View.Module

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.Model.Constants.TEMP_MEMORY_WIDTH
import space.webkombinat.feg2.ViewModel.ChartVM
import space.webkombinat.feg2.getScreenSize

@Composable
fun TempMemoryCanvas(
    modifier: Modifier = Modifier,
    topRange: Int = 300,
    bottomRange: Int = 0
) {
    val ctx = LocalContext.current
    val textMeasure = rememberTextMeasurer()
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier= modifier
        .height(ctx.getScreenSize().second.dp)
        .width(TEMP_MEMORY_WIDTH.dp)
    ) {
//        val topRange = 300
//        val bottomRange = 0

        val height = size.height
        val oneStepHeight = height / ((topRange - bottomRange) / 10)

        for(temp in bottomRange .. topRange step 10) {
            val y = height - ((temp - bottomRange) / 10 * oneStepHeight)
            val start = Offset(x = 0f, y = y)
            val end = Offset(x = 10.dp.toPx(), y = y)

            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = 1.dp.toPx()
            )

            if(temp % 50 == 0) {
                drawText(
                    textMeasurer = textMeasure,
                    text = "$temp",
                    style = TextStyle(
                        fontSize = 10.sp,
                        textAlign = TextAlign.Start,
                        color = color
                    ),
                    topLeft = Offset(13.dp.toPx(), y = y - 6.dp.toPx())
                )
            }
        }
    }
}
