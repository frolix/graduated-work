package com.example.graduatedwork.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.graduatedwork.R
import com.example.graduatedwork.adapter.NoteViewHolder
import com.example.graduatedwork.model.NotesModel
import com.example.graduatedwork.model.UserModel
import com.example.graduatedwork.repo.FirebaseNoteRepo
import com.example.graduatedwork.util.FirestoreUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_important.*

/**
 * A simple [Fragment] subclass.
 */
class ImportantFragment : Fragment() {

    private val TAG = "ImportantFragment"

    private lateinit var currentUser: UserModel
    private val firebaseRepository: FirebaseNoteRepo = FirebaseNoteRepo()

    //TEST RECYCLER VIEW
    private var mAdapter: FirestoreRecyclerAdapter<NotesModel, NoteViewHolder>? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var notesList = mutableListOf<NotesModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_important, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val doc = FirebaseAuth.getInstance().currentUser!!.uid

        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(context?.applicationContext)
        important_recycler_view.layoutManager = mLayoutManager
        important_recycler_view.itemAnimator = DefaultItemAnimator()

        if (firebaseRepository.getUser() == null) {
            Log.d(TAG, "Error log user")
        } else {
            loadNotesList()
        }



        firestoreListener = firestoreDB!!.collection("notesChannels")
            .document(doc)
            .collection("notes")
            .whereEqualTo("important",true)
//            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { documentSnapshots, e ->
                if (e != null) {
                    Log.d(TAG, "ListenerFireld", e)
                }
                notesList = mutableListOf()

                for (document in documentSnapshots!!) {
                    val note = document.toObject(NotesModel::class.java)
                    note.idDoc = document.id
//                    if (note.important==true){}
                    notesList.add(note)
                    Log.d(TAG, "noteList: ${notesList.size}")

                }

                mAdapter!!.notifyDataSetChanged()
                important_recycler_view.adapter = mAdapter
            }
    }

    private fun loadNotesList() {

        val doc = FirebaseAuth.getInstance().currentUser!!.uid
        val query = firestoreDB!!.collection("notesChannels")
            .document(doc)
            .collection("notes")
            .whereEqualTo("important",true)
//            .orderBy("time", Query.Direction.ASCENDING)

        val response = FirestoreRecyclerOptions.Builder<NotesModel>()
            .setQuery(query, NotesModel::class.java)
            .build()

        mAdapter = object : FirestoreRecyclerAdapter<NotesModel, NoteViewHolder>(response) {
            override fun onBindViewHolder(
                holder: NoteViewHolder,
                position: Int,
                model: NotesModel
            ) {
                val note = notesList[position]
                Log.d(TAG, "get title: ${note.title}")

                if (note.important) {
                    holder.importantButton.setImageResource(R.drawable.ic_important_red)
                } else {
                    holder.importantButton.setImageResource(R.drawable.ic_important_black)
                }


                holder.importantButton.setOnClickListener {
                    val itemImportant: NotesModel = notesList[position]
                    if (itemImportant.important) {
                        FirestoreUtil.updateImportant(itemImportant.idDoc, false)
                    } else {
                        FirestoreUtil.updateImportant(itemImportant.idDoc, true)
                    }
                }


                if (note.title == "") {
                    note.title = "default"
                }
                if (note.textNotes == "") {
                    note.textNotes = "default"
                }
                holder.titleHolder.text = note.title
                holder.textHolder.text = note.textNotes
                holder.timeHolder.text = note.time.toString()

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_layout, parent, false)
                return NoteViewHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                super.onError(e)
                Log.d(TAG, e!!.message)
            }
        }

        mAdapter!!.notifyDataSetChanged()
        important_recycler_view.adapter = mAdapter
    }


    override fun onDestroy() {
        super.onDestroy()
        firestoreListener!!.remove()
        Log.d(TAG,"onDestroy")

    }


    override fun onStart() {
        super.onStart()
        mAdapter!!.startListening()
        Log.d(TAG,"onStart")

    }

    override fun onStop() {
        super.onStop()
        firestoreListener!!.remove()
//        mAdapter!!.stopListening()
        Log.d(TAG,"onStop")
    }
}
