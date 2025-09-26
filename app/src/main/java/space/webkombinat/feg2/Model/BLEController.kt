package space.webkombinat.feg2.Model

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.webkombinat.feg2.Model.BLEController.BLE_STATUS.SCANNING
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_BRIGHTNESS_STRING
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_BRIGHTNESS_UUID
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_F_CARIB_STRING
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_F_CARIB_UUID
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_F_STRING
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_F_UUID
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_S_CARIB_STRING
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_S_CARIB_UUID
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_S_STRING
import space.webkombinat.feg2.Model.Constants.CHARACTERISTIC_UUID_S_UUID
import space.webkombinat.feg2.Model.Constants.SERVICE_UUID
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.LinkedList
import java.util.Queue
import java.util.UUID



@Suppress("DEPRECATION")
class BLEController @Inject constructor(
    val context: Context,
    val userPreferences: UserPreferencesRepository
) {
    private var BLEManager: BluetoothManager? = null
    private var BLEAdapter: BluetoothAdapter? = null
    private var device: BluetoothDevice? = null
    private var scannerHolder: BluetoothLeScanner? = null
    private var gattHolder: BluetoothGatt? = null

    private var BLEtaskQueue: BLETaskQueue? = null
    private var characteristicBrightness: BluetoothGattCharacteristic? = null
    private var characteristicTempCarib_F: BluetoothGattCharacteristic? = null
    private var characteristicTempCarib_S: BluetoothGattCharacteristic? = null
    private var ignoreDeviceList: MutableList<String> = mutableListOf()

    private val _temp_f = MutableStateFlow(0.0f)
    val temp_f_state = _temp_f.asStateFlow()
    private val _temp_s = MutableStateFlow(0.0f)
    val temp_s_state = _temp_s.asStateFlow()
    var BLE_STATE: MutableState<BLE_STATUS> = mutableStateOf(BLE_STATUS.DISCONNECTED)

    val deviceName = mutableStateOf("")
    val deviceVersion = mutableStateOf("")

    private var number = 0;
    fun resetTemp() {
        _temp_f.value = 0.0f
        _temp_s.value = 0.0f
    }

    //SCAN BLE DEVICE -start-
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScanBLE() {
        BLE_STATE.value = SCANNING
        BLEManager = context.getSystemService(BluetoothManager::class.java)
        BLEAdapter = BLEManager?.adapter
        if(BLEAdapter == null || BLEAdapter?.isEnabled == false) return //BLEが無効だとストップにする

        val BLEScanner = BLEAdapter!!.bluetoothLeScanner
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        BLEScanner.let  { scanner ->
            if(scannerHolder == null) {
                scannerHolder = scanner
                scannerHolder!!.startScan(null, scanSettings, BLEScanCallback)
                println("scannerHolder = ${scannerHolder}")
                println("BLEManager = ${BLEManager}")
                println("BLEAdapter = ${BLEAdapter}")
                println("ble scan start")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanBLE(scanTiming: String? = null) {
        println("called stopScanBLE()")
        scannerHolder?.stopScan(BLEScanCallback)
        scannerHolder = null
        if(scanTiming == "user_scan_cancel") {
            BLE_STATE.value = BLE_STATUS.DISCONNECTED
        }
    }

    private val BLEScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(allOf = arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN))
        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            println("called onScanResult()")

            if (result.device?.name != null) {
                println("discover noname device ${result.device.name}")
                if (userPreferences.bleAddress == "") {
                    println("discover device")
                    if (ignoreDeviceList.contains(result.device.address)) return
                    device = result.device
                    stopScanBLE()
                    startConnectBLE()
                } else {
                    if (result.device.address == userPreferences.bleAddress) {
                        println("保存済みのデバイス発見 ${result.device.address}")
                        device = result.device
                        stopScanBLE()
                        startConnectBLE()
                    }
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            println("Scan failed with error code: $errorCode")
        }
    }
    //SCAN BLE DEVICE -end-

    //CONNECT BLE DEVICE -start-
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startConnectBLE(){
        if (BLEAdapter == null || device == null) return
        BLEAdapter.let  { adapter ->
            try {
                println("try Gatt connect")
                val Rdevice = adapter?.getRemoteDevice(device!!.address) //??
                if(Rdevice == null) {
                    println("Rdevice is null")
                    return
                }
                gattHolder = Rdevice.connectGatt(context, false, bluetoothGattCallback)
                BLEtaskQueue = BLETaskQueue(gattHolder!!)
            } catch (e:Exception) {
                println("BLE GATT 接続エラー ${e.message}")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnectBLE(disconnectTiming: String? = null){
        if (disconnectTiming == "user_disconnect") {
            BLE_STATE.value = BLE_STATUS.DISCONNECTING
        }
        gattHolder?.disconnect()
    }

    val bluetoothGattCallback = object : BluetoothGattCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d("BLE", "onConnectionStateChange: status=$status, newState=$newState")
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                println("called bleConnect() and successfully BLE GATT")
                gattHolder?.discoverServices()

            } else if ( newState == BluetoothGatt.STATE_DISCONNECTED) {
                BLE_STATE.value = BLE_STATUS.DISCONNECTING
                println("called disconnectBLE()")
                try {
                    BLEAdapter = null
                    BLEManager = null
                    device = null
                    BLEtaskQueue = null
                    gattHolder?.close()
                    gattHolder = null
                    characteristicBrightness = null
                    characteristicTempCarib_F = null
                    characteristicTempCarib_S = null
                    deviceName.value = ""
                    deviceVersion.value = ""
                    if(BLE_STATE.value == BLE_STATUS.DISCONNECTING) {
                        BLE_STATE.value = BLE_STATUS.DISCONNECTED
                    }
                } catch (e:Exception) {
                    println("BLE GATT Close error")
                    e.printStackTrace()
                }

            } else if ( newState == BluetoothGatt.STATE_DISCONNECTING) {
                println("GATT DISCONNECTING")
            }
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN))
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if(status != BluetoothGatt.GATT_SUCCESS) return
            var targetServiceContain = false //目的のBLE端末かどうかServiceのUUIDとCharacteristicのUUIDでチェック
            val services = gatt?.getServices()
            services!!.forEach { service ->
                if(service.uuid.toString() == SERVICE_UUID) {
                    targetServiceContain = true
                }
            }

            if(!targetServiceContain) {
                ignoreDeviceList.add(device!!.address)
                disconnectBLE()
                device = null
                scannerHolder = null
                gattHolder = null
                startScanBLE()
                return
            }

//            BLE_STATE.value = BLE_STATUS.CONNECTED
            runBlocking {
                userPreferences.saveBLE_ADD(device!!.address)
            }

            services.forEach { service ->
                if(service.uuid.toString() == "0000180a-0000-1000-8000-00805f9b34fb") {
                    service.characteristics.forEach { chara ->
                        if(chara.uuid.toString() == "00002a26-0000-1000-8000-00805f9b34fb") {
                            val version = service?.getCharacteristic(UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb"))
                            println("v read queue")
                            BLEtaskQueue?.queueRead(version!!)
                        }
                    }
                }
                if (service.uuid.toString() == "00001800-0000-1000-8000-00805f9b34fb"){
                    service.characteristics.forEach { chara ->
                        if(chara.uuid.toString() == "00002a00-0000-1000-8000-00805f9b34fb") {
                            val name = service?.getCharacteristic(UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb"))
                            BLEtaskQueue?.queueRead(name!!)
                        }
                    }
                }

                if (service.uuid.toString() == SERVICE_UUID) {
                    service.characteristics.forEach { chara ->
                        if (chara.uuid.toString() == CHARACTERISTIC_UUID_F_STRING) {
                            val characteristic_F = service?.getCharacteristic(CHARACTERISTIC_UUID_F_UUID)
                            BLEtaskQueue?.queueWriteNotif(characteristic_F!!)
                        } else if (chara.uuid.toString() == CHARACTERISTIC_UUID_S_STRING) {
                            val characteristic_S = service?.getCharacteristic(CHARACTERISTIC_UUID_S_UUID)
                            BLEtaskQueue?.queueWriteNotif(characteristic_S!!)
                        }

                        else if (chara.uuid.toString() == CHARACTERISTIC_UUID_F_CARIB_STRING) {
                            val characteristic_F = service?.getCharacteristic(CHARACTERISTIC_UUID_F_CARIB_UUID)
                            BLEtaskQueue?.queueRead(characteristic_F!!)
                            characteristicTempCarib_F = characteristic_F
                        } else if (chara.uuid.toString() == CHARACTERISTIC_UUID_S_CARIB_STRING) {
                            val characteristic_S = service?.getCharacteristic(CHARACTERISTIC_UUID_S_CARIB_UUID)
                            BLEtaskQueue?.queueRead(characteristic_S!!)
                            characteristicTempCarib_S = characteristic_S
                        } else if (chara.uuid.toString() == CHARACTERISTIC_UUID_BRIGHTNESS_STRING) {
                            val characteristic_Bright = service?.getCharacteristic(CHARACTERISTIC_UUID_BRIGHTNESS_UUID)
                            BLEtaskQueue?.queueRead(characteristic_Bright!!)
                            characteristicBrightness = characteristic_Bright
                        }
                    }
                }
            }
            println("すべてのキャラクタリスティックの読み取り完了")
            Intent(context, BackgroundService::class.java).also { intent ->
                intent.action = BackgroundService.Action.NOTIF_START.toString()
                context.startService(intent)
            }
            BLEtaskQueue?.onOperationFinishedCheck {
                BLE_STATE.value = BLE_STATUS.CONNECTED
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            BLEtaskQueue?.onOperationFinishedCheck {
                BLE_STATE.value = BLE_STATUS.CONNECTED
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            println("onCharacteristicChangedがよびだされたよ counter ${number}")
            // 値を取得
            if (characteristic != null) {
                val current_value = floatFrom8ByteArray(characteristic.value)
                if(characteristic.uuid.toString() == CHARACTERISTIC_UUID_F_STRING){
                    if (!current_value.isNaN()) {
                        _temp_f.value = current_value
                    }
                } else if(characteristic.uuid.toString() == CHARACTERISTIC_UUID_S_STRING){
                    if (!current_value.isNaN()) {
                        _temp_s.value = current_value
                    }
                }
                number += 1
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (characteristic != null) {
                when(characteristic.uuid.toString()) {
                    CHARACTERISTIC_UUID_BRIGHTNESS_STRING -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            val data: ByteArray = characteristic.value
                            println("read brightness ${characteristic.value} - ${data[0].toInt()}")
                            userPreferences.saveBrightness(data[0].toInt())
                        }
                    }
                    CHARACTERISTIC_UUID_F_CARIB_STRING -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val data: ByteArray = characteristic.value
                            println("read carib data ${data[0].toInt()}")
                            userPreferences.saveCaribF(data[0].toInt())
                        }
                    }
                    CHARACTERISTIC_UUID_S_CARIB_STRING -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val data: ByteArray = characteristic.value
                            userPreferences.saveCaribS(data[0].toInt())
                        }
                    }

                    "00002a00-0000-1000-8000-00805f9b34fb" -> {
//                        val deviceName = String(characteristic.value)
                        val deviceLocalName = characteristic.getStringValue(0);
                        deviceName.value = deviceLocalName

                        println("gatt 接続後の名前の確認 - ${deviceLocalName}")
                    }
                    "00002a26-0000-1000-8000-00805f9b34fb" -> {
                        val deviceVer = String(characteristic.value)
                        deviceVersion.value = deviceVer
                        println("gatt 接続後のバージョン確認 - ${deviceVer}")
                    }
                }
                BLEtaskQueue?.onOperationFinishedCheck {
                    BLE_STATE.value = BLE_STATUS.CONNECTED
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            println("書き込みが完了しました。")
            val data: ByteArray = characteristic.value

            if(characteristic.uuid.toString() == CHARACTERISTIC_UUID_BRIGHTNESS_STRING){
                println("brightness書き込み完了 ${data[0].toInt()}")
                CoroutineScope(Dispatchers.IO).launch {
                    userPreferences.saveBrightness(data[0].toInt())
                }
            }
            if(characteristic.uuid.toString() == CHARACTERISTIC_UUID_F_CARIB_STRING) {
                println("chara_f書き込み完了 ${data[0].toInt()}")
                CoroutineScope(Dispatchers.IO).launch {
                    userPreferences.saveCaribF(data[0].toInt())
                }
            }

            if(characteristic.uuid.toString() == CHARACTERISTIC_UUID_S_CARIB_STRING) {

                println("chara_s書き込み完了 ${data[0].toInt()}")
                CoroutineScope(Dispatchers.IO).launch {
                    userPreferences.saveCaribS(data[0].toInt())
                }
            }
            BLEtaskQueue?.onOperationFinishedCheck {
                BLE_STATE.value = BLE_STATUS.CONNECTED
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setBrightness(brightness: Int) {
        if(characteristicBrightness == null) return
        BLEtaskQueue?.queueWrite(characteristicBrightness!!, byteArrayOf(brightness.toByte()))
        BLEtaskQueue?.onOperationFinishedCheck{}
        CoroutineScope(Dispatchers.IO).launch {
            userPreferences.saveBrightness(brightness)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setCarib(carib: Int, type: Constants.CHARA_CARIB) {
        if(characteristicTempCarib_F == null && characteristicTempCarib_S == null) return
        when(type) {
            Constants.CHARA_CARIB.TEMP_F -> {
                BLEtaskQueue?.queueWrite(characteristicTempCarib_F!!, byteArrayOf(carib.toByte()))
            }
            Constants.CHARA_CARIB.TEMP_S -> {
                BLEtaskQueue?.queueWrite(characteristicTempCarib_S!!, byteArrayOf(carib.toByte()))
            }
        }
        BLEtaskQueue?.onOperationFinishedCheck{}
    }

    fun floatFrom8ByteArray(value: ByteArray): Float {
        if(value.size == 8) {
            return Float.fromBits(
                (value[7].toInt() and 0xff shl 56) or
                        (value[6].toInt() and 0xff shl 48) or
                        (value[5].toInt() and 0xff shl 40) or
                        (value[4].toInt() and 0xff shl 32) or
                        (value[3].toInt() and 0xff shl 24) or
                        (value[2].toInt() and 0xff shl 16) or
                        (value[1].toInt() and 0xff shl 8) or
                        (value[0].toInt() and 0xff)
            )
        } else if (value.size == 4) {

            return Float.fromBits(
            (value[3].toInt() and 0xff shl 24) or
                    (value[2].toInt() and 0xff shl 16) or
                    (value[1].toInt() and 0xff shl 8) or
                    (value[0].toInt() and 0xff)
            )
        } else {
            return 0.0f
        }

    }

    //CONNECT BLE DEVICE -end-

    enum class BLE_STATUS {
        SCANNING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
    }
}