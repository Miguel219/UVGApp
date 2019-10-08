package com.example.douglasdeleon.horasuvg

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.TooltipCompat
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.douglasdeleon.horasuvg.Model.Event
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_student_register.*
import kotlinx.android.synthetic.main.admin_create_event.*
import kotlinx.android.synthetic.main.student_create_problem.*
import java.text.SimpleDateFormat
import java.util.*



class StudentCreateProblem: Fragment() {
    val PICK_PHOTO_CODE = 1046
    var imgUpload=false
    lateinit var spinner: Spinner
    lateinit var photoUri: Uri
    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var thisContext: Context? = null
    lateinit var dateButton: Button
    lateinit var date: TextView
    lateinit var datePicker: DatePickerDialog
    var edit =false;
    var imgEdit=false;
    lateinit var imageText: ImageView;
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        thisContext = container!!.context
        if(MyApplication.editEventId!=""){
            edit=true;
        }
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.student_create_problem, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "Nuevo Problema"
        if(MyApplication.editEventId!=""){
            title.text = "Editar Problema"
            activity!!.title = "Editar Problema"
            buttonCreate.text= "Actualizar"
        }
        dateButton = view.findViewById(R.id.dateButton)

        buttonCreate.setOnClickListener {
            createEvent()
        }
        if(edit==true){
            name_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.name)
            description_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.description)
            class_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.place)
            date_editText.text = Editable.Factory.getInstance().newEditable(MyApplication.eventEdit.date)
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
        if(MyApplication.problemCheckId!=""){
            var now= System.currentTimeMillis() -1000;
            title.text = "Ver Problema"
            activity!!.title = "Ver Problema"
            buttonCreate.visibility = View.INVISIBLE;
            dateButton.visibility = View.INVISIBLE
            name_editText.text = Editable.Factory.getInstance().newEditable("Nombre: "+MyApplication.problemCheck.name)
            description_editText.text = Editable.Factory.getInstance().newEditable("Descripcion: "+MyApplication.problemCheck.description)
            class_editText.text = Editable.Factory.getInstance().newEditable("Clase: " +MyApplication.problemCheck.problemClass)
            date_editText.text = Editable.Factory.getInstance().newEditable("Fecha: " +MyApplication.problemCheck.date)
            date_editText.isEnabled =false;
            name_editText.isEnabled = false;
            description_editText.isEnabled = false;
            class_editText.isEnabled =false;
            var url =
                "https://firebasestorage.googleapis.com/v0/b/proyectoapp-add00.appspot.com/o/" + MyApplication.problemCheck.problemId.toString() + "?alt=media"
            imgEdit=true
            imageText= this.view!!.findViewById(R.id.problemImageUpload);
            Glide.with(this@StudentCreateProblem)
                .load(url)
                .into(imageText)
            MyApplication.problemCheckId ="";
        }
        var RESULT_LOAD_IMAGE:Int =1;
        problemImageUpload.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(this.activity!!.packageManager) != null) {
                // Bring up gallery to select a photo
                startActivityForResult(intent, PICK_PHOTO_CODE)
            }

        }
        TooltipCompat.setTooltipText(problemImageUpload, "Haz click para subir la imagen del problema.");
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //https://github.com/codepath/android_guides/wiki/Accessing-the-Camera-and-Stored-Media
        if (data != null) {
            photoUri = data.data!!
            imgUpload=true
            // Do something with the photo based on Uri
            var selectedImage: Bitmap = MediaStore.Images.Media.getBitmap(this.activity!!.contentResolver, photoUri);
            // Load the selected image into a preview

            problemImageUpload.setImageBitmap(selectedImage);

        }
    }

    private fun createEvent(){

        val nameStr = name_editText.text.toString()
        val descriptionStr = description_editText.text.toString()
        val classStr = class_editText.text.toString()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


        val solved = false;
        var cancel = false
        var message = ""

        if(nameStr==""){
            message="El nombre no puede estar vacío."
            cancel = true
        }else if(descriptionStr==""){
            message="La descripción no puede estar vacía."
            cancel = true
        }else if(classStr==""){
            message="La clase no puede estar vacío."
            cancel = true
        }else if(imgUpload==false && imgEdit==false){
            message="El problema debe tener una imagen."
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

                val newProblem = HashMap<String, String>()
                newProblem.put("userCreatedId", MyApplication.userInsideId)
                newProblem.put("name", nameStr)
                newProblem.put("description", descriptionStr)
                newProblem.put("problemClass", classStr)
                newProblem.put("date", dateStr)
                newProblem.put("solved", "0")

                var doc = FirebaseFirestore.getInstance().collection("problems").document()
                newProblem.put("problemId", doc.id)
                val storage = FirebaseStorage.getInstance("gs://proyectoapp-add00.appspot.com")
                val ref=storage.reference.child(doc.id)
                //SUBIR IMAGEN
                if(imgUpload==true) {
                    var uploadTask = ref.putFile(photoUri)
                }
                doc.set(newProblem as Map<String, Any>).addOnCompleteListener {
                    val relation = HashMap<String, String>()
                    relation.put("userId", MyApplication.userInsideId)
                    relation.put("problemId", doc.id)

                    FirebaseFirestore.getInstance().collection("userproblems").document()
                        .set(relation as Map<String, Any>)
                    var fragmentManager: FragmentManager = fragmentManager!!
                    var fragment: Fragment = StudentMyProblemsActivity()
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit()
                    Toast.makeText(thisContext, "Se ha creado el problema correctamente", Toast.LENGTH_LONG).show()
                }
            }else{
                val newProblem = HashMap<String, String>()
                newProblem.put("userCreatedId", MyApplication.userInsideId)
                newProblem.put("name", nameStr)
                newProblem.put("description", descriptionStr)
                newProblem.put("class", classStr)
                newProblem.put("date", dateStr)
                newProblem.put("solved", "0")

                var doc = FirebaseFirestore.getInstance().collection("events").document(MyApplication.editEventId)
                newProblem.put("eventId", doc.id)
                doc.set(newProblem as Map<String, Any>).addOnCompleteListener {

                    var fragmentManager: FragmentManager = fragmentManager!!
                    var fragment: Fragment = Start()
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit()
                    Toast.makeText(thisContext, "Se ha editado el problema correctamente", Toast.LENGTH_LONG).show()
                    MyApplication.editEventId = ""
                }


            }
        }

    }
}