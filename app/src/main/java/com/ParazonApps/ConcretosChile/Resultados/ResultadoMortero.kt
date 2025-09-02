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
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMorteros
import com.example.aplicacion_calculadora_concretos.data.ResultadoMorterosTabla
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

class ResultadoMortero : AppCompatActivity() {

    //EditText//
    private lateinit var resulCementoM: EditText
    private lateinit var resulAguaM: EditText
    private lateinit var resulArenaM: EditText
    private lateinit var nombreGuardarM: EditText
    //DAO//
    private lateinit var daoResultadoMorteros: DaoResultadoMorteros
    //ImageView//
    lateinit var btnConfigurar: ImageView
    //Button//
    private lateinit var btnGuardarM: Button
    //FirebaseAuth//
    private lateinit var firebaseAuth: FirebaseAuth
    //val currentUser = firebaseAuth.currentUser
    //val userUID = currentUser?.uid.toString()
    private var currentUser: FirebaseUser? = null
    private var userUID: String = ""


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_mortero)
        initComponents()
        initListeners()

    }

    private fun initComponents()
    {
        resulCementoM = findViewById(R.id.ResulSacosM)
        resulAguaM = findViewById(R.id.ResulAguaM)
        resulArenaM = findViewById(R.id.ResulArenaM)

        btnConfigurar = findViewById(R.id.config)

        btnGuardarM = findViewById(R.id.buttonGuardarResulM)

        nombreGuardarM = findViewById(R.id.nombreGuardarM)

        val intent = intent
        val cementoFinal = intent.getStringExtra("CEMENTO_TOTAL")
        val aguaFinal = intent.getStringExtra("AGUA_TOTAL")
        val arenaFinal = intent.getStringExtra("ARENA_TOTAL")

        resulCementoM.setText(cementoFinal.toString())
        resulArenaM.setText(arenaFinal.toString())
        resulAguaM.setText(aguaFinal.toString())

        firebaseAuth = Firebase.auth
        currentUser = firebaseAuth.currentUser
        userUID = currentUser?.uid ?: ""

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initListeners()
    {
        btnGuardarM.setOnClickListener {

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
                    insertCollectionMorteros("${nombreGuardarM.text.toString()}$fechaActual")
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
        daoResultadoMorteros = ResultadosDataBase.getInstance(this).daoResultadoMorteros
        GlobalScope.launch(Dispatchers.IO) {
            val compNombre = daoResultadoMorteros.obtenerNombreResultadoMorteros(nombreGuardarM.text.toString())

            if (compNombre != nombreGuardarM.text.toString() && nombreGuardarM.text.toString() != ""){
                // Guardar los resultados en la base de datos
                val fechaActual = date()
                val nombreResultadoMorteros = "${nombreGuardarM.text.toString()}$fechaActual"//nombreGuardarM.text.toString() // Reemplaza esto con el nombre adecuado
                val cementoResultadoMorteros = resulCementoM.text.toString().toDouble()
                val arenaResultadoMorteros = resulArenaM.text.toString().toDouble()
                val aguaResultadoMorteros = resulAguaM.text.toString().toDouble()

                val resultadoMTabla = ResultadoMorterosTabla(
                    nombreResultadoMorteros,
                    cementoResultadoMorteros,
                    arenaResultadoMorteros,
                    aguaResultadoMorteros,
                    userUID
                )

                // Utilizar coroutines para realizar la inserción en un hilo separado
                GlobalScope.launch(Dispatchers.IO) {
                    daoResultadoMorteros.insertarResultadoMorteros(resultadoMTabla)
                }

                // Mostrar un mensaje con Toast en el hilo principal
                val mensaje = "Datos Guardados Correctamente"
                runOnUiThread {
                    Toast.makeText(this@ResultadoMortero, mensaje, Toast.LENGTH_SHORT).show()
                }
            }else{
                val mensaje = "El Nombre Ya Existe o Dejó la Casilla en Blanco, Por Favor Cambiar."
                runOnUiThread {
                    Toast.makeText(this@ResultadoMortero, mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun insertCollectionMorteros(collectionID : String)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_morteros = resulCementoM.text.toString().toDouble()
        val arena_morteros = resulArenaM.text.toString().toDouble()
        val agua_morteros = resulAguaM.text.toString().toDouble()
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseMorteros").document(collectionID)

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
                    val MorterosData = hashMapOf(
                        "agua_morteros" to agua_morteros,
                        "arena_morteros" to arena_morteros,
                        "cemento_morteros" to cemento_morteros,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseMorteros").document(collectionID).set(MorterosData).addOnSuccessListener {
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