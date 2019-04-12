package com.example.douglasdeleon.horasuvg

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.android.synthetic.main.activity_student_register.*
import kotlinx.android.synthetic.main.admin_create_event.*
import java.util.*



class AdminCreateEvent: Fragment() {

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null
    lateinit var dateButton: Button
    lateinit var date: TextView
    lateinit var datePicker: DatePickerDialog
    var edit =false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        thisContext = container!!.context
        if(MyApplication.editEventId!=""){
            edit=true;
        }
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.admin_create_event, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "Crear Evento"
        if(MyApplication.editEventId!=""){
            title.text = "Editar Evento"
            activity!!.title = "Editar Evento"
            buttonCreate.text= "Actualizar"
        }
        dateButton = view.findViewById(R.id.dateButton)

        buttonCreate.setOnClickListener {
            createEvent()
        }
        if(edit==true){
            name_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.name)
            description_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.description)
            place_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.place)
            date_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.date)
            volunteers_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.volunteers)
            hours_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.hours)
            volunteers_editText.isEnabled = false;
            date_editText.isEnabled =false;
        }

        date = view.findViewById(R.id.date_editText)
        val calendar: Calendar = Calendar.getInstance()

        dateButton.setOnClickListener {
            datePicker = DatePickerDialog(activity,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    date.text = "${dayOfMonth}/${month+1}/${year}" },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
            var now= System.currentTimeMillis() -1000;
            datePicker.datePicker.minDate= now
            var months3 = Calendar.getInstance()
            months3.add(Calendar.MONTH,3)
            datePicker.datePicker.maxDate= months3.timeInMillis
            datePicker.show()
        }
        if(MyApplication.eventCheckId!=""){
            var now= System.currentTimeMillis() -1000;
            title.text = "Ver Evento"
            activity!!.title = "Ver Evento"
            buttonCreate.visibility = View.INVISIBLE;
            dateButton.visibility = View.INVISIBLE
            name_editText.text = Editable.Factory.getInstance().newEditable("Actividad: "+MyApplication.eventCheck.name)
            description_editText.text = Editable.Factory.getInstance().newEditable("Descripcion: "+MyApplication.eventCheck.description)
            place_editText.text = Editable.Factory.getInstance().newEditable("Lugar: " +MyApplication.eventCheck.place)
            date_editText.text = Editable.Factory.getInstance().newEditable("Fecha: " +MyApplication.eventCheck.date)
            volunteers_editText.text = Editable.Factory.getInstance().newEditable("Voluntarios: " +MyApplication.eventCheck.volunteers)
            hours_editText.text = Editable.Factory.getInstance().newEditable("Horas: " +MyApplication.eventCheck.hours)
            volunteers_editText.isEnabled = false;
            date_editText.isEnabled =false;
            name_editText.isEnabled = false;
            description_editText.isEnabled = false;
            place_editText.isEnabled =false;
            hours_editText.isEnabled =false;

        }
    }

    private fun createEvent(){

        val nameStr = name_editText.text.toString()
        val descriptionStr = description_editText.text.toString()
        val placeStr = place_editText.text.toString()
        val dateStr = date_editText.text.toString()
        val volunteers = volunteers_editText.text.toString()
        val hours = hours_editText.text.toString()

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
        }else if(hours.toDouble()<=0){
            message="Las horas deben ser mayores a 0."
            cancel = true
        }else if(volunteers.toDouble()<=0){
            message="La cantidad de voluntarios debe ser mayor a 0."
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

            if(edit==false) {

                val newEvent = HashMap<String, String>()
                newEvent.put("adminId", MyApplication.userInsideId)
                newEvent.put("name", nameStr)
                newEvent.put("description", descriptionStr)
                newEvent.put("place", placeStr)
                newEvent.put("date", dateStr)
                newEvent.put("hours", hours)
                newEvent.put("volunteers", volunteers)
                newEvent.put("cupo", volunteers)

                var doc = FirebaseFirestore.getInstance().collection("events").document()
                newEvent.put("eventId", doc.id)
                doc.set(newEvent as Map<String, Any>).addOnCompleteListener {
                    val relation = HashMap<String, String>()
                    relation.put("userId", MyApplication.userInsideId)
                    relation.put("eventId", doc.id)

                    FirebaseFirestore.getInstance().collection("userevents").document()
                        .set(relation as Map<String, Any>)
                    var fragmentManager: FragmentManager = fragmentManager!!
                    var fragment: Fragment = Start()
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit()
                    Toast.makeText(thisContext, "Se ha creado el evento correctamente", Toast.LENGTH_LONG).show()
                }
            }else{
                val newEvent = HashMap<String, String>()
                newEvent.put("adminId", MyApplication.userInsideId)
                newEvent.put("name", nameStr)
                newEvent.put("description", descriptionStr)
                newEvent.put("place", placeStr)
                newEvent.put("date", dateStr)
                newEvent.put("hours", hours)
                newEvent.put("volunteers", volunteers)
                newEvent.put("cupo", volunteers)

                var doc = FirebaseFirestore.getInstance().collection("events").document(MyApplication.editEventId)
                newEvent.put("eventId", doc.id)
                doc.set(newEvent as Map<String, Any>).addOnCompleteListener {

                    var fragmentManager: FragmentManager = fragmentManager!!
                    var fragment: Fragment = Start()
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit()
                    Toast.makeText(thisContext, "Se ha editado el evento correctamente", Toast.LENGTH_LONG).show()
                    MyApplication.editEventId = ""
                }


            }
        }

    }
}