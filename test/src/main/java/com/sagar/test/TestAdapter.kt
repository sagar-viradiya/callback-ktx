package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagar.test.R

class TestAdapter : RecyclerView.Adapter<TestViewHolder>() {

    val list = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false);
        return TestViewHolder(view);
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.loadData()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}