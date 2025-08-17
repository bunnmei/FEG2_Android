package space.webkombinat.feg2.ViewModel

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Model.BLEController
import space.webkombinat.feg2.Model.BackgroundService
import space.webkombinat.feg2.Model.ChartState
import space.webkombinat.feg2.Model.Constants.decodeTemp
import space.webkombinat.feg2.Model.Constants.unpackTemp
import space.webkombinat.feg2.Model.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Model.StopWatch
import space.webkombinat.feg2.Model.UserPreferencesRepository
import space.webkombinat.feg2.ViewModel.LogDetailVM.UiState

@HiltViewModel
class ChartVM @Inject constructor(
    val bleController: BLEController,
    val stopWatch: StopWatch,
    val chartState: ChartState,
    val userPreferencesRepository: UserPreferencesRepository,
    private val profileRepository: ProfileRepository,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(0.0f, 0.0f))
    val uiState = _uiState.asStateFlow()

    val profileId = userPreferencesRepository.isProfileId
    val _profileLinkChart = MutableStateFlow<LogDetailVM.UiState>(UiState(profile = null))
    val profileLinkChart = _profileLinkChart.asStateFlow()

    init {
        viewModelScope.launch { //分けたほうがいいのか?
            bleController.temp_f_state.combine(bleController.temp_s_state) { temp_f, temp_s ->
                _uiState.update {
                    it.copy(temp_f = temp_f, temp_s = temp_s)
                }
            }.collect()
        }

        viewModelScope.launch(Dispatchers.IO) {
//            getChartData(userPreferencesRepository.isProfileIdNotFlow)
            profileId.collect { profileId ->
                if(profileId == -1L) {
                    _profileLinkChart.value.temp_f.clear()
                    _profileLinkChart.value.temp_s.clear()
                    return@collect
                }
                getChartData(profileId)
            }
        }
    }

    val userSettings = combine(
        userPreferencesRepository.isTopRange,
        userPreferencesRepository.isBottomRange,
    ) {topRange, bottomRange ->
        RangeSettings(
            topRange = topRange,
            bottomRange = bottomRange,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RangeSettings(
            topRange = 230,
            bottomRange = 0,
        )
    )

    fun ble(ctx: Context) {
        intentToBGService(act = BackgroundService.Action.BLE_START)
    }

    fun stopWatchOpe() {
        when (stopWatch.state.value) {
            StopWatch.StopWatchState.STOPED -> {
                stopWatch.start()
            }
            StopWatch.StopWatchState.STARTED -> {
                stopWatch.pause()
            }
            StopWatch.StopWatchState.PAUSED -> {
                stopWatch.start()
            }
        }
    }

    fun clear() {
        stopWatch.clear()
        chartState.clearTemp()
    }

    fun keepData() {
        chartState.keepChartData()
    }

    private fun getChartData(profileId: Long) {
        println("chart water mark init profileId $profileId")
        try {
            val profile = profileRepository.readProfileLinkChart(profileId)
//                    println("init LogDetailVM size ${profile.chart.size}\n")
//            _profileLinkChart.value.temp_f.clear()
//            _profileLinkChart.value.temp_s.clear()
            val temp_f = mutableStateListOf<Float>()
            val temp_s = mutableStateListOf<Float>()
            profile.chart.sortedBy { it.pointIndex }.forEach { chart ->
                val temps = unpackTemp(chart.temp)
                temp_f.add(decodeTemp(temps.first))
                temp_s.add(decodeTemp(temps.second))
            }
            _profileLinkChart.value = LogDetailVM.UiState(
                profile = profile,
                temp_f = temp_f,
                temp_s = temp_s,
            )
            println("profile link chart size ${_profileLinkChart.value.temp_f.size}")
        } catch (e: Exception){
            println(e)
        }
    }

    private fun intentToBGService(act: BackgroundService.Action) {
        Intent(context, BackgroundService::class.java).also { intent ->
            intent.action = act.toString()
            context.startService(intent)
        }
    }

    data class UiState(
        var temp_f: Float,
        var temp_s: Float,
    )

    data class RangeSettings(
        val topRange: Int,
        val bottomRange: Int,
    )
}
