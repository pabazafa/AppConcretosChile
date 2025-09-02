// CaptchaDialog.kt
package com.ParazonApps.ConcretosChile.Registro

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ParazonApps.ConcretosChile.R

class CaptchaDialog(context: Context, private val onCaptchaVerified: (Boolean) -> Unit) : Dialog(context) {

    private lateinit var tvCaptcha: TextView
    private lateinit var edtCaptchaInput: EditText
    private lateinit var btnVerifyCaptcha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_captcha_dialog)

        tvCaptcha = findViewById(R.id.tvCaptcha)
        edtCaptchaInput = findViewById(R.id.edtCaptchaInput)
        btnVerifyCaptcha = findViewById(R.id.btnVerifyCaptcha)

        val actualCaptcha = generateCaptcha() // Cambia esto con tu lógica de generación de captcha
        tvCaptcha.text = actualCaptcha

        btnVerifyCaptcha.setOnClickListener {
            val enteredCaptcha = edtCaptchaInput.text.toString()
            val isValidCaptcha = validateCaptcha(enteredCaptcha, actualCaptcha)
            onCaptchaVerified(isValidCaptcha)
            dismiss()
        }
    }

    private fun generateCaptcha(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val length = 6
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    private fun validateCaptcha(enteredCaptcha: String, actualCaptcha: String): Boolean {
        return enteredCaptcha.equals(actualCaptcha, ignoreCase = false)
    }
}
