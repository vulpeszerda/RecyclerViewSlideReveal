package com.vulpeszerda.recyclerviewanimation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Created by vulpes on 2017. 6. 27..
 */
class SlideRevealHelper(
        private val viewHolderAnimationDuration: Long = 200L,
        private val viewHolderAnimationOffset: Long = 30L) {

    private var recyclerView: RecyclerView? = null
    private var prevGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var animator: Animator? = null

    private var appliedVisibility = false
    var visible: Boolean = false
        set(value) {
            val prev = field
            field = value
            if (prev != value) {
                onVisibilityChanged(value)
            }
        }

    var canScroll: Boolean = true
        private set

    fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        (viewHolder as? ViewHolder)?.fraction = if (appliedVisibility) 1f else 0f
    }

    fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        this.recyclerView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = null
    }

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
        (holder as? ViewHolder)?.onAttachedToRecyclerView()
    }

    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
        (holder as? ViewHolder)?.onDetachedToRecyclerView()
    }

    private fun onVisibilityChanged(visible: Boolean) {
        val recyclerView = this.recyclerView ?: return
        val viewTreeObserver = recyclerView.viewTreeObserver
        if (!viewTreeObserver.isAlive) {
            return
        }
        prevGlobalLayoutListener?.let {
            viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        if (recyclerView.visibility == View.VISIBLE) {
            playViewHolderAnimation(recyclerView, visible)
        } else {
            prevGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    playViewHolderAnimation(recyclerView, visible)
                    val observer = recyclerView.viewTreeObserver
                    if (observer.isAlive) {
                        observer.removeOnGlobalLayoutListener(this)
                    }
                    prevGlobalLayoutListener = null
                }
            }
            viewTreeObserver.addOnGlobalLayoutListener(prevGlobalLayoutListener)
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun playViewHolderAnimation(recyclerView: RecyclerView, visible: Boolean) {
        animator?.cancel()
        animator = createViewHolderAnimator(recyclerView, visible)
                ?.apply {
                    addListener(object : Animator.AnimatorListener {

                        private var cancelled: Boolean = false

                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            if (!cancelled) {
                                appliedVisibility = visible
                                if (!visible) {
                                    recyclerView.visibility = View.GONE
                                }
                            }
                            canScroll = true
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                            cancelled = true
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            canScroll = false
                        }
                    })
                    start()
                }
    }

    private fun createViewHolderAnimator(recyclerView: RecyclerView, visible: Boolean): Animator? {
        val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return null
        val first = manager.findFirstVisibleItemPosition()
        val last = manager.findLastVisibleItemPosition()
        val animations: List<Animator> = (first..last)
                .map { index ->
                    (recyclerView.findViewHolderForAdapterPosition(index) as? ViewHolder)
                            ?.createAnimation(
                                    visible,
                                    viewHolderAnimationOffset * (index - first),
                                    viewHolderAnimationDuration)
                }
                .filterNotNull()

        return AnimatorSet().apply { playTogether(animations) }
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var fraction: Float = 1f
            set(value) {
                field = value
                applyAnimationFraction(value)
            }

        private val animatorListener = object :
                Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

            override fun onAnimationUpdate(animation: ValueAnimator?) {
                animation?.let { fraction = it.animatedValue as Float }
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                itemView.setHasTransientState(false)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                itemView.setHasTransientState(true)
            }
        }

        private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            applyAnimationFraction(fraction)
        }

        fun onAttachedToRecyclerView() {
            with(itemView.viewTreeObserver) {
                if (isAlive) {
                    addOnGlobalLayoutListener(layoutListener)
                }
            }
        }

        fun onDetachedToRecyclerView() {
            with(itemView.viewTreeObserver) {
                if (isAlive) {
                    removeOnGlobalLayoutListener(layoutListener)
                }
            }
        }

        fun createAnimation(visible: Boolean, startOffset: Long, maxDuration: Long): Animator {
            val toValue = if (visible) 1f else 0f
            val fromValue = fraction
            return ValueAnimator.ofFloat(fromValue, toValue)
                    .apply {
                        duration = (maxDuration * Math.abs(fromValue - toValue)).toLong()
                        startDelay = startOffset
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener(this@ViewHolder.animatorListener)
                        addListener(this@ViewHolder.animatorListener)
                    }
        }

        private fun applyAnimationFraction(fraction: Float) {
            val targetWidth = itemView.width.toFloat() / 2
            itemView.translationX = targetWidth - targetWidth * fraction
            itemView.alpha = fraction
        }
    }
}