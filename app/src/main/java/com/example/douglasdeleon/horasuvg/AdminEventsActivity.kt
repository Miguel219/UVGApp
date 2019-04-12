package com.example.douglasdeleon.horasuvg

import android.content.Context
import android.content.Intent
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_events.*

class AdminEventsActivity : Fragment() {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        thisContext = container!!.context
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.activity_admin_events, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity!!.title = "Eventos"

        recyclerAdminEvents.layoutManager= LinearLayoutManager(thisContext, LinearLayout.VERTICAL,false)

        MyApplication.eventsList = ArrayList<Event>()
        var adapter = AdminEventsAdapter(thisContext!!, MyApplication.eventsList)
        adapter.notifyDataSetChanged()
        recyclerAdminEvents.adapter = adapter
        db.collection("userevents").whereEqualTo("userId",MyApplication.userInsideId).get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.forEach {
                    var eventId = it.get("eventId")

                    db.collection("events").document(eventId as String).get()
                        .addOnSuccessListener { documentSnapshot ->
                            if(!documentSnapshot.data.isNullOrEmpty()) {
                                var event: Event = documentSnapshot.toObject(Event::class.java)!!
                                MyApplication.eventsList.add(event)
                                recyclerAdminEvents.adapter = adapter
                            }
                        }
                }
            }
    }
}
