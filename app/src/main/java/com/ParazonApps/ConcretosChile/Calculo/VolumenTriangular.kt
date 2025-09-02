package com.ParazonApps.ConcretosChile.Calculo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ParazonApps.ConcretosChile.Perfil.Menu
import com.ParazonApps.ConcretosChile.R
import java.text.DecimalFormat
import android.widget.Toast

import com.google.android.play.core.integrity.v

class VolumenTriangular : AppCompatActivity() {

    private lateinit var alturaTri: EditText
    private lateinit var alturaBaseTri: EditText
    private lateinit var baseTri: EditText
    private lateinit var btnCalcular: Button
    private lateinit var resultadoTriangular: EditText
    private lateinit var btnCopiar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volumen_triangular)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

        alturaTri = findViewById(R.id.IngresoAlturaTriangular)
        alturaBaseTri = findViewById(R.id.IngresoAlturaBaseTriangular)
        baseTri = findViewById(R.id.IngresoBaseTriangular)
        btnCalcular = findViewById(R.id.CalcularTriangular)
        resultadoTriangular = findViewById(R.id.ResultadoVolTriangular)
        btnCopiar = findViewById(R.id.btnCopiarT)

        btnCalcular.setOnClickListener {
            val alturaTriValue = alturaTri.text.toString()
            val alturaBaseTriValue = alturaBaseTri.text.toString()
            val baseTriValue = baseTri.text.toString()

            if (alturaTriValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleAlturaTri = alturaTriValue.toDouble()
            if (doubleAlturaTri == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if (alturaBaseTriValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleAlturaBaseTri = alturaBaseTriValue.toDouble()
            if (doubleAlturaBaseTri == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if (baseTriValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleBaseTri = baseTriValue.toDouble()
            if(doubleBaseTri == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            val volumenBase = doubleBaseTri * doubleAlturaBaseTri
            val resultado = volumenBase * doubleAlturaTri
            val decimal = DecimalFormat("#.##")
            val resultadoFinal = decimal.format(resultado)
            resultadoTriangular.setText(resultadoFinal)

        }
        btnCopiar.setOnClickListener {
            val copiarTexto = resultadoTriangular.text.toString()

            if(copiarTexto == ""){
                Toast.makeText(this, "No Hay Texto Para Copiar", Toast.LENGTH_SHORT).show()
            }else {
                copyToClipboard(copiarTexto)
                Toast.makeText(this, "Texto copiado al Portapapeles", Toast.LENGTH_SHORT).show()
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