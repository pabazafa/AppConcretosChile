package com.ParazonApps.ConcretosChile.Calculo

import android.annotation.SuppressLint
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
import com.ParazonApps.ConcretosChile.Resultados.ResultadoVolumenes
import com.example.aplicacion_calculadora_concretos.data.volumenes_datos
import java.text.DecimalFormat

class CalculoConcretoVolumenes : AppCompatActivity() {

    private lateinit var volumenCV: EditText
    private lateinit var desperdicioCV: EditText
    private lateinit var palasCV: Spinner
    private lateinit var pesoCV: Spinner
    private lateinit var btnCalcularCV: Button
    private var cementoCVData: Double = 0.0
    private var finoCVData: Double = 0.0
    private var gruesoCVData: Double = 0.0
    private var aguaCVData: Double = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_concreto_volumenes)


        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }

        volumenCV = findViewById(R.id.VolumenCV)
        desperdicioCV = findViewById(R.id.DesperdicioCV)
        palasCV = findViewById(R.id.spinnerPalasCV)
        pesoCV = findViewById(R.id.spinnerPesoCementoCV)
        btnCalcularCV = findViewById(R.id.btnCalcularCV)

        //Filas del data
        val datosVolumenes = volumenes_datos.datos_volumenes
        val palas = datosVolumenes.map{it.resistencia}.toTypedArray()

        //Spinner de cemento
        val SpinnerCemento = arrayOf("25", "42.5", "50")

        //adapters y diseño
        val palasCVAdapter = ArrayAdapter(this, R.layout.text_spinner,palas)
        val pesoCVAdapter = ArrayAdapter(this,R.layout.text_spinner,SpinnerCemento)

        palasCVAdapter.setDropDownViewResource(R.layout.text_spinner)
        pesoCVAdapter.setDropDownViewResource(R.layout.text_spinner)

        palasCV.adapter = palasCVAdapter
        pesoCV.adapter = pesoCVAdapter

        btnCalcularCV.setOnClickListener{
            val volumenCVValue = volumenCV.text.toString()
            val desperdicioCVvalue = desperdicioCV.text.toString()
            val palasCVValue = palasCV.selectedItem.toString()
            val pesoCVValue = pesoCV.selectedItem.toString()
            if(volumenCVValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleVolumen = volumenCVValue.toDouble()
            if (doubleVolumen == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }
            if (desperdicioCVvalue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleDesperdicio = desperdicioCVvalue.toDouble()

            val datosPalas = volumenes_datos.datos_volumenes.find { it.resistencia == palasCVValue }
            if (datosPalas != null){
                cementoCVData = datosPalas.cemento
                finoCVData = datosPalas.agregado_fino
                gruesoCVData = datosPalas.agregado_grueso
                aguaCVData = datosPalas.agua
            }

            //Cálculos usados en todos los datosPalas
            val desperdicioCalculo = doubleDesperdicio/100

            val cementoCalculo = when (pesoCVValue) {
                "25" -> (doubleVolumen * cementoCVData) / 25
                "42.5" -> (doubleVolumen * cementoCVData)/42.5
                else -> (doubleVolumen * cementoCVData) / 50
            }
            //Conseguimos el valor de todos con el volumen
            val aguaResultado = aguaCVData * doubleVolumen
            val finoResultado = finoCVData * doubleVolumen
            val gruesoResultado = gruesoCVData * doubleVolumen


            //Buscamos el valor del desperdicio
            val cementoDesperdicio = cementoCalculo * desperdicioCalculo
            val aguaDesperdicio = aguaResultado * desperdicioCalculo
            val finoDesperdicio = finoResultado * desperdicioCalculo
            val gruesoDesperdicio = gruesoResultado * desperdicioCalculo

            //Obtenemos el total con la suma del Resultado + Desperdicio
            val cementoTotal = (cementoCalculo + cementoDesperdicio)
            val aguaTotal = aguaResultado + aguaDesperdicio
            val finoTotal = finoResultado + finoDesperdicio
            val gruesoTotal = gruesoResultado + gruesoDesperdicio

            //Solo 2 decimales

            val decimal = DecimalFormat("#.##")
            val cementoFinal= decimal.format(cementoTotal)
            val aguaFinal= decimal.format(aguaTotal)
            val arenaFinal= decimal.format(finoTotal)
            val piedraFinal= decimal.format(gruesoTotal)

            //Iniciamos el intent para llevar los resultados

            val intent = Intent(this, ResultadoVolumenes::class.java)

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
        Toast.makeText(baseContext, "Volumen Mayor a 0", Toast.LENGTH_SHORT).show()
    }
}