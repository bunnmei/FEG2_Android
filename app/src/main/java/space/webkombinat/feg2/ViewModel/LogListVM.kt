package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Model.DB.Profile.ProfileEntity
import space.webkombinat.feg2.Model.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Model.UserPreferencesRepository

@HiltViewModel
class LogListVM @Inject constructor(
    val profileRepository: ProfileRepository,
    val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    val profiles: Flow<List<ProfileEntity>> = profileRepository.readAllProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val profileId = userPreferencesRepository.isProfileId

    fun clearProfileId() {
        viewModelScope.launch {
            userPreferencesRepository.saveProfileId(-1)
        }
    }
}