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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ParazonApps.ConcretosChile.Perfil.Menu
import com.ParazonApps.ConcretosChile.R
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoVolumen
import com.example.aplicacion_calculadora_concretos.data.ResultadoVolumenTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadosDataBase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ResultadoVolumenes : AppCompatActivity() {

    //EditText//
    private lateinit var resulCementoCV: EditText
    private lateinit var resulArenaCV: EditText
    private lateinit var resulGravillaCV: EditText
    private lateinit var resulAguaCV: EditText
    private lateinit var nombreGuardarCV: EditText
    //DAO//
    private lateinit var daoResultadoVolumen: DaoResultadoVolumen
    //ImageView//
    lateinit var btnConfigurar: ImageView
    //Button//
    private lateinit var btnGuardarCV: Button
    //FirebaseAuth//
    private lateinit var firebaseAuth: FirebaseAuth
    //val currentUser = firebaseAuth.currentUser
    //val userUID = currentUser?.uid.toString()
    private var currentUser: FirebaseUser? = null
    private var userUID: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_volumenes)
        initComponents()
        initListeners()

    }

    private fun initComponents()
    {
        resulCementoCV = findViewById(R.id.ResulSacosCV)
        resulArenaCV = findViewById(R.id.ResulArenaCV)
        resulGravillaCV = findViewById(R.id.ResulGravillaCV)
        resulAguaCV = findViewById(R.id.ResulAguaCV)

        btnConfigurar = findViewById(R.id.config)

        btnGuardarCV = findViewById(R.id.buttonGuardarResulCV)

        nombreGuardarCV = findViewById(R.id.nombreGuardarCV)

        val intent = intent
        val cementoFinal = intent.getStringExtra("CEMENTO_TOTAL")
        val aguaFinal = intent.getStringExtra("AGUA_TOTAL")
        val arenaFinal = intent.getStringExtra("ARENA_TOTAL")
        val piedraFinal = intent.getStringExtra("PIEDRA_TOTAL")

        resulCementoCV.setText(cementoFinal.toString())
        resulArenaCV.setText(arenaFinal.toString())
        resulGravillaCV.setText(piedraFinal.toString())
        resulAguaCV.setText(aguaFinal.toString())

        firebaseAuth = Firebase.auth
        currentUser = firebaseAuth.currentUser
        userUID = currentUser?.uid ?: ""

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initListeners()
    {
        btnGuardarCV.setOnClickListener {

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
                    insertCollectionVolumen("${nombreGuardarCV.text.toString()}$fechaActual")
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
        daoResultadoVolumen = ResultadosDataBase.getInstance(this).daoResultadoVolumen

        GlobalScope.launch(Dispatchers.IO) {
            val compNombre = daoResultadoVolumen.obtenerNombreResultadoVolumen(nombreGuardarCV.text.toString())

            if (compNombre != nombreGuardarCV.text.toString() && nombreGuardarCV.text.toString() != ""){
                // Guardar los resultados en la base de datos
                val fechaActual = date()
                val nombreResultadoCV = "${nombreGuardarCV.text.toString()}$fechaActual"//nombreGuardarCV.text.toString() // Reemplaza esto con el nombre adecuado
                val cementoResultadoCV = resulCementoCV.text.toString().toDouble()
                val arenaResultadoCV = resulArenaCV.text.toString().toDouble()
                val gravillaResultadoCV = resulGravillaCV.text.toString().toDouble()
                val aguaResultadoCV = resulAguaCV.text.toString().toDouble()

                val resultadoVolumenTabla = ResultadoVolumenTabla(
                    nombreResultadoCV,
                    cementoResultadoCV,
                    arenaResultadoCV,
                    gravillaResultadoCV,
                    aguaResultadoCV,
                    userUID
                )

                // Utilizar coroutines para realizar la inserción en un hilo separado
                GlobalScope.launch(Dispatchers.IO) {
                    daoResultadoVolumen.insertarResultadoVolumen(resultadoVolumenTabla)
                }

                // Mostrar un mensaje con Toast en el hilo principal
                val mensaje = "Datos Guardados Correctamente"
                runOnUiThread {
                    Toast.makeText(this@ResultadoVolumenes, mensaje, Toast.LENGTH_SHORT).show()
                }
            }else{
                val mensaje = "El Nombre Ya Existe o Dejó la Casilla en Blanco, Por Favor Cambiar."
                runOnUiThread {
                    Toast.makeText(this@ResultadoVolumenes, mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insertCollectionVolumen(collectionID : String)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_volumen = resulCementoCV.text.toString().toDouble()
        val arena_volumen = resulArenaCV.text.toString().toDouble()
        val gravilla_volumen = resulGravillaCV.text.toString().toDouble()
        val agua_volumen = resulAguaCV.text.toString().toDouble()
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseVolumen").document(collectionID)

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
                    val VolumenData = hashMapOf(
                        "agua_volumen" to agua_volumen,
                        "arena_volumen" to arena_volumen,
                        "cemento_volumen" to cemento_volumen,
                        "gravilla_volumen" to gravilla_volumen,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseVolumen").document(collectionID).set(VolumenData).addOnSuccessListener {
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
        return name.replace(Regex("[^a-zA-Z0-9()_; -]"), "_")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun date(): String {

        val fechaActual = LocalDateTime.now()

        // Formatear la fecha en estilo personalizado
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