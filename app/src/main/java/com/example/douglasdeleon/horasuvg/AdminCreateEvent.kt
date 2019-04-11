package com.example.douglasdeleon.horasuvg

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.douglasdeleon.horasuvg.Model.Event
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
            createEvent()
        }

        dateButton = view.findViewById(R.id.dateButton)
        date = view.findViewById(R.id.date_editText)
        val calendar: Calendar = Calendar.getInstance()

        dateButton.setOnClickListener {
            datePicker = DatePickerDialog(activity,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    date.text = "$dayOfMonth / $month / $year" },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }

    }

    private fun createEvent(){
        val nameStr = name_editText.text.toString()
        val descriptionStr = description_editText.text.toString()
        val placeStr = place_editText.text.toString()
        val dateStr = date_editText.text.toString()
        var cancel = false
        var message = ""

        if(nameStr==""){
            message="El nombre no puede estar vacío."
            cancel = true
        }else if(descriptionStr==""){
            message="La descripción no puede estar vacía."
            cancel = true
        }else if(placeStr==""){
            message="El lugar no puede estar vacío."
            cancel = true
        }else if(dateStr==""){
            message="La fecha no puede estar vacía."
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            // Initialize a new instance of
            val builder = AlertDialog.Builder(thisContext!!)

            // Enviar alerta
            builder.setTitle("Error")

            // Mostrar mensaje de alerta si los datos no son validos
            builder.setMessage("$message")
            builder.setPositiveButton("Ok"){dialog, which ->

            }
            builder.show()

        } else {
            var newEvent: Event = Event(nameStr,descriptionStr,placeStr,dateStr)
            db.collection("events").document().set(newEvent)

            var fragmentManager: FragmentManager = fragmentManager!!
            var fragment: Fragment = Start()
            fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit()
            Toast.makeText(thisContext, "Se ha creado el evento correctamente", Toast.LENGTH_LONG).show()
        }

    }
}