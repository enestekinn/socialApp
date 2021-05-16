package com.example.socialapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.socialapp.R
import com.example.socialapp.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    val TAG = "Error"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        Log.d(TAG, "onCreate: AuthActivity worked")

        if (FirebaseAuth.getInstance().currentUser != null) {
            Intent(this,MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}