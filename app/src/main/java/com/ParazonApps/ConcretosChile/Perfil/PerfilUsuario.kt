package com.ParazonApps.ConcretosChile.Perfil

import android.accounts.Account
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Credentials
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ParazonApps.ConcretosChile.Principales.PantallaPrincipal
import com.ParazonApps.ConcretosChile.R
import com.ParazonApps.ConcretosChile.Registro.PantallaInicio
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.net.PasswordAuthentication

class PerfilUsuario : AppCompatActivity() {

    //EditText//
    private lateinit var edtAccountEmail : EditText
    private lateinit var edtNewAccountEmail : EditText
    //Button///
    private lateinit var btnChangeEmail : Button
    private lateinit var btnChangePassword : Button
    private lateinit var btnDeleteAccount : Button
    //Firebase//
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)
        initComponents()
        initListeners()

        val btnConfig = findViewById<ImageView>(R.id.config)
        btnConfig.setOnClickListener {
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }
    }


    private fun initComponents()
    {
        //EditText//
        edtAccountEmail = findViewById(R.id.edtAccountEmail)
        edtNewAccountEmail = findViewById(R.id.edtNewAccountEmail)
        //Button//
        btnChangeEmail = findViewById(R.id.btnChangeEmail)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)
        //Firebase//
        firebaseAuth = Firebase.auth
    }

    @SuppressLint("NewApi")
    private fun initListeners()
    {
        //Button//
        btnChangeEmail.setOnClickListener {

            if (isConnected(this))
            {
                if(isLogged())
                {
                    if(verifyEmail(edtNewAccountEmail.text.toString()))
                    {
                        if (edtNewAccountEmail.text.toString() != edtAccountEmail.text.toString())
                        {
                            updateEmail()
                            signOut()
                        }
                        else
                        {
                            Toast.makeText(this, "El Nuevo Email es Identico al Actual",Toast.LENGTH_SHORT).show()
                        }

                    }
                    else
                    {
                        Toast.makeText(this, "Por Favor Ingrese un Email Válido",Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this, "Por Favor Inicie Sesión",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Sin Conexión a internet",Toast.LENGTH_SHORT).show()
            }
        }
        btnChangePassword.setOnClickListener {

            if (isConnected(this))
            {
                if(isLogged())
                {
                    sendPasswordReset(edtAccountEmail.text.toString())
                    signOut()
                }
                else
                {
                    Toast.makeText(this, "Por Favor Inicie Sesión",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Sin Conexion a Internet",Toast.LENGTH_SHORT).show()
            }
        }
        btnDeleteAccount.setOnClickListener {

            if(isConnected(this))
            {
                if(isLogged())
                {
                    showDialog()
                }
                else
                {
                    Toast.makeText(this, "Por Favor Inicie Sesión",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Sin Conexion a Internet",Toast.LENGTH_SHORT).show()
            }
        }

        //TextView//

        if(isLogged())
        {
            edtAccountEmail.setText(userEmail())
            edtAccountEmail.isEnabled = false
        }
        else
        {
            edtAccountEmail.isEnabled = false
            edtNewAccountEmail.isEnabled = false
        }

    }

    private fun userEmail() : String
    {
        val currentUser = firebaseAuth.currentUser
        val currentEmail = currentUser?.email
        return currentEmail.toString()
    }

    private fun isLogged() : Boolean
    {
        val currentUser = firebaseAuth.currentUser
        if (currentUser !== null)
        {
            return true
        }
        return false

    }

    private fun verifyEmail(email : String) : Boolean
    {
        return (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    @SuppressLint("NewApi")
    private fun updateEmail()
    {
        if (isLogged())
        {
            if (isConnected(this))
            {
                if(verifyEmail(edtNewAccountEmail.text.toString()))
                {
                    if(edtNewAccountEmail.text.toString() != edtAccountEmail.text.toString())
                    {
                        sendEmailVerification()
                    }
                    else
                    {
                        Toast.makeText(baseContext,"El Email es Identico al Actual",Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(baseContext,"El Email no Cumple con el Formato",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(baseContext,"Sin conexion",Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            Toast.makeText(baseContext,"Inicie Sesión Por Favor", Toast.LENGTH_SHORT).show()
        }
        signOut()
    }

    private fun sendEmailVerification() {
        val currentUser = firebaseAuth.currentUser
        val newEmail = edtNewAccountEmail.text.toString()

        currentUser?.verifyBeforeUpdateEmail(newEmail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Correo de Verificación Enviado a $newEmail", Toast.LENGTH_LONG).show()
                Toast.makeText(baseContext, "Vuelva a Iniciar Sesion Después de Verificar su Correo", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(baseContext, "Error ${task.exception.toString()}", Toast.LENGTH_SHORT).show()
                Log.e("Error: ", task.exception.toString())
            }
        }
    }

    private fun signOut()
    {
        firebaseAuth.signOut()
        navigate(PantallaInicio::class.java)
        finish()

    }

    private fun sendPasswordReset (email : String)
    {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(){ task ->
            if (task.isSuccessful)
            {
                Toast.makeText(baseContext,"Correo Enviado a: $email",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(baseContext,"Error",Toast.LENGTH_SHORT).show()
            }
        }

    }

    @SuppressLint("NewApi")
    private fun showDialog()
    {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_user)

        //Init Components//
        val edtConfirmEmail : EditText = dialog.findViewById(R.id.edtConfirmEmail)
        val edtConfirmPassword : EditText = dialog.findViewById(R.id.edtConfirmPassword)
        val btnConfirmDelete : Button = dialog.findViewById(R.id.btnConfirmDelete)

        //Init Listener//

        if(isLogged())
        {
            edtConfirmEmail.setText(userEmail())
            edtConfirmEmail.isEnabled = false
        }

        btnConfirmDelete.setOnClickListener {
            if (isConnected(this))
            {
                val currentUser = firebaseAuth.currentUser
                val userID = currentUser?.uid
                if (userID != null) {
                    deleteUserDocuments("FirebaseFC",userID)
                    deleteUserDocuments("FirebaseHormigon",userID)
                    deleteUserDocuments("FirebaseMetrado",userID)
                    deleteUserDocuments("FirebaseMorteros",userID)
                    deleteUserDocuments("FirebaseVolumen",userID)
                }
                reAuthenticate(edtConfirmEmail.text.toString(),edtConfirmPassword.text.toString())
            }
        }

        dialog.show()
    }

    private fun reAuthenticate(email: String, password: String)
    {
        val user = firebaseAuth.currentUser
        val credentials = EmailAuthProvider.getCredential(user?.email!!,password)

        user.reauthenticate(credentials).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful)
            {
                user.delete().addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful)
                    {
                        Toast.makeText(applicationContext, "Usuario Fue Eliminado Exitosamente", Toast.LENGTH_LONG).show()
                        navigate(PantallaInicio::class.java)
                        finish()
                    }
                    else
                    {
                        Toast.makeText(applicationContext, "Error al Eliminar al Usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Toast.makeText(applicationContext, "Email o Password no Válidos",Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun <T : Activity>navigate(activityClass: Class<T>)
    {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun deleteUserDocuments(Collection: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        // Obtener la referencia de la colección
        val coleccionRef = db.collection(Collection)

        // Consultar documentos que tienen el atributo igual al ID del usuario
        coleccionRef.whereEqualTo("user_id", userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Obtener los documentos que cumplen con la condición
                    val documents = task.result

                    // Lista para almacenar tareas de eliminación
                    val tareasEliminacion = mutableListOf<Task<Void>>()

                    // Iterar sobre los documentos y programar las tareas de eliminación
                    for (document in documents!!) {
                        val tareaEliminacion = coleccionRef.document(document.id).delete()
                        tareasEliminacion.add(tareaEliminacion)
                    }

                    // Ejecutar todas las tareas de eliminación en paralelo
                    Tasks.whenAllComplete(tareasEliminacion)
                        .addOnCompleteListener { tasks ->
                            // Manejar la finalización de todas las tareas de eliminación
                            // tasks.isSuccessful() indicará si todas las tareas se completaron con éxito
                            if (tasks.isSuccessful()) {
                                // Proceso completado con éxito
                                println("Documentos Eliminados con Éxito.")
                            } else {
                                // Al menos una tarea de eliminación falló
                                println("Error al Eliminar Documentos.")
                            }
                        }
                } else {
                    // La consulta no fue exitosa
                    println("Error al Consultar Documentos Para Eliminación.")
                }
            }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}