package com.xoco96.pzplayv1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_log.*

class LogActivity : AppCompatActivity() {

    // id enviado como respuesta por el login por autenticacion de google
    private val GOOGLE_SIGN_IN = 100

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
        // comprobar el estado de la sesion
        sesion()
    }

    // ir al segundo activity si la sension no fue cerrada anteriormente por el usuario
    private fun sesion(){
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider =prefs.getString("provider",null)

        if (email != null && provider != null){
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    // Funcion que valida que los campos de correo y contraseña no esten vacios para crear un
    // usuario y contraseña
    private fun setup(){
        title = "Autenticación"

        // Acción del boton -REGISTRAR-
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

        //Acción del boton -ACCEDER-
        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() ){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showalert()
                    }
                }
            }
        }

        // Acción del boton de acceso por Google
        googlebutton.setOnClickListener {
            //configuracion de login por defecto con google
            val googleconfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()     //se solicita el email del usuario
                .build()

            // creacion del cliente de autenticacion
            val googleclient = GoogleSignIn.getClient(this, googleconfig)
            googleclient.signOut()      // cierre de sesion para cualquier cuenta google que pueda antes estar logeada en el boton Google
            // Muestra la pantalla de autenticacion de Google
            startActivityForResult(googleclient.signInIntent, GOOGLE_SIGN_IN)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {         // la respuesta del activity corresponde con la de la autenticacion con google
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)          //recuperacion de la respuesta del activity

            try {
                val account = task.getResult(ApiException::class.java)    // recuperacion de la cuenta autenticada
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null )    // obtención de la credencial de la cuenta
                FirebaseAuth.getInstance()
                    .signInWithCredential(credential)           // Autenticacion en Firebase. La credencial obtenida de -account-
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        } else {
                            showalert()
                        }
                    }
                }
            } catch (e: ApiException){
                showalert()
            }
        }
    }

}