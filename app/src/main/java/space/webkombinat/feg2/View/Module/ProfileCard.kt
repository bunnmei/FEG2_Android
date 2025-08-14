package space.webkombinat.feg2.View.Module

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.getScreenSize
import java.text.SimpleDateFormat

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    name: String? = null,
    description: String? = null,
    navNext: () -> Unit = {},
    rmProfileId: () -> Unit = {},
    createAt: Long,
    profileId: Long,
    keptProfileId: Long,
) {
    val ctx = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp)),
        onClick = {
            navNext()
        }
    ) {
        Column {
            Row {
                Text(
                    modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    text = name ?: "No Name",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = modifier.weight(1f))
                Box(
                    modifier = modifier
                        .height(50.dp)
                        .width(50.dp)
                        .background(
                            if (keptProfileId == profileId) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                rmProfileId()
                            }
                        ),
//                                .clip(RoundedCornerShape(0.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "favi",
                        tint = if (keptProfileId == profileId) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current
                    )
                }
            }
        }

        Text(
            text = description ?: "No Description",
            modifier = modifier.padding(start = 16.dp, end = 16.dp),
            color = Color.Gray,
            maxLines = 3
        )
        Spacer(modifier = modifier.weight(1f))
        Row(
            modifier = modifier.padding(end = 16.dp, bottom = 16.dp)
        ) {
            Spacer(modifier = modifier.weight(1f))
            Text("${SimpleDateFormat("yyyy/MM/dd HH:mm").format(createAt)}")
        }
    }
    Spacer(modifier = modifier.height((ctx.getScreenSize().first / 20).dp))
}