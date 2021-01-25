package com.xoco96.pzplayv1

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
    }

    //Función que muestra en pantalla el correo y provedor mediante el cual
    // el usuario se ha registrado
    private fun setup(email: String, provider: String){
        title = "Inicio"

        emailTextView.text = email
        providerTextView.text = provider

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}