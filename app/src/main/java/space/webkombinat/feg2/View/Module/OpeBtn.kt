package space.webkombinat.feg2.View.Module

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothAudio
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Model.BLEController

@Composable
fun OpeBtn(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    visible: Boolean = true,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(animationSpec = tween(durationMillis = 150)),
        exit = scaleOut(animationSpec = tween(durationMillis = 150))
    ) {
        Button(
            modifier = modifier.height(50.dp).width(50.dp),
            enabled = enabled,
            contentPadding = PaddingValues(0.dp),
            shape = CircleShape,
            onClick = onClick

        ){
            content()
        }
    }

}