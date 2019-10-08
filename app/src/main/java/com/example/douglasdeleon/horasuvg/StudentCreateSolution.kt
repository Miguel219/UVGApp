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
import kotlinx.android.synthetic.main.student_create_solution.*
import java.text.SimpleDateFormat
import java.util.*



class StudentCreateSolution: Fragment() {
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
        return inflater.inflate(com.example.douglasdeleon.horasuvg.R.layout.student_create_solution, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "Nueva Solución"

        dateButton = view.findViewById(R.id.dateButton)

        buttonUpload.setOnClickListener {
            createEvent()
        }




        if(MyApplication.seeSolution){
            title1.text = "Ver Solución"
            activity!!.title = "Ver Solución"
            buttonUpload.visibility = View.INVISIBLE;
            dateButton.visibility = View.INVISIBLE
            comment_editText.text = Editable.Factory.getInstance().newEditable("Comentarios: "+MyApplication.solution.solutionComments)


            comment_editText.isEnabled = false;

            var url =
                "https://firebasestorage.googleapis.com/v0/b/proyectoapp-add00.appspot.com/o/" + MyApplication.solution.solutionId.toString() + "?alt=media"
            imgEdit=true
            imageText= this.view!!.findViewById(R.id.solutionImageUpload);
            Glide.with(this@StudentCreateSolution)
                .load(url)
                .into(imageText)
            MyApplication.seeSolution = false
        }
        var RESULT_LOAD_IMAGE:Int =1;
        solutionImageUpload.setOnClickListener {

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
        TooltipCompat.setTooltipText(solutionImageUpload, "Haz click para subir la imagen del problema.");
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

            solutionImageUpload.setImageBitmap(selectedImage);

        }
    }

    private fun createEvent(){

        val comments = comment_editText.text.toString()

        var cancel = false
        var message = ""

         if(imgUpload==false && imgEdit==false){
            message="La solución debe tener una imagen."
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

                val newSolution = HashMap<String, String>()
                newSolution.put("userSolvedId", MyApplication.userInsideId)
                newSolution.put("comments", comments)
                newSolution.put("problemId", MyApplication.problemCheck.problemId)
                var doc = FirebaseFirestore.getInstance().collection("solutions").document()
                newSolution.put("solutionId", doc.id)
                val storage = FirebaseStorage.getInstance("gs://proyectoapp-add00.appspot.com")
                val ref=storage.reference.child(doc.id)
                //SUBIR IMAGEN
                if(imgUpload==true) {
                    var uploadTask = ref.putFile(photoUri)
                }
                doc.set(newSolution as Map<String, Any>).addOnCompleteListener {
                    val relation = HashMap<String, String>()
                    relation.put("userId", MyApplication.userInsideId)
                    relation.put("solutionId", doc.id)
                    relation.put("problemId", MyApplication.problemCheck.problemId)
                    FirebaseFirestore.getInstance().collection("usersolutions").document()
                        .set(relation as Map<String, Any>)
                    var docUser = FirebaseFirestore.getInstance().collection("users").document(MyApplication.userInside.userId)
                    var docProblem = FirebaseFirestore.getInstance().collection("problems").document(MyApplication.problemCheck.problemId)
                    MyApplication.problemCheck.solved = "1";
                    MyApplication.problemCheck.solutionId = doc.id
                    MyApplication.problemCheck.solutionComments = comments
                    docProblem.set(MyApplication.problemCheck)
                    MyApplication.userInside.problemsSolved = MyApplication.userInside.problemsSolved +1;
                    var fragmentManager: FragmentManager = fragmentManager!!

                    var fragment: Fragment
                            if(MyApplication.userInside.type==1){
                                fragment = StudentProblemsUnSolvedActivity()
                            }else{
                                fragment = StudentProblemsUnSolvedActivity()
                            }

                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit()
                    Toast.makeText(thisContext, "Se ha creado la solución correctamente", Toast.LENGTH_LONG).show()
                    MyApplication.problemCheckId = ""
                }
            }/*else{
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


            }*/
        }

    }
}