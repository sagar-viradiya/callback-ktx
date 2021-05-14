package com.example.myapplication

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sagar.test.R


class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    public fun loadData() {
        val title = itemView.findViewById<TextView>(R.id.dummy_text)
        title.text = "This is the " + bindingAdapterPosition + "th view"
    }
}