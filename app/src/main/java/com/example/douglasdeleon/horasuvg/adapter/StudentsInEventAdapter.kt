package com.example.douglasdeleon.horasuvg.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.User
import com.example.douglasdeleon.horasuvg.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class StudentsInEventAdapter(var context: Context, var list: ArrayList<User>): RecyclerView.Adapter<StudentsInEventAdapter.ViewHolder>(){

    var myContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.students_in_event_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StudentsInEventAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener {
            MyApplication.selectedStudent = MyApplication.studentsInEventList.get(position)
            //Se cambia de pantalla
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()


        fun bindItems(data: User){
            val title: TextView =itemView.findViewById(R.id.text_view_title)
            val email: TextView =itemView.findViewById(R.id.text_view_description)
            val button: Button =itemView.findViewById(R.id.assignedButton)

            title.text=data.name
            email.text=data.email

            db.collection("userevents").whereEqualTo("userId",data.userId).whereEqualTo("eventId",MyApplication.selectedEvent.eventId).get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.forEach {
                        if (!it.data.isNullOrEmpty()) {
                            if (it.get("status") == "1") {
                                button.text = "Aprobado"
                                button.setBackgroundColor(Color.WHITE)
                            }
                            }
                        }
                    }

            button.setOnClickListener {
                db.collection("userevents").whereEqualTo("userId",data.userId).whereEqualTo("eventId",MyApplication.selectedEvent.eventId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        documentSnapshot.forEach {
                            if (!it.data.isNullOrEmpty()) {
                                val relation = HashMap<String, String>()
                                relation.put("userId", it.get("userId") as String)
                                relation.put("eventId", it.get("eventId") as String)
                                if(it.get("status") == "0"){
                                    relation.put("status", "1")
                                    FirebaseFirestore.getInstance().collection("userevents").document(it.id)
                                        .set(relation as Map<String, Any>).addOnSuccessListener {
                                            db.collection("events").document(MyApplication.selectedEvent.eventId).get()
                                                .addOnSuccessListener {documentSnapshot ->

                                                    var hoursEvent  = documentSnapshot.data!!.get("hours")
                                                    data.hours = data.hours + hoursEvent.toString().toInt()
                                                    FirebaseFirestore.getInstance().collection("users").document(data.userId)
                                                        .set(data).addOnSuccessListener {
                                                            button.text = "Aprobado"
                                                            button.setBackgroundColor(Color.WHITE)
                                                        }
                                                }
                                        }
                                } else{
                                    relation.put("status", "0")
                                    FirebaseFirestore.getInstance().collection("userevents").document(it.id)
                                        .set(relation as Map<String, Any>).addOnSuccessListener {
                                            db.collection("events").document(MyApplication.selectedEvent.eventId).get()
                                                .addOnSuccessListener {documentSnapshot ->
                                                    var hoursEvent = documentSnapshot.data!!.get("hours")
                                                    data.hours = data.hours - hoursEvent.toString().toInt()
                                                    FirebaseFirestore.getInstance().collection("users").document(data.userId)
                                                        .set(data).addOnSuccessListener {
                                                            button.text = "Aprobar"
                                                            button.setBackgroundColor(Color.parseColor("#FF05EA28"))
                                                        }
                                                }
                                        }
                                }
                            }
                        }
                    }
            }
        }
    }

}