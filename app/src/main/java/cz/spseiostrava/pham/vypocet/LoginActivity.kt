package cz.spseiostrava.pham.vypocet

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvGoRegister: TextView

    private companion object {
        const val KEY_EMAIL = "email"  // Password intentionally never saved for security
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionManager.isLoggedIn(this)) { goToMain(); return }

        setContentView(R.layout.activity_login)

        etEmail      = findViewById(R.id.etLoginEmail)
        etPassword   = findViewById(R.id.etLoginPassword)
        tvGoRegister = findViewById(R.id.tvGoToRegister)

        // Restore email only (password cleared for security)
        savedInstanceState?.getString(KEY_EMAIL)?.let { etEmail.setText(it) }

        findViewById<android.widget.Button>(R.id.btnLogin).setOnClickListener { attemptLogin() }
        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            etEmail.error = getString(R.string.error_fill_all_fields); return
        }

        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            val user = db.userDao().getUserByEmail(email)
            if (user == null || !BCrypt.checkpw(password, user.passwordHash)) {
                runOnUiThread { etEmail.error = getString(R.string.error_invalid_credentials) }
                return@launch
            }
            SessionManager.saveSession(this@LoginActivity, user.userID.toLong())
            goToMain()
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    // ── Instance state ────────────────────────────────────────────────────────

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_EMAIL, etEmail.text.toString())
        // Password is NOT saved intentionally
    }
}
