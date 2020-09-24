package com.example.graduatedwork.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduatedwork.R
import com.example.graduatedwork.adapter.UsersViewHolder
import com.example.graduatedwork.model.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_friends_list.*

/**
 * A simple [Fragment] subclass.
 */
class FriendsListFragment : Fragment() {

    private val TAG = "FriendsListFragment"

    private var mAdapter: FirestoreRecyclerAdapter<UserModel, UsersViewHolder>? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var usersList = mutableListOf<UserModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(context?.applicationContext)
        users_recycler_view.layoutManager = mLayoutManager
        users_recycler_view.itemAnimator = DefaultItemAnimator()

        loadUsersList()

        firestoreListener = firestoreDB!!.collection("users")
            .orderBy("userName", Query.Direction.ASCENDING)
            .addSnapshotListener { documentSnapshots, e ->
                if (e != null) {
                    Log.d(TAG, "ListenerFireld", e)
                }
                usersList = mutableListOf()

                for (document in documentSnapshots!!) {
                    Log.d("Users", "${document.id}=>${document.data}")
                }



                for (document in documentSnapshots!!) {
                    val users = document.toObject(UserModel::class.java)
                    Log.d("Users","doc id ${document.toObject(UserModel::class.java)}")
                    usersList.add(users)
                    Log.d(TAG, "usersList: ${users.userName}")
                }

                mAdapter!!.notifyDataSetChanged()
                users_recycler_view.adapter = mAdapter
            }

    }


    private fun loadUsersList() {


        val query = firestoreDB!!.collection("users")
            .orderBy("userName", Query.Direction.ASCENDING)

        val response = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .build()

        mAdapter = object : FirestoreRecyclerAdapter<UserModel, UsersViewHolder>(response) {


            override fun onBindViewHolder(
                holder: UsersViewHolder,
                position: Int,
                model: UserModel
            ) {
                val user = usersList[position]
                holder.usersNameHolder.text = user.userName
                holder.usersEmailHolder.text = user.userEmail
            }


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.users_layout, parent, false)

                return UsersViewHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                super.onError(e)
                Log.d(TAG, e!!.message)
            }


        }


        mAdapter!!.notifyDataSetChanged()
        users_recycler_view.adapter = mAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener!!.remove()
    }


    override fun onStart() {
        super.onStart()
        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }


}
