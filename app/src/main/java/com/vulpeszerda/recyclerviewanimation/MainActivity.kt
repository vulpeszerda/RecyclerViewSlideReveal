package com.vulpeszerda.recyclerviewanimation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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
        slideRevealHelper = SlideRevealHelper()
        adapter = Adapter(slideRevealHelper)
        list.layoutManager = object: LinearLayoutManager(this) {

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
        adapter.visible = !adapter.visible
    }
}
