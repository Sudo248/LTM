package com.sudo248.ltm.utils

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup

/**
 * ## Created by
 * @author **Sudo248**
 * @since 00:00 - 15/08/2022
 */

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

val View.isGone: Boolean
    get() = visibility == View.GONE

val View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE

fun View.setVisible(isVisible: Boolean) {
    if (isVisible) visible() else gone()
}

fun View.setGone(isGone: Boolean) {
    if (isGone) gone() else visible()
}

fun View.height(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

fun View.width(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    params.width = width
    layoutParams = params
    return this
}

fun View.animateWidth(
    toValue: Int,
    duration: Long = 200,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(width, toValue).apply {
            addUpdateListener {
                width(it.animatedValue as Int)
                action?.invoke(it.animatedFraction)
            }
            listener?.let {
                addListener(it)
            }
            setDuration(duration)
            start()
        }
    }
    return animator
}

fun View.animateHeight(
    toValue: Int,
    duration: Long = 200,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(width, toValue).apply {
            addUpdateListener {
                height(it.animatedValue as Int)
                action?.invoke(it.animatedFraction)
            }
            listener?.let {
                addListener(it)
            }
            setDuration(duration)
            start()
        }
    }
    return animator
}

fun View.animateWidthAndHeight(
    toWidth: Int,
    toHeight: Int,
    duration: Long = 200,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        animator = ValueAnimator.ofInt(width, toWidth).apply {
            addUpdateListener {
                widthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, toHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

