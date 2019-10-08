package com.example.douglasdeleon.horasuvg.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
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

import com.example.douglasdeleon.horasuvg.AdminEventsActivity
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.Problem
import com.example.douglasdeleon.horasuvg.Model.UserInside
import com.example.douglasdeleon.horasuvg.R
import com.example.douglasdeleon.horasuvg.StudentCreateProblem
import com.example.douglasdeleon.horasuvg.StudentCreateSolution
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class StudentMyEventsAdapter (var context: Context, var list: ArrayList<Problem>): RecyclerView.Adapter<StudentMyEventsAdapter.ViewHolder>(){

    var myContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.student_myevent_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StudentMyEventsAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener {
            MyApplication.problemCheck = MyApplication.problemList.get(position)
            MyApplication.problemCheckId = MyApplication.problemCheck.problemId;
            //Se cambia de pantalla
            var fragment: Fragment = StudentCreateProblem();
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit()
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        fun bindItems(data: Problem){
            val title: TextView =itemView.findViewById(R.id.text_view_title)
            val date: TextView =itemView.findViewById(R.id.text_view_date)
            val description: TextView =itemView.findViewById(R.id.text_view_description)
            val button: Button =itemView.findViewById(R.id.assignedButton)

            title.text=data.name
            date.text=data.date
            description.text="Clase: "+ data.problemClass

            if(data.solved == "1") {
                button.text = "Ver solución"
                button.setBackgroundColor(Color.GREEN)
            }else{
                button.visibility = View.INVISIBLE;
            }
            //if(data.status) {
              //  button.text = "Aprobado"
               // button.setBackgroundColor(Color.GREEN)
            //}
            //
            //
            button.setOnClickListener {
                MyApplication.problemCheck = data
             MyApplication.solution = data


                MyApplication.seeSolution = true
                var fragment:Fragment = StudentCreateSolution();

                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit()
            }


        }
    }

}