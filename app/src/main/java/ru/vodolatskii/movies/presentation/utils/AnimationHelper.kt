package ru.vodolatskii.movies.presentation.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import ru.vodolatskii.movies.R
import java.util.concurrent.Executors
import kotlin.math.hypot
import kotlin.math.roundToInt

object AnimationHelper {
    private const val menuItems = 4

    fun performFragmentCircularRevealAnimation(rootView: View, activity: Activity, position: Int) {
        Executors.newSingleThreadExecutor().execute {
            while (true) {
                if (rootView.isAttachedToWindow) {
                    activity.runOnUiThread {
                        val itemCenter = rootView.width / (menuItems * 2)
                        val step = (itemCenter * 2) * (position - 1) + itemCenter

                        val x: Int = step
                        val y: Int = rootView.y.roundToInt() + rootView.height

                        val startRadius = 0
                        val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble())

                        val circularAnim = ViewAnimationUtils.createCircularReveal(
                            rootView,
                            x,
                            y,
                            startRadius.toFloat(),
                            endRadius.toFloat()
                        ).apply {
                            duration = 300
                            interpolator = AccelerateDecelerateInterpolator()

                            doOnEnd {
                                rootView.setBackgroundResource(R.color.black)
                            }
                        }

                        val alfaAnim = ObjectAnimator.ofFloat(rootView, View.ALPHA, 1f, 0f).apply {
                            duration = 400
                            interpolator = AccelerateDecelerateInterpolator()
                            doOnEnd {
                                rootView.alpha = 1f
                            }
                        }

                        AnimatorSet().apply {
                            playTogether(circularAnim, alfaAnim)
                        }.start()

                        rootView.visibility = View.VISIBLE
                    }
                    return@execute
                }
            }
        }
    }
}