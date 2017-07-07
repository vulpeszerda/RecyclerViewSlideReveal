package com.vulpeszerda.recyclerviewanimation

import android.animation.Animator
import android.animation.ValueAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Created by vulpes on 2017. 6. 27..
 */
class SlideRevealHelper(private val maxAnimationDuration: Long = 500L,
                        private val singleViewHolderAnimationRatio: Float = 1f / 3) {

    private var recyclerView: RecyclerView? = null
    private var prevGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var animator: Animator? = null

    private var lastRequestedWithAnim = false
    private var lastRequestedReveal = 0.0f

    private var appliedReveal = 0.0f
        set(value) {
            val prev = field
            field = value
            if (prev != value) {
                onAppliedRevealChanged(value)
            }
        }


    var canScroll: Boolean = false
        get() = lastRequestedReveal == appliedReveal

    fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        (viewHolder as? ViewHolder)?.let {
            val manager = recyclerView!!.layoutManager as? LinearLayoutManager ?: return
            val first = manager.findFirstVisibleItemPosition()
            val last = manager.findLastVisibleItemPosition()
            applyRevealFractionToViewHolder(it, position, first, last)
        }
    }

    fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        updateReveal(lastRequestedReveal, lastRequestedWithAnim)
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

    fun updateReveal(reveal: Float, withAnim: Boolean) {
        lastRequestedReveal = reveal
        lastRequestedWithAnim = withAnim

        val recyclerView = this.recyclerView ?: return
        val viewTreeObserver = recyclerView.viewTreeObserver
        if (!viewTreeObserver.isAlive) {
            return
        }
        prevGlobalLayoutListener?.let {
            viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        if (recyclerView.visibility == View.VISIBLE) {
            updateRevealInternal(reveal, withAnim)
        } else if (reveal != appliedReveal || reveal > 0f) {
            prevGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    updateRevealInternal(reveal, withAnim)
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

    private fun updateRevealInternal(reveal: Float, withAnim: Boolean) {
        if (withAnim && reveal != appliedReveal) {
            animator?.cancel()

            val toValue = reveal
            val fromValue = appliedReveal

            animator = ValueAnimator.ofFloat(fromValue, toValue)
                    .apply {
                        duration = (maxAnimationDuration * Math.abs(fromValue - toValue)).toLong()
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener(animatorListener)
                        addListener(animatorListener)
                        start()
                    }
        } else {
            appliedReveal = reveal
        }
    }

    private fun onAppliedRevealChanged(reveal: Float) {
        recyclerView?.let {
            forEachVisibleViewHolder(it, this::applyRevealFractionToViewHolder)
        }
        if (reveal == 0f) {
            recyclerView?.visibility = View.GONE
        }
    }

    private fun applyRevealFractionToViewHolder(viewHolder: ViewHolder,
                                                index: Int,
                                                first: Int,
                                                last: Int) {
        val fixedIndex = Math.max(Math.min(last, index), first)
        val singleFraction = singleViewHolderAnimationRatio
        val childCount = last - first
        val cascadeFraction = (1f - singleFraction) / childCount
        val startFraction = (fixedIndex - first) * cascadeFraction
        viewHolder.fraction = Math.min(
                Math.max((appliedReveal - startFraction) / singleFraction, 0f), 1f)
    }

    private inline fun forEachVisibleViewHolder(
            recyclerView: RecyclerView,
            block: (ViewHolder, Int, Int, Int) -> Unit) {

        val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val first = manager.findFirstVisibleItemPosition()
        val last = manager.findLastVisibleItemPosition()
        return (first..last)
                .forEach { index ->
                    (recyclerView.findViewHolderForAdapterPosition(index) as? ViewHolder)
                            ?.let { block.invoke(it, index, first, last) }
                }
    }

    private val animatorListener = object :
            Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

        private var cancelled: Boolean = false

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            animation?.let { appliedReveal = it.animatedValue as Float }
        }

        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            recyclerView?.let {
                forEachVisibleViewHolder(it) { viewHolder, _, _, _ ->
                    viewHolder.itemView.setHasTransientState(false)
                }
            }
            animator = null
        }

        override fun onAnimationCancel(animation: Animator?) {
            cancelled = true
        }

        override fun onAnimationStart(animation: Animator?) {
            cancelled = false
            recyclerView?.let {
                forEachVisibleViewHolder(it) { viewHolder, _, _, _ ->
                    viewHolder.itemView.setHasTransientState(true)
                }
            }
        }
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var fraction: Float = 1f
            set(value) {
                field = value
                applyRevealFraction(value)
            }

        private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            applyRevealFraction(fraction)
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

        private fun applyRevealFraction(fraction: Float) {
            val targetWidth = itemView.width.toFloat()
            itemView.translationX = targetWidth - targetWidth * fraction
            itemView.alpha = fraction
        }
    }
}