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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etUsername: EditText
    private lateinit var etBio: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoLogin: TextView

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
        btnRegister       = findViewById(R.id.btnRegister)
        tvGoLogin         = findViewById(R.id.tvGoToLogin)

        btnRegister.setOnClickListener { attemptRegister() }
        tvGoLogin.setOnClickListener { finish() }   // Back to LoginActivity
    }

    private fun attemptRegister() {
        val firstName = etFirstName.text.toString().trim()
        val lastName  = etLastName.text.toString().trim()
        val email     = etEmail.text.toString().trim()
        val password  = etPassword.text.toString()
        val confirm   = etConfirmPassword.text.toString()
        val username  = etUsername.text.toString().trim()
        val bio       = etBio.text.toString().trim()

        // Validate
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
            password.isBlank() || username.isBlank()
        ) {
            showError(getString(R.string.error_fill_all_fields))
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_invalid_email)
            return
        }
        if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_too_short)
            return
        }
        if (password != confirm) {
            etConfirmPassword.error = getString(R.string.error_passwords_no_match)
            return
        }

        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            try {
                db.userDao().createUserWithProfile(
                    firstName = firstName,
                    lastName  = lastName,
                    email     = email,
                    password  = password,
                    username  = username,
                    bio       = bio
                )
                // Auto-login after registration
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    SessionManager.saveSession(this@RegisterActivity, user.userID.toLong())
                    goToMain()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    etEmail.error = getString(R.string.error_email_taken)
                }
            }
        }
    }

    private fun showError(msg: String) {
        runOnUiThread { etFirstName.error = msg }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
