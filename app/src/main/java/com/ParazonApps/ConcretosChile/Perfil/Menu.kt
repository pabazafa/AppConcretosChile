package com.ParazonApps.ConcretosChile.Perfil

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.ParazonApps.ConcretosChile.Principales.PantallaPrincipal
import com.ParazonApps.ConcretosChile.R
import com.ParazonApps.ConcretosChile.Registro.PantallaInicio
import com.ParazonApps.ConcretosChile.Registro.PantallaRegistro
import com.example.aplicacion_calculadora_concretos.data.ResultadosDataBase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Menu : AppCompatActivity() {

    //Button//
    private lateinit var btnProfile : Button
    private lateinit var btnHistory : Button
    private lateinit var btnTerms : Button
    private lateinit var btnHome : Button
    private lateinit var btnCloseSession : Button
    private lateinit var btnHistorial: Button
    private lateinit var btnTerminos: Button
    //FIREBASE//
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initComponents()
        initListeners()

        btnHistorial = findViewById(R.id.btnHistorial)
        btnHistorial.setOnClickListener {
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Historial::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents()
    {
        //Button//
        btnProfile = findViewById(R.id.btnProfile)
        btnTerms = findViewById(R.id.btnTerms)
        btnHome = findViewById(R.id.btnHome)
        btnCloseSession = findViewById(R.id.btnCloseSession)
        //Firebase//
        firebaseAuth = FirebaseAuth.getInstance()
        //Function//
        if (isGuest()) {
            btnCloseSession.text = getString(R.string.login)
        }
    }

    private fun initListeners()
    {
        btnProfile.setOnClickListener {
            if(isGuest())
            {
                Toast.makeText(this, "Por favor Inicie Sesion Para Acceder a Esta Funcionalidad",Toast.LENGTH_SHORT).show()
            }
            else
            {
                navigate(PerfilUsuario::class.java)
            }
             }
        btnTerms.setOnClickListener {
            val intent = Intent(this, TerminosCondiciones::class.java)
            startActivity(intent)
        }
        btnHome.setOnClickListener { navigate(PantallaPrincipal::class.java) }
        btnCloseSession.setOnClickListener {
            signOut()
            cleanDataBase()
        }
    }

    private fun signOut()
    {
        firebaseAuth.signOut()
        navigate(PantallaInicio::class.java)
        finish()

    }

    private fun cleanDataBase()
    {
        val daoResultadoFC = ResultadosDataBase.getInstance(this).daoResultadoFC
        val daoResultadoHormigon = ResultadosDataBase.getInstance(this).daoResultadoHormigon
        val daoResultadoMetrado = ResultadosDataBase.getInstance(this).daoResultadoMetrado
        val daoResultadoVolumen = ResultadosDataBase.getInstance(this).daoResultadoVolumen
        val daoResultadoMorteros = ResultadosDataBase.getInstance(this).daoResultadoMorteros
        GlobalScope.launch (Dispatchers.IO ) {
            firebaseAuth = Firebase.auth
            val currentUser = firebaseAuth.currentUser
            val userUID = currentUser?.uid.toString()
            daoResultadoFC.eliminarTodosResultadoFCIngresados()
            daoResultadoHormigon.eliminarTodosResultadoHormigonIngresados()
            daoResultadoMetrado.eliminarTodosResultadoMetradoIngresados()
            daoResultadoVolumen.eliminarTodosResultadoVolumenIngresados()
            daoResultadoMorteros.eliminarTodosResultadoMorterosIngresados()
        }
    }

    private fun isGuest() : Boolean
    {
        val user = firebaseAuth.currentUser
        val userUID = user?.uid

        if (userUID.isNullOrBlank())
        {
            return true
        }
        else
        {
            return false
        }
    }

    private fun <T : Activity>navigate(activityClass: Class<T>)
    {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

}