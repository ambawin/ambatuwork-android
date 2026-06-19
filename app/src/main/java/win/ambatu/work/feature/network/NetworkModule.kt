package win.ambatu.work.feature.network

import win.ambatu.work.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import win.ambatu.work.data.storage.SessionManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        sessionManager: SessionManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request()
                val requestBuilder = request.newBuilder()
                    .header("Accept", "application/json")
                
                if (request.header("Authorization") == null) {
                    sessionManager.getToken()?.let { token ->
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticationApi(retrofit: Retrofit): win.ambatu.work.generated.api.AuthenticationApi {
        return retrofit.create(win.ambatu.work.generated.api.AuthenticationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBacklogItemsApi(retrofit: Retrofit): win.ambatu.work.generated.api.BacklogItemsApi {
        return retrofit.create(win.ambatu.work.generated.api.BacklogItemsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDailyCheckInsApi(retrofit: Retrofit): win.ambatu.work.generated.api.DailyCheckInsApi {
        return retrofit.create(win.ambatu.work.generated.api.DailyCheckInsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDefinitionOfDoneApi(retrofit: Retrofit): win.ambatu.work.generated.api.DefinitionOfDoneApi {
        return retrofit.create(win.ambatu.work.generated.api.DefinitionOfDoneApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImpedimentsApi(retrofit: Retrofit): win.ambatu.work.generated.api.ImpedimentsApi {
        return retrofit.create(win.ambatu.work.generated.api.ImpedimentsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideInvitationsApi(retrofit: Retrofit): win.ambatu.work.generated.api.InvitationsApi {
        return retrofit.create(win.ambatu.work.generated.api.InvitationsApi::class.java)
    }

    @Provides
    @Singleton
    fun providePeerReviewsApi(retrofit: Retrofit): win.ambatu.work.generated.api.PeerReviewsApi {
        return retrofit.create(win.ambatu.work.generated.api.PeerReviewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProjectMembersApi(retrofit: Retrofit): win.ambatu.work.generated.api.ProjectMembersApi {
        return retrofit.create(win.ambatu.work.generated.api.ProjectMembersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProjectsApi(retrofit: Retrofit): win.ambatu.work.generated.api.ProjectsApi {
        return retrofit.create(win.ambatu.work.generated.api.ProjectsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrospectiveApi(retrofit: Retrofit): win.ambatu.work.generated.api.RetrospectiveApi {
        return retrofit.create(win.ambatu.work.generated.api.RetrospectiveApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSprintBoardApi(retrofit: Retrofit): win.ambatu.work.generated.api.SprintBoardApi {
        return retrofit.create(win.ambatu.work.generated.api.SprintBoardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSprintReviewApi(retrofit: Retrofit): win.ambatu.work.generated.api.SprintReviewApi {
        return retrofit.create(win.ambatu.work.generated.api.SprintReviewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSprintsApi(retrofit: Retrofit): win.ambatu.work.generated.api.SprintsApi {
        return retrofit.create(win.ambatu.work.generated.api.SprintsApi::class.java)
    }
}