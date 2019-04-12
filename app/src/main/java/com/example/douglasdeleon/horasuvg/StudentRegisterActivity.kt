package com.example.douglasdeleon.horasuvg

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.text.Editable
import kotlinx.android.synthetic.main.activity_student_register.*
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_admin_register.*
import kotlinx.android.synthetic.main.activity_logged_in.*
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream




class StudentRegisterActivity : AppCompatActivity() {
    val PICK_PHOTO_CODE = 1046
    var imgUpload=false
    lateinit var spinner: Spinner
    lateinit var photoUri: Uri
    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var edit_message =""

    var imgEdit=false;
    lateinit var imageText:ImageView ;
    override fun onCreate(savedInstanceState: Bundle?) {
        var RESULT_LOAD_IMAGE:Int =1;

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_register)

        if(MyApplication.userInsideId==""){
            edit_message="Usuario creado correctamente."
            okbutton.text="Listo"
        }
        else {
            edit_message="Cambios en usuario realizados correctamente."
            okbutton.text="Actualizar"

        }

        //Código para funcionalidad del spinner de carreras.
        spinner = findViewById(R.id.career_spinner)

        //Asignar foto si se esta editando.
        if(MyApplication.userInsideId !="") {
            student_mail_textview.text = Editable.Factory.getInstance().newEditable(MyApplication.userInside.email);
            student_name_textview.text = Editable.Factory.getInstance().newEditable(MyApplication.userInside.name);
            student_mail_textview.isEnabled = false;

            var url =
                "https://firebasestorage.googleapis.com/v0/b/proyectoapp-add00.appspot.com/o/" + MyApplication.userInsideId.toString() + "?alt=media"
            imgEdit=true
            imageText= findViewById(R.id.studentImageUpload);
            Glide.with(this@StudentRegisterActivity)
                .load(url)
                .into(imageText)
        }
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this@StudentRegisterActivity, R.array.carreras , R.layout.support_simple_spinner_dropdown_item )
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var text: String = parent!!.getItemAtPosition(position).toString()
            }
        }

        studentImageUpload.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Bring up gallery to select a photo
                startActivityForResult(intent, PICK_PHOTO_CODE)
            }

        }

        okbutton.setOnClickListener {
            register()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //https://github.com/codepath/android_guides/wiki/Accessing-the-Camera-and-Stored-Media
        if (data != null) {
            photoUri = data.data!!
            imgUpload=true
            // Do something with the photo based on Uri
            var selectedImage: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri);
            // Load the selected image into a preview

            studentImageUpload.setImageBitmap(selectedImage);

        }
    }

    private fun register()  {
        val emailStr = student_mail_textview.text.toString()
        val passwordStr = student_password_textview.text.toString()
        val nameStr = student_name_textview.text.toString()
        var cancel = false
        var message = ""

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(passwordStr)) {

            message="La contraseña debe contener al menos 8 dígitos."
            cancel = true
        }else if(passwordStr==""){
            message="La contraseña no puede estar vacía."
            cancel = true
        }else if(nameStr==""){
            message="El nombre no puede estar vacío."
            cancel = true
        }else if(imgUpload==false && imgEdit==false){
            message="Debe tener una imagen."
            cancel = true
        }

        // Check for a valid email address.
        if (emailStr=="") {

            message="El correo no puede estar vacío."
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            message="El correo debe ser de la UVG."


            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            // Initialize a new instance of
            val builder = AlertDialog.Builder(this@StudentRegisterActivity)

            // Enviar alerta
            builder.setTitle("Error")

            // Mostrar mensaje de alerta si los datos no son validos
            builder.setMessage("$message")
            builder.setPositiveButton("Ok"){dialog, which ->

            }
            builder.show()

        } else {
            if(MyApplication.userInsideId==""){
            mFirebaseAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener{
                if (it.isSuccessful){
                    var img =("gs://proyectoapp-add00.appspot.com/"+mFirebaseAuth.currentUser!!.uid.toString())

                    val storage = FirebaseStorage.getInstance("gs://proyectoapp-add00.appspot.com")
                    val ref=storage.reference.child(mFirebaseAuth.currentUser!!.uid.toString())

                    //val bitmap = (selectedImage as BitmapDrawable).bitmap
                    //val baos = ByteArrayOutputStream()
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    //val data = baos.toByteArray()
                    //SUBIR IMAGEN
                    if(imgUpload==true) {
                        var uploadTask = ref.putFile(photoUri)
                    }
                    var newUser: User = User(nameStr,emailStr,1,0,mFirebaseAuth.currentUser!!.uid.toString())
                    // ImageView in your Activity


                    if(MyApplication.userInsideId=="") {
                        db.collection("users").document(mFirebaseAuth.currentUser!!.uid)
                            .set(newUser);
                    }else{
                        db.collection("users").document(MyApplication.userInsideId)
                            .set(newUser);
                    }
                    Toast.makeText(this@StudentRegisterActivity,"$edit_message", Toast.LENGTH_LONG).show()
                    MyApplication.userInsideId=""
                    val intent2 = Intent(this@StudentRegisterActivity, LoginActivity::class.java);
                    startActivity(intent2);
                }
            }
            mFirebaseAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnFailureListener {


                val builder = AlertDialog.Builder(this)

                // Enviar alerta
                builder.setTitle("Error")

                // Mostrar mensaje de alerta si los datos no son validos
                builder.setMessage("Correo ingresado ya existe como usuario. Intente con otro correo.")
                builder.setPositiveButton("Ok") { dialog, which ->

                }

                builder.show()

            }
            }
            else{
                mFirebaseAuth.currentUser!!.updateEmail(emailStr)
                mFirebaseAuth.currentUser!!.updatePassword(passwordStr)
                var newUser: User = User(nameStr,emailStr,1,0,MyApplication.userInsideId)
                db.collection("users").document(MyApplication.userInsideId)
                    .set(newUser)
                val storage = FirebaseStorage.getInstance("gs://proyectoapp-add00.appspot.com")
                val ref=storage.reference.child(MyApplication.userInsideId)
                if(imgUpload==true) {
                    var uploadTask = ref.putFile(photoUri)
                }
                Toast.makeText(this@StudentRegisterActivity,"$edit_message", Toast.LENGTH_LONG).show()
                MyApplication.userInsideId=""
                val intent2 = Intent(this@StudentRegisterActivity, LoginActivity::class.java);
                startActivity(intent2)

            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        //debe ser un correo de la uvg
        return email.contains("@uvg.edu.gt")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length >= 8

    }
}
