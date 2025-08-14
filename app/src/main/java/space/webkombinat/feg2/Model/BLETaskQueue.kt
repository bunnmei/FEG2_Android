package space.webkombinat.feg2.Model

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.util.LinkedList
import java.util.Queue
import java.util.UUID


sealed class BleCommand {
    data class Read(val characteristic: BluetoothGattCharacteristic) : BleCommand()
    data class Write(val characteristic: BluetoothGattCharacteristic, val data: ByteArray) : BleCommand()
    data class WriteNotif(val characteristic: BluetoothGattCharacteristic) : BleCommand()
}

class BLETaskQueue(
    val gattHolder: BluetoothGatt
) {
    private val commandTask: MutableList<BleCommand> = mutableListOf()
    private var isProcessing = false

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun queueRead(chara: BluetoothGattCharacteristic) {
        commandTask.add(BleCommand.Read(chara))
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun queueWrite(chara: BluetoothGattCharacteristic, value: ByteArray) {
        commandTask.add(BleCommand.Write(chara, value))
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun queueWriteNotif(chara: BluetoothGattCharacteristic) {
        commandTask.add(BleCommand.WriteNotif(chara))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun next() {
        if(isProcessing) return
        isProcessing = true
        val command = commandTask.getOrNull(0) ?: return
        commandTask.removeAt(0)
        when (command) {
            is BleCommand.Read -> {
                gattHolder.readCharacteristic(command.characteristic)
            }
            is BleCommand.Write -> {
                command.characteristic.value = command.data
                command.characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                gattHolder.writeCharacteristic(command.characteristic)
            }
            is BleCommand.WriteNotif -> {
                gattHolder.setCharacteristicNotification(command.characteristic, true)
                val descriptor = command.characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                gattHolder.writeDescriptor(descriptor)
            }
        }


    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun onOperationFinishedCheck(
        updateBleState: () -> Unit
    ) {
        isProcessing = false
        if (commandTask.isEmpty()) {
            updateBleState()
        }
        next()
    }

}