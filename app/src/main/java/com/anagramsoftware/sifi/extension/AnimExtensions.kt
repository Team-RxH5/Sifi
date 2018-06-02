package com.anagramsoftware.sifi.extension

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View

fun View.fadeIn(duration: Long = 260) {
    val animator = ValueAnimator.ofFloat(this@fadeIn.alpha, 1.0f)
    animator.duration = duration
    animator.addUpdateListener {
        this.alpha = it.animatedValue as Float
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {}

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {
            this@fadeIn.visibility = View.VISIBLE
        }

    })
    animator.start()
}

fun View.fadeOut(duration: Long = 260) {
    val animator = ValueAnimator.ofFloat(this.alpha, 0.0f)
    animator.duration = duration
    animator.addUpdateListener {
        this.alpha = it.animatedValue as Float
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            this@fadeOut.visibility = View.INVISIBLE
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {}

    })
    animator.start()
}