package com.example.douglasdeleon.horasuvg

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.Problem
import com.example.douglasdeleon.horasuvg.adapter.AdminEventsAdapter
import com.example.douglasdeleon.horasuvg.adapter.StudentMyEventsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_student_events.*

class StudentProblemsUnSolvedActivity : Fragment() {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        thisContext = container!!.context
        return inflater.inflate(
            R.layout.activity_student_myproblems
            , container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity!!.title = "Problemas Por Resolver"

        recyclerStudentEvents.layoutManager= LinearLayoutManager(thisContext, LinearLayout.VERTICAL,false)

        MyApplication.problemList = ArrayList<Problem>()
        var adapter = AdminEventsAdapter(thisContext!!, MyApplication.problemList)
        adapter.notifyDataSetChanged()
        recyclerStudentEvents.adapter = adapter
        db.collection("problems").whereEqualTo("solved","0").get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.forEach {
                    var problem: Problem = it.toObject(Problem::class.java)!!

                            //event.status = documentSnapshot.
                            if(!documentSnapshot.isEmpty) {
                                MyApplication.problemList.add(problem)
                            }
                            recyclerStudentEvents.adapter = adapter

                }
            }
    }
}