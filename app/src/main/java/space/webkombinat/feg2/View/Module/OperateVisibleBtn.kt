package space.webkombinat.feg2.View.Module

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun OperateVisibleBtn(
    modifier: Modifier = Modifier,
    rotate: MutableState<Boolean>,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
//    LaunchedEffect(scrollState.value) {
//        println(scrollState.value)
//    }
    Column(
        modifier = modifier.padding(start = 24.dp)
    ) {

        Column(
            modifier = modifier.weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }

        val rotateAnime by animateIntAsState(
            targetValue = if(rotate.value) 45 else 0,
            animationSpec = tween(
                durationMillis = 150,
            ), label = ""
        )
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = modifier
                .rotate(rotateAnime.toFloat()),
            onClick = {
                rotate.value = !rotate.value
            }
        ){
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "open ope buttons",
                modifier = modifier.size(24.dp)
            )
        }
    }
}