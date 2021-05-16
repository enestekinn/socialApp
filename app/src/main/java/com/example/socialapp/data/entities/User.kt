package com.example.socialapp.data.entities

import com.example.socialapp.other.DEFAULT_PROFILE_PICTURE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude

data class User(
    val uid: String = "",
    val username: String = "",
    val profilePictureUrl: String = DEFAULT_PROFILE_PICTURE_URL,
    val description: String = "",
    var follows: List<String> = listOf(FirebaseAuth.getInstance().uid!!),
    @Exclude
    var isFollowing: Boolean = false
)
