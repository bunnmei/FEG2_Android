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

    //ET_tempが 253.4の場合 * 100000で25340000
    //BT_tempが 123.3の場合 * 10で1233
    //BT + ET で 25341233というInt型を保存する
    fun ConvertFloat2ToInt(ET_temp: Float, BT_temp: Float): Int {
        val et_temp = round(ET_temp * 10).toInt() * 10000
        val bt_temp = round(BT_temp * 10).toInt()
//        println("convert func bt-${bt_temp} et-${et_temp} calc-${et_temp+bt_temp}")
        return (et_temp + bt_temp)
    }

    fun DeconvertIntToFloat2(temp: Int): Pair<Float, Float> {
        val et_temp = ((temp / 10000) / 10.0f) //上三桁
        val bt_temp = ((temp % 10000) / 10.0f)  //下三桁
//        println("deconvert func bt-${bt_temp} et-${et_temp}")
        return Pair(et_temp, bt_temp)
    }

    enum class CHARA_CARIB {
        TEMP_F,
        TEMP_S
    }
}
