package com.xoco96.pzplayv1

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Configuración: se extraen los valores recibidos por el activity Log
        // y se envían a la funcion setup
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email?: "",provider?:"")

        // Guardado de datos (inicio de sesion automatico si el usuario no cierra la secion
        // de forma manual e inicia la aplicacion nuevamente)
        val prefs : SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    //Función que muestra en pantalla el correo y provedor mediante el cual
    // el usuario se ha registrado
    private fun setup(email: String, provider: String){
        title = "Inicio"

        emailTextView.text = email
        providerTextView.text = provider

        logoutButton.setOnClickListener {
            //borrado de datos
            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}