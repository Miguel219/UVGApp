package com.example.douglasdeleon.horasuvg.Model

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView

import kotlinx.android.synthetic.main.*

//Aplicacion de contactos Silvio Orozco.
internal class MyApplication : Application() {

    companion object {
        var userInsideId:String =""
        lateinit var eventCheck:Event
        var eventCheckId="";
        var editEventId:String =""
        lateinit var userInside:UserInside
        lateinit var eventEdit:Event
        var eventsList: ArrayList<Event> = ArrayList<Event>()
        var selectedEvent: Event = Event()
        var studentsInEventList: ArrayList<User> = ArrayList<User>()
        var selectedStudent:User  = User()
        lateinit var myContext: Context
    }
}