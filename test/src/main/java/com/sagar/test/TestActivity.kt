package com.sagar.test

import androidx.appcompat.app.AppCompatActivity
import android.view.View

class TestActivity : AppCompatActivity(R.layout.activity_test) {
    val demoView: View by lazy { findViewById(R.id.demo_view) }
}