package space.webkombinat.feg2.Model.DB.Profile

import kotlinx.coroutines.flow.Flow
import space.webkombinat.feg2.Model.DB.ProfileLinkChart

class ProfileRepository(
    private val profileDao: ProfileDao
) {
    suspend fun insertProfile(profileEntity: ProfileEntity): Long {
        return profileDao.create(profileEntity)
    }

    suspend fun readProfile(id: Long): ProfileEntity? {
        return profileDao.read(id)
    }

    suspend fun updateProfile(profileEntity: ProfileEntity) {
        profileDao.update(profileEntity)
    }

    suspend fun deleteProfile(profileEntity: ProfileEntity) {
        profileDao.delete(profileEntity)
    }

    fun readAllProfiles(): Flow<List<ProfileEntity>> {
        return profileDao.getProfileAll()
    }

    fun readProfileLinkChart(id: Long): ProfileLinkChart {
        return profileDao.profileAndChart(id)
    }

}