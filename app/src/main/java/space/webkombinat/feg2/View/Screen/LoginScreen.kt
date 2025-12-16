package space.webkombinat.feg2.View.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    web_google_key: String,
    nav: () -> Unit
) {
    val auth = remember { AuthenticationManager(web_google_key) }
    val scope = rememberCoroutineScope()
    val ctx  = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            colors = ButtonDefaults.buttonColors(
                // カスタムの背景色 (例: Google Red)
                containerColor = Color(0xFFDB4437),
                // カスタムのコンテンツ色 (例: 白)
                contentColor = Color.White,
                // (オプション) 無効化時の背景色
//                disabledContainerColor = Color.LightGray,
                // (オプション) 無効化時のコンテンツ色
//                disabledContentColor = Color.DarkGray
            ),
            onClick = {
                scope.launch {
                    val uid = auth.signInWithGoogle(ctx)
                    if (uid != null) {
                        val auth = FirebaseAuth.getInstance()
                        println("google auth ${auth.uid}")
                        nav()
                    } else {
                        println("null user")
                    }
                }
            }
        ) {
            Text("Google")
        }
    }
}