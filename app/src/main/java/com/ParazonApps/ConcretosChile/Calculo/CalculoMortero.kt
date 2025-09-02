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
import com.ParazonApps.ConcretosChile.Resultados.ResultadoMortero
import com.example.aplicacion_calculadora_concretos.data.morteros_datos
import java.text.DecimalFormat

class CalculoMortero : AppCompatActivity() {

    private lateinit var volumenMor: EditText
    private lateinit var pesoMor: Spinner
    private lateinit var tipoMor: Spinner
    private lateinit var desperdicioMor: EditText
    private lateinit var btnCalcularMor: Button
    private var cementoMorData:Double = 0.0
    private var finoMorData: Double = 0.0
    private var aguaMorData: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_mortero)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }

        //buscamos por id
        btnCalcularMor = findViewById(R.id.btnCalcularMor)
        volumenMor = findViewById(R.id.VolumenMor)
        pesoMor = findViewById(R.id.spinnerPesoCementoMor)
        tipoMor = findViewById(R.id.spinnerTipoArenaMor)
        desperdicioMor = findViewById(R.id.DesperdicioMor)

        //Buscamos los datos estáticos
        val datosMortero = morteros_datos.datos_morteros
        val proporcionMortero = datosMortero.map{it.proporcion}.toTypedArray()

        //adapter y diseño spinners
        val SpinnerCemento = arrayOf("25", "42.5", "50")

        val pesoMorAdapter = ArrayAdapter(this, R.layout.text_spinner,SpinnerCemento)
        val tipoMorAdapter = ArrayAdapter(this, R.layout.text_spinner, proporcionMortero)

        pesoMorAdapter.setDropDownViewResource(R.layout.text_spinner)
        tipoMorAdapter.setDropDownViewResource(R.layout.text_spinner)

        pesoMor.adapter = pesoMorAdapter
        tipoMor.adapter = tipoMorAdapter

        //calcular
        btnCalcularMor.setOnClickListener{
            val volumenMorValue = volumenMor.text.toString()
            val desperdicioMorValue = desperdicioMor.text.toString()
            val pesoMorValue = pesoMor.selectedItem.toString()
            val tipoMorValue = tipoMor.selectedItem.toString()

            if (volumenMorValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleVolumen = volumenMorValue.toDouble()
            if (doubleVolumen == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }
            if (desperdicioMorValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleDesperdicio = desperdicioMorValue.toDouble()

            val datosProporcion = morteros_datos.datos_morteros.find { it.proporcion == tipoMorValue }
            if (datosProporcion != null){
                cementoMorData = datosProporcion.cemento
                finoMorData = datosProporcion.agregado_fino
                aguaMorData = datosProporcion.agua
            }

            // Cálculos usados en todos los casos de pesoValue
            val desperdicioCalculo = doubleDesperdicio / 100


            val cementoCalculo = when (pesoMorValue) {
                "25" -> cementoMorData / 25
                "42.5" -> cementoMorData / 42.5
                else -> cementoMorData / 50
            }

            val cementoResultado = cementoCalculo * doubleVolumen
            val finoResultado = finoMorData * doubleVolumen
            val aguaResultado = aguaMorData * doubleVolumen


            val cementoDesperdicio = cementoResultado * desperdicioCalculo
            val finoDespercidicio = finoResultado * desperdicioCalculo
            val aguaDesperdicio = aguaResultado * desperdicioCalculo

            val cementoTotal = cementoResultado + cementoDesperdicio
            val finoTotal = finoResultado + finoDespercidicio
            val aguaTotal = aguaResultado + aguaDesperdicio


            //Solo 2 decimales

            val decimal = DecimalFormat("#.##")
            val cementoFinal= decimal.format(cementoTotal)
            val aguaFinal= decimal.format(aguaTotal)
            val arenaFinal= decimal.format(finoTotal)

            //Intent para mover los datos

            val intent = Intent(this, ResultadoMortero::class.java)

            intent.putExtra("CEMENTO_TOTAL", cementoFinal).toString()
            intent.putExtra("AGUA_TOTAL", aguaFinal).toString()
            intent.putExtra("ARENA_TOTAL", arenaFinal).toString()


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