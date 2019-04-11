package com.example.douglasdeleon.horasuvg.adapter

import android.content.Context
import android.content.Intent
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

class AdminEventsAdapter (var context: Context, var list: ArrayList<Event>): RecyclerView.Adapter<AdminEventsAdapter.ViewHolder>(){

    var myContext: Context = context

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

        fun bindItems(data: Event){
            val title: TextView =itemView.findViewById(R.id.text_view_title)
            val date: TextView =itemView.findViewById(R.id.text_view_date)
            val description: TextView =itemView.findViewById(R.id.text_view_description)

            title.text=data.name
            date.text=data.date
            description.text=data.description
        }
    }

}