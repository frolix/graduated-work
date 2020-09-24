package com.example.graduatedwork.util

import android.util.Log
import com.example.graduatedwork.model.Notes
import com.example.graduatedwork.model.NotesModel
import com.example.graduatedwork.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.lang.NullPointerException

object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val notesChannnelsCollectionRef = firestoreInstance.collection("notesChannels")


    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().uid ?: throw NullPointerException("UID is null.")}"
        )


    fun initCurrentUserFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser =
                    UserModel(FirebaseAuth.getInstance().currentUser?.displayName ?: "", "", null)
                currentUserDocRef.set(newUser).addOnCompleteListener {
                    onComplete()
                }
            } else {
                onComplete()
            }
        }
    }


    fun updateCurrentUser(name: String = "", email: String = "", profilePicture: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (email.isNotBlank()) userFieldMap["email"] = email
        if (profilePicture != null)
            userFieldMap["profilePicture"] = profilePicture
        currentUserDocRef.update(userFieldMap)
    }


    fun getNotesListener(): Task<QuerySnapshot> {
        val doc = FirebaseAuth.getInstance().currentUser!!.uid

        return notesChannnelsCollectionRef
            .document(doc)
            .collection("notes")
            .orderBy("time")
            .get()

    }

    fun getUsers() {
        firestoreInstance.collection("users")
            .orderBy("userName", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { it ->
                for (document in it) {
                    Log.d("Users", "${document.id}=>${document.data}")
                }
            }
    }


    fun deleteNote(idDocument: String) {
        val doc = FirebaseAuth.getInstance().currentUser!!.uid
        notesChannnelsCollectionRef
            .document(doc)
            .collection("notes")
            .document(idDocument)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    "HomeFragment",
                    "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w("HomeFragment", "Error deleting document", e) }

    }


    fun getCurrentUser(onComplete: (UserModel) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(UserModel::class.java)!!)
            }
    }


    fun sendNotes(notes: Notes, userId: String): Task<DocumentReference> {
        return notesChannnelsCollectionRef.document(userId)
            .collection("notes")
            .add(notes)

    }

    fun updateImportant( doc: String, important: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        notesChannnelsCollectionRef
            .document(userId)
            .collection("notes")
            .document(doc)
            .update("important", important)
            .addOnCompleteListener { Log.d("HomeFragment", "onComplete important $doc") }
            .addOnSuccessListener { Log.d("HomeFragment", "onSuccess important") }
            .addOnFailureListener { exception ->
                Log.d("HomeFragment", "onFailure important", exception)
            }


    }


    //
    fun updateNotesDocId(userId: String): Task<QuerySnapshot> {

        val idList = ArrayList<String>()

        return notesChannnelsCollectionRef
            .document(userId)
            .collection("notes")
            .get()
            .addOnSuccessListener { it ->
                it.documents.forEach {
                    val id = it.id

                    notesChannnelsCollectionRef.document(userId)
                        .collection("notes")
                        .document(id)
                        .update("idDoc", id)
                        .addOnCompleteListener { Log.d("HomeFragment", "onComplete") }
                        .addOnSuccessListener { Log.d("HomeFragment", "onSuccess") }
                        .addOnFailureListener { exception ->
                            Log.d("HomeFragment", "onFailure ", exception)
                        }

                    Log.d("HomeFragment", "set id = $id")
                    idList.add(id)
                }
            }

    }

}





















