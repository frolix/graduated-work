package com.example.graduatedwork.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedwork.R

class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var titleHolder: TextView = view.findViewById(R.id.note_title)
    var textHolder: TextView = view.findViewById(R.id.note_text)
    var timeHolder: TextView = view.findViewById(R.id.note_time)
    var importantButton: ImageButton = view.findViewById(R.id.button_important)
}