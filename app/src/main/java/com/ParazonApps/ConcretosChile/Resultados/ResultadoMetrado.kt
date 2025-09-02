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
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMetrado
import com.example.aplicacion_calculadora_concretos.data.ResultadoMetradoTabla
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

class ResultadoMetrado : AppCompatActivity() {

    //EditText//
    private lateinit var resulCementoMC: EditText
    private lateinit var resulAguaMC: EditText
    private lateinit var resulPiedraMC: EditText
    private lateinit var resulArenaMC: EditText
    private lateinit var nombreGuardarMC: EditText
    //DAO//
    private lateinit var daoResultadoMetrado: DaoResultadoMetrado
    //ImageView//
    lateinit var btnConfigurar: ImageView
    //Button//
    private lateinit var btnGuardarMC : Button
    //FirebaseAuth//
    private lateinit var firebaseAuth: FirebaseAuth
    //val currentUser = firebaseAuth.currentUser
    //val userUID = currentUser?.uid.toString()
    private var currentUser: FirebaseUser? = null
    private var userUID: String = ""


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_metrado)

        initComponents()
        initListeners()
    }

    private fun initComponents()
    {
        resulCementoMC = findViewById(R.id.ResulSacosMC)
        resulAguaMC = findViewById(R.id.ResulAguaMC)
        resulPiedraMC = findViewById(R.id.ResulGravillaMC)
        resulArenaMC = findViewById(R.id.ResulArenaMC)

        btnConfigurar = findViewById(R.id.config)

        btnGuardarMC =  findViewById(R.id.buttonGuardarResulMC)

        nombreGuardarMC = findViewById(R.id.nombreGuardarMC)

        val intent = intent
        val cementoFinal = intent.getStringExtra("CEMENTO_TOTAL")
        val aguaFinal = intent.getStringExtra("AGUA_TOTAL")
        val arenaFinal = intent.getStringExtra("ARENA_TOTAL")
        val piedraFinal = intent.getStringExtra("PIEDRA_TOTAL")

        resulCementoMC.setText(cementoFinal.toString())
        resulArenaMC.setText(arenaFinal.toString())
        resulPiedraMC.setText(piedraFinal.toString())
        resulAguaMC.setText(aguaFinal.toString())

        firebaseAuth = Firebase.auth
        currentUser = firebaseAuth.currentUser
        userUID = currentUser?.uid ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initListeners()
    {
        btnGuardarMC.setOnClickListener {

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
                    insertCollectionMC("${nombreGuardarMC.text.toString()}$fechaActual")
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

        btnConfigurar.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveOnLocalDB()
    {
        daoResultadoMetrado = ResultadosDataBase.getInstance(this).daoResultadoMetrado
        // Orden fijo ya que debe funcionar fuera del hilo principal para comparar el nombre
        GlobalScope.launch(Dispatchers.IO) {
            val compNombre = daoResultadoMetrado.obtenerNombreResultadoMetrado(nombreGuardarMC.text.toString())

            if (compNombre != nombreGuardarMC.text.toString() && nombreGuardarMC.text.toString() != ""){
                // Guardar los resultados en la base de datos
                val fechaActual = date()
                val nombreResultadoMC = "${nombreGuardarMC.text.toString()}$fechaActual"//nombreGuardarMC.text.toString() // Reemplaza esto con el nombre adecuado
                val cementoResultadoMC = resulCementoMC.text.toString().toDouble()
                val arenaResultadoMC = resulArenaMC.text.toString().toDouble()
                val piedraResultadoMC = resulPiedraMC.text.toString().toDouble()
                val aguaResultadoMC = resulAguaMC.text.toString().toDouble()

                // Orden de ingreso de los datos
                val resultadoMCTabla = ResultadoMetradoTabla(
                    nombreResultadoMC,
                    cementoResultadoMC,
                    arenaResultadoMC,
                    piedraResultadoMC,
                    aguaResultadoMC,
                    userUID
                )

                // Utilizar coroutines para realizar la inserción en un hilo separado
                GlobalScope.launch(Dispatchers.IO) {
                    daoResultadoMetrado.insertarResultadoMetrado(resultadoMCTabla)
                }

                // Mostrar un mensaje con Toast en el hilo principal
                val mensaje = "Datos Guardados Correctamente"
                runOnUiThread {
                    Toast.makeText(this@ResultadoMetrado, mensaje, Toast.LENGTH_SHORT).show()
                }
            }else{
                val mensaje = "El Nombre Ya Existe o Dejó la Casilla en Blanco, Por Favor Cambiar."
                runOnUiThread {
                    Toast.makeText(this@ResultadoMetrado, mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insertCollectionMC(collectionID : String)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_metrado = resulCementoMC.text.toString().toDouble()
        val arena_metrado = resulArenaMC.text.toString().toDouble()
        val gravilla_metrado = resulPiedraMC.text.toString().toDouble()
        val agua_metrado = resulAguaMC.text.toString().toDouble()
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseMetrado").document(collectionID)

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
                    val MetradoData = hashMapOf(
                        "agua_metrado" to agua_metrado,
                        "arena_metrado" to arena_metrado,
                        "cemento_metrado" to cemento_metrado,
                        "gravilla_metrado" to gravilla_metrado,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseMetrado").document(collectionID).set(MetradoData).addOnSuccessListener {
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