package com.example.socialapp.di.auth

import com.example.socialapp.repository.auth.AuthRepository
import com.example.socialapp.repository.auth.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object AuthModule {

    @ActivityScoped
    @Provides
    fun providesAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

}