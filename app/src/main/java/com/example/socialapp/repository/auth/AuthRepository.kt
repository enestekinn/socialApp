package com.example.socialapp.repository.auth

import com.example.socialapp.other.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepository {

    suspend fun register(email: String, username: String, password: String): Resource<AuthResult>
    suspend fun login(email: String, password: String): Resource<AuthResult>
}