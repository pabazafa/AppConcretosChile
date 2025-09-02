package com.ParazonApps.ConcretosChile.Principales


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ParazonApps.ConcretosChile.Perfil.Historial
import com.ParazonApps.ConcretosChile.R
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoFC
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoHormigon
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMetrado
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMorteros
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoVolumen
import com.example.aplicacion_calculadora_concretos.data.ResultadoFCTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadoHormigonTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadoMetradoTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadoMorterosTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadoVolumenTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadosDataBase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PantallaPrincipal : AppCompatActivity() {

    private lateinit var preferencia: SharedPreferences

    private lateinit var firebaseAuth: FirebaseAuth
    //DAO//
    private lateinit var daoResultadoFC: DaoResultadoFC
    private lateinit var daoResultadoHormigon: DaoResultadoHormigon
    private lateinit var daoResultadoMetrado: DaoResultadoMetrado
    private lateinit var daoResultadoVolumen: DaoResultadoVolumen
    private lateinit var daoResultadoMorteros: DaoResultadoMorteros

    //Button//
    private lateinit var btnHistory : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        initComponents()
        initListeners()
        firebaseAuth = Firebase.auth
        val user = firebaseAuth.currentUser
        val userid = user?.uid
        lifecycleScope.launch {
            if (userid != null) {
                syncFirebaseFC(userid)
                syncFirebaseHormigon(userid)
                syncFirebaseMetrado(userid)
                syncFirebaseVolumen(userid)
                syncFirebaseMorteros(userid)
            }
        }

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }
        preferencia = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val btnRectangular = findViewById<Button>(R.id.btnRectangular)
        val btnCilindrica = findViewById<Button>(R.id.btnCilindrica)
        val btnTriangular = findViewById<Button>(R.id.btnTriangular)

        btnRectangular.setOnClickListener {
            guardarSeleccion("VolumenRectangulo")
            abrirSeleccionarTipo()
        }

        btnCilindrica.setOnClickListener {
            guardarSeleccion("VolumenCilindro")
            abrirSeleccionarTipo()
        }

        btnTriangular.setOnClickListener {
            guardarSeleccion("VolumenTriangular")
            abrirSeleccionarTipo()
        }
        onBackPressed()
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }


    private fun initComponents()
    {
        btnHistory = findViewById(R.id.btnHistory)
        firebaseAuth = Firebase.auth
        val user = firebaseAuth.currentUser
        val userid = user?.uid
        println(userid)
    }

    private fun initListeners()
    {
        btnHistory.setOnClickListener {

            navigate(Historial::class.java)
        }
    }

    private fun <T : Activity>navigate(activityClass: Class<T>)
    {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }





    private fun guardarSeleccion(seleccion: String) {
        val editor = preferencia.edit()
        editor.putString("seleccion_usuario", seleccion)
        editor.apply()
    }

    private fun abrirSeleccionarTipo() {
        val intent = Intent(this, SeleccionarTipo::class.java)
        startActivity(intent)
    }

    private suspend fun syncFirebaseFC(userId: String) {
        val db = FirebaseFirestore.getInstance()
        try {
            val querySnapshot = db.collection("FirebaseFC")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val resultados = mutableListOf<ResultadoFCTabla>()

            for (document in querySnapshot.documents) {
                val resultado = ResultadoFCTabla(
                    nombre_resultado_fc = document.id,
                    cemento_resultado_fc = document.getDouble("cemento_fc") ?: 0.0,
                    arena_resultado_fc = document.getDouble("arena_fc") ?: 0.0,
                    gravilla_resultado_fc = document.getDouble("gravilla_fc") ?: 0.0,
                    agua_resultado_fc = document.getDouble("agua_fc") ?: 0.0,
                    user_id_fc = document.getString("user_id") ?: ""
                )
                resultados.add(resultado)
            }

            // Imprimir resultados antes de insertarlos
            resultados.forEach { println(it) }

            // Insertar los resultados en la base de datos de Room en un hilo de IO
            withContext(Dispatchers.IO) {
                daoResultadoFC = ResultadosDataBase.getInstance(this@PantallaPrincipal).daoResultadoFC
                daoResultadoFC.insertarResultadosFC(resultados)
            }

        } catch (e: Exception) {
            // Manejar las excepciones y proporcionar información en la consola
            Log.e("RoomInsert", "Error al sincronizar desde Firebase: ${e.message}")
            // Imprimir la traza completa de la excepción
            e.printStackTrace()
        }
    }

    private suspend fun syncFirebaseHormigon(userId: String) {
        val db = FirebaseFirestore.getInstance()
        try {
            val querySnapshot = db.collection("FirebaseHormigon")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val resultados = mutableListOf<ResultadoHormigonTabla>()

            for (document in querySnapshot.documents) {
                val resultado = ResultadoHormigonTabla(
                    nombre_resultado_hormigon = document.id,
                    cemento_resultado_hormigon = document.getDouble("cemento_hormigon") ?: 0.0,
                    hormigon_resultado_hormigon = document.getDouble("hormigon_hormigon") ?: 0.0,
                    agua_resultado_hormigon = document.getDouble("agua_hormigon") ?: 0.0,
                    user_id_hormigon = document.getString("user_id") ?: ""
                )
                resultados.add(resultado)
            }

            // Imprimir resultados antes de insertarlos
            resultados.forEach { println(it) }

            // Insertar los resultados en la base de datos de Room en un hilo de IO
            withContext(Dispatchers.IO) {
                daoResultadoHormigon = ResultadosDataBase.getInstance(this@PantallaPrincipal).daoResultadoHormigon
                daoResultadoHormigon.insertarResultadosHormigon(resultados)
            }

        } catch (e: Exception) {
            // Manejar las excepciones y proporcionar información en la consola
            Log.e("RoomInsert", "Error al sincronizar desde Firebase: ${e.message}")
            // Imprimir la traza completa de la excepción
            e.printStackTrace()
        }
    }

    private suspend fun syncFirebaseMetrado(userId: String) {
        val db = FirebaseFirestore.getInstance()
        try {
            val querySnapshot = db.collection("FirebaseMetrado")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val resultados = mutableListOf<ResultadoMetradoTabla>()

            for (document in querySnapshot.documents) {
                val resultado = ResultadoMetradoTabla(
                    nombre_resultado_metrado = document.id,
                    cemento_resultado_metrado = document.getDouble("cemento_metrado") ?: 0.0,
                    arena_resultado_metrado = document.getDouble("arena_metrado") ?: 0.0,
                    gravilla_resultado_metrado = document.getDouble("gravilla_metrado") ?: 0.0,
                    agua_resultado_metrado = document.getDouble("agua_metrado") ?: 0.0,
                    user_id_metrado = document.getString("user_id") ?: ""
                )
                resultados.add(resultado)
            }

            // Imprimir resultados antes de insertarlos
            resultados.forEach { println(it) }

            // Insertar los resultados en la base de datos de Room en un hilo de IO
            withContext(Dispatchers.IO) {
                daoResultadoMetrado = ResultadosDataBase.getInstance(this@PantallaPrincipal).daoResultadoMetrado
                daoResultadoMetrado.insertarResultadosMetrado(resultados)
            }

        } catch (e: Exception) {
            // Manejar las excepciones y proporcionar información en la consola
            Log.e("RoomInsert", "Error al sincronizar desde Firebase: ${e.message}")
            // Imprimir la traza completa de la excepción
            e.printStackTrace()
        }
    }

    private suspend fun syncFirebaseVolumen(userId: String) {
        val db = FirebaseFirestore.getInstance()
        try {
            val querySnapshot = db.collection("FirebaseVolumen")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val resultados = mutableListOf<ResultadoVolumenTabla>()

            for (document in querySnapshot.documents) {
                val resultado = ResultadoVolumenTabla(
                    nombre_resultado_volumen = document.id,
                    cemento_resultado_volumen = document.getDouble("cemento_volumen") ?: 0.0,
                    arena_resultado_volumen = document.getDouble("arena_volumen") ?: 0.0,
                    gravilla_resultado_volumen = document.getDouble("gravilla_volumen") ?: 0.0,
                    agua_resultado_volumen = document.getDouble("agua_volumen") ?: 0.0,
                    user_id_volumen = document.getString("user_id") ?: ""
                )
                resultados.add(resultado)
            }

            // Imprimir resultados antes de insertarlos
            resultados.forEach { println(it) }

            // Insertar los resultados en la base de datos de Room en un hilo de IO
            withContext(Dispatchers.IO) {
                daoResultadoVolumen = ResultadosDataBase.getInstance(this@PantallaPrincipal).daoResultadoVolumen
                daoResultadoVolumen.insertarResultadosVolumen(resultados)
            }

        } catch (e: Exception) {
            // Manejar las excepciones y proporcionar información en la consola
            Log.e("RoomInsert", "Error al sincronizar desde Firebase: ${e.message}")
            // Imprimir la traza completa de la excepción
            e.printStackTrace()
        }
    }

    private suspend fun syncFirebaseMorteros(userId: String) {
        val db = FirebaseFirestore.getInstance()
        try {
            val querySnapshot = db.collection("FirebaseMorteros")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val resultados = mutableListOf<ResultadoMorterosTabla>()

            for (document in querySnapshot.documents) {
                val resultado = ResultadoMorterosTabla(
                    nombre_resultado_morteros = document.id,
                    cemento_resultado_morteros = document.getDouble("cemento_morteros") ?: 0.0,
                    arena_resultado_morteros = document.getDouble("arena_morteros") ?: 0.0,
                    agua_resultado_morteros = document.getDouble("agua_morteros") ?: 0.0,
                    user_id_morteros = document.getString("user_id") ?: ""
                )
                resultados.add(resultado)
            }

            // Imprimir resultados antes de insertarlos
            resultados.forEach { println(it) }

            // Insertar los resultados en la base de datos de Room en un hilo de IO
            withContext(Dispatchers.IO) {
                daoResultadoMorteros = ResultadosDataBase.getInstance(this@PantallaPrincipal).daoResultadoMorteros
                daoResultadoMorteros.insertarResultadosMorteros(resultados)
            }

        } catch (e: Exception) {
            // Manejar las excepciones y proporcionar información en la consola
            Log.e("RoomInsert", "Error al sincronizar desde Firebase: ${e.message}")
            // Imprimir la traza completa de la excepción
            e.printStackTrace()
        }
    }




}