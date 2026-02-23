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

    protected fun setToolbar(title: String, showUp: Boolean) {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    }

    /**
     * Skryje softwarovou klávesnici.
     * Zdroj: https://rmirabelle.medium.com/close-hide-the-soft-keyboard-in-android-db1da22b09d2
     */
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun setBottomMenu(selectedItemId: Int) {
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = selectedItemId

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomNavItemBmiCalc -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.bottomNavItemAbout -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.bottomNavItemHistory -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
