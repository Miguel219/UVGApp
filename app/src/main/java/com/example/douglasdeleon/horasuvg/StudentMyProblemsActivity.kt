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
import com.example.douglasdeleon.horasuvg.Model.Problem
import com.example.douglasdeleon.horasuvg.adapter.AdminEventsAdapter
import com.example.douglasdeleon.horasuvg.adapter.StudentEventsAdapter
import com.example.douglasdeleon.horasuvg.adapter.StudentMyEventsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_events.*
import kotlinx.android.synthetic.main.activity_student_events.*

class StudentMyProblemsActivity : Fragment() {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        thisContext = container!!.context
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.activity_student_myproblems
            , container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity!!.title = "Mis Problemas"

        recyclerStudentEvents.layoutManager= LinearLayoutManager(thisContext, LinearLayout.VERTICAL,false)

        MyApplication.problemList = ArrayList<Problem>()
        var adapter = StudentMyEventsAdapter(thisContext!!, MyApplication.problemList)
        adapter.notifyDataSetChanged()
        recyclerStudentEvents.adapter = adapter
        db.collection("problems").get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.forEach {
                    var problem: Problem = it.toObject(Problem::class.java)!!
                    db.collection("userproblems").whereEqualTo("userId",MyApplication.userInsideId).whereEqualTo("problemId",it.id).get()
                        .addOnSuccessListener { documentSnapshot ->
                            problem.assigned = !documentSnapshot.isEmpty
                            //event.status = documentSnapshot.
                            if(!documentSnapshot.isEmpty) {
                                MyApplication.problemList.add(problem)
                            }
                            recyclerStudentEvents.adapter = adapter
                        }
                }
            }
    }
}