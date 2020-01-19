package com.example.android.trackmysleepquality.sleeptracker

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


// Create a ViewHolder class extending the RecyclerView.ViewHolder.
// Here we wrap a text view in a text item view holder (look at constructor)
// This is required since recycler view works on view holder and not the text view directly
// A view holder describes an item view and has meta data about its position in the recycle view
class TextItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
}