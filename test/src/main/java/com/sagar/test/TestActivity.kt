package com.sagar.test

import android.app.Activity
import android.os.Bundle
import android.view.View

class TestActivity : Activity() {
    val demoView: View by lazy { findViewById(R.id.demo_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }
}
