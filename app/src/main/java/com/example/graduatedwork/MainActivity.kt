package com.example.graduatedwork

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
//import com.example.familyplaner1.databinding.ActivityMainBinding
import com.example.graduatedwork.R
import com.example.graduatedwork.model.UserModel
import com.example.graduatedwork.util.FirestoreUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import javax.xml.parsers.DocumentBuilder

class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN: Int = 1
    private val AUTH_REQUEST_CODE = 1911
    private val SPLASH_TIME_OUT: Long = 1000

    private val TAG = "MainActivity"

    private lateinit var provider: List<AuthUI.IdpConfig>
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logout_btn.visibility = View.INVISIBLE

//        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        provider = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )


        if (user == null) {
            logon()
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        logout_btn.setOnClickListener {
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    logout_btn.isEnabled = false
                    logon()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        }


    }


    private fun logon() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(provider)
                .build(), AUTH_REQUEST_CODE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                logout_btn.isEnabled = true

                val name = user?.displayName
                val email = user?.email
                val photoUrl = user?.photoUrl

                val userData = hashMapOf(
                    "userName" to name  ,
                    "userEmail" to email
                )


                FirebaseFirestore.getInstance().collection("users")
                    .document(user!!.uid)
                    .set(userData)
                    .addOnSuccessListener {
//                            documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                        documentReference.set(userData)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

                Handler().postDelayed({
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }, SPLASH_TIME_OUT)

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}
