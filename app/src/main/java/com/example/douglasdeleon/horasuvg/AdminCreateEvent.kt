package com.example.douglasdeleon.horasuvg

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.admin_create_event.*
import java.util.*

class AdminCreateEvent: Fragment() {

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null
    lateinit var dateButton: Button
    lateinit var date: TextView
    lateinit var datePicker: DatePickerDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        thisContext = container!!.context
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.admin_create_event, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "Crear Evento"

        buttonCreate.setOnClickListener {
        }

        dateButton = view.findViewById(R.id.dateButton)
        date = view.findViewById(R.id.date_editText)
        val calendar: Calendar = Calendar.getInstance()

        dateButton.setOnClickListener {
            datePicker = DatePickerDialog(activity,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    date.text = "${dayOfMonth}/${month}/${year}" },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }

    }
}