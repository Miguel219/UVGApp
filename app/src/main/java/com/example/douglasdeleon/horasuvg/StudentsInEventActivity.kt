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
import com.example.douglasdeleon.horasuvg.Model.User
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

        MyApplication.studentsInEventList = ArrayList<User>()
        var adapter = StudentsInEventAdapter(thisContext!!, MyApplication.studentsInEventList)
        adapter.notifyDataSetChanged()
        recyclerStudentsInEvent.adapter = adapter
        db.collection("userevents").whereEqualTo("eventId",MyApplication.selectedEvent.eventId).get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.forEach {
                    var userId = it.get("userId")

                    db.collection("users").document(userId as String).get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (!documentSnapshot.data.isNullOrEmpty()) {
                                var user: User = documentSnapshot.toObject(User::class.java)!!
                                if(user.type == 1)
                                MyApplication.studentsInEventList.add(user)
                                recyclerStudentsInEvent.adapter = adapter
                            }
                        }
                }
            }
    }
}
