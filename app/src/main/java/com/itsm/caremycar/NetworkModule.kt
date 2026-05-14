package com.itsm.caremycar

import com.itsm.caremycar.api.ApiService
import com.itsm.caremycar.repository.TokenManager
import com.itsm.caremycar.session.UnauthorizedSessionNotifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenManager: TokenManager,
        unauthorizedSessionNotifier: UnauthorizedSessionNotifier
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .addInterceptor { chain ->
                val token = tokenManager.getToken()
                val hadToken = token != null
                val requestBuilder = chain.request().newBuilder()
                token?.let { requestBuilder.addHeader("Authorization", "Bearer $it") }
                val response = chain.proceed(requestBuilder.build())
                if (response.code == 401 &&
                    hadToken &&
                    !isPublicAuthPath(chain.request().url.encodedPath)
                ) {
                    tokenManager.clearToken()
                    unauthorizedSessionNotifier.notifySessionExpired()
                }
                response
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun isPublicAuthPath(encodedPath: String): Boolean {
        return encodedPath.contains("/api/auth/login") ||
            encodedPath.contains("/api/auth/register")
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
