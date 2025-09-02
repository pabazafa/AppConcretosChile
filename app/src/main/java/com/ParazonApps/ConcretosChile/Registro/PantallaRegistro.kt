package com.ParazonApps.ConcretosChile.Registro

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ParazonApps.ConcretosChile.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class PantallaRegistro : AppCompatActivity() {

    //Button//
    private lateinit var btnNewRegister : Button
    //EditText//
    private lateinit var edtRegisterEmail : EditText
    private lateinit var edtNewPassword : EditText
    private lateinit var edtNewRepeatPassword : EditText
    //Firebase//
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_registro)
        initComponents()
        initListeners()
    }

    private fun initComponents()
    {
        //Button//
        btnNewRegister = findViewById(R.id.btnNewRegister)
        //EditText//
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtNewRepeatPassword = findViewById(R.id.edtNewRepeatPassword)
        //Firebase
        firebaseAuth = FirebaseAuth.getInstance()
    }

    @SuppressLint("NewApi")
    private fun initListeners()
    {
        btnNewRegister.setOnClickListener {
            val pass1 = edtNewPassword.text.toString()
            val pass2 = edtNewRepeatPassword.text.toString()
            val email = edtRegisterEmail.text.toString()

            if (isConnected(this)) {
                if (verifyEmail(email)) {
                    if (verifyPassword(pass1, pass2)) {
                        showCaptchaDialog { isValidCaptcha ->
                            if (isValidCaptcha) {
                                createAccount(email, pass1)
                            } else {
                                Toast.makeText(baseContext, "Captcha Mal Escrito", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(baseContext, "Error: Las Contraseñas No Coinciden y/o Son Menores a 6 Caracteres", Toast.LENGTH_SHORT).show()
                        edtNewPassword.requestFocus()
                    }
                } else {
                    Toast.makeText(baseContext, "El Email No Cumple Con el Formato", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "No Hay Conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun verifyPassword(pass1 : String, pass2: String) : Boolean
    {
        return pass1 == pass2 && pass1.length >= 6
    }

    private fun verifyEmail(email : String) : Boolean
    {
        return (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }


    private fun createAccount(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                sendEmailVerification()
                Toast.makeText(baseContext, "Verifique su Correo Electrónico", Toast.LENGTH_LONG).show()
            } else {
                val exception = task.exception
                if (exception is FirebaseAuthException) {
                    Log.e("FirebaseAuthException", "Error Code: ${exception.errorCode}")
                    // Verificar si la excepción es debido a que el correo electrónico ya está en uso
                    if (exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                        // Mostrar un mensaje personalizado al usuario indicando que el correo ya está en uso
                        Toast.makeText(baseContext, "Este Correo Electrónico Ya Está en Uso. Por favor, Utiliza Otro.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Manejar otras posibles excepciones de Firebase Authentication aquí
                        Toast.makeText(baseContext, "Error de Autenticación: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Manejar otras posibles excepciones aquí
                    Toast.makeText(baseContext, "Error Inesperado: ${exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun sendEmailVerification()
    {
        val user = firebaseAuth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener(this) { task ->
            if (task.isSuccessful)
            {
                Toast.makeText(baseContext, "Correo Enviado Correctamente", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show()
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
    private fun showCaptchaDialog(onCaptchaVerified: (Boolean) -> Unit) {
        val captchaDialog = CaptchaDialog(this, onCaptchaVerified)
        captchaDialog.show()
    }
}