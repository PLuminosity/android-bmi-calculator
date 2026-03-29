package cz.spseiostrava.pham.vypocet

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etBio: TextInputEditText

    private companion object {
        // Passwords are intentionally excluded from saved state
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME  = "last_name"
        const val KEY_EMAIL      = "email"
        const val KEY_USERNAME   = "username"
        const val KEY_BIO        = "bio"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etFirstName       = findViewById(R.id.etRegFirstName)
        etLastName        = findViewById(R.id.etRegLastName)
        etEmail           = findViewById(R.id.etRegEmail)
        etPassword        = findViewById(R.id.etRegPassword)
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword)
        etUsername        = findViewById(R.id.etRegUsername)
        etBio             = findViewById(R.id.etRegBio)

        // Restore non-sensitive fields after rotation
        savedInstanceState?.let {
            etFirstName.setText(it.getString(KEY_FIRST_NAME))
            etLastName.setText(it.getString(KEY_LAST_NAME))
            etEmail.setText(it.getString(KEY_EMAIL))
            etUsername.setText(it.getString(KEY_USERNAME))
            etBio.setText(it.getString(KEY_BIO))
            // Passwords intentionally not restored
        }

        findViewById<android.widget.Button>(R.id.btnRegister).setOnClickListener { attemptRegister() }
        findViewById<TextView>(R.id.tvGoToLogin).setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val firstName = etFirstName.text.toString().trim()
        val lastName  = etLastName.text.toString().trim()
        val email     = etEmail.text.toString().trim()
        val password  = etPassword.text.toString()
        val confirm   = etConfirmPassword.text.toString()
        val username  = etUsername.text.toString().trim()
        val bio       = etBio.text.toString().trim()

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
            password.isBlank() || username.isBlank()) {
            etFirstName.error = getString(R.string.error_fill_all_fields); return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_invalid_email); return
        }
        if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_too_short); return
        }
        if (password != confirm) {
            etConfirmPassword.error = getString(R.string.error_passwords_no_match); return
        }

        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            try {
                db.userDao().createUserWithProfile(firstName, lastName, email, password, username, bio)
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    SessionManager.saveSession(this@RegisterActivity, user.userID.toLong())
                    goToMain()
                }
            } catch (e: Exception) {
                runOnUiThread { etEmail.error = getString(R.string.error_email_taken) }
            }
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
        outState.putString(KEY_FIRST_NAME, etFirstName.text.toString())
        outState.putString(KEY_LAST_NAME,  etLastName.text.toString())
        outState.putString(KEY_EMAIL,      etEmail.text.toString())
        outState.putString(KEY_USERNAME,   etUsername.text.toString())
        outState.putString(KEY_BIO,        etBio.text.toString())
        // Passwords intentionally NOT saved
    }
}
