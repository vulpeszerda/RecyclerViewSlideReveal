package com.vulpeszerda.recyclerviewanimation

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: Adapter
    private lateinit var slideRevealHelper: SlideRevealHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLayout()
        setupItems()
    }

    private fun initLayout() {
        slideRevealHelper = SlideRevealHelper(controlRecyclerViewVisibility = false)

        adapter = Adapter(slideRevealHelper)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val fullWidth = metrics.widthPixels
        val params = right_drawer.layoutParams as DrawerLayout.LayoutParams
        params.width = fullWidth
        right_drawer.layoutParams = params

        drawer_layout.apply {
            setScrimColor(Color.TRANSPARENT)
            addDrawerListener(drawerListener)
        }

        list.layoutManager = object : LinearLayoutManager(this) {

            override fun canScrollVertically(): Boolean {
                return super.canScrollVertically() && slideRevealHelper.canScroll
            }

        }
        list.adapter = adapter

        btn_do.setOnClickListener { doSomething() }
    }

    private fun setupItems() {
        val items = (0..1000).map { it.toString() }
        adapter.setItems(items)
    }

    private fun doSomething() {
        if (drawer_layout.isDrawerOpen(right_drawer)) {
            drawer_layout.closeDrawer(right_drawer)
        } else {
            drawer_layout.openDrawer(right_drawer)
        }
    }

    private val drawerListener = object : DrawerLayout.DrawerListener {

        private var prevState = DrawerLayout.STATE_IDLE
        private var prevOffset = 0.0f

        override fun onDrawerStateChanged(newState: Int) {
            prevState = newState
        }

        override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
            if (prevState != DrawerLayout.STATE_SETTLING) {
                if (slideOffset == 0.0f) {
                    unrevealIfNecessary()
                } else if (slideOffset == 1.0f) {
                    revealIfNecessary()
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    slideRevealHelper.updateReveal(
                            Math.min(Math.max(slideOffset / 1.5f, 0f), 1f), false)
                }
            } else if (slideOffset > prevOffset) {
                revealIfNecessary()
            } else if (slideOffset <= prevOffset) {
                unrevealIfNecessary()
            }
            prevOffset = slideOffset
        }

        override fun onDrawerOpened(drawerView: View?) {
            revealIfNecessary()
        }

        override fun onDrawerClosed(drawerView: View?) {
            unrevealIfNecessary()
        }

        private fun unrevealIfNecessary() {
            if (slideRevealHelper.let {
                (it.isAnimating && it.isAnimatingToReveal) ||
                        (it.appliedReveal != 0.0f && !it.isAnimating)
            }) {
                slideRevealHelper.updateReveal(0.0f, true)
            }
        }

        private fun revealIfNecessary() {
            if (slideRevealHelper.let {
                (it.isAnimating && !it.isAnimatingToReveal) ||
                        (it.appliedReveal != 1.0f && !it.isAnimating)
            }) {
                slideRevealHelper.updateReveal(1.0f, true)
            }
        }
    }
}
