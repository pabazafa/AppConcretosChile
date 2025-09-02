package com.ParazonApps.ConcretosChile.Calculo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ParazonApps.ConcretosChile.Perfil.Menu
import com.ParazonApps.ConcretosChile.R
import java.text.DecimalFormat
import kotlin.math.PI

class VolumenCilindro : AppCompatActivity() {

    private lateinit var alturaCilindro: EditText
    private lateinit var radioCilindro: EditText
    private lateinit var CalcularCilindro: Button
    private lateinit var resultadoCilindro: EditText
    private lateinit var btnCopiar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volumen_cilindro)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

        alturaCilindro = findViewById(R.id.IngresoAlturaCilindro)
        radioCilindro = findViewById(R.id.IngresoRadioCilindro)
        CalcularCilindro = findViewById(R.id.CalcularCilindro)
        resultadoCilindro = findViewById(R.id.ResultadoVolCilindro)
        btnCopiar = findViewById(R.id.btnCopiarC)


        CalcularCilindro.setOnClickListener{
            val alturaCilindroValue = alturaCilindro.text.toString()
            val radioCilindroValue = radioCilindro.text.toString()

            if (alturaCilindroValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleAltura = alturaCilindroValue.toDouble()
            if (doubleAltura == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if (radioCilindroValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleRadio = radioCilindroValue.toDouble()
            if (doubleRadio == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            val radioCuadrado = doubleRadio * doubleRadio
            val resultado = PI * radioCuadrado * doubleAltura
            val decimal = DecimalFormat("#.##")
            val resultadoFinal = decimal.format(resultado)
            resultadoCilindro.setText(resultadoFinal)
        }
        btnCopiar.setOnClickListener {
            val copiarTexto = resultadoCilindro.text.toString()
            if(copiarTexto == ""){
                Toast.makeText(this, "No Hay Texto Para Copiar", Toast.LENGTH_SHORT).show()
            }else {
                copyToClipboard(copiarTexto)
                Toast.makeText(this, "Texto Copiado al Portapapeles", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun copyToClipboard(text: String) {
        // Obtener el servicio de gestión del portapapeles
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Crear un objeto ClipData para almacenar el texto a copiar
        val clipData = ClipData.newPlainText("text", text)

        // Establecer el contenido del portapapeles
        clipboardManager.setPrimaryClip(clipData)

    }
    private fun mensajeVacio(){
        Toast.makeText(baseContext, "Hay un Dato sin Llenar", Toast.LENGTH_SHORT).show()
    }
    private fun mensajeMayor(){
        Toast.makeText(baseContext, "No Se Puede Ingresar un 0", Toast.LENGTH_SHORT).show()
    }
}