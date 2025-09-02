package com.ParazonApps.ConcretosChile.Registro

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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ParazonApps.ConcretosChile.Principales.PantallaPrincipal
import com.ParazonApps.ConcretosChile.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.auth

class PantallaInicio : AppCompatActivity() {

    //Button//
    private lateinit var btnLogin : Button
    private lateinit var btnRegister : Button
    private lateinit var btnGuest : Button
    private lateinit var btnForgotPassword : Button
    //EditText//
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    //Firebase//
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener





    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(500)
        installSplashScreen()
        setContentView(R.layout.activity_pantalla_inicio)
        initComponents()
        initListeners()
        isLogged()
        //isLogged() hay que cambiar esta funcion de lugar cuando tengamos una splash screen.
    }

    private fun initComponents()
    {
        //Button//
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnGuest = findViewById(R.id.btnGuest)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
        //EditText//
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        //Firebase
        firebaseAuth = Firebase.auth

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initListeners()
    {
        //Button//
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                showCaptchaDialog { isValidCaptcha ->
                    if (isValidCaptcha) {
                        signIn(email, password)
                    } else {
                        Toast.makeText(baseContext, "Captcha Mal Escrito", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(baseContext, "Por Favor, Complete Todos los Campos", Toast.LENGTH_SHORT).show()
            }
        }
        btnRegister.setOnClickListener { navigate(PantallaRegistro::class.java) }
        btnGuest.setOnClickListener { navigate(PantallaPrincipal::class.java) }
        btnForgotPassword.setOnClickListener { navigate(RecuperarPassword::class.java) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun signIn(email: String, password: String) {
        if (isConnected(this)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val verify = user?.isEmailVerified

                        if (verify == true) {
                            Toast.makeText(baseContext, "Ingreso Exitoso", Toast.LENGTH_SHORT).show()
                            navigate(PantallaPrincipal::class.java)
                            finish()
                        } else {
                            Toast.makeText(baseContext, "Cuenta No Verificada Por Correo", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "Error de Email y/o Contraseña", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(baseContext, "No Hay Conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLogged()
    {
        val currentUser = firebaseAuth.currentUser
        if (currentUser !== null)
        {
            navigate(PantallaPrincipal::class.java)
        }

    }




    private fun <T : Activity>navigate(activityClass: Class<T>)
    {
        val intent = Intent(this, activityClass)
        startActivity(intent)
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