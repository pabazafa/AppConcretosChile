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

class VolumenRectangulo : AppCompatActivity() {

    private lateinit var altoRect: EditText
    private lateinit var anchoRect: EditText
    private lateinit var largoRect: EditText
    private lateinit var btnCalcularRect: Button
    private lateinit var resultadoRectangulo: EditText
    private lateinit var btnCopiar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volumen_rectangulo)

        val btnConfigurar = findViewById<ImageView>(R.id.config)
        btnConfigurar.setOnClickListener{
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

        altoRect = findViewById(R.id.AltoRectIngresado)
        anchoRect = findViewById(R.id.AnchoRectIngresado)
        largoRect = findViewById(R.id.LargoRectIngresado)
        btnCalcularRect = findViewById(R.id.btnCalcularRect)
        resultadoRectangulo = findViewById(R.id.RectResultado)
        btnCopiar = findViewById(R.id.btnCopiarR)

        btnCalcularRect.setOnClickListener{
            val altoRectValue = altoRect.text.toString()
            val anchoRectValue = anchoRect.text.toString()
            val largoRectValue = largoRect.text.toString()

            if (altoRectValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleAlto = altoRectValue.toDouble()
            if (doubleAlto == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if (anchoRectValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleAncho = anchoRectValue.toDouble()
            if (doubleAncho == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            if (largoRectValue.isEmpty()){
                mensajeVacio()
                return@setOnClickListener
            }
            val doubleLargo = largoRectValue.toDouble()
            if(doubleLargo == 0.0){
                mensajeMayor()
                return@setOnClickListener
            }

            val resultado = doubleAlto * doubleAncho * doubleLargo
            val decimal = DecimalFormat("#.##")
            val resultadoFinal = decimal.format(resultado)
            resultadoRectangulo.setText(resultadoFinal)
        }
        btnCopiar.setOnClickListener {
            val copiarTexto = resultadoRectangulo.text.toString()
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