package es.studium.ficheroskotlin

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var edtNombreFichero: EditText
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnAbrir: MaterialButton
    private lateinit var edtContenido: EditText
    private lateinit var btnBorrar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle System Window Insets for proper layout padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        edtNombreFichero = findViewById(R.id.edtNombreFichero)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnAbrir = findViewById(R.id.btnAbrir)
        edtContenido = findViewById(R.id.edtContenido)
        btnBorrar = findViewById(R.id.btnBorrar)

        // Enable internal vertical scrolling for multiline content box
        edtContenido.movementMethod = ScrollingMovementMethod.getInstance()

        // Button listeners
        btnGuardar.setOnClickListener {
            guardarFichero()
        }

        btnAbrir.setOnClickListener {
            abrirFichero()
        }

        btnBorrar.setOnClickListener {
            confirmarBorrado()
        }
    }

    private fun guardarFichero() {
        val nombre = edtNombreFichero.text.toString().trim()
        val contenido = edtContenido.text.toString()

        if (nombre.isEmpty()) {
            Toast.makeText(this, R.string.msg_ingresa_nombre, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val file = File(filesDir, nombre)
            file.writeText(contenido)
            Toast.makeText(this, getString(R.string.msg_guardado_ok), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.msg_error_guardar, e.localizedMessage ?: e.message),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun abrirFichero() {
        val nombre = edtNombreFichero.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, R.string.msg_ingresa_nombre, Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(filesDir, nombre)
        if (!file.exists()) {
            Toast.makeText(this, R.string.msg_fichero_no_existe, Toast.LENGTH_SHORT).show()
            return
        }

        val currentText = edtContenido.text.toString()
        if (currentText.isEmpty()) {
            // Content text box is empty, load directly
            cargarFichero(file)
        } else {
            // Content text box is not empty, ask confirmation to replace or not
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_titulo_confirmar)
                .setMessage(R.string.dialog_mensaje_sustituir)
                .setPositiveButton(R.string.dialog_btn_sustituir) { _, _ ->
                    cargarFichero(file)
                }
                .setNegativeButton(R.string.dialog_btn_cancelar, null)
                .show()
        }
    }

    private fun cargarFichero(file: File) {
        try {
            val contenido = file.readText()
            edtContenido.setText(contenido)
            edtContenido.setSelection(edtContenido.text.length)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.msg_error_abrir, e.localizedMessage ?: e.message),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun confirmarBorrado() {
        val currentText = edtContenido.text.toString()
        if (currentText.isEmpty()) {
            Toast.makeText(this, R.string.msg_ya_vacio, Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_titulo_borrar)
                .setMessage(R.string.dialog_mensaje_borrar)
                .setPositiveButton(R.string.dialog_btn_borrar) { _, _ ->
                    edtContenido.setText("")
                    Toast.makeText(this, R.string.msg_borrado_ok, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.dialog_btn_cancelar, null)
                .show()
        }
    }
}