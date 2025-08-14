package space.webkombinat.feg2.View.Module

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckBoxPanel(
    modifier: Modifier = Modifier,
    value: Boolean,
    title: String,
    desc: String?,
    enabled: Boolean = true,
    click: () -> Unit
) {
    Row(
        modifier = modifier
            .height(70.dp)
            .toggleable(
                value = value,
                role = Role.Checkbox,
                onValueChange = {
                    click()
                }
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 20.sp
            )
            Text(
                text = desc ?: "",
                fontSize = 12.sp
            )
        }
        Spacer(modifier = modifier.weight(1f))
        Checkbox(
            checked = value,
            onCheckedChange = null,
            enabled = enabled
        )
    }
}