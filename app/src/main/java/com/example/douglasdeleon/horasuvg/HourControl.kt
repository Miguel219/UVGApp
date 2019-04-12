package com.example.douglasdeleon.horasuvg

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.UserInside
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_hour_control.*

class HourControl: Fragment() {

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var thisContext: Context? = null
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        thisContext = container!!.context

        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.fragment_hour_control, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "Control de Horas"
        db.collection("users").document(MyApplication.userInsideId).get()
            .addOnSuccessListener { documentSnapshot ->
                var user: UserInside = documentSnapshot.toObject(UserInside::class.java)!!
                MyApplication.userInside = user
                hours.text = MyApplication.userInside.hours.toString();

            }
        MyApplication.eventCheckId=""
        MyApplication.editEventId=""

    }
}