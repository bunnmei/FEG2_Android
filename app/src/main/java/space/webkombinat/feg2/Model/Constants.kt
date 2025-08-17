package space.webkombinat.feg2.Model

import java.util.UUID
import kotlin.math.round
import kotlin.math.roundToInt

object Constants {
    // Chart Settings
    const val CHART_MINUTE = 30
    const val ONE_MINUTE_WIDTH = 300f
    const val ONE_SECOND_WIDTH = 5f
    const val BUFFER_WIDTH = 100f
    const val CANVAS_WIDTH = CHART_MINUTE * ONE_MINUTE_WIDTH + BUFFER_WIDTH
    const val TEMP_MEMORY_WIDTH = 60

    // BLE Settings
    const val SERVICE_UUID = "811b864d-718c-b035-804d-92c4761243c0"
    const val CHARACTERISTIC_UUID_F_STRING = "0601a001-0eca-b5ab-edc2-887fd5f32b84"
    const val CHARACTERISTIC_UUID_S_STRING = "620321e5-17c6-ca12-8328-7db87b02fbe5"
    const val CHARACTERISTIC_UUID_BRIGHTNESS_STRING = "8a959d93-3d42-0c75-eaa3-3b37f5275fff"
    const val CHARACTERISTIC_UUID_F_CARIB_STRING = "324010a6-eede-268f-8c88-c5632308eb41"
    const val CHARACTERISTIC_UUID_S_CARIB_STRING = "04525bfa-46e2-3540-1174-afaba112e062"

    val CHARACTERISTIC_UUID_F_UUID: UUID = UUID.fromString(CHARACTERISTIC_UUID_F_STRING)
    val CHARACTERISTIC_UUID_S_UUID: UUID = UUID.fromString(CHARACTERISTIC_UUID_S_STRING)
    val CHARACTERISTIC_UUID_BRIGHTNESS_UUID: UUID = UUID.fromString(CHARACTERISTIC_UUID_BRIGHTNESS_STRING)
    val CHARACTERISTIC_UUID_F_CARIB_UUID: UUID = UUID.fromString(CHARACTERISTIC_UUID_F_CARIB_STRING)
    val CHARACTERISTIC_UUID_S_CARIB_UUID: UUID = UUID.fromString(CHARACTERISTIC_UUID_S_CARIB_STRING)
    //Notif
    fun NOTIF_CONTENT_TEXT(time: String, temp_f: String, temp_s: String) = "時間 $time 温度 $temp_f°C/$temp_s°C"
    
    fun encodeTemp(temp: Float): Int {
        val scaled = (temp * 10).toInt()
        return scaled + 2000 // -200.0℃のときを基準に合わせるため
    }

    fun decodeTemp(encoded: Int): Float {
        return (encoded - 2000) / 10.0f
    }

    fun packTemp(temp_f: Int, temp_s: Int): Int {
        return (temp_f.shl(14) or temp_s)
    }

    fun unpackTemp(packed: Int): Pair<Int, Int> {
        val temp_f = packed.shr(14)
        val temp_s = packed.and(0x3FFF)
        return Pair(temp_f, temp_s)
    }

    enum class CHARA_CARIB {
        TEMP_F,
        TEMP_S
    }
}
