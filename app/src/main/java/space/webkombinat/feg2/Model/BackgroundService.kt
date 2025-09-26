package space.webkombinat.feg2.Model

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundService: Service() {
    @Inject lateinit var notif: Notif
    @Inject lateinit var bleController: BLEController
    @Inject lateinit var stopWatch: StopWatch

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    @OptIn(FlowPreview::class)
    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            bleController.temp_f_state.zip(bleController.temp_s_state) { temp_f, temp_s ->
                Pair(temp_f, temp_s)
            }.collect { (temp_f, temp_s) ->


                if (
                    bleController.BLE_STATE.value == BLEController.BLE_STATUS.CONNECTED &&
                    (stopWatch.state.value == StopWatch.StopWatchState.STOPED ||
                            stopWatch.state.value == StopWatch.StopWatchState.PAUSED)
                ) {
                    println("update notification Service Class bgservice")

                    notif.notifUpdate(
                        time = stopWatch.displayTime.value.slice(0..4),
                        temp_f = "%.1f".format(temp_f),
                        temp_s = "%.1f".format(temp_s)
                    )
                }
            }

        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresPermission(allOf = arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN))
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("onStartCommand action=${intent?.action}")
        when(intent?.action) {
            Action.DEBUG.toString() -> {
                println("Service running DEBUG Intent")
            }
            Action.BLE_START.toString() -> {
                when(bleController.BLE_STATE.value) {
                    BLEController.BLE_STATUS.SCANNING -> {
                        println("BGS BLE_STATE STOPSCANNING")
                        bleController.stopScanBLE(scanTiming = "user_scan_cancel")
                    }
                    BLEController.BLE_STATUS.CONNECTED -> {
                        println("BGS BLE_STATE DISCONNECTING")
                        bleController.disconnectBLE()
                        if (stopWatch.state.value == StopWatch.StopWatchState.STOPED) {
                            bleController.resetTemp()
                        }
                        if (stopWatch.state.value != StopWatch.StopWatchState.STARTED) {
                            stopForegroundService()
                        }
                    }
                    BLEController.BLE_STATUS.DISCONNECTED -> {
                        println("BGS BLE_STATE SCANNING")
                        bleController.startScanBLE()
                    }

                    BLEController.BLE_STATUS.DISCONNECTING -> {
                        println("BGS BLE_STATE DISCONNECTING NOW")
                    }
                }
            }

            Action.NOTIF_START.toString() -> {
                if (!notif.notifCreated){
                    notif.notifCreate()
                    notifStart()
                }
            }

            Action.NOTIF_STOP.toString() -> {
                bleController.disconnectBLE()
                when(stopWatch.state.value) {
                    StopWatch.StopWatchState.STOPED -> {}
                    StopWatch.StopWatchState.STARTED -> {
                        stopWatch.pause()
                        stopWatch.clear()
                    }
                    StopWatch.StopWatchState.PAUSED -> {
                        stopWatch.clear()
                    }
                }
                stopForegroundService()
            }

            Action.MAX_TIME.toString() -> {
                bleController.disconnectBLE()
                stopForegroundService()
            }
        }
        return START_NOT_STICKY
    }

    private fun notifStart(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notif.notifBuilder.build(), FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else{
            startForeground(1, notif.notifBuilder.build())
        }
    }

    private fun stopForegroundService() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000) //serviceStop後に、notif.updateが呼ばれるのを防ぐため
            println("stopForegroundServiceがよばれたよ")
            stopForeground(STOP_FOREGROUND_REMOVE)
            notif.delete();
            stopSelf()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        println("onTaskRemovedがよばれたよ")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Serviceが破棄されるときに、このスコープで起動したすべてのコルーチンをキャンセルする
        serviceScope.cancel()
        println("Service destroyed and scope cancelled.")
    }

    enum class Action {
        DEBUG,
        BLE_START,
        NOTIF_STOP,
        NOTIF_START,
        MAX_TIME,
    }

}
