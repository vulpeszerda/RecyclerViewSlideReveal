package com.vulpeszerda.recyclerviewanimation

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.view.ViewCompat
import android.support.v4.widget.FakeDrawerLayout
import android.support.v4.widget.FakeViewDragHelper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_right.*
import kotlin.coroutines.experimental.EmptyCoroutineContext.plus

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: Adapter
    private lateinit var slideRevealHelper: SlideRevealHelper
    private lateinit var drawerLayout: FakeDrawerLayout
    private lateinit var rightDrawer: View

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

        val inflater = LayoutInflater.from(this)
        rightDrawer = inflater.inflate(R.layout.drawer_right, null)
        val drawerParams = FakeDrawerLayout.LayoutParams(
                fullWidth,
                ViewGroup.LayoutParams.MATCH_PARENT)
        drawerParams.gravity = Gravity.END

        drawerLayout = FakeDrawerLayout(this, DrawerDelegate(rightDrawer, fullWidth))
        drawer_holder.addView(drawerLayout, 0, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))

        drawerLayout.addView(inflater.inflate(R.layout.content, drawerLayout, false),
                FakeDrawerLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT))

        drawerLayout.addView(rightDrawer, drawerParams)

        drawerLayout.setScrimColor(Color.TRANSPARENT)
        slideRevealHelper.setupWithDrawer(drawerLayout, list)

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
        val isOpen = drawerLayout.isDrawerOpen(rightDrawer)
        Log.d("TEST11", "is open : $isOpen")
        if (isOpen) {
            drawerLayout.closeDrawer(rightDrawer)
        } else {
            drawerLayout.openDrawer(rightDrawer)
        }
    }

    private inner class DrawerDelegate(
            private val targetView: View,
            private val width: Int) : FakeViewDragHelper.ViewDelegate() {

        private var fakeLeft: Int = 0

        override fun getViewLeft(view: View): Int {
            if (view != targetView) {
                return super.getViewLeft(view)
            }
            val value = fakeLeft + width
            return value
        }

        override fun setViewOffsetLeftAndRight(view: View, offset: Int) {
            if (view != targetView) {
                return super.setViewOffsetLeftAndRight(view, offset)
            }
            fakeLeft += offset
            if (fakeLeft < 0) {
                if (targetView.left != 0) {
                    ViewCompat.offsetLeftAndRight(targetView, -targetView.left)
                }
                targetView.visibility = View.VISIBLE
            } else {
                targetView.visibility = View.GONE
            }
        }
    }
}
