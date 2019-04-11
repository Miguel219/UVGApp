package com.example.douglasdeleon.horasuvg

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.adapter.AdminEventsAdapter
import com.example.douglasdeleon.horasuvg.adapter.StudentEventsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_student_events.*

class StudentEventsActivity : Fragment() {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        thisContext = container!!.context
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.activity_student_events, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity!!.title = "Eventos"

        recyclerStudentEvents.layoutManager= LinearLayoutManager(thisContext, LinearLayout.VERTICAL,false)

        MyApplication.eventsList = ArrayList<Event>()
        var adapter = StudentEventsAdapter(thisContext!!, MyApplication.eventsList)
        adapter.notifyDataSetChanged()
        recyclerStudentEvents.adapter = adapter
        db.collection("events").get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.forEach {
                    var event: Event = it.toObject(Event::class.java)!!
                    MyApplication.eventsList.add(event)
                    recyclerStudentEvents.adapter = adapter
                }
            }
    }
}
