package com.example.graduatedwork.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.graduatedwork.R
import com.example.graduatedwork.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

//    private lateinit var firestoreUtil: FirestoreUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser

        val name = user?.displayName
        username.text = name

        val email = user?.email
        user_email_profile.text = email

        val photoUrl = user!!.photoUrl

        Picasso.with(context).load(photoUrl).into(profile_image_fragment)




    }

}
