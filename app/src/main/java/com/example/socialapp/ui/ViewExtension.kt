package com.example.socialapp.ui

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.socialapp.R

fun View.slideUp(context: Context, animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)

}

fun slideUpViews(context: Context, vararg  views: View, animeTime: Long = 300L,delay : Long = 150L) {
    for (i in views.indices){
        views[i].slideUp(context,animeTime,delay * i )
    }
}