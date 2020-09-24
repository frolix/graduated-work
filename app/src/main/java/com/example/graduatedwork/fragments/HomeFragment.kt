package com.example.graduatedwork.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.graduatedwork.R
import com.example.graduatedwork.adapter.NoteViewHolder
import com.example.graduatedwork.adapter.SwipeToDeleteCallback
import com.example.graduatedwork.model.NotesModel
import com.example.graduatedwork.model.UserModel
import com.example.graduatedwork.repo.FirebaseNoteRepo
import com.example.graduatedwork.util.FirestoreUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog_with_text.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.note_layout.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    private fun init() {

        FirestoreUtil.getCurrentUser {
            currentUser = it
        }

        val doc = FirebaseAuth.getInstance().currentUser!!.uid

        firestoreDB = FirebaseFirestore.getInstance()

        val mLayoutManager = LinearLayoutManager(context?.applicationContext)
        note_list_recycler_view.layoutManager = mLayoutManager
        note_list_recycler_view.itemAnimator = DefaultItemAnimator()

        if (firebaseRepository.getUser() == null) {
            Log.d(TAG, "Error log user")
        } else {
            loadNotesList()
        }



        firestoreListener = firestoreDB!!.collection("notesChannels")
            .document(doc)
            .collection("notes")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { documentSnapshots, e ->
                if (e != null) {
                    Log.d(TAG, "ListenerFireld", e)
                }
                notesList = mutableListOf()

                for (document in documentSnapshots!!) {
                    val note = document.toObject(NotesModel::class.java)
                    note.idDoc = document.id
                    notesList.add(note)
                    Log.d(TAG, "noteList: ${notesList.size}")
                }

                mAdapter!!.notifyDataSetChanged()
                note_list_recycler_view?.adapter = mAdapter
            }

        add_note_fab.setOnClickListener {
            noteCreateDialog()
        }
    }


    private fun noteCreateDialog() {

        val user = FirebaseAuth.getInstance().currentUser
        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        builder.setTitle("create notes")
        val dialogLayout = inflater.inflate(R.layout.dialog_with_text, null)
        builder.setView(dialogLayout)


        dialogLayout.send_button.setOnClickListener {
            val noteTitle = dialogLayout.title.text.toString()
            val noteText = dialogLayout.edit_text_dialog.text.toString()
            val userName = user!!.displayName


            val notesToSend = NotesModel(
                noteTitle,
                noteText,
                false,
                "",
                Calendar.getInstance().time,
                FirebaseAuth.getInstance().currentUser!!.uid,
                FirebaseAuth.getInstance().currentUser!!.uid,
                userName.toString()
            )
            FirestoreUtil.sendNotes(notesToSend, FirebaseAuth.getInstance().currentUser!!.uid)


            Toast.makeText(
                activity,
                "title: ${noteTitle}, your text: ${noteText} added",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }


    private fun loadNotesList() {


        val doc = FirebaseAuth.getInstance().currentUser!!.uid
        val query = firestoreDB!!.collection("notesChannels")
            .document(doc)
            .collection("notes")
            .orderBy("title", Query.Direction.DESCENDING)

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

        fun removeAt(position: Int) {

            val itemDelete: NotesModel = notesList[position]

            Log.d("HomeFragment", "delete id : ${itemDelete.idDoc}")
            FirestoreUtil.deleteNote(itemDelete.idDoc)
        }



        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeAt(viewHolder.adapterPosition)
            }
        }



        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(note_list_recycler_view)


        mAdapter!!.notifyDataSetChanged()
        note_list_recycler_view.adapter = mAdapter
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
        mAdapter!!.stopListening()
        Log.d(TAG,"onStop")
    }

}
