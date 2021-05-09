package com.sagar.test

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewTestActivity : Activity() {

    val demoRecyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recycler_view_test)
    }
}