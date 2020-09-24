package com.example.graduatedwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_header.*
import kotlinx.android.synthetic.main.profile_header.view.*
import kotlinx.android.synthetic.main.profile_header.view.profile_image_header

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    val TAG = "homeActivity"


    private val END_SCALE = 0.7f
    private lateinit var mToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var contentView: ConstraintLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        init()
    }


    private fun init() {

        Log.d(TAG, "init")


        drawerLayout = findViewById(R.id.drawer_layout)
        navController = findNavController(R.id.nav_host_fragment_conteiner)
        navView = findViewById(R.id.navigation_view)
        mToolbar = findViewById(R.id.home_toolbar)
        contentView = findViewById(R.id.content_view)
        setSupportActionBar(mToolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_calendar, R.id.nav_profile
            ), drawerLayout
        )

        navView.addHeaderView(layoutInflater.inflate(R.layout.profile_header, null))

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, mToolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val header: View = navView.getHeaderView(0)

        setupActionBar(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener(this)
        animationNavDrawer()
        userInformationUpdate(header)

    }

    private fun setupActionBar(
        navController: NavController,
        appBarConfig: AppBarConfiguration
    ) {
        setupActionBarWithNavController(navController, appBarConfig)
    }


    private fun userInformationUpdate(header: View) {
        Log.d(TAG, "update")

        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName
        header.username_header.text = name


        val photoUrl = user?.photoUrl

        Picasso.with(this).load(photoUrl).into(header.profile_image_header)

        Log.d(TAG, "username1 " + name)
    }


    private fun animationNavDrawer() {
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val diffScaledOffset = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                contentView.scaleX = offsetScale
                contentView.scaleY = offsetScale


                val xOffset = drawerView.width * slideOffset;
                val xOffsetDiff = contentView.width * diffScaledOffset / 2;
                val xTranslation = xOffset - xOffsetDiff;
                contentView.translationX = xTranslation;
            }
        })


    }

    override fun onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                Log.d(TAG, "logout button select")
                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_calendar -> {
                navController.navigate(R.id.nav_calendar)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_important -> {
                navController.navigate(R.id.nav_important)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_friends -> {
                navController.navigate(R.id.nav_friends)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_profile->{
                navController.navigate(R.id.nav_profile)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_settings->{
                navController.navigate(R.id.nav_settings)
                drawerLayout.closeDrawer(GravityCompat.START)
            }

        }
        return true
    }

}
