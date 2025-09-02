package com.ParazonApps.ConcretosChile.Perfil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.ParazonApps.ConcretosChile.R
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoFC
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoHormigon
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMetrado
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoMorteros
import com.example.aplicacion_calculadora_concretos.data.DaoResultadoVolumen
import com.example.aplicacion_calculadora_concretos.data.ResultadosDataBase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.Rectangle
import kotlinx.coroutines.CoroutineScope


class EditarResultado : AppCompatActivity() {
    private lateinit var btnCerrar: ImageView
    private lateinit var btnEditar: ImageView
    private lateinit var btnBorrar: ImageView
    private lateinit var btnGuardar: Button
    private lateinit var nombreCargado: EditText
    private lateinit var btnCompartir: Button

    private lateinit var daoResultadoFC: DaoResultadoFC
    private lateinit var daoResultadoVolumen: DaoResultadoVolumen
    private lateinit var daoResultadoHormigon: DaoResultadoHormigon
    private lateinit var daoResultadoMorteros: DaoResultadoMorteros
    private lateinit var daoResultadoMetrado: DaoResultadoMetrado

    //FirebaseAuth//
    private lateinit var firebaseAuth: FirebaseAuth


    @SuppressLint("InflateParams", "QueryPermissionsNeeded")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_editar_resultado)


        daoResultadoFC = ResultadosDataBase.getInstance(this).daoResultadoFC
        daoResultadoVolumen = ResultadosDataBase.getInstance(this).daoResultadoVolumen
        daoResultadoHormigon = ResultadosDataBase.getInstance(this).daoResultadoHormigon
        daoResultadoMorteros = ResultadosDataBase.getInstance(this).daoResultadoMorteros
        daoResultadoMetrado = ResultadosDataBase.getInstance(this).daoResultadoMetrado


        btnCerrar = findViewById(R.id.btnCerrar)
        btnEditar = findViewById(R.id.btnEditar)
        btnBorrar = findViewById(R.id.btnEliminar)
        btnGuardar = findViewById(R.id.btnGuardarCambios)
        nombreCargado = findViewById(R.id.nombreCargado)
        btnCompartir = findViewById(R.id.btnCompartir)

        firebaseAuth = FirebaseAuth.getInstance()

        // Se consiguen los strings de nombre y sección
        val section = intent.getStringExtra("section")
        val nombre = intent.getStringExtra("nombre")

        // Buscamos el container del layout para luego llamar a la sección
        val container = findViewById<LinearLayout>(R.id.container)

        // Diferenciamos el click del usuario mediante "when"
        when (section) {
            "FC" -> {
                // Creamos la variable para buscar la sección correspondiente
                val fcView = layoutInflater.inflate(R.layout.section_fc, null)
                container.addView(fcView)

                // Se gregan las variables para posicionar las variables de la base de datos.
                val sectionCemento = fcView.findViewById<EditText>(R.id.sectionSacosCementoFC)
                val sectionArena = fcView.findViewById<EditText>(R.id.sectionArenaFC)
                val sectionGravilla = fcView.findViewById<EditText>(R.id.sectionGravillaFC)
                val sectionAgua = fcView.findViewById<EditText>(R.id.sectionAguaFC)

                nombreCargado.setText(nombre)
                val nombreBuscar = nombre.toString()
                GlobalScope.launch(Dispatchers.IO){

                    val cementoDBFC = daoResultadoFC.obtenerCementoResultadoFC(nombreBuscar).toString()
                    val arenaDBFC = daoResultadoFC.obtenerArenaResultadoFC(nombreBuscar).toString()
                    val gravillaDBFC = daoResultadoFC.obtenerGravillaResultadoFC(nombreBuscar).toString()
                    val aguaDBFC = daoResultadoFC.obtenerAguaResultadoFC(nombreBuscar).toString()

                    runOnUiThread {
                        sectionCemento.setText(cementoDBFC)
                        sectionArena.setText(arenaDBFC)
                        sectionGravilla.setText(gravillaDBFC)
                        sectionAgua.setText(aguaDBFC)
                    }
                }

                btnBorrar.setOnClickListener {
                    val nombreEliminar = nombreCargado.text.toString()
                    val resultadoDao = ResultadosDataBase.getInstance(this).daoResultadoFC
                    val internet = isConnected(this)
                    GlobalScope.launch(Dispatchers.IO) {
                        val eliminado = resultadoDao.eliminarResultadoFC(nombreEliminar)
                        firebaseAuth = Firebase.auth
                        val user = firebaseAuth.currentUser
                        val userid = user?.uid
                        if (userid != null)
                        {
                            if(internet)
                            {
                                eliminarDocumento(nombreEliminar,"FirebaseFC")
                            }
                        }
                    }
                    mensajeEliminado()
                    volverHistorial()
                }
                btnGuardar.setOnClickListener {
                    val nombreViejo = nombre.toString()
                    var nombreNuevo = nombreCargado.text.toString()

                    if (nombreNuevo.isBlank()) {
                        mensajeVacio()
                    } else if (nombreNuevo == nombreViejo) {
                        mensajeCopia()
                    } else {
                        val internet = isConnected(this)
                        GlobalScope.launch(Dispatchers.IO) {
                            val nombreExistente = daoResultadoFC.obtenerNombreResultadoFC(nombreNuevo)
                            if(nombreExistente == null)
                            {
                                nombreNuevo = cleanDocumentId(nombreNuevo)

                                daoResultadoFC.actualizarNombreResultadoFC(nombreViejo, nombreNuevo)
                                val cemento = daoResultadoFC.obtenerCementoResultadoFC(nombreNuevo)
                                val arena = daoResultadoFC.obtenerArenaResultadoFC(nombreNuevo)
                                val gravilla = daoResultadoFC.obtenerGravillaResultadoFC(nombreNuevo)
                                val agua = daoResultadoFC.obtenerAguaResultadoFC(nombreNuevo)

                                firebaseAuth = Firebase.auth
                                val user = firebaseAuth.currentUser
                                val userid = user?.uid

                                if (userid != null)
                                {
                                    if(internet)
                                    {
                                        insertCollectionFC(nombreNuevo,cemento,arena,gravilla,agua)
                                        eliminarDocumento(nombreViejo,"FirebaseFC")
                                    }
                                }
                                ////
                                mensajeGuardado()
                                volverHistorial()
                            }
                            else
                            {
                                mensajeExiste()
                            }
                        }
                    }
                }

                btnCompartir.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO){
                        val cementoDBFC = daoResultadoFC.obtenerCementoResultadoFC(nombreBuscar).toString()
                        val arenaDBFC = daoResultadoFC.obtenerArenaResultadoFC(nombreBuscar).toString()
                        val gravillaDBFC = daoResultadoFC.obtenerGravillaResultadoFC(nombreBuscar).toString()
                        val aguaDBFC = daoResultadoFC.obtenerAguaResultadoFC(nombreBuscar).toString()

                        val fileName= nombreCargado.text.toString()

                        val document = Document()

                        // crear un FileOutputStream para la ruta deseada
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val filePath = File(downloadsDir, "$fileName.pdf")
                        val fileOutputStream = FileOutputStream(filePath)

                        // Asociar el PdfWriter con el Document y FileOutputStream
                        val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

                        // Crear un estilo para el título con letra negra y centrado
                        val titleStyle = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, BaseColor.BLACK)

                        // Crear una tabla con una columna
                        val table = PdfPTable(1)

                        // Abrir el documento para escribir en el
                        document.open()

                        // Configurar el título y agregarlo a la tabla
                        val titleCell = PdfPCell(Paragraph("$fileName \n\n", titleStyle))
                        titleCell.horizontalAlignment = Element.ALIGN_CENTER
                        titleCell.border = Rectangle.NO_BORDER
                        table.addCell(titleCell)

                        // Agregar los demás elementos a la tabla
                        table.addCell("Sacos de Cemento: $cementoDBFC")
                        table.addCell("Total de Arena: $arenaDBFC M3")
                        table.addCell("Total de Gravilla: $gravillaDBFC M3")
                        table.addCell("Total de Agua: $aguaDBFC L")

                        // Agregar la tabla al documento
                        document.add(table)

                        // Cerrar el documento
                        document.close()

                        // Cerrar PdfWriter
                        pdfWriter.close()

                        // Cerrar FileOutputStream
                        fileOutputStream.close()

                        // Llamar mensaje de aviso
                        mensajePDF()

                        // Lanzar el intent para compartir después de crear el PDF
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@EditarResultado, "${this@EditarResultado.packageName}.provider", filePath))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Verificar si hay aplicaciones disponibles para manejar el intent
                        if (shareIntent.resolveActivity(this@EditarResultado.packageManager) != null) {
                            this@EditarResultado.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
                        } else {
                            // Manejar caso en el que no hay aplicaciones disponibles para manejar el intent
                            // Puedes mostrar un mensaje al usuario
                        }
                    }
                }
            }
            "Volumen"->{
                val volView = layoutInflater.inflate(R.layout.section_volumen, null)
                container.addView(volView)

                val sectionCemento = volView.findViewById<EditText>(R.id.sectionSacosCementoVol)
                val sectionArena = volView.findViewById<EditText>(R.id.sectionArenaVol)
                val sectionGravilla = volView.findViewById<EditText>(R.id.sectionGravillaVol)
                val sectionAgua = volView.findViewById<EditText>(R.id.sectionAguaVol)

                nombreCargado.setText(nombre)
                val nombreBuscar = nombre.toString()
                GlobalScope.launch(Dispatchers.IO){
                    val cementoDBVOL = daoResultadoVolumen.obtenerCementoResultadoVolumen(nombreBuscar).toString()
                    val arenaDBVOL = daoResultadoVolumen.obtenerArenaResultadoVolumen(nombreBuscar).toString()
                    val gravillaDBVOL = daoResultadoVolumen.obtenerGravillaResultadoVolumen(nombreBuscar).toString()
                    val aguaDBVOL = daoResultadoVolumen.obtenerAguaResultadoVolumen(nombreBuscar).toString()

                    runOnUiThread {
                        sectionCemento.setText(cementoDBVOL)
                        sectionArena.setText(arenaDBVOL)
                        sectionGravilla.setText(gravillaDBVOL)
                        sectionAgua.setText(aguaDBVOL)
                    }
                }

                btnBorrar.setOnClickListener {
                    val nombreEliminar = nombreCargado.text.toString()
                    val resultadoDao = ResultadosDataBase.getInstance(this).daoResultadoVolumen
                    val internet = isConnected(this)
                    GlobalScope.launch(Dispatchers.IO){
                        val eliminado = resultadoDao.eliminarResultadoVolumen(nombreEliminar)
                        firebaseAuth = Firebase.auth
                        val user = firebaseAuth.currentUser
                        val userid = user?.uid
                        if (userid != null)
                        {
                            if(internet)
                            {
                                eliminarDocumento(nombreEliminar,"FirebaseVolumen")
                            }
                        }
                    }
                    mensajeEliminado()
                    volverHistorial()
                }
                btnGuardar.setOnClickListener {
                    val nombreViejo = nombre.toString()
                    var nombreNuevo = nombreCargado.text.toString()

                    if (nombreNuevo.isBlank()) {
                        mensajeVacio()
                    } else if (nombreNuevo == nombreViejo) {
                        mensajeCopia()
                    } else {
                        val internet = isConnected(this)
                        GlobalScope.launch(Dispatchers.IO) {
                            val nombreExistente = daoResultadoVolumen.obtenerNombreResultadoVolumen(nombreNuevo)
                            if(nombreExistente == null)
                            {
                                nombreNuevo = cleanDocumentId(nombreNuevo)
                                daoResultadoVolumen.actualizarNombreResultadoVolumen(nombreViejo, nombreNuevo)
                                val cemento = daoResultadoVolumen.obtenerCementoResultadoVolumen(nombreNuevo)
                                val arena = daoResultadoVolumen.obtenerArenaResultadoVolumen(nombreNuevo)
                                val gravilla = daoResultadoVolumen.obtenerGravillaResultadoVolumen(nombreNuevo)
                                val agua = daoResultadoVolumen.obtenerAguaResultadoVolumen(nombreNuevo)

                                firebaseAuth = Firebase.auth
                                val user = firebaseAuth.currentUser
                                val userid = user?.uid
                                if (userid != null)
                                {
                                    if(internet)
                                    {
                                        insertCollectionVolumen(nombreNuevo,cemento,arena,gravilla,agua)
                                        eliminarDocumento(nombreViejo,"FirebaseVolumen")
                                    }
                                }
                                mensajeGuardado()
                                volverHistorial()
                            }
                            else
                            {
                                mensajeExiste()
                            }
                        }
                    }
                }

                btnCompartir.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO){
                        val cementoDBVOL = daoResultadoVolumen.obtenerCementoResultadoVolumen(nombreBuscar).toString()
                        val arenaDBVOL = daoResultadoVolumen.obtenerArenaResultadoVolumen(nombreBuscar).toString()
                        val gravillaDBVOL = daoResultadoVolumen.obtenerGravillaResultadoVolumen(nombreBuscar).toString()
                        val aguaDBVOL = daoResultadoVolumen.obtenerAguaResultadoVolumen(nombreBuscar).toString()

                        val fileName= nombreCargado.text.toString()

                        val document = Document()

                        // crear un FileOutputStream para la ruta deseada
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val filePath = File(downloadsDir, "$fileName.pdf")
                        val fileOutputStream = FileOutputStream(filePath)

                        // Asociar el PdfWriter con el Document y FileOutputStream
                        val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

                        // Crear un estilo para el título con letra negra y centrado
                        val titleStyle = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, BaseColor.BLACK)

                        // Crear una tabla con una columna
                        val table = PdfPTable(1)

                        // Abrir el documento para escribir en el
                        document.open()

                        // Configurar el título y agregarlo a la tabla
                        val titleCell = PdfPCell(Paragraph("$fileName \n\n", titleStyle))
                        titleCell.horizontalAlignment = Element.ALIGN_CENTER
                        titleCell.border = Rectangle.NO_BORDER
                        table.addCell(titleCell)

                        // Agregar los demás elementos a la tabla
                        table.addCell("Sacos de Cemento: $cementoDBVOL")
                        table.addCell("Total de Arena: $arenaDBVOL M3")
                        table.addCell("Total de Gravilla: $gravillaDBVOL M3")
                        table.addCell("Total de Agua: $aguaDBVOL L")

                        // Agregar la tabla al documento
                        document.add(table)

                        // Cerrar el documento
                        document.close()

                        // Cerrar PdfWriter
                        pdfWriter.close()

                        // Cerrar FileOutputStream
                        fileOutputStream.close()

                        // Llamar mensaje de aviso
                        mensajePDF()

                        // Lanzar el intent para compartir después de crear el PDF
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@EditarResultado, "${this@EditarResultado.packageName}.provider", filePath))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Verificar si hay aplicaciones disponibles para manejar el intent
                        if (shareIntent.resolveActivity(this@EditarResultado.packageManager) != null) {
                            this@EditarResultado.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
                        } else {
                            // Manejar caso en el que no hay aplicaciones disponibles para manejar el intent
                            // Puedes mostrar un mensaje al usuario
                        }
                    }
                }
            }
            "Hormigon"->{
                val horView = layoutInflater.inflate(R.layout.section_hormigon, null)
                container.addView(horView)

                val sectionCemento = horView.findViewById<EditText>(R.id.sectionSacosCementoHor)
                val sectionHormigon = horView.findViewById<EditText>(R.id.sectionHormigonHor)
                val sectionAgua = horView.findViewById<EditText>(R.id.sectionAguaHor)

                nombreCargado.setText(nombre)
                val nombreBuscar = nombre.toString()
                GlobalScope.launch(Dispatchers.IO){

                    val cementoDBHOR = daoResultadoHormigon.obtenerCementoResultadoHormigon(nombreBuscar).toString()
                    val hormigonDBHOR = daoResultadoHormigon.obtenerHormigonResultadoHormigon(nombreBuscar).toString()
                    val aguaDBHOR = daoResultadoHormigon.obtenerAguaResultadoHormigon(nombreBuscar).toString()

                    runOnUiThread {
                        sectionCemento.setText(cementoDBHOR)
                        sectionHormigon.setText(hormigonDBHOR)
                        sectionAgua.setText(aguaDBHOR)
                    }
                }

                btnBorrar.setOnClickListener {
                    val nombreEliminar = nombreCargado.text.toString()
                    val resultadoDao = ResultadosDataBase.getInstance(this).daoResultadoHormigon
                    val internet = isConnected(this)
                    GlobalScope.launch(Dispatchers.IO){
                        val eliminado = resultadoDao.eliminarResultadoHormigon(nombreEliminar)
                        firebaseAuth = Firebase.auth
                        val user = firebaseAuth.currentUser
                        val userid = user?.uid
                        if (userid != null)
                        {
                            if(internet)
                            {
                                eliminarDocumento(nombreEliminar,"FirebaseHormigon")
                            }
                        }
                    }
                    mensajeEliminado()
                    volverHistorial()
                }
                btnGuardar.setOnClickListener {
                    val nombreViejo = nombre.toString()
                    var nombreNuevo = nombreCargado.text.toString()

                    if (nombreNuevo.isBlank()) {
                        mensajeVacio()
                    } else if (nombreNuevo == nombreViejo) {
                        mensajeCopia()
                    } else {

                        val internet = isConnected(this)

                        GlobalScope.launch(Dispatchers.IO) {
                            val nombreExistente = daoResultadoHormigon.obtenerNombreResultadoHormigon(nombreNuevo)
                            if(nombreExistente == null)
                            {
                                nombreNuevo = cleanDocumentId(nombreNuevo)
                                daoResultadoHormigon.actualizarNombreResultadoHormigon(nombreViejo, nombreNuevo)
                                val cemento = daoResultadoHormigon.obtenerCementoResultadoHormigon(nombreNuevo)
                                val hormigon = daoResultadoHormigon.obtenerHormigonResultadoHormigon(nombreNuevo)
                                val agua = daoResultadoHormigon.obtenerAguaResultadoHormigon(nombreNuevo)
                                firebaseAuth = Firebase.auth
                                val user = firebaseAuth.currentUser
                                val userid = user?.uid

                                if (userid != null)
                                {
                                    if(internet)
                                    {
                                        insertCollectionHormigon(nombreNuevo,cemento,hormigon,agua)
                                        eliminarDocumento(nombreViejo,"FirebaseHormigon")
                                    }
                                }
                                
                                mensajeGuardado()
                                volverHistorial()
                            }
                            else
                            {
                                mensajeExiste()
                            }
                        }
                    }
                }

                btnCompartir.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO){
                        val cementoDBHOR = daoResultadoHormigon.obtenerCementoResultadoHormigon(nombreBuscar).toString()
                        val hormigonDBHOR = daoResultadoHormigon.obtenerHormigonResultadoHormigon(nombreBuscar).toString()
                        val aguaDBHOR = daoResultadoHormigon.obtenerAguaResultadoHormigon(nombreBuscar).toString()

                        val fileName= nombreCargado.text.toString()

                        val document = Document()

                        // crear un FileOutputStream para la ruta deseada
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val filePath = File(downloadsDir, "$fileName.pdf")
                        val fileOutputStream = FileOutputStream(filePath)

                        // Asociar el PdfWriter con el Document y FileOutputStream
                        val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

                        // Crear un estilo para el título con letra negra y centrado
                        val titleStyle = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, BaseColor.BLACK)
                        // Crear una tabla con una columna
                        val table = PdfPTable(1)

                        // Abrir el documento para escribir en el
                        document.open()

                        // Configurar el título y agregarlo a la tabla
                        val titleCell = PdfPCell(Paragraph("$fileName \n\n", titleStyle))
                        titleCell.horizontalAlignment = Element.ALIGN_CENTER
                        titleCell.border = Rectangle.NO_BORDER
                        table.addCell(titleCell)

                        // Agregar los demás elementos a la tabla
                        table.addCell("Sacos de Cemento: $cementoDBHOR")
                        table.addCell("Total de Hormigón: $hormigonDBHOR M3")
                        table.addCell("Total de Agua: $aguaDBHOR L")

                        // Agregar la tabla al documento
                        document.add(table)

                        // Cerrar el documento
                        document.close()

                        // Cerrar PdfWriter
                        pdfWriter.close()

                        // Cerrar FileOutputStream
                        fileOutputStream.close()

                        // Llamar mensaje de aviso
                        mensajePDF()

                        // Lanzar el intent para compartir después de crear el PDF
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@EditarResultado, "${this@EditarResultado.packageName}.provider", filePath))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Verificar si hay aplicaciones disponibles para manejar el intent
                        if (shareIntent.resolveActivity(this@EditarResultado.packageManager) != null) {
                            this@EditarResultado.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
                        } else {
                            // Manejar caso en el que no hay aplicaciones disponibles para manejar el intent
                            // Puedes mostrar un mensaje al usuario
                        }
                    }
                }
            }
            "Morteros" ->{
                val morView = layoutInflater.inflate(R.layout.section_mortero, null)
                container.addView(morView)

                val sectionCemento = morView.findViewById<EditText>(R.id.sectionSacosCementoMor)
                val sectionArena = morView.findViewById<EditText>(R.id.sectionArenaMor)
                val sectionAgua = morView.findViewById<EditText>(R.id.sectionAguaMor)

                nombreCargado.setText(nombre)
                val nombreBuscar = nombre.toString()
                GlobalScope.launch(Dispatchers.IO) {

                    val cementoDBMOR = daoResultadoMorteros.obtenerCementoResultadoMorteros(nombreBuscar).toString()
                    val arenaDBMOR = daoResultadoMorteros.obtenerArenaResultadoMorteros(nombreBuscar).toString()
                    val aguaDBMOR = daoResultadoMorteros.obtenerAguaResultadoMorteros(nombreBuscar).toString()

                    runOnUiThread {
                        sectionCemento.setText(cementoDBMOR)
                        sectionArena.setText(arenaDBMOR)
                        sectionAgua.setText(aguaDBMOR)
                    }
                }

                btnBorrar.setOnClickListener {
                    val nombreEliminar = nombreCargado.text.toString()
                    val resultadoDao = ResultadosDataBase.getInstance(this).daoResultadoMorteros
                    val internet = isConnected(this)
                    GlobalScope.launch(Dispatchers.IO){
                        val eliminado = resultadoDao.eliminarResultadoMorteros(nombreEliminar)
                        firebaseAuth = Firebase.auth
                        val user = firebaseAuth.currentUser
                        val userid = user?.uid
                        if (userid != null)
                        {
                            if(internet)
                            {
                                eliminarDocumento(nombreEliminar,"FirebaseMorteros")
                            }
                        }
                    }
                    mensajeEliminado()
                    volverHistorial()
                }
                btnGuardar.setOnClickListener {
                    val nombreViejo = nombre.toString()
                    var nombreNuevo = nombreCargado.text.toString()

                    if (nombreNuevo.isBlank()) {
                        mensajeVacio()
                    } else if (nombreNuevo == nombreViejo) {
                        mensajeCopia()
                    } else {
                        val internet = isConnected(this)
                        GlobalScope.launch(Dispatchers.IO) {
                            val nombreExistente = daoResultadoMorteros.obtenerNombreResultadoMorteros(nombreNuevo)
                            if(nombreExistente == null)
                            {
                                nombreNuevo = cleanDocumentId(nombreNuevo)

                                daoResultadoMorteros.actualizarNombreResultadoMorteros(nombreViejo, nombreNuevo)
                                val cemento = daoResultadoMorteros.obtenerCementoResultadoMorteros(nombreNuevo)
                                val arena = daoResultadoMorteros.obtenerArenaResultadoMorteros(nombreNuevo)
                                val agua = daoResultadoMorteros.obtenerAguaResultadoMorteros(nombreNuevo)

                                firebaseAuth = Firebase.auth
                                val user = firebaseAuth.currentUser
                                val userid = user?.uid

                                if (userid != null)
                                {
                                    if(internet)
                                    {
                                        insertCollectionMorteros(nombreNuevo,cemento,arena,agua)
                                        eliminarDocumento(nombreViejo,"FirebaseMorteros")
                                    }
                                }

                                mensajeGuardado()
                                volverHistorial()
                            }
                            else
                            {
                                mensajeExiste()
                            }
                        }
                    }
                }

                btnCompartir.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO){
                        val cementoDBMOR = daoResultadoMorteros.obtenerCementoResultadoMorteros(nombreBuscar).toString()
                        val arenaDBMOR = daoResultadoMorteros.obtenerArenaResultadoMorteros(nombreBuscar).toString()
                        val aguaDBMOR = daoResultadoMorteros.obtenerAguaResultadoMorteros(nombreBuscar).toString()

                        val fileName= nombreCargado.text.toString()

                        val document = Document()

                        // crear un FileOutputStream para la ruta deseada
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val filePath = File(downloadsDir, "$fileName.pdf")
                        val fileOutputStream = FileOutputStream(filePath)

                        // Asociar el PdfWriter con el Document y FileOutputStream
                        val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

                        // Crear un estilo para el título con letra negra y centrado
                        val titleStyle = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, BaseColor.BLACK)

                        // Crear una tabla con una columna
                        val table = PdfPTable(1)

                        // Abrir el documento para escribir en el
                        document.open()

                        // Configurar el título y agregarlo a la tabla
                        val titleCell = PdfPCell(Paragraph("$fileName \n\n", titleStyle))
                        titleCell.horizontalAlignment = Element.ALIGN_CENTER
                        titleCell.border = Rectangle.NO_BORDER
                        table.addCell(titleCell)

                        // Agregar los demás elementos a la tabla
                        table.addCell("Sacos de Cemento: $cementoDBMOR")
                        table.addCell("Total de Arena: $arenaDBMOR M3")
                        table.addCell("Total de Agua: $aguaDBMOR L")

                        // Agregar la tabla al documento
                        document.add(table)

                        // Cerrar el documento
                        document.close()

                        // Cerrar PdfWriter
                        pdfWriter.close()

                        // Cerrar FileOutputStream
                        fileOutputStream.close()

                        // Llamar mensaje de aviso
                        mensajePDF()

                        // Lanzar el intent para compartir después de crear el PDF
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@EditarResultado, "${this@EditarResultado.packageName}.provider", filePath))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Verificar si hay aplicaciones disponibles para manejar el intent
                        if (shareIntent.resolveActivity(this@EditarResultado.packageManager) != null) {
                            this@EditarResultado.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
                        } else {
                            // Manejar caso en el que no hay aplicaciones disponibles para manejar el intent
                            // Puedes mostrar un mensaje al usuario
                        }
                    }
                }
            }
            "Metrado" ->{
                val  metView = layoutInflater.inflate(R.layout.section_metrado, null)
                container.addView(metView)

                val sectionCemento = metView.findViewById<EditText>(R.id.sectionSacosCementoMet)
                val sectionArena = metView.findViewById<EditText>(R.id.sectionArenaMet)
                val sectionGravilla = metView.findViewById<EditText>(R.id.sectionGravillaMet)
                val sectionAgua = metView.findViewById<EditText>(R.id.sectionAguaMet)

                nombreCargado.setText(nombre)
                val nombreBuscar = nombre.toString()
                GlobalScope.launch(Dispatchers.IO){

                    val cementoDBMET = daoResultadoMetrado.obtenerCementoResultadoMetrado(nombreBuscar).toString()
                    val arenaDBMET = daoResultadoMetrado.obtenerArenaResultadoMetrado(nombreBuscar).toString()
                    val gravillaDBMET = daoResultadoMetrado.obtenerGravillaResultadoMetrado(nombreBuscar).toString()
                    val aguaDBMET = daoResultadoMetrado.obtenerAguaResultadoMetrado(nombreBuscar).toString()

                    runOnUiThread {
                        sectionCemento.setText(cementoDBMET)
                        sectionArena.setText(arenaDBMET)
                        sectionGravilla.setText(gravillaDBMET)
                        sectionAgua.setText(aguaDBMET)
                    }
                }

                btnBorrar.setOnClickListener {
                    val nombreEliminar = nombreCargado.text.toString()
                    val resultadoDao = ResultadosDataBase.getInstance(this).daoResultadoMetrado
                    val internet = isConnected(this)
                    GlobalScope.launch(Dispatchers.IO){
                        val eliminado = resultadoDao.eliminarResultadoMetrado(nombreEliminar)
                        firebaseAuth = Firebase.auth
                        val user = firebaseAuth.currentUser
                        val userid = user?.uid
                        if (userid != null)
                        {
                            if(internet)
                            {
                                eliminarDocumento(nombreEliminar,"FirebaseMetrado")
                            }
                        }
                    }
                    mensajeEliminado()
                    volverHistorial()
                }
                btnGuardar.setOnClickListener {
                    val nombreViejo = nombre.toString()
                    var nombreNuevo = nombreCargado.text.toString()

                    if (nombreNuevo.isBlank()) {
                        mensajeVacio()
                    } else if (nombreNuevo == nombreViejo) {
                        mensajeCopia()
                    } else {
                        val internet = isConnected(this)
                        GlobalScope.launch(Dispatchers.IO) {
                            val nombreExistente = daoResultadoMetrado.obtenerNombreResultadoMetrado(nombreNuevo)
                            if(nombreExistente == null)
                            {
                                nombreNuevo = cleanDocumentId(nombreNuevo)

                                daoResultadoMetrado.actualizarNombreResultadoMetrado(nombreViejo, nombreNuevo)
                                val cemento = daoResultadoMetrado.obtenerCementoResultadoMetrado(nombreNuevo)
                                val arena = daoResultadoMetrado.obtenerArenaResultadoMetrado(nombreNuevo)
                                val gravilla = daoResultadoMetrado.obtenerGravillaResultadoMetrado(nombreNuevo)
                                val agua = daoResultadoMetrado.obtenerAguaResultadoMetrado(nombreNuevo)

                                firebaseAuth = Firebase.auth
                                val user = firebaseAuth.currentUser
                                val userid = user?.uid

                                if (userid != null)
                                {
                                    if(internet)
                                    {
                                        insertCollectionMetrado(nombreNuevo,cemento,arena,gravilla,agua)
                                        eliminarDocumento(nombreViejo,"FirebaseMetrado")
                                    }
                                }

                                mensajeGuardado()
                                volverHistorial()
                            }
                            else
                            {
                                mensajeExiste()
                            }
                        }
                    }
                }

                btnCompartir.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO){
                        val cementoDBMET = daoResultadoMetrado.obtenerCementoResultadoMetrado(nombreBuscar).toString()
                        val arenaDBMET = daoResultadoMetrado.obtenerArenaResultadoMetrado(nombreBuscar).toString()
                        val gravillaDBMET = daoResultadoMetrado.obtenerGravillaResultadoMetrado(nombreBuscar).toString()
                        val aguaDBMET = daoResultadoMetrado.obtenerAguaResultadoMetrado(nombreBuscar).toString()

                        val fileName= nombreCargado.text.toString()

                        val document = Document()

                        // crear un FileOutputStream para la ruta deseada
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val filePath = File(downloadsDir, "$fileName.pdf")
                        val fileOutputStream = FileOutputStream(filePath)

                        // Asociar el PdfWriter con el Document y FileOutputStream
                        val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

                        // Crear un estilo para el título con letra negra y centrado
                        val titleStyle = Font(Font.FontFamily.HELVETICA, 25f, Font.BOLD, BaseColor.BLACK)

                        // Crear una tabla con una columna
                        val table = PdfPTable(1)

                        // Abrir el documento para escribir en el
                        document.open()

                        // Configurar el título y agregarlo a la tabla
                        val titleCell = PdfPCell(Paragraph("$fileName \n\n", titleStyle))
                        titleCell.horizontalAlignment = Element.ALIGN_CENTER
                        titleCell.border = Rectangle.NO_BORDER
                        table.addCell(titleCell)

                        // Agregar los demás elementos a la tabla
                        table.addCell("Sacos de Cemento: $cementoDBMET")
                        table.addCell("Total de Arena: $arenaDBMET M3")
                        table.addCell("Total de Gravilla: $gravillaDBMET M3")
                        table.addCell("Total de Agua: $aguaDBMET L")

                        // Agregar la tabla al documento
                        document.add(table)

                        // Cerrar el documento
                        document.close()

                        // Cerrar PdfWriter
                        pdfWriter.close()

                        // Cerrar FileOutputStream
                        fileOutputStream.close()

                        // Llamar mensaje de aviso
                        mensajePDF()

                        // Lanzar el intent para compartir después de crear el PDF
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@EditarResultado, "${this@EditarResultado.packageName}.provider", filePath))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Verificar si hay aplicaciones disponibles para manejar el intent
                        if (shareIntent.resolveActivity(this@EditarResultado.packageManager) != null) {
                            this@EditarResultado.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
                        } else {
                            // Manejar caso en el que no hay aplicaciones disponibles para manejar el intent
                            // Puedes mostrar un mensaje al usuario
                        }
                    }
                }
            }
        }
        btnCerrar.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            finish()
        }
        btnEditar.setOnClickListener {
            nombreCargado.isEnabled=true
            nombreCargado.selectAll()
            nombreCargado.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(nombreCargado, InputMethodManager.SHOW_FORCED)
        }
    }

    private fun volverHistorial() {
        val intent = Intent(this, Historial::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun mensajeEliminado(){
        runOnUiThread {
            Toast.makeText(this@EditarResultado, "Eliminado", Toast.LENGTH_SHORT).show()
        }
    }
    private fun mensajeGuardado(){
        runOnUiThread {
            Toast.makeText(this@EditarResultado, "Guardado", Toast.LENGTH_SHORT).show()
        }
    }
    private fun mensajeVacio(){
        runOnUiThread {
            Toast.makeText(this@EditarResultado, "Campo vacio", Toast.LENGTH_SHORT).show()
        }
    }
    private fun mensajeCopia(){
        runOnUiThread {
            Toast.makeText(this@EditarResultado, "Contienen el mismo nombre", Toast.LENGTH_SHORT).show()
        }
    }
    private fun mensajeExiste(){
        runOnUiThread {
            Toast.makeText(this@EditarResultado, "El nombre ya esta en uso", Toast.LENGTH_SHORT).show()
        }
    }
    private fun mensajePDF(){
        runOnUiThread {
            Toast.makeText(this@EditarResultado, "PDF guardado en descargas.", Toast.LENGTH_SHORT).show()
        }
    }


    //FIREBASE FUNCTIONS//

    private fun cleanDocumentId(name: String): String {
        // Reemplazar caracteres no permitidos con un guion bajo
        //return name.replace(Regex("[^a-zA-Z0-9_]"), "_")
        return name.replace(Regex("[^a-zA-Z0-9()_; -]"), "_")
    }

    //Eliminar Firebase//
    private fun eliminarDocumento(collectionID: String,collectionName: String)
    {
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection(collectionName).document(collectionID)
        documentReference.delete()
    }

    //FC COLLECTION//

    private fun insertCollectionFC(collectionID : String, cemento : Double, arena : Double, gravilla : Double, agua : Double)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_fc = cemento
        val arena_fc = arena
        val gravilla_fc = gravilla
        val agua_fc = agua
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseFC").document(collectionID)

        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    Toast.makeText(this, "El nombre ya existe en firebase", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val fcData = hashMapOf(
                        "agua_fc" to agua_fc,
                        "arena_fc" to arena_fc,
                        "cemento_fc" to cemento_fc,
                        "gravilla_fc" to gravilla_fc,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseFC").document(collectionID).set(fcData).addOnSuccessListener {
                        Toast.makeText(this, "Guardado en Firebase exitoso", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Guardado en Firebase fallido", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //HORMIGON COLLECTION//

    private fun insertCollectionHormigon(CollectionID : String, cemento : Double, hormigon : Double, agua : Double)
    {
        val currentUser = firebaseAuth.currentUser
        val db = FirebaseFirestore.getInstance()

        val cemento_hormigon = cemento
        val hormigon_hormigon = hormigon
        val agua_hormigon = agua
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseHormigon").document(CollectionID)

        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    Toast.makeText(this, "El nombre ya existe en firebase", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val hormigonData = hashMapOf(
                        "cemento_hormigon" to cemento_hormigon,
                        "hormigon_hormigon" to hormigon_hormigon,
                        "agua_hormigon" to agua_hormigon,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseHormigon").document(CollectionID).set(hormigonData).addOnSuccessListener {
                        Toast.makeText(this, "Guardado en Firebase exitoso", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Guardado en Firebase fallido", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //METRADO COLLECTION//

    private fun insertCollectionMetrado(collectionID : String, cemento : Double, arena : Double, gravilla : Double, agua : Double)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_metrado = cemento
        val arena_metrado = arena
        val gravilla_metrado = gravilla
        val agua_metrado = agua
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseMetrado").document(collectionID)

        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    Toast.makeText(this, "El nombre ya existe en firebase", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val MetradoData = hashMapOf(
                        "agua_metrado" to agua_metrado,
                        "arena_metrado" to arena_metrado,
                        "cemento_metrado" to cemento_metrado,
                        "gravilla_metrado" to gravilla_metrado,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseMetrado").document(collectionID).set(MetradoData).addOnSuccessListener {
                        Toast.makeText(this, "Guardado en Firebase exitoso", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Guardado en Firebase fallido", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //VOLUMEN COLLECTION//

    private fun insertCollectionVolumen(collectionID : String, cemento : Double, arena : Double, gravilla : Double, agua : Double)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_volumen = cemento
        val arena_volumen = arena
        val gravilla_volumen = gravilla
        val agua_volumen = agua
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseVolumen").document(collectionID)

        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    Toast.makeText(this, "El Nombre Ya Existe en Firebase", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val VolumenData = hashMapOf(
                        "agua_volumen" to agua_volumen,
                        "arena_volumen" to arena_volumen,
                        "cemento_volumen" to cemento_volumen,
                        "gravilla_volumen" to gravilla_volumen,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseVolumen").document(collectionID).set(VolumenData).addOnSuccessListener {
                        Toast.makeText(this, "Guardado en Firebase Exitoso", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Guardado en Firebase Callido", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //MORTEROS COLLECTION//

    private fun insertCollectionMorteros(collectionID : String, cemento : Double, arena : Double, agua : Double)
    {
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()
        //val collectionID = nombreDato.text.toString()
        val cemento_morteros = cemento
        val arena_morteros = arena
        val agua_morteros = agua
        val user_id = currentUser?.uid

        val documentReference = db.collection("FirebaseMorteros").document(collectionID)

        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    Toast.makeText(this, "El nombre ya existe en firebase", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val MorterosData = hashMapOf(
                        "agua_morteros" to agua_morteros,
                        "arena_morteros" to arena_morteros,
                        "cemento_morteros" to cemento_morteros,
                        "user_id" to user_id
                    )

                    db.collection("FirebaseMorteros").document(collectionID).set(MorterosData).addOnSuccessListener {
                        Toast.makeText(this, "Guardado en Firebase exitoso", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Guardado en Firebase fallido", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }

    }




    //Internet//

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }


}