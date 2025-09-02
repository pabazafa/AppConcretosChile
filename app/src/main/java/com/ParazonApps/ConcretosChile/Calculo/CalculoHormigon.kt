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
import com.ParazonApps.ConcretosChile.Resultados.ResultadoHormigon
import com.example.aplicacion_calculadora_concretos.data.hormigon_datos
import java.text.DecimalFormat

class CalculoHormigon : AppCompatActivity() {

    private lateinit var VolumenH: EditText
    private lateinit var DesperdicioH: EditText
    private lateinit var ProporcionH: Spinner
    private lateinit var PesoH: Spinner
    private lateinit var btnCalcularH: Button
    private var cementoDataH: Double = 0.0
    private var hormigonDataH: Double = 0.0
    private var aguaDataH: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_hormigon)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }

        //Se buscan las varibales mediante las id de los layouts.
        VolumenH= findViewById(R.id.VolumenH)
        DesperdicioH = findViewById(R.id.DesperdicioH)
        ProporcionH = findViewById(R.id.spinnerTipoHormigon)
        PesoH = findViewById(R.id.spinnerPesoCementoH)
        btnCalcularH = findViewById(R.id.btnCalcularH)

        //Se rescatan las filas del data pertenecientes al hormigon
        val datosHormigon = hormigon_datos.datos_hormigon
        val ProporcionHormigon = datosHormigon.map{it.proporcion}.toTypedArray()

        //array de cemento
        val SpinnerCemento = arrayOf("25", "42.5", "50")

        //adaptadores y apariencia
        val proporcionHAdapter = ArrayAdapter(this, R.layout.text_spinner, ProporcionHormigon)
        val PesoHAdapter = ArrayAdapter(this,R.layout.text_spinner, SpinnerCemento)

        proporcionHAdapter.setDropDownViewResource(R.layout.text_spinner)
        PesoHAdapter.setDropDownViewResource(R.layout.text_spinner)

        //Asignar los adapters
        ProporcionH.adapter = proporcionHAdapter
        PesoH.adapter = PesoHAdapter

        //Botón calcular
        btnCalcularH.setOnClickListener{
            val volumenHValue = VolumenH.text.toString()
            val desperdicioHValue = DesperdicioH.text.toString()
            val proporcionHValue = ProporcionH.selectedItem.toString()
            val pesoHValue = PesoH.selectedItem.toString()

            if(volumenHValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleVolumen = volumenHValue.toDouble()
            if (doubleVolumen == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }
            if(desperdicioHValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleDesperdicio = desperdicioHValue.toDouble()

            val datosProporcionH = hormigon_datos.datos_hormigon.find{it.proporcion == proporcionHValue}
            if(datosProporcionH != null){
                cementoDataH = datosProporcionH.cemento
                hormigonDataH = datosProporcionH.hormigon
                aguaDataH = datosProporcionH.agua
            }

            //Cálculos
            //Datos usados en todos los casos de DatosProporcion
            val aguaReal = aguaDataH * 1000
            val desperdicioCalculo = doubleDesperdicio / 100

            val cementoCalculo = when (pesoHValue) {
                "25" -> (42.5 * cementoDataH) / 25
                "42.5" -> cementoDataH
                else -> (42.5 * cementoDataH) / 50
            }
            val cementoResultado = cementoCalculo * doubleVolumen
            val hormigonResultado = hormigonDataH * doubleVolumen
            val aguaResultado = aguaReal * doubleVolumen


            val cementoDesperdicio = cementoResultado * desperdicioCalculo
            val hormigonDesperdicio = hormigonResultado * desperdicioCalculo
            val aguaDesperdicio = aguaResultado * desperdicioCalculo

            val cementoTotal = cementoResultado + cementoDesperdicio
            val hormigonTotal = hormigonResultado + hormigonDesperdicio
            val aguaTotal = aguaResultado + aguaDesperdicio

            //Solo 2 decimales

            val decimal = DecimalFormat("#.##")
            val cementoFinal= decimal.format(cementoTotal)
            val aguaFinal= decimal.format(aguaTotal)
            val hormigonFinal= decimal.format(hormigonTotal)

            //Intent para llevarnos los resultados

            val intent = Intent(this, ResultadoHormigon::class.java)

            intent.putExtra("CEMENTO_TOTAL", cementoFinal).toString()
            intent.putExtra("AGUA_TOTAL", aguaFinal).toString()
            intent.putExtra("HORMIGON_TOTAL", hormigonFinal).toString()


            startActivity(intent)
        }

    }
    private fun mensajeVacio(){
        Toast.makeText(baseContext, "Hay un Dato sin Llenar", Toast.LENGTH_SHORT).show()
    }
    private fun mensajeMayor(){
        Toast.makeText(baseContext, "Volumen Mayor a 0", Toast.LENGTH_SHORT).show()
    }
}