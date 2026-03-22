package cz.spseiostrava.pham.vypocet

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

open class AppParentActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toolbar: Toolbar

    /** Convenience: current logged-in user's ID from the session. */
    protected val currentUserId: Long
        get() = SessionManager.userId(this)

    protected fun setToolbar(title: String, showUp: Boolean) {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    }

    /** Hides the soft keyboard. */
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun setBottomMenu(selectedItemId: Int) {
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = selectedItemId

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomNavItemBmiCalc -> {
                    if (selectedItemId != R.id.bottomNavItemBmiCalc)
                        startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.bottomNavItemAbout -> {
                    if (selectedItemId != R.id.bottomNavItemAbout)
                        startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.bottomNavItemHistory -> {
                    if (selectedItemId != R.id.bottomNavItemHistory)
                        startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    /** Log out the current user and navigate back to LoginActivity. */
    protected fun logout() {
        SessionManager.clear(this)
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
