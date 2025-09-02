package com.ParazonApps.ConcretosChile.Perfil

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ParazonApps.ConcretosChile.R
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoFC
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoHormigon
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMetrado
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMorteros
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoVolumen
import com.example.aplicacion_calculadora_concretos.data.ResultadosDataBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.ParazonApps.ConcretosChile.Perfil.OnItemClickListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Historial : AppCompatActivity(), OnItemClickListener {

    private lateinit var daoResultadoFC: DaoResultadoFC
    private lateinit var daoResultadoVolumen: DaoResultadoVolumen
    private lateinit var daoResultadoHormigon: DaoResultadoHormigon
    private lateinit var daoResultadoMorteros: DaoResultadoMorteros
    private lateinit var daoResultadoMetrado: DaoResultadoMetrado

    private lateinit var adapterFC: HistorialAdapter
    private lateinit var adapterVol: HistorialAdapter
    private lateinit var adapterHor: HistorialAdapter
    private lateinit var adapterMor: HistorialAdapter
    private lateinit var adapterMC: HistorialAdapter

    private lateinit var recyclerViewFC: RecyclerView
    private lateinit var recyclerViewVol: RecyclerView
    private lateinit var recyclerViewHor: RecyclerView
    private lateinit var recyclerViewMor: RecyclerView
    private lateinit var recyclerViewMC: RecyclerView

    //Firebase//
    private lateinit var firebaseAuth: FirebaseAuth



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val btnConfig = findViewById<ImageView>(R.id.config)
        btnConfig.setOnClickListener {
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }


        //Firebase//
        firebaseAuth = Firebase.auth
        val currentUser = firebaseAuth.currentUser
        val userUID = currentUser?.uid.orEmpty()

        daoResultadoFC = ResultadosDataBase.getInstance(this).daoResultadoFC
        daoResultadoVolumen = ResultadosDataBase.getInstance(this).daoResultadoVolumen
        daoResultadoHormigon = ResultadosDataBase.getInstance(this).daoResultadoHormigon
        daoResultadoMorteros = ResultadosDataBase.getInstance(this).daoResultadoMorteros
        daoResultadoMetrado = ResultadosDataBase.getInstance(this).daoResultadoMetrado

        recyclerViewFC = findViewById(R.id.recyclerViewFC)
        recyclerViewVol = findViewById(R.id.recyclerViewVol)
        recyclerViewHor = findViewById(R.id.recyclerViewHor)
        recyclerViewMor = findViewById(R.id.recyclerViewMor)
        recyclerViewMC = findViewById(R.id.recyclerViewMC)

        recyclerViewFC.layoutManager = LinearLayoutManager(this)
        recyclerViewVol.layoutManager = LinearLayoutManager(this)
        recyclerViewHor.layoutManager = LinearLayoutManager(this)
        recyclerViewMor.layoutManager = LinearLayoutManager(this)
        recyclerViewMC.layoutManager = LinearLayoutManager(this)

        GlobalScope.launch(Dispatchers.Main) {
            val nombresFC = withContext(Dispatchers.Default) {
                daoResultadoFC.obtenerSoloNombresResultadoFC(userUID)
            }
            adapterFC = HistorialAdapter(nombresFC.map { HistorialItem("FC", it) }, this@Historial)
            recyclerViewFC.adapter = adapterFC

            val nombresVolumen = withContext(Dispatchers.Default) {
                daoResultadoVolumen.obtenerSoloNombresResultadoVolumen(userUID)
            }
            adapterVol = HistorialAdapter(nombresVolumen.map { HistorialItem("Volumen", it) }, this@Historial)
            recyclerViewVol.adapter = adapterVol

            val nombresHormigon = withContext(Dispatchers.Default) {
                daoResultadoHormigon.obtenerSoloNombresResultadoHormigon(userUID)
            }
            adapterHor = HistorialAdapter(nombresHormigon.map { HistorialItem("Hormigon", it) }, this@Historial)
            recyclerViewHor.adapter = adapterHor

            val nombresMorteros = withContext(Dispatchers.Default) {
                daoResultadoMorteros.obtenerSoloNombresResultadoMorteros(userUID)
            }
            adapterMor = HistorialAdapter(nombresMorteros.map { HistorialItem("Morteros", it) }, this@Historial)
            recyclerViewMor.adapter = adapterMor

            val nombresMetrado = withContext(Dispatchers.Default) {
                daoResultadoMetrado.obtenerSoloNombresResultadoMetrado(userUID)
            }
            adapterMC = HistorialAdapter(nombresMetrado.map { HistorialItem("Metrado", it) }, this@Historial)
            recyclerViewMC.adapter = adapterMC
        }
    }

    override fun onItemClick(position: Int, section: String) {
        //When para que proceder según lo que necesite el usuario
        when (section) {
            "FC" -> {
                val nombreSeleccionado = adapterFC.getItem(position).nombre
                abrirActividadEdicion("FC", nombreSeleccionado)
            }
            "Volumen" -> {
                val nombreSeleccionado = adapterVol.getItem(position).nombre
                abrirActividadEdicion("Volumen", nombreSeleccionado)
            }
            "Hormigon" -> {
                val nombreSeleccionado = adapterHor.getItem(position).nombre
                abrirActividadEdicion("Hormigon", nombreSeleccionado)
            }
            "Morteros" -> {
                val nombreSeleccionado = adapterMor.getItem(position).nombre
                abrirActividadEdicion("Morteros", nombreSeleccionado)
            }
            "Metrado" -> {
                val nombreSeleccionado = adapterMC.getItem(position).nombre
                abrirActividadEdicion("Metrado", nombreSeleccionado)
            }
        }
    }

    private fun abrirActividadEdicion(section: String, nombre: String) {
        val intent = Intent(this, EditarResultado::class.java)
        intent.putExtra("section", section)
        intent.putExtra("nombre", nombre)
        startActivity(intent)
    }

}
