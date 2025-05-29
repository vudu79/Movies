package ru.vodolatskii.movies.presentation.utils

import android.view.View
import android.view.animation.Animation

fun View.startAnimation(animation: Animation, onStart: () -> Unit, onEnd: () -> Unit) {
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart()
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd()
        }

        override fun onAnimationRepeat(animation: Animation?) = Unit
    })
    this.startAnimation(animation)
}