package space.webkombinat.feg2

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import space.webkombinat.feg2.Model.AppTheme
import space.webkombinat.feg2.Model.UserPreferencesRepository
import space.webkombinat.feg2.View.Navigation
import space.webkombinat.feg2.ui.theme.FEG2Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var userPreferences: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(this)
        setContent {
            val theme by userPreferences.isTheme.collectAsState(initial = AppTheme.System.num)
            val isDarkTheme = when(theme) {
                0 -> isSystemInDarkTheme()
                1 -> true
                2 -> false
                else -> false
            }
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()),
                navigationBarStyle = if (isDarkTheme) {
                    SystemBarStyle.auto(
                        Color.Black.toArgb(),
                        Color.Black.toArgb()
                    )
                } else {
                    SystemBarStyle.auto(
                        Color.White.toArgb(),
                        Color.White.toArgb()
                    )
                }
            )
            val view = LocalView.current
//            SideEffect {
//                //  status bar
//                val window = (view.context as android.app.Activity).window
//                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
//                // navigation bar
//                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
//            }
            FEG2Theme(
                darkTheme = isDarkTheme
            ) {
                Navigation()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FEG2Theme {
        Greeting("Android")
    }
}

fun Context.getScreenSize(): Pair<Int, Int> {
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels
    val density = displayMetrics.density
    return Pair(
        first = (screenWidth / density).toInt(),
        second = (screenHeight/ density).toInt()
    )
}

fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    return this.composed {
        this.clickable(
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    }
}


fun requestPermissions(activity: Activity) {
    var permissions: Array<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION, // 必要なら残す
            Manifest.permission.POST_NOTIFICATIONS
        )
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        else -> arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    ActivityCompat.requestPermissions(
        activity,
        permissions,
        0
    )
}

fun bleAdapter(context: Context) : Boolean {
    val bleManager = context.getSystemService(BluetoothManager::class.java)
    if(bleManager.adapter.isEnabled){
        return true
    }
    return false
}

fun gpsAdapter(context: Context) : Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    println("GPS State ${isGpsEnabled}")
        return if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            return try {
                val mode = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                mode != Settings.Secure.LOCATION_MODE_OFF
            } catch (e: Exception) {
                false
            }
        }
}