package com.ParazonApps.ConcretosChile.Principales

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ParazonApps.ConcretosChile.Calculo.CalculoConcretoVolumenes
import com.ParazonApps.ConcretosChile.Calculo.CalculoFC
import com.ParazonApps.ConcretosChile.Calculo.CalculoHormigon
import com.ParazonApps.ConcretosChile.Calculo.CalculoMetrado
import com.ParazonApps.ConcretosChile.Calculo.CalculoMortero
import com.ParazonApps.ConcretosChile.Calculo.VolumenCilindro
import com.ParazonApps.ConcretosChile.Calculo.VolumenRectangulo
import com.ParazonApps.ConcretosChile.Calculo.VolumenTriangular
import com.ParazonApps.ConcretosChile.R


class SeleccionarTipo : AppCompatActivity() {

    private lateinit var preferencia: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_tipo)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }



        val btnSelecFC: Button = findViewById(R.id.btnSelecConcretoFC)
        btnSelecFC.setOnClickListener {

            val intent = Intent(this, CalculoFC:: class.java)
            startActivity(intent)
        }
        val btnSelecHormigon: Button = findViewById(R.id.btnSelecHormigon)
        btnSelecHormigon.setOnClickListener {

            val intent = Intent(this, CalculoHormigon:: class.java)
            startActivity(intent)
        }
        val btnSelecMetrado: Button = findViewById(R.id.btnSelecMetrado)
        btnSelecMetrado.setOnClickListener {

            val intent = Intent(this, CalculoMetrado:: class.java)
            startActivity(intent)
        }
        val btnSelecMortero: Button = findViewById(R.id.btnSelecMortero)
        btnSelecMortero.setOnClickListener {

            val intent = Intent(this, CalculoMortero:: class.java)
            startActivity(intent)
        }
        val btnSelecVolumenes: Button = findViewById(R.id.btnSelecPorVolumenes)
        btnSelecVolumenes.setOnClickListener {

            val intent = Intent(this, CalculoConcretoVolumenes:: class.java)
            startActivity(intent)
        }

        preferencia = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val btnSelecCalcVolumen = findViewById<Button>(R.id.btnSelecCalcVolumen)

        btnSelecCalcVolumen.setOnClickListener {
            val seleccion = obtenerSeleccionUsuario()
            Log.d("SeleccionUsuario", "Selección recuperada: $seleccion")
            abrirPantallaAsociada(seleccion)

        }
    }

    private fun obtenerSeleccionUsuario(): String {
        return preferencia.getString("seleccion_usuario", "") ?: ""
    }

    private fun abrirPantallaAsociada(seleccion: String) {
        when (seleccion) {
            "VolumenRectangulo" -> {
                val intent = Intent(this, VolumenRectangulo::class.java)
                startActivity(intent)
            }
            "VolumenCilindro" -> {
                val intent = Intent(this, VolumenCilindro::class.java)
                startActivity(intent)
            }
            "VolumenTriangular" -> {
                val intent = Intent(this, VolumenTriangular::class.java)
                startActivity(intent)
            }
        }
    }
}