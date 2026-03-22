package cz.spseiostrava.pham.vypocet

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import kotlinx.coroutines.launch

class AboutActivity : AppParentActivity() {

    private lateinit var tvProfileUsername: TextView
    private lateinit var tvProfileBio: TextView
    private lateinit var tvProfileFirstName: TextView
    private lateinit var tvProfileLastName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileMemberSince: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setBottomMenu(R.id.bottomNavItemAbout)
        setToolbar(getString(R.string.AboutTitle), true)

        tvProfileUsername    = findViewById(R.id.tvProfileUsername)
        tvProfileBio         = findViewById(R.id.tvProfileBio)
        tvProfileFirstName   = findViewById(R.id.tvProfileFirstName)
        tvProfileLastName    = findViewById(R.id.tvProfileLastName)
        tvProfileEmail       = findViewById(R.id.tvProfileEmail)
        tvProfileMemberSince = findViewById(R.id.tvProfileMemberSince)

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            val user    = db.userDao().getUser(currentUserId)
            val profile = db.profileDao().getProfile(currentUserId)

            if (user != null) {
                tvProfileFirstName.text   = getString(R.string.profile_first_name, user.firstName)
                tvProfileLastName.text    = getString(R.string.profile_last_name, user.lastName)
                tvProfileEmail.text       = getString(R.string.profile_email, user.email)
                val sdf = java.text.SimpleDateFormat("dd. MM. yyyy", java.util.Locale.getDefault())
                tvProfileMemberSince.text = getString(
                    R.string.profile_member_since,
                    sdf.format(java.util.Date(user.createdAt))
                )
            }
            if (profile != null) {
                tvProfileUsername.text = getString(R.string.profile_username, profile.username)
                tvProfileBio.text      = profile.bio?.takeIf { it.isNotBlank() }
                    ?: getString(R.string.profile_no_bio)
            }
        }
    }
}
