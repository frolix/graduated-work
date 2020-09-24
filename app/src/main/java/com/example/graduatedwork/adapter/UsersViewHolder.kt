package com.example.graduatedwork.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedwork.R
import de.hdodenhof.circleimageview.CircleImageView

class UsersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var usersPictureHolder: CircleImageView = view.findViewById(R.id.imageView_profile_picture)
    var usersNameHolder: TextView = view.findViewById(R.id.textView_name)
    var usersEmailHolder: TextView = view.findViewById(R.id.textView_email)


}