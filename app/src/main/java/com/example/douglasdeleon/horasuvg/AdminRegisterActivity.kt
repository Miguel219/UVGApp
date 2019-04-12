package com.example.douglasdeleon.horasuvg

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_admin_register.*
import kotlinx.android.synthetic.main.activity_login.*
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.widget.*
import com.bumptech.glide.Glide
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_logged_in.*
import kotlinx.android.synthetic.main.activity_student_register.*

class AdminRegisterActivity : AppCompatActivity() {


    val PICK_PHOTO_CODE = 1046
    var imgUpload=false
    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var spinner: Spinner
    var edit_message=""
    var imgEdit =false;
    lateinit var imageText:ImageView ;
    lateinit var photoUri:Uri;

    override fun onCreate(savedInstanceState: Bundle?) {
        var RESULT_LOAD_IMAGE:Int =1
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_register)
        imageText = findViewById<ImageView>(R.id.adminImageUpload)
        if(MyApplication.userInsideId==""){
            edit_message="Usuario creado correctamente."
            okbuttona.text="Listo"
        }else{
            edit_message="Cambios en usuario realizados correctamente."
            okbuttona.text="Actualizar"

        }

        if(MyApplication.userInsideId !="") {
            admin_mail_textview.text = Editable.Factory.getInstance().newEditable(MyApplication.userInside.email);
            admin_name_textview3.text = Editable.Factory.getInstance().newEditable(MyApplication.userInside.name);
            admin_token_textview.text = Editable.Factory.getInstance().newEditable("adminHorasUvg");
            admin_mail_textview.isEnabled =false;
//Asignar foto si se esta editando.
            imageText= findViewById(R.id.adminImageUpload);
            var url =
                "https://firebasestorage.googleapis.com/v0/b/proyectoapp-add00.appspot.com/o/" + MyApplication.userInsideId.toString() + "?alt=media"
            imgEdit =true
            Glide.with(this@AdminRegisterActivity)
                .load(url)
                .into(imageText)
        }
        //Código para funcionalidad del spinner de departamentos.
        spinner = findViewById(R.id.departments_spinner)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this@AdminRegisterActivity, R.array.departamentos , R.layout.support_simple_spinner_dropdown_item )
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var text: String = parent!!.getItemAtPosition(position).toString()
            }
        }

        adminImageUpload.setOnClickListener {

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

        okbuttona.setOnClickListener {
            register()
        }
    }
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //https://github.com/codepath/android_guides/wiki/Accessing-the-Camera-and-Stored-Media
        if (data != null) {
            photoUri = data.data!!
            // Do something with the photo based on Uri
            imgUpload=true
            var selectedImage: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            // Load the selected image into a preview

            adminImageUpload.setImageBitmap(selectedImage)
            Toast.makeText(this@AdminRegisterActivity, "Imagen cargada con éxito.. ", Toast.LENGTH_SHORT).show();

        }
    }

    private fun register()  {
        val emailStr = admin_mail_textview.text.toString()
        val passwordStr = admin_password_textview.text.toString()
        val nameStr = admin_name_textview3.text.toString()
        val token = admin_token_textview.text.toString()
        var email=false
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
        } else if(token!="adminHorasUvg"){
            cancel=true
            message="Usted no tiene un token valido para ser un administrador."
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            // Initialize a new instance of
            val builder = AlertDialog.Builder(this@AdminRegisterActivity)

            // Enviar alerta
            builder.setTitle("Error")

            // Mostrar mensaje de alerta si los datos no son validos
            builder.setMessage("$message")
            builder.setPositiveButton("Ok"){dialog, which ->

            }
            builder.show()

        } else {
            if(MyApplication.userInsideId=="") {
                mFirebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var img =("gs://proyectoapp-add00.appspot.com/"+mFirebaseAuth.currentUser!!.uid.toString())
                        val storage = FirebaseStorage.getInstance("gs://proyectoapp-add00.appspot.com")
                        val ref=storage.reference.child(mFirebaseAuth.currentUser!!.uid.toString())
                        var newUser: User = User(nameStr, emailStr, 2,0,ref.downloadUrl.toString())
                        if (MyApplication.userInsideId == "") {
                           db.collection("users")
                                .document(mFirebaseAuth.currentUser!!.uid)
                                .set(newUser)
                        } else {
                            db.collection("users").document(MyApplication.userInsideId)
                                .set(newUser)
                        }
                        if(imgUpload==true) {
                            var uploadTask = ref.putFile(photoUri)
                        }
                        Toast.makeText(this, "$edit_message", Toast.LENGTH_LONG).show()
                        MyApplication.userInsideId = ""
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                mFirebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnFailureListener() {


                    val builder = AlertDialog.Builder(this)

                    // Enviar alerta
                    builder.setTitle("Error")

                    // Mostrar mensaje de alerta si los datos no son validos
                    builder.setMessage("Correo ingresado ya existe como usuario. Intente con otro correo.")
                    builder.setPositiveButton("Ok") { dialog, which ->

                    }

                    builder.show()


                }
            }else{
                mFirebaseAuth.currentUser!!.updateEmail(emailStr)
                mFirebaseAuth.currentUser!!.updatePassword(passwordStr)
                var newUser: User = User(nameStr,emailStr,2,0,"")
                db.collection("users").document(MyApplication.userInsideId)
                    .set(newUser)
                val storage = FirebaseStorage.getInstance("gs://proyectoapp-add00.appspot.com")
                val ref=storage.reference.child(MyApplication.userInsideId)
                if(imgUpload==true) {
                    var uploadTask = ref.putFile(photoUri)
                }
                MyApplication.userInsideId=""
                Toast.makeText(this@AdminRegisterActivity,"$edit_message", Toast.LENGTH_LONG).show()
                val intent2 = Intent(this@AdminRegisterActivity, LoginActivity::class.java);
                startActivity(intent2);

            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@uvg.edu.gt")

    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 8
    }


}
