package com.ParazonApps.ConcretosChile.Resultados

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ParazonApps.ConcretosChile.R
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoFC
import com.example.aplicacion_calculadora_concretos.data.ResultadoFCTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadosDataBase
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ParazonApps.ConcretosChile.Perfil.Menu
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ResultadoFC : AppCompatActivity() {

    //EditText//
    private lateinit var resulCementoFC: EditText
    private lateinit var resulArenaFC: EditText
    private lateinit var resulGravillaFC: EditText
    private lateinit var resulAguaFC: EditText
    private lateinit var nombreGuardarFC : EditText
    //DAO//
    private lateinit var daoResultadoFC: DaoResultadoFC
    //ImageView//
    lateinit var btnConfigurar : ImageView
    //Button//
    private lateinit var btnGuardarFC : Button
    //FirebaseAuth//
    private lateinit var firebaseAuth: FirebaseAuth
    //val currentUser = firebaseAuth.currentUser
    //val userUID = currentUser?.uid.toString()
    private var currentUser: FirebaseUser? = null
    private var userUID: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_fc)
        initComponents()
        initListeners()

    }

    private fun initComponents()
    {
        resulCementoFC = findViewById(R.id.ResulSacosFC)
        resulArenaFC = findViewById(R.id.ResulArenaFC)
        resulGravillaFC = findViewById(R.id.ResulGravillaFC)
        resulAguaFC = findViewById(R.id.ResulAguaFC)

        btnConfigurar = findViewById(R.id.config)

        btnGuardarFC = findViewById(R.id.buttonGuardarResulFC)

        nombreGuardarFC = findViewById(R.id.nombreGuardarFC)

        val intent = intent
        val cementoFinal = intent.getStringExtra("CEMENTO_TOTAL")
        val aguaFinal = intent.getStringExtra("AGUA_TOTAL")
        val arenaFinal = intent.getStringExtra("ARENA_TOTAL")
        val piedraFinal = intent.getStringExtra("PIEDRA_TOTAL")

        resulCementoFC.setText(cementoFinal.toString())
        resulArenaFC.setText(arenaFinal.toString())
        resulGravillaFC.setText(piedraFinal.toString())
        resulAguaFC.setText(aguaFinal.toString())

        firebaseAuth = Firebase.auth
        currentUser = firebaseAuth.currentUser
        userUID = currentUser?.uid ?: ""
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initListeners()
    {
        btnGuardarFC.setOnClickListener {

            val currentUser = firebaseAuth.currentUser

            if(currentUser == null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    saveOnLocalDB()
                }
            }
            else
            {
                if (isConnected(this))
                {
                    val fechaActual = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        date()
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                    insertCollectionFC("${nombreGuardarFC.text.toString()}$fechaActual")
                    saveOnLocalDB()
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        saveOnLocalDB()
                    }
                }
            }
        }

        btnConfigurar.setOnClickListener{
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveOnLocalDB()
    {
        daoResultadoFC = ResultadosDataBase.getInstance(this).daoResultadoFC
        // Orden fijo ya que debe funcionar fuera del hilo principal para comparar el nombre
        GlobalScope.launch(Dispatchers.IO) {
            val compNombre = daoResultadoFC.obtenerNombreResultadoFC(nombreGuardarFC.text.toString())

            if (compNombre != nombreGuardarFC.text.toString() && nombreGuardarFC.text.toString() != ""){
                // Guardar los resultados en la base de datos
                val fechaActual = date()
                val nombreResultadoFC = "${nombreGuardarFC.text.toString()}$fechaActual"//nombreGuardarFC.text.toString() // Reemplaza esto con el nombre adecuado
                val cementoResultadoFC = resulCementoFC.text.toString().toDouble()
                val arenaResultadoFC = resulArenaFC.text.toString().toDouble()
                val gravillaResultadoFC = resulGravillaFC.text.toString().toDouble()
                val aguaResultadoFC = resulAguaFC.text.toString().toDouble()

                // Orden de ingreso de los datos
                val resultadoFCTabla = ResultadoFCTabla(
                    nombreResultadoFC,
                    cementoResultadoFC,
                    arenaResultadoFC,
                    gravillaResultadoFC,
                    aguaResultadoFC,
                    userUID
                )

                // Utilizar coroutines para realizar la inserción en un hilo separado
                GlobalScope.launch(Dispatchers.IO) {
                    daoResultadoFC.insertarResultadoFC(resultadoFCTabla)
                }

                // Mostrar un mensaje con Toast en el hilo principal
                val mensaje = "Datos Guardados Correctamente"
                runOnUiThread {
                    Toast.makeText(this@ResultadoFC, mensaje, Toast.LENGTH_SHORT).show()
                }
            }else{
                val mensaje = "El Nombre Ya Existe o Dejó la Casilla en Blanco, Por Favor Cambiar."
                runOnUiThread {
                    Toast.makeText(this@ResultadoFC, mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun insertCollectionFC(collectionID : String)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_fc = resulCementoFC.text.toString().toDouble()
        val arena_fc = resulArenaFC.text.toString().toDouble()
        val gravilla_fc = resulGravillaFC.text.toString().toDouble()
        val agua_fc = resulAguaFC.text.toString().toDouble()
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseFC").document(collectionID)

        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    Toast.makeText(this, "El Nombre Ya Existe en Firebase", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val fcData = hashMapOf(
                        "agua_fc" to agua_fc,
                        "arena_fc" to arena_fc,
                        "cemento_fc" to cemento_fc,
                        "gravilla_fc" to gravilla_fc,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseFC").document(collectionID).set(fcData).addOnSuccessListener {
                        Toast.makeText(this, "Guardado en Firebase Exitoso", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Guardado en Firebase Fallido", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun cleanDocumentId(name: String): String {
        // Reemplazar caracteres no permitidos con un guion bajo
        //return name.replace(Regex("[^a-zA-Z0-9_]"), "_")
        return name.replace(Regex("[^a-zA-Z0-9()_; -]"), "_")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun date(): String {

        val fechaActual = LocalDateTime.now()

        // Formatear la fecha en estilo personalizado
        //val formato = DateTimeFormatter.ofPattern("'_Fecha_' dd_MM_yyyy '_Hora_' HH_mm_ss")
        val formato = DateTimeFormatter.ofPattern("'(Fecha)' dd-MM-yyyy '(Hora)' HH;mm;ss")
        var fechaFormateada = fechaActual.format(formato)

        // Limpiar el resultado para asegurarse de que es un ID de documento válido
        fechaFormateada = cleanDocumentId(fechaFormateada)

        return fechaFormateada
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