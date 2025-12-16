package space.webkombinat.feg2.View.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginSuccessScreen(
    logout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auth = remember { FirebaseAuth.getInstance() }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Login Success")

        Text("uid: ${auth.uid}")
        Text("email: ${auth.currentUser?.email}")
        Text("displayName: ${auth.currentUser?.displayName}")
        Text("photoUrl: ${auth.currentUser?.photoUrl}")
        Text("phoneNumber: ${auth.currentUser?.phoneNumber}")
        Text("providerId: ${auth.currentUser?.providerId}")
        Text("isAnonymous: ${auth.currentUser?.isAnonymous}")
        Text("metadata: ${auth.currentUser?.metadata}")
        Text("lastSignInTimestamp: ${auth.currentUser?.metadata?.lastSignInTimestamp}")
        Text("creationTimestamp: ${auth.currentUser?.metadata?.creationTimestamp}")
        Text("isEmailVerified: ${auth.currentUser?.isEmailVerified}")
        Text("isAnonymous: ${auth.currentUser?.isAnonymous}")


        Button(onClick = {
            if (auth.uid != null) {
                auth.signOut()
                logout()
            }
        }) {
            Text(text = "ログアウト")
        }
    }
}