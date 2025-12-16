package space.webkombinat.feg2.View.Screen

import android.content.Context
import android.credentials.GetCredentialException
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import kotlin.random.Random


class AuthenticationManager(
    private val web_google_key: String
) {
    private val auth = FirebaseAuth.getInstance()

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Error(task.exception?.message.toString()))
                }
            }
    }

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Error(task.exception?.message.toString()))
                }
            }
    }

    fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    private fun generateSecureRandomNonce(): String {
        // 例: Base64エンコードされたランダムなバイト列をノンスとして使用
        val randomBytes = Random.nextBytes(32)
        return Base64.encodeToString(randomBytes, Base64.NO_WRAP)
    }


//    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signInWithGoogle(context: Context): String?{

//        val webclientId = context.getString()
//        val webClientId = "825780778120-l99chspjdtfb88q1rcrs96q42b43l9q5.apps.googleusercontent.com"
        val auth = FirebaseAuth.getInstance()
        val credentialManager = CredentialManager.create(context)
//        val nonce = generateSecureRandomNonce()

        // 1. Google IDトークンを取得するためのオプションを構築
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // 常にアカウント選択を表示 (必要に応じてtrueに変更)
            .setServerClientId(web_google_key)
//            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            // 2. Credential Managerを呼び出す (ボトムシートが表示される)
            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context =  context
            )

            // 3. 取得した認証情報からIDトークンを抽出
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(response.credential.data)

            // 4. ノンスの検証 (セキュリティ)
//            if (googleIdTokenCredential.nonce != nonce) {
//                Log.e("Auth", "Nonce mismatch.")
//                return null
//            }

            // 5. Firebase Authで認証
            val idToken = googleIdTokenCredential.idToken
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            val result = auth.signInWithCredential(firebaseCredential).await() // Coroutineで非同期呼び出しを待機

            // 6. 成功
            return result.user?.uid

        } catch (e: Exception) {
            // ユーザーがキャンセル、またはエラーが発生した場合
            Log.e("Auth", "Google Sign-in failed: ${e.message}")
            return null
        } catch (e: Exception) {
            // その他のエラー (Firebase認証失敗など)
            Log.e("Auth", "Firebase Auth failed: ${e.message}")
            return null
        }
    }

//    fun

}

interface AuthResponse {
    data object Success: AuthResponse
    data class Error(val message: String): AuthResponse
}