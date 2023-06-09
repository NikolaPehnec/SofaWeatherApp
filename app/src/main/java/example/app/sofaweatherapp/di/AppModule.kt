package example.app.sofaweatherapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import example.app.sofaweatherapp.api.WeatherServiceApi
import example.app.sofaweatherapp.dao.WeatherDao
import example.app.sofaweatherapp.db.WeatherDb
import example.app.sofaweatherapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideApiService(): WeatherServiceApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient).build()
            .create(WeatherServiceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): WeatherDb {
        return Room.databaseBuilder(
            appContext,
            WeatherDb::class.java,
            "WeatherDB"
        ).fallbackToDestructiveMigration()
            .setQueryCallback(
                object : RoomDatabase.QueryCallback {
                    override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
                        println("SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }
                },
                Executors.newSingleThreadExecutor()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDAO(weatherDb: WeatherDb): WeatherDao =
        weatherDb.weatherDao()
}
