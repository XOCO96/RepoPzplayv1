package com.xoco96.pzplayv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log.*

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // Evento personalizado para Google Analytics
        val analytics :FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen",bundle)

        //  Configuración
        setup()
    }

    // Funcion que valida que los campos de correo y contraseña no esten vacios para crear un
    // usuario y contraseña
    private fun setup(){
        title = "Autenticación"

        siginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() ){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showalert()
                    }
                }
            }
        }

    }

    // Funcion que muestra una ventana al producirse un error al intentar autenticar
    // las credenciales de un usuario
    private fun showalert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función que inicia el nuevo activity y envía el correo y provedor del nuevo usuario al activity Home
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent: Intent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

}