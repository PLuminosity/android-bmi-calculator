package cz.spseiostrava.pham.vypocet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cz.spseiostrava.pham.vypocet.api.QuoteApiService
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import kotlinx.coroutines.launch

class AboutActivity : AppParentActivity() {

    // Profile views
    private lateinit var tvProfileUsername: TextView
    private lateinit var tvProfileBio: TextView
    private lateinit var tvProfileFirstName: TextView
    private lateinit var tvProfileLastName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileMemberSince: TextView

    // Quote card views
    private lateinit var tvQuoteText: TextView
    private lateinit var tvQuoteAuthor: TextView
    private lateinit var quoteProgress: ProgressBar
    private lateinit var btnRefreshQuote: ImageButton
    private lateinit var btnShareQuote: ImageButton

    private var currentQuote: QuoteApiService.Quote? = null

    private companion object {
        const val KEY_QUOTE_TEXT   = "quote_text"
        const val KEY_QUOTE_AUTHOR = "quote_author"
    }

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
        tvQuoteText          = findViewById(R.id.tvQuoteText)
        tvQuoteAuthor        = findViewById(R.id.tvQuoteAuthor)
        quoteProgress        = findViewById(R.id.quoteProgressBar)
        btnRefreshQuote      = findViewById(R.id.btnRefreshQuote)
        btnShareQuote        = findViewById(R.id.btnShareQuote)

        btnRefreshQuote.setOnClickListener { fetchQuote() }

        btnShareQuote.setOnClickListener {
            val quote = currentQuote ?: return@setOnClickListener
            val shareText = buildString {
                append("\u201C${quote.text}\u201D")
                if (quote.author.isNotBlank()) append("\n\u2014 ${quote.author}")
                append("\n\n${getString(R.string.share_quote_footer)}")
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_quote_subject))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_quote_chooser)))
        }

        loadUserProfile()

        // Restore saved quote instead of hitting the API again after rotation
        val savedText   = savedInstanceState?.getString(KEY_QUOTE_TEXT)
        val savedAuthor = savedInstanceState?.getString(KEY_QUOTE_AUTHOR)
        if (savedText != null) {
            currentQuote    = QuoteApiService.Quote(savedText, savedAuthor ?: "")
            tvQuoteText.text   = savedText
            tvQuoteAuthor.text = savedAuthor ?: ""
            btnShareQuote.visibility = View.VISIBLE
        } else {
            fetchQuote()
        }
    }

    private fun loadUserProfile() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            val user    = db.userDao().getUser(currentUserId)
            val profile = db.profileDao().getProfile(currentUserId)

            if (user != null) {
                tvProfileFirstName.text   = user.firstName
                tvProfileLastName.text    = user.lastName
                tvProfileEmail.text       = user.email
                val sdf = java.text.SimpleDateFormat("dd. MM. yyyy", java.util.Locale.getDefault())
                tvProfileMemberSince.text = getString(
                    R.string.profile_member_since,
                    sdf.format(java.util.Date(user.createdAt))
                )
            }
            if (profile != null) {
                tvProfileUsername.text = profile.username
                tvProfileBio.text      = profile.bio?.takeIf { it.isNotBlank() }
                    ?: getString(R.string.profile_no_bio)
            }
        }
    }

    private fun fetchQuote() {
        setQuoteLoading(true)
        lifecycleScope.launch {
            val quote = QuoteApiService.fetchRandomQuote()
            setQuoteLoading(false)
            if (quote != null) {
                currentQuote = quote
                tvQuoteText.text   = "\u201C${quote.text}\u201D"
                tvQuoteAuthor.text = if (quote.author.isNotBlank()) "\u2014 ${quote.author}" else ""
                btnShareQuote.visibility = View.VISIBLE
            } else {
                tvQuoteText.text   = getString(R.string.quote_fallback)
                tvQuoteAuthor.text = ""
                btnShareQuote.visibility = View.GONE
            }
        }
    }

    private fun setQuoteLoading(loading: Boolean) {
        quoteProgress.visibility  = if (loading) View.VISIBLE  else View.GONE
        tvQuoteText.visibility    = if (loading) View.INVISIBLE else View.VISIBLE
        tvQuoteAuthor.visibility  = if (loading) View.INVISIBLE else View.VISIBLE
        btnRefreshQuote.isEnabled = !loading
        btnShareQuote.isEnabled   = !loading
    }

    // ── Instance state ────────────────────────────────────────────────────────

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the displayed quote so rotation doesn't trigger a new API call
        currentQuote?.let {
            outState.putString(KEY_QUOTE_TEXT,   tvQuoteText.text.toString())
            outState.putString(KEY_QUOTE_AUTHOR, tvQuoteAuthor.text.toString())
        }
    }
}
