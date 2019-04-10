package com.example.douglasdeleon.horasuvg

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_admin_register.*
import kotlinx.android.synthetic.main.activity_login.*
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.douglasdeleon.horasuvg.Model.MyApplication
import com.example.douglasdeleon.horasuvg.Model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class AdminRegisterActivity : AppCompatActivity() {


    val PICK_PHOTO_CODE = 1046
    private var mFirebaseAuth: FirebaseAuth? = null
    lateinit var spinner: Spinner
    var edit_message="";
    override fun onCreate(savedInstanceState: Bundle?) {
        var RESULT_LOAD_IMAGE:Int =1;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_register)
        if(MyApplication.userInsideId==""){
            edit_message="Usuario creado correctamente."
            okbuttona.text="Listo"
        }else{
            edit_message="Cambios en usuario realizados correctamente."
            okbuttona.text="Actualizar"



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
        //Inicializa FireBase
        mFirebaseAuth = FirebaseAuth.getInstance();

        okbuttona.setOnClickListener {
            register()
        }
    }
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //https://github.com/codepath/android_guides/wiki/Accessing-the-Camera-and-Stored-Media
        if (data != null) {
            var photoUri: Uri = data.getData();
            // Do something with the photo based on Uri
            var selectedImage: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri);
            // Load the selected image into a preview

            adminImageUpload.setImageBitmap(selectedImage);
            Toast.makeText(this@AdminRegisterActivity, "Imagen cargada con éxito.. ", Toast.LENGTH_SHORT).show();

        }
    }

    private fun register()  {
        val emailStr = admin_mail_textview.text.toString()
        val passwordStr = admin_password_textview.text.toString()
        val nameStr = admin_name_textview3.text.toString()
        val token = admin_token_textview.text.toString()
        var email=false;
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
                mFirebaseAuth!!.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var newUser: User = User(nameStr, emailStr, 2,"")
                        if (MyApplication.userInsideId == "") {
                            FirebaseFirestore.getInstance().collection("users")
                                .document(mFirebaseAuth!!.currentUser!!.uid)
                                .set(newUser);
                        } else {
                            FirebaseFirestore.getInstance().collection("users").document(MyApplication.userInsideId)
                                .set(newUser);
                        }
                        Toast.makeText(this, "$edit_message", Toast.LENGTH_LONG).show()
                        MyApplication.userInsideId = ""
                        val intent = Intent(this, LoginActivity::class.java);
                        startActivity(intent);
                    }
                }
                mFirebaseAuth!!.createUserWithEmailAndPassword(emailStr, passwordStr).addOnFailureListener() {


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
                mFirebaseAuth!!.currentUser!!.updateEmail(emailStr)
                mFirebaseAuth!!.currentUser!!.updatePassword(passwordStr)
                var newUser: User = User(nameStr,emailStr,0,"")
                FirebaseFirestore.getInstance().collection("users").document(MyApplication.userInsideId)
                    .set(newUser);



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
