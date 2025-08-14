package space.webkombinat.feg2.View.Module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusPanel(
    modifier: Modifier = Modifier,
    str: String,
    color: Color
) {
    Row(
        modifier = modifier.height(80.dp),
    ) {
        Text(
            text = str,
            fontSize = 60.sp,
            fontStyle = FontStyle.Italic,
            color = color
        )
        Spacer(modifier = modifier.width(10.dp))
    }
}