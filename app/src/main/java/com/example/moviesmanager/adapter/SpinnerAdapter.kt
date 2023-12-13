package com.example.moviesmanager.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerAdapter(context: Context, private val options: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = options[position]
        textView.textSize = 24f
        textView.setTextColor(ColorStateList.valueOf(Color.WHITE))
        return view
    }
}