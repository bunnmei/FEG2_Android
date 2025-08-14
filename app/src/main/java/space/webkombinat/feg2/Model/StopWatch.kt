package space.webkombinat.feg2.Model

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Model.BLEController.BLE_STATUS
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class StopWatch @Inject constructor(
    val chartState: ChartState,
    val bleController: BLEController,
    val notif: Notif,
    val context: Context
) {
    val displayTime: MutableState<String> = mutableStateOf("00:00.00")
    val state: MutableState<StopWatchState> = mutableStateOf(StopWatchState.STOPED)

    private var time: Duration = Duration.ZERO
    private var displayTimer: Timer? = null
    private var totalSeconds = -1L

    fun start() {
        if (state.value == StopWatchState.STARTED) return
        state.value = StopWatchState.STARTED

        displayTimer = fixedRateTimer(initialDelay = 0L, period = 10L) {
            time = time.plus(10.milliseconds)
            time.toComponents { hours, minutes, seconds, nanoseconds ->
                displayTime.value = "%02d:%02d.%02d".format( minutes, seconds, nanoseconds / 10_000_000)
            }
            if (time.inWholeSeconds > totalSeconds) {
//              一秒おきに実行される処理
                if (
                    bleController.BLE_STATE.value == BLEController.BLE_STATUS.CONNECTED ||
                    state.value == StopWatch.StopWatchState.STARTED
                    ) {
                    notif.notifUpdate(
                        time = displayTime.value.slice(0..4),
                        temp_f = "%.1f".format(bleController.temp_f_state.value),
                        temp_s = "%.1f".format(bleController.temp_s_state.value)
                    )
                }
                if (time.inWholeMinutes >= 30) {
                    pause()
                    chartState.keepChartData()
                    bgSave()
                }
                chartState.addTemp(bleController.temp_f_state.value, bleController.temp_s_state.value)
                totalSeconds = time.inWholeSeconds
            }
        }
        chartState.charData.value = ChartState.CharData.UNSAVED
    }

    fun pause() {
        if (state.value == StopWatchState.PAUSED) return
        state.value = StopWatchState.PAUSED
        displayTimer?.cancel()
    }

    fun clear() {
        if (state.value == StopWatchState.PAUSED) {
            state.value = StopWatchState.STOPED
            displayTimer = null
            time = Duration.ZERO
            displayTime.value = "00:00.00"
            totalSeconds = -1L
            chartState.clearTemp()
            if (bleController.BLE_STATE.value != BLE_STATUS.CONNECTED) {
                bleController.resetTemp()
            }

        }
    }

    private fun bgSave() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            if (chartState.charData.value != ChartState.CharData.SAVED) {
                bgSave()
            } else {
                Intent(context, BackgroundService::class.java).also { intent ->
                    intent.action = BackgroundService.Action.MAX_TIME.toString()
                    context.startService(intent)
                }
            }
        }
    }

    enum class StopWatchState {
        STOPED, //初期状態、リセットあと
        STARTED,
        PAUSED
    }
}