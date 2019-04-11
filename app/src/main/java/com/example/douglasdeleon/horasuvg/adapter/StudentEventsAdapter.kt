package com.example.douglasdeleon.horasuvg.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.EventLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.R

class StudentEventsAdapter (var context: Context, var list: ArrayList<Event>): RecyclerView.Adapter<StudentEventsAdapter.ViewHolder>(){

    var myContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.student_event_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StudentEventsAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener {
            MyApplication.selectedEvent = MyApplication.eventsList.get(position)
            //Se cambia de pantalla
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bindItems(data: Event){
            val title: TextView =itemView.findViewById(R.id.text_view_title)
            val date: TextView =itemView.findViewById(R.id.text_view_date)
            val description: TextView =itemView.findViewById(R.id.text_view_description)
            val button: TextView =itemView.findViewById(R.id.assignedButton)

            title.text=data.name
            date.text=data.date
            description.text=data.description

            if(data.assigned){
                button.text = "Asignado"
                button.setBackgroundColor(Color.WHITE)
            }
        }
    }

}