package com.example.douglasdeleon.horasuvg

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import kotlinx.android.synthetic.main.activity_logged_in_admin.*
import kotlinx.android.synthetic.main.app_bar_logged_in_admin.*
import kotlinx.android.synthetic.main.nav_header_logged_in_admin.*
import android.R.id.*
import kotlinx.android.synthetic.main.nav_header_logged_in_admin.view.*
import android.R.*
import android.net.Uri
import android.support.v4.app.Fragment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class LoggedInAdmin : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in_admin)
        setSupportActionBar(toolbar)

        MyApplication.myContext = this

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        var header = nav_view.getHeaderView(0)
        var emailText = header.findViewById<TextView>(R.id.emailUser)
        var nameText = header.findViewById<TextView>(R.id.nameUser)
        var imageText = header.findViewById<ImageView>(R.id.imageUser)
        emailText.text= MyApplication.userInside.email
        nameText.text= MyApplication.userInside.name


        val ref = FirebaseStorage.getInstance("gs://proyectoapp-add00.appspot.com").reference.child(MyApplication.userInsideId).downloadUrl;

        var url ="https://firebasestorage.googleapis.com/v0/b/proyectoapp-add00.appspot.com/o/"+MyApplication.userInsideId.toString()+"?alt=media"

        Glide.with(this@LoggedInAdmin)
            .load(url)
            .into(imageText)

        var fragment: Fragment = Start()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (supportFragmentManager.findFragmentById(R.id.content_frame)  is Start) {
            super.onBackPressed()
        }else{
            var fragment: Fragment = Start()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logged_in_admin, menu)
        return true
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_myevents -> {
                var fragment: Fragment = AdminEventsActivity()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit()
            }
            R.id.nav_create_event -> {
                MyApplication.editEventId = ""
                MyApplication.eventCheckId = ""
                var fragment: Fragment = AdminCreateEvent()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit()
            }
            R.id.nav_logout -> {
                MyApplication.userInsideId= ""
                val intent: Intent = Intent(this, LoginActivity::class.java);
                startActivity(intent);
                Toast.makeText(this, "Sesión cerrada correctamente.", Toast.LENGTH_LONG).show()
            }
            R.id.nav_settings -> {
                if(MyApplication.userInside.type==1) {
                    val intent: Intent = Intent(this, StudentRegisterActivity::class.java);
                    startActivity(intent)
                }else{
                    val intent: Intent = Intent(this, AdminRegisterActivity::class.java);
                    startActivity(intent)
                }

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
