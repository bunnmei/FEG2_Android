package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Model.DB.Profile.ProfileEntity
import space.webkombinat.feg2.Model.DB.Profile.ProfileRepository
import space.webkombinat.feg2.ViewModel.LogDetailVM.UiState

@HiltViewModel
class LogEditVM @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    val profileId: Long = savedStateHandle["profileId"] ?: -1L
    val _profileLinkChart = MutableStateFlow<ProfileEntity>(ProfileEntity(
        id = -1L, createAt = -1L,
        name = null,
        description = null
    ))
    val profileLinkChart = _profileLinkChart.asStateFlow()

    init {
        viewModelScope.launch {
            if (profileId != -1L) {
                _profileLinkChart.value = profileRepository.readProfile(profileId)!!
            }
        }
    }

    fun updateName(name: String) {
        _profileLinkChart.value = _profileLinkChart.value.copy(name = name)
        viewModelScope.launch {
            profileRepository.updateProfile(_profileLinkChart.value)
        }
    }

    fun updateDescription(description: String) {
        _profileLinkChart.value = _profileLinkChart.value.copy(description = description)
        viewModelScope.launch {
            profileRepository.updateProfile(_profileLinkChart.value)
        }
    }

}