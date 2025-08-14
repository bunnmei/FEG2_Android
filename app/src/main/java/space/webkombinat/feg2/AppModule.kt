package space.webkombinat.feg2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import space.webkombinat.feg2.Model.BLEController
import space.webkombinat.feg2.Model.ChartState
import space.webkombinat.feg2.Model.DB.Chart.ChartDao
import space.webkombinat.feg2.Model.DB.Chart.ChartRepository
import space.webkombinat.feg2.Model.DB.Profile.ProfileDao
import space.webkombinat.feg2.Model.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Model.DB.ProfileDatabase
import space.webkombinat.feg2.Model.Notif
import space.webkombinat.feg2.Model.StopWatch
import space.webkombinat.feg2.Model.UserPreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBleController(
        @ApplicationContext context: Context,
        userPreferencesRepository: UserPreferencesRepository
    ): BLEController = BLEController(context = context, userPreferences = userPreferencesRepository)

    @Singleton
    @Provides
    fun provideChartState(
        profileRepository: ProfileRepository,
        chartRepository: ChartRepository
    ): ChartState = ChartState(
        profileRepository = profileRepository,
        chartRepository = chartRepository
    )

    @Singleton
    @Provides
    fun provideStopWatch(
        chartState: ChartState,
        bleController: BLEController,
        notif: Notif,
        @ApplicationContext context: Context,
    ): StopWatch = StopWatch(
        chartState = chartState,
        bleController = bleController,
        notif = notif,
        context = context
    )

    @Singleton
    @Provides
    fun provideNotif(
        @ApplicationContext context: Context,
    ) : Notif {
        return Notif(context)
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = {
            context.preferencesDataStoreFile("settings")
        }
    )

}


//@Module
//@InstallIn(ServiceComponent::class)
//object ServiceModule {
//
//    @ServiceScoped
//    @Provides
//
//}


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProfileDatabase {
        return Room.databaseBuilder(
            context,
            ProfileDatabase::class.java,
            "profile.db",
        ).build()
    }

    @Provides
    @Singleton
    fun provideProfile(db: ProfileDatabase): ProfileDao {
        return db.profileDao()
    }

    @Provides
    @Singleton
    fun provideChart(db: ProfileDatabase): ChartDao {
        return db.chartDao()
    }

    @Provides
    @Singleton
    fun provideRepoP(db: ProfileDatabase): ProfileRepository {
        return ProfileRepository(db.profileDao())
    }

    @Singleton
    @Provides
    fun provideRepoC(db: ProfileDatabase): ChartRepository {
        return ChartRepository(db.chartDao())
    }
}