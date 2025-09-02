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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RecuperarPassword : AppCompatActivity() {

    //EditText//
    private lateinit var edtRecoverPasswordEmail : EditText
    //Button//
    private lateinit var btnRecoverPassword : Button
    //Firebase//
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)
        initComponents()
        initListeners()
    }

    private fun initComponents()
    {
        //EditText//
        edtRecoverPasswordEmail = findViewById(R.id.edtRecoverPasswordEmail)
        //Button//
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword)
        //Firebase//
        firebaseAuth = Firebase.auth

    }

    @SuppressLint("NewApi")
    private fun initListeners()
    {
        //Button//
        btnRecoverPassword.setOnClickListener {
            val email = edtRecoverPasswordEmail.text.toString()
            if(isConnected(this))
            {
                if(verifyEmail(email))
                {
                    showCaptchaDialog { isValidCaptcha ->
                        if (isValidCaptcha) {
                            sendPasswordReset(email)
                        } else {
                            Toast.makeText(baseContext, "Captcha Mal Escrito", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else
                {
                    Toast.makeText(baseContext,"El Correo No Cumple Con el Formato",Toast.LENGTH_SHORT).show()
                    edtRecoverPasswordEmail.requestFocus()
                }
            }
            else
            {
                Toast.makeText(baseContext,"No Hay Conexión a Internet",Toast.LENGTH_SHORT).show()
            }
        }
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
                Toast.makeText(baseContext,"Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun verifyEmail(email : String) : Boolean
    {
        return (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    private fun showCaptchaDialog(onCaptchaVerified: (Boolean) -> Unit) {
        val captchaDialog = CaptchaDialog(this, onCaptchaVerified)
        captchaDialog.show()
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