package com.ParazonApps.ConcretosChile.Calculo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ParazonApps.ConcretosChile.R
import com.ParazonApps.ConcretosChile.Resultados.ResultadoMetrado
import java.text.DecimalFormat

class CalculoMetrado : AppCompatActivity() {
    private lateinit var volumenMC: EditText
    private lateinit var aguaMC: EditText
    private lateinit var pesoMC: Spinner
    private lateinit var btnCalcularMC: Button
    private lateinit var cementoMC: EditText
    private lateinit var arenaMC: EditText
    private lateinit var gravillaMC: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_metrado)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }


        volumenMC = findViewById(R.id.VolumenMC)
        aguaMC = findViewById(R.id.CantidadAguaMC)
        pesoMC = findViewById(R.id.spinnerPesoCementoMC)
        btnCalcularMC = findViewById(R.id.btnCalcularMC)
        cementoMC = findViewById(R.id.CantidadCementoMC)
        arenaMC = findViewById(R.id.CantidadArenaMC)
        gravillaMC = findViewById(R.id.CantidadGravillaMC)

        val SpinnerCemento = arrayOf("25", "42.5", "50")
        val pesoMCAdapter = ArrayAdapter(this, R.layout.text_spinner, SpinnerCemento)
        pesoMCAdapter.setDropDownViewResource(R.layout.text_spinner)

        pesoMC.adapter = pesoMCAdapter


        btnCalcularMC.setOnClickListener{
            val cantidadAguaMCValue = aguaMC.text.toString()
            val cantidadCementoMCValue = cementoMC.text.toString()
            val cantidadArenaMCValue = arenaMC.text.toString()
            val cantidadGravillaMCValue = gravillaMC.text.toString()
            val volumenMCValue = volumenMC.text.toString()
            val pesoMCValue = pesoMC.selectedItem.toString()

            if(cantidadCementoMCValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleCemento = cantidadCementoMCValue.toDouble()
            if(doubleCemento == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if(cantidadArenaMCValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleArena = cantidadArenaMCValue.toDouble()
            if (doubleArena == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if (cantidadGravillaMCValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleGravilla = cantidadGravillaMCValue.toDouble()
            if (doubleGravilla == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if(cantidadAguaMCValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleAgua = cantidadAguaMCValue.toDouble()
            if(doubleAgua == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if(volumenMCValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleVolumen = volumenMCValue.toDouble()
            if (doubleVolumen == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            val aguaReal = doubleAgua * 1000

            val cementoCalculo: Double = when (pesoMCValue) {
                "25" -> 25.0
                "42.5" -> 42.5
                else -> 50.0
            }

            val cementoTotal = (doubleCemento * doubleVolumen) / cementoCalculo
            val arenaTotal = doubleArena * doubleVolumen
            val piedraTotal = doubleGravilla * doubleVolumen
            val aguaTotal = aguaReal * doubleVolumen

            //Solo 2 decimales

            val decimal = DecimalFormat("#.##")
            val cementoFinal= decimal.format(cementoTotal)
            val aguaFinal= decimal.format(aguaTotal)
            val arenaFinal= decimal.format(arenaTotal)
            val piedraFinal= decimal.format(piedraTotal)

            //Intent para llevarnos los datos

            val intent = Intent(this, ResultadoMetrado::class.java)

            intent.putExtra("CEMENTO_TOTAL", cementoFinal).toString()
            intent.putExtra("AGUA_TOTAL", aguaFinal).toString()
            intent.putExtra("ARENA_TOTAL", arenaFinal).toString()
            intent.putExtra("PIEDRA_TOTAL", piedraFinal).toString()

            startActivity(intent)

        }
    }
    private fun mensajeVacio(){
        Toast.makeText(baseContext, "Hay un Dato sin Llenar", Toast.LENGTH_SHORT).show()
    }
    private fun mensajeMayor(){
        Toast.makeText(baseContext, "Ingresar Valores Mayores a 0", Toast.LENGTH_SHORT).show()
    }
}