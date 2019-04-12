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
import com.example.douglasdeleon.horasuvg.adapter.StudentEventsAdapter
import com.example.douglasdeleon.horasuvg.adapter.StudentsInEventAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_students_in_event.*

class StudentsInEventActivity : Fragment() {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        thisContext = container!!.context
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.activity_students_in_event, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity!!.title = "Usuarios Asignados"

        recyclerStudentsInEvent.layoutManager= LinearLayoutManager(thisContext, LinearLayout.VERTICAL,false)

        MyApplication.eventsList = ArrayList<Event>()
        var adapter = StudentsInEventAdapter(thisContext!!, MyApplication.eventsList)
        adapter.notifyDataSetChanged()
        recyclerStudentsInEvent.adapter = adapter
        db.collection("events").get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.forEach {
                    var event: Event = it.toObject(Event::class.java)!!

                    db.collection("userevents").whereEqualTo("userId", MyApplication.userInsideId).whereEqualTo("eventId",it.id).get()
                        .addOnSuccessListener { documentSnapshot ->
                            event.assigned = !documentSnapshot.isEmpty
                            MyApplication.eventsList.add(event)
                            recyclerStudentsInEvent.adapter = adapter
                        }
                }
            }
    }
}
