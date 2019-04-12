package com.example.douglasdeleon.horasuvg

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.google.firebase.auth.FirebaseAuth

class Start: Fragment() {

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var thisContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        thisContext = container!!.context
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.start, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "Inicio"
        MyApplication.eventCheckId=""
        MyApplication.editEventId=""
    }
}