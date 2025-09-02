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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoHormigon
import com.example.aplicacion_calculadora_concretos.data.ResultadoHormigonTabla
import com.example.aplicacion_calculadora_concretos.data.ResultadosDataBase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ResultadoHormigon : AppCompatActivity() {

    //EditText//
    private lateinit var resulCementoH : EditText
    private lateinit var resulHormingonH : EditText
    private lateinit var resulAguaH : EditText
    private lateinit var nombreGuardarHormigon : EditText //(R.id.nombreGuardarH)
    //DAO//
    private lateinit var daoResultadoHormigon: DaoResultadoHormigon
    //ImageView//
    lateinit var btnConfigurar : ImageView //(R.id.config)
    //Button//
    private lateinit var btnGuardarHormigon : Button//(R.id.buttonGuardarResulH)
    //FirebaseAuth//
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var userUID: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_hormigon)
        initComponents()
        initListeners()

    }

    private fun initComponents()
    {
        resulCementoH = findViewById(R.id.ResulSacosH)
        resulHormingonH = findViewById(R.id.ResulHormigonH)
        resulAguaH = findViewById(R.id.ResulAguaH)

        btnConfigurar = findViewById(R.id.config)

        btnGuardarHormigon = findViewById(R.id.buttonGuardarResulH)

        nombreGuardarHormigon = findViewById(R.id.nombreGuardarH)

        val intent = intent
        val cementoFinal = intent.getStringExtra("CEMENTO_TOTAL")
        val aguaFinal = intent.getStringExtra("AGUA_TOTAL")
        val hormigonFinal = intent.getStringExtra("HORMIGON_TOTAL")

        resulCementoH.setText(cementoFinal.toString())
        resulAguaH.setText(aguaFinal.toString())
        resulHormingonH.setText(hormigonFinal.toString())

        firebaseAuth = Firebase.auth
        currentUser = firebaseAuth.currentUser
        userUID = currentUser?.uid ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initListeners()
    {
        btnGuardarHormigon.setOnClickListener {

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
                    insertCollectionHormigon("${nombreGuardarHormigon.text.toString()}$fechaActual")
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
        daoResultadoHormigon = ResultadosDataBase.getInstance(this).daoResultadoHormigon
        // Orden fijo ya que debe funcionar fuera del hilo principal para comparar el nombre
        GlobalScope.launch(Dispatchers.IO) {
            val compNombre = daoResultadoHormigon.obtenerNombreResultadoHormigon(nombreGuardarHormigon.text.toString())

            if (compNombre != nombreGuardarHormigon.text.toString() && nombreGuardarHormigon.text.toString() != ""){
                // Guardar los resultados en la base de datos
                val fechaActual = date()
                val nombreResultadoH = "${nombreGuardarHormigon.text.toString()}$fechaActual"// Reemplaza esto con el nombre adecuado
                val cementoResultadoH = resulCementoH.text.toString().toDouble()
                val hormigonResultadoH = resulHormingonH.text.toString().toDouble()
                val aguaResultadoH = resulAguaH.text.toString().toDouble()

                val resultadoHormigonTabla = ResultadoHormigonTabla(
                    nombreResultadoH,
                    cementoResultadoH,
                    hormigonResultadoH,
                    aguaResultadoH,
                    userUID
                )

                // Utilizar coroutines para realizar la inserción en un hilo separado
                GlobalScope.launch(Dispatchers.IO) {
                    daoResultadoHormigon.insertarResultadoHormigon(resultadoHormigonTabla)
                }

                // Mostrar un mensaje con Toast en el hilo principal
                val mensaje = "Datos Guardados Correctamente"
                runOnUiThread {
                    Toast.makeText(this@ResultadoHormigon, mensaje, Toast.LENGTH_SHORT).show()
                }
            }else{
                val mensaje = "El Nombre Ya Existe o Dejó la Casilla en Blanco, Por Favor Cambiar."
                runOnUiThread {
                    Toast.makeText(this@ResultadoHormigon, mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insertCollectionHormigon(collectionID : String)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_hormigon = resulCementoH.text.toString().toDouble()
        val hormigon_hormigon = resulHormingonH.text.toString().toDouble()
        val agua_hormigon = resulAguaH.text.toString().toDouble()
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseHormigon").document(collectionID)

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
                    val HormigonData = hashMapOf(
                        "agua_hormigon" to agua_hormigon,
                        "hormigon_hormigon" to hormigon_hormigon,
                        "cemento_hormigon" to cemento_hormigon,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseHormigon").document(collectionID).set(HormigonData).addOnSuccessListener {
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