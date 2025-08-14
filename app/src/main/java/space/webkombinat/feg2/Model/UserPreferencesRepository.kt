package space.webkombinat.feg2.Model

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_PROFILE_ID = longPreferencesKey("is_profile_id")
        val IS_THEME = intPreferencesKey("is_theme")
        val IS_TOP_RANGE = intPreferencesKey("is_top_range")
        val IS_BOTTOM_RANGE = intPreferencesKey("is_bottom_range")
        val BLE_DEVICE_ADDRESS = stringPreferencesKey("ble_device_address")

        val IS_BLE_DEVICE_BRIGHTNESS = intPreferencesKey("is_ble_device_brightness")
        val IS_TEMP_F_CARIB = intPreferencesKey("is_temp_f_carib")
        val IS_TEMP_S_CARIB = intPreferencesKey("is_temp_s_carib")

    }

//    IS_PROFILE_ID
    suspend fun saveProfileId(id: Long) {
        dataStore.edit { preferences ->
            preferences[IS_PROFILE_ID] = id
        }
    }
    val isProfileId = dataStore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_PROFILE_ID] ?: -1
        }
    val isProfileIdNotFlow = runBlocking {
        dataStore.data.first()[IS_PROFILE_ID] ?: -1
    }

//    IS_THEME
    suspend fun saveTheme(id: Int){
        dataStore.edit { preferences ->
            preferences[IS_THEME] = id
        }
    }

    val isTheme = dataStore.data
        .catch {
            if(it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {preferences ->
            preferences[IS_THEME] ?: AppTheme.System.num
        }

    val isThemeNotFlow = runBlocking {
        dataStore.data.first()[IS_THEME] ?: AppTheme.System.num
    }
    // TOP RANGE
    suspend fun saveTopRange(temp: Int){
        dataStore.edit { preferences ->
            preferences[IS_TOP_RANGE] = temp
        }
    }

    val isTopRange = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_TOP_RANGE] ?: 230
        }

//    BOTTOM RANGE
    suspend fun saveBottomRange(temp: Int){
        dataStore.edit { preferences ->
            preferences[IS_BOTTOM_RANGE] = temp
        }
    }

    val isBottomRange = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_BOTTOM_RANGE] ?: 0
        }

    // BLE_DEVICE_ADDRESS
    suspend fun saveBLE_ADD(addr: String) {
        dataStore.edit { preferences ->
            preferences[BLE_DEVICE_ADDRESS] = addr
        }
    }

    val isBleAddress = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[BLE_DEVICE_ADDRESS] ?: ""
        }

    val bleAddress = runBlocking {
        dataStore.data.first()[BLE_DEVICE_ADDRESS] ?: ""
    }

//    Brightness
    suspend fun saveBrightness(brightness: Int) {
        dataStore.edit { preferences ->
            preferences[IS_BLE_DEVICE_BRIGHTNESS] = brightness
        }
    }

    val isBrightness= dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_BLE_DEVICE_BRIGHTNESS] ?: 3
        }


    //    Carib F
    suspend fun saveCaribF(carib: Int)  {
        dataStore.edit { preferences ->
            preferences[IS_TEMP_F_CARIB] = carib
        }
    }

    val isCaribF= dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_TEMP_F_CARIB] ?: 0
        }

    //    Carib S
    suspend fun saveCaribS(carib: Int)  {
        dataStore.edit { preferences ->
            preferences[IS_TEMP_S_CARIB] = carib
        }
    }

    val isCaribS = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_TEMP_S_CARIB] ?: 0
        }
}