package com.example.socialapp.ui

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.socialapp.other.TAG
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackbar(text: String) {
  Snackbar.make(
      requireView(),
      text,
      Snackbar.LENGTH_LONG
  ).show()
    Log.d(TAG, "snackbar: $text")
}