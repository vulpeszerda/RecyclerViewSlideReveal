package com.vulpeszerda.recyclerviewanimation.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.ViewDelegateDrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import com.vulpeszerda.recyclerviewanimation.sample.R
import com.vulpeszerda.recyclerviewanimation.SlideRevealHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_right.*

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
        slideRevealHelper = SlideRevealHelper(
                controlRecyclerViewVisibility = false)

        adapter = Adapter(slideRevealHelper)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val fullWidth = metrics.widthPixels

        val drawerParams = ViewDelegateDrawerLayout.LayoutParams(
                fullWidth,
                ViewGroup.LayoutParams.MATCH_PARENT)
        drawerParams.gravity = Gravity.END
        right_drawer.layoutParams = drawerParams

        drawer_layout.setScrimColor(Color.TRANSPARENT)
        drawer_layout.setFakeTarget(right_drawer)

        slideRevealHelper.setupWithDrawer(drawer_layout, list)

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
        val isOpen = drawer_layout.isDrawerOpen(right_drawer)
        Log.d("TEST11", "is open : $isOpen")
        if (isOpen) {
            drawer_layout.closeDrawer(right_drawer)
        } else {
            drawer_layout.openDrawer(right_drawer)
        }
    }
}
