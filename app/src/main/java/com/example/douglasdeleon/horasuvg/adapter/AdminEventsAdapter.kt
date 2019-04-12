package com.example.douglasdeleon.horasuvg.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.EventLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.douglasdeleon.horasuvg.*
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.UserInside
import com.google.firebase.firestore.FirebaseFirestore





class AdminEventsAdapter (var context: Context, var list: ArrayList<Event>): RecyclerView.Adapter<AdminEventsAdapter.ViewHolder>(){

    public var myContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.admin_event_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdminEventsAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener {
            MyApplication.selectedEvent = MyApplication.eventsList.get(position)
            //Se cambia de pantalla
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        fun bindItems(data: Event){
            val title: TextView =itemView.findViewById(R.id.text_view_title)
            val date: TextView =itemView.findViewById(R.id.text_view_date)
            val description: TextView =itemView.findViewById(R.id.text_view_description)
            val button1: Button =itemView.findViewById(R.id.seeButton)
            val button2: Button =itemView.findViewById(R.id.editButton)
            val button3: Button =itemView.findViewById(R.id.deleteButton)

            title.text=data.name
            date.text=data.date
            description.text=data.description

            button1.setOnClickListener {
                MyApplication.selectedEvent = data
                var fragment:Fragment = StudentsInEventActivity();

                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit()
            }

            button2.setOnClickListener {
                db.collection("events").document(data.eventId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        MyApplication.eventEdit=documentSnapshot.toObject(Event::class.java)!!
                        MyApplication.editEventId = data.eventId
                        var fragment:Fragment = AdminCreateEvent();

                        val activity = it.context as AppCompatActivity
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit()

                    }

            }

            button3.setOnClickListener {
                db.collection("events").document(data.eventId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        documentSnapshot.reference.delete()
                        var fragment:Fragment = AdminEventsActivity();

                        val activity = it.context as AppCompatActivity
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit()
                    }

            }
        }
    }

}