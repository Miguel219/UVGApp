package com.example.douglasdeleon.horasuvg.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.User
import com.example.douglasdeleon.horasuvg.R

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

        fun bindItems(data: User){
            val title: TextView =itemView.findViewById(R.id.text_view_title)
            val email: TextView =itemView.findViewById(R.id.text_view_description)
            val button: TextView =itemView.findViewById(R.id.assignedButton)

            title.text=data.name
            email.text=data.email
        }
    }

}