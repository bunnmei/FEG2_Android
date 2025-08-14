package space.webkombinat.feg2.View.Module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants

@Composable
fun SettingPanel(
    modifier: Modifier = Modifier,
    settingTitle: String,
    colPosi: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = colPosi,
    ) {
        Column(
            modifier.fillMaxWidth(0.9f),
        ) {
            Spacer(modifier.height(8.dp))
            Spacer(modifier.height(1.dp).fillMaxWidth().background(Color.Gray.copy(alpha = 0.7f)))
            Spacer(modifier.height(8.dp))
            Text(
                text = settingTitle,
                fontSize = 12.sp,
            )
        }
        content()
        Spacer(modifier.height(8.dp))
    }
}