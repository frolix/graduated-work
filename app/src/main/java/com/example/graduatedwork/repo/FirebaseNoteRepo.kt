package com.example.graduatedwork.repo

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FirebaseNoteRepo {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Auth
    fun getUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun getNoteList(): Task<QuerySnapshot> {

        Log.d("HomeFragment","    ad "+ firebaseFirestore.collection("Notes").get().toString())
        return firebaseFirestore.collection("Notes")
            .orderBy("title",Query.Direction.DESCENDING)
            .get()

    }
}