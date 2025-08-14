package space.webkombinat.feg2.ViewModel

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Model.AppTheme
import space.webkombinat.feg2.Model.BLEController
import space.webkombinat.feg2.Model.Constants
import space.webkombinat.feg2.Model.UserPreferencesRepository
import kotlin.Long

@HiltViewModel
class SettingVM @Inject constructor(
   private val userPreferencesRepository: UserPreferencesRepository,
   val bleController: BLEController
): ViewModel() {
    val userSettings = combine(
        listOf(
            userPreferencesRepository.isProfileId,
            userPreferencesRepository.isTheme,
            userPreferencesRepository.isTopRange,
            userPreferencesRepository.isBottomRange,
            userPreferencesRepository.isBleAddress,
            userPreferencesRepository.isBrightness,
            userPreferencesRepository.isCaribF,
            userPreferencesRepository.isCaribS
        )
    ) { values ->
        UserSettings(
            profileId = values[0] as Long,
            uiTheme = values[1] as Int,
            topRange = values[2] as Int,
            bottomRange =  values[3] as Int,
            BLEAddress = values[4] as String,
            isBrightness = values[5] as Int,
            isCaribF = values[6] as Int,
            isCaribS = values[7] as Int
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings(
            profileId = -1L,
            uiTheme = AppTheme.System.num,
            topRange = 230,
            bottomRange = 0,
            BLEAddress = "",
            isBrightness = 3,
            isCaribF = 0,
            isCaribS = 0
        )
    )

    fun updateRange(bottom: Int, top: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveBottomRange(bottom)
            userPreferencesRepository.saveTopRange(top)
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.saveTheme(theme.num)
        }
    }

    fun clearBLEAddress() {
        viewModelScope.launch {
            userPreferencesRepository.saveBLE_ADD("")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setBrightness(brightness: Int) {
        bleController.setBrightness(brightness)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setTempCarib(temp: Int, type: Constants.CHARA_CARIB) {
        bleController.setCarib(carib = temp, type =  type)
    }
}

data class UserSettings(
    val profileId: Long,
    val uiTheme: Int,
    val topRange: Int,
    val bottomRange: Int,
    val BLEAddress: String,
    val isBrightness: Int,
    val isCaribF: Int,
    val isCaribS: Int
)
