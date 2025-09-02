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
import com.ParazonApps.ConcretosChile.Resultados.ResultadoFC
import com.example.aplicacion_calculadora_concretos.data.fc_metrado_datos
import java.text.DecimalFormat

class CalculoFC : AppCompatActivity() {
    lateinit var Volumen: EditText
    private lateinit var Desperdicio: EditText
    private lateinit var Tipo: Spinner
    private lateinit var Peso: Spinner
    private lateinit var buttonCalcular: Button
    private var cementoData: Double = 0.0
    private var arenaData: Double = 0.0
    private var piedraData: Double = 0.0
    private var aguaData: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_fc)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, com.ParazonApps.ConcretosChile.Perfil.Menu::class.java)
            startActivity(intent)
        }


        Volumen = findViewById(R.id.VolumenFC)
        Desperdicio = findViewById(R.id.DesperdicioFC)
        Tipo = findViewById(R.id.spinnerTipoConcretoFC)
        Peso = findViewById(R.id.spinnerPesoCementoFC)
        buttonCalcular = findViewById(R.id.buttonCalcularFC)

        // Se rescatan las filas de la data de los datos estáticos.
        val datosMetrado = fc_metrado_datos.datos_metrado
        val resistencias = datosMetrado.map { it.resistencia }.toTypedArray()

        // Se crean los array para los spinner referente al cemento
        val SpinnerCemento = arrayOf("25", "42.5", "50")

        // Configurar los adaptadores para la apariencia del spinner
        val tipoAdapter = ArrayAdapter(this, R.layout.text_spinner, resistencias)
        val pesoAdapter = ArrayAdapter(this, R.layout.text_spinner, SpinnerCemento)
        tipoAdapter.setDropDownViewResource(R.layout.text_spinner)
        pesoAdapter.setDropDownViewResource(R.layout.text_spinner)

        // Asignar los adaptadores a los spinners
        Tipo.adapter = tipoAdapter
        Peso.adapter = pesoAdapter

        //Se guardan los datos al apretar el botón calcular.
        buttonCalcular.setOnClickListener {
            // Obtiene los valores de las vistas y almacénalos en las variables
            val volumenValue = Volumen.text.toString()
            val desperdicioValue = Desperdicio.text.toString()
            val tipoValue = Tipo.selectedItem.toString()
            val pesoValue = Peso.selectedItem.toString()

            if(volumenValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleVolumen = volumenValue.toDouble()
            if(doubleVolumen == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }
            if (desperdicioValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleDesperdicio = desperdicioValue.toDouble()

            val datosResistencia = fc_metrado_datos.datos_metrado.find{it.resistencia == tipoValue}
            if(datosResistencia != null){
                cementoData = datosResistencia.cemento
                arenaData = datosResistencia.arena
                piedraData = datosResistencia.piedra
                aguaData = datosResistencia.agua
            }

            // Cálculos usados en todos los casos de pesoValue
            val desperdicioCalculo = doubleDesperdicio / 100
            val aguaReal: Double = aguaData * 1000

            // Calculos específicos según el peso
            val cementoCalculo = when (pesoValue) {
                "25" -> (42.5 * cementoData) / 25
                "42.5" -> cementoData
                else -> (42.5 * cementoData) / 50
            }

            val cementoResultado = cementoCalculo * doubleVolumen
            val aguaResultado = aguaReal * doubleVolumen
            val arenaResultado = arenaData * doubleVolumen
            val piedraResultado = piedraData * doubleVolumen

            // Cálculos de desperdicio
            val cementoDesperdicio = cementoResultado * desperdicioCalculo
            val aguaDesperdicio = aguaResultado * desperdicioCalculo
            val arenaDesperdicio = arenaResultado * desperdicioCalculo
            val piedraDesperdicio = piedraResultado * desperdicioCalculo

            // Resultados finales
            val cementoTotal = cementoResultado + cementoDesperdicio
            val aguaTotal = aguaResultado + aguaDesperdicio
            val arenaTotal = arenaResultado + arenaDesperdicio
            val piedraTotal = piedraResultado + piedraDesperdicio

            //Solo 2 decimales

            val decimal = DecimalFormat("#.##")
            val cementoFinal= decimal.format(cementoTotal)
            val aguaFinal= decimal.format(aguaTotal)
            val arenaFinal= decimal.format(arenaTotal)
            val piedraFinal= decimal.format(piedraTotal)

            //Iniciamos Intent para llevar los resultados

            val intent = Intent(this, ResultadoFC::class.java)

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