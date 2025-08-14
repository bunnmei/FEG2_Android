package space.webkombinat.feg2.View.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.View.Module.ProfileCard
import space.webkombinat.feg2.ViewModel.LogEditVM
import space.webkombinat.feg2.getScreenSize

@Composable
fun LogEditScreen(
    modifier: Modifier = Modifier,
    vm: LogEditVM
) {
    val profile by vm.profileLinkChart.collectAsState()
    val ctx = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "編集",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(16.dp)
        )

        Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = (ctx.getScreenSize().first / 20).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier.height(8.dp))
            Spacer(modifier.height(1.dp).fillMaxWidth().background(Color.Gray.copy(alpha = 0.7f)))
            Spacer(modifier.height(16.dp))
            OutlinedTextField(
                modifier = modifier
                    .fillMaxWidth(),
                value = profile.name ?: "",
                onValueChange = {
                    vm.updateName(it)
                },
                label = { Text("Profile Title") }
            )

            Spacer(modifier = modifier.height((ctx.getScreenSize().first / 20).dp))

            OutlinedTextField(
                modifier = modifier
                    .fillMaxWidth()
                    .height((40 * 3 + 2 + 2).dp)
                    .padding(bottom = (ctx.getScreenSize().first / 20).dp),
                value = profile.description ?: "",
                onValueChange = {
                    vm.updateDescription(it)
                },
                label = { Text("Profile Description") },
                maxLines = 3
            )
        }

        Text(
            text = "プレビュー",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(16.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth().padding(horizontal = (ctx.getScreenSize().first / 20).dp)
        ) {
            Spacer(modifier.height(8.dp))
            Spacer(modifier.height(1.dp).fillMaxWidth().background(Color.Gray.copy(alpha = 0.7f)))
            Spacer(modifier.height(16.dp))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            ProfileCard(
                name = profile.name ?: "",
                description = profile.description ?: "",
                createAt = profile.createAt,
                profileId = profile.id,
                keptProfileId = -1
            )
        }

        Spacer(modifier = modifier.height(60.dp))
    }
}