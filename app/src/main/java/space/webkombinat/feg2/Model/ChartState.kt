package space.webkombinat.feg2.Model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Model.Constants.encodeTemp
import space.webkombinat.feg2.Model.Constants.packTemp
import space.webkombinat.feg2.Model.DB.Chart.ChartEntity
import space.webkombinat.feg2.Model.DB.Chart.ChartRepository
import space.webkombinat.feg2.Model.DB.Profile.ProfileEntity
import space.webkombinat.feg2.Model.DB.Profile.ProfileRepository
import kotlin.math.roundToInt

class ChartState @Inject constructor(
    val profileRepository: ProfileRepository,
    val chartRepository: ChartRepository
) {
    var temp_f_chart = mutableStateListOf<Float>()
    var temp_s_chart = mutableStateListOf<Float>()
    val charData = mutableStateOf<CharData>(CharData.NONE)

//    val keepState = mutableStateOf<Boolean>(true)

    val scope = CoroutineScope(Dispatchers.IO)

    fun addTemp(temp_f: Float, temp_s: Float){
//        println("addTemp: ${(temp_f  * 10).roundToInt() / 10.0f}, ${(temp_s * 10).roundToInt() / 10.0f}")
        temp_f_chart.add((temp_f  * 10).roundToInt() / 10.0f)
        temp_s_chart.add((temp_s * 10).roundToInt() / 10.0f)
    }

    fun clearTemp(){
        temp_f_chart.clear()
        temp_s_chart.clear()
    }

    fun keepChartData() {
        if (temp_f_chart.isEmpty() && charData.value != CharData.UNSAVED) return
        charData.value = CharData.SAVING
        val newProfile = ProfileEntity(
            id = 0,
            name = null,
            description = null,
            createAt = System.currentTimeMillis()
        )
        scope.launch {
            try {
                val profId = profileRepository.insertProfile(newProfile)
                temp_f_chart.forEachIndexed { index, value ->
                    val temp_f_encoded = encodeTemp(value)
                    val temp_s_encoded = encodeTemp(temp_s_chart[index])
                    val newChart = ChartEntity(
                        id = 0,
                        profileId = profId,
                        pointIndex = index,
                        temp = packTemp(temp_f_encoded, temp_s_encoded)
                    )
                    chartRepository.insertChart(newChart)
                }
                charData.value = CharData.SAVED
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    enum class CharData {
        NONE,
        UNSAVED,
        SAVING,
        SAVED
    }
}