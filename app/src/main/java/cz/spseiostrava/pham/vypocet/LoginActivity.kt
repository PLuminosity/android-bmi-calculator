package cz.spseiostrava.pham.vypocet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in skip to MainActivity
        if (SessionManager.isLoggedIn(this)) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)

        etEmail      = findViewById(R.id.etLoginEmail)
        etPassword   = findViewById(R.id.etLoginPassword)
        btnLogin     = findViewById(R.id.btnLogin)
        tvGoRegister = findViewById(R.id.tvGoToRegister)

        btnLogin.setOnClickListener { attemptLogin() }
        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            showError(getString(R.string.error_fill_all_fields))
            return
        }

        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            val user = db.userDao().getUserByEmail(email)
            if (user == null) {
                showError(getString(R.string.error_invalid_credentials))
                return@launch
            }

            val passwordMatch = BCrypt.checkpw(password, user.passwordHash)
            if (!passwordMatch) {
                showError(getString(R.string.error_invalid_credentials))
                return@launch
            }

            // Success – persist session and open the app
            SessionManager.saveSession(this@LoginActivity, user.userID.toLong())
            goToMain()
        }
    }

    private fun showError(msg: String) {
        // Run on main thread (we might be inside a coroutine)
        runOnUiThread {
            etPassword.error = null
            etEmail.error = msg
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
