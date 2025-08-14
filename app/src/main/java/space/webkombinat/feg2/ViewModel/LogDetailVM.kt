package space.webkombinat.feg2.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Model.Constants.DeconvertIntToFloat2
import space.webkombinat.feg2.Model.DB.Chart.ChartRepository
import space.webkombinat.feg2.Model.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Model.DB.ProfileLinkChart
import space.webkombinat.feg2.Model.UserPreferencesRepository

@HiltViewModel
class LogDetailVM @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val chartRepository: ChartRepository,
    private val savedStateHandle: SavedStateHandle,
    val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {
    val profileId: Long = savedStateHandle["profileId"] ?: -1L
    val _profileLinkChart = MutableStateFlow<UiState>(UiState(profile = null))
    val profileLinkChart = _profileLinkChart.asStateFlow()
    val _deleteProfileState = MutableStateFlow(DeleteProfileState.None)
    val deleteProfileState = _deleteProfileState.asStateFlow()

    val userSettings = combine(
        userPreferencesRepository.isTopRange,
        userPreferencesRepository.isBottomRange,
    ) {topRange, bottomRange ->
        ChartVM.RangeSettings(
            topRange = topRange,
            bottomRange = bottomRange,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChartVM.RangeSettings(
            topRange = 230,
            bottomRange = 0,
        )
    )

    init {
        println("init LogDetailVM $profileId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val profile = profileRepository.readProfileLinkChart(profileId)
//                println("init LogDetailVM size ${profile.chart.size}\n")
                val temp_f = mutableStateListOf<Float>()
                val temp_s = mutableStateListOf<Float>()
                profile.chart.sortedBy { it.pointIndex }.forEach { chart ->
                    val temp = DeconvertIntToFloat2(temp = chart.temp)
                    temp_f.add(temp.first)
                    temp_s.add(temp.second)
                }
                _profileLinkChart.value = UiState(
                    profile = profile,
                    temp_f = temp_f,
                    temp_s = temp_s,
                )
            } catch (e: Exception){
                println(e)
            }
        }
    }

    fun bookmarkProfileId() {
        viewModelScope.launch {
            userPreferencesRepository.saveProfileId(profileId)
        }
    }

    fun deleteProfile(navCont: NavController) {
        _deleteProfileState.value = DeleteProfileState.Deleting
        viewModelScope.launch(Dispatchers.IO) {
            if (profileId == userPreferencesRepository.isProfileIdNotFlow) {
                userPreferencesRepository.saveProfileId(-1L)
            }
            val profile = profileRepository.readProfileLinkChart(profileId)
            profile.chart.forEach { chart ->
                chartRepository.deleteChart(chart)
            }
            profileRepository.deleteProfile(profile.profile)
            _deleteProfileState.value = DeleteProfileState.Deleted
        }

    }

    data class UiState(
        val profile: ProfileLinkChart? = null,
        val temp_f: SnapshotStateList<Float> = mutableStateListOf<Float>(),
        val temp_s: SnapshotStateList<Float> = mutableStateListOf<Float>(),
    )

    enum class DeleteProfileState {
        None,
        Deleting,
        Deleted,
    }
}