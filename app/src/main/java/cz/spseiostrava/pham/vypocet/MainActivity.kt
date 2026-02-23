package cz.spseiostrava.pham.vypocet

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit

class MainActivity : AppParentActivity() {

    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewResult: TextView
    private lateinit var textViewCategory: TextView

    // Konstanty pro klíče, uzavřené v 'companion object' (ekvivalent static final)
    private companion object {
        const val STATE_KEY_RESULT = "result"
        const val STATE_KEY_CATEGORY = "category"
        const val SHARED_PREFERENCES_FILENAME = "general"
        const val SHARED_PREFERENCES_KEY_HEIGHT = "lastHeight"
        const val SHARED_PREFERENCES_KEY_WEIGHT = "lastWeight"
        const val SHARED_PREFERENCES_KEY_RESULT = "lastResult"
        const val SHARED_PREFERENCES_KEY_CATEGORY = "lastCategory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState != null) {
            textViewResult.text = savedInstanceState.getString(STATE_KEY_RESULT)
            textViewCategory.text = savedInstanceState.getString(STATE_KEY_CATEGORY)
        }

        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textViewResult = findViewById(R.id.textViewResult)
        textViewCategory = findViewById(R.id.textViewCategory)

        buttonCalculate.setOnClickListener {
            calculateBMI()
            hideKeyboard(this)
        }

        setBottomMenu(R.id.bottomNavItemBmiCalc)
        setToolbar(getString(R.string.BmiTitle), false)
    }

    private fun calculateBMI() {
        var height = editTextHeight.text.toString().toFloatOrNull()
        val weight = editTextWeight.text.toString().toFloatOrNull()
        if (height == null || weight == null || height <= 54 || weight <= 0) {
            showToast("Please enter valid height and weight")
            return
        }
        height = height / 100
        val bmi = weight / (height * height)
        textViewResult.text = getString(R.string.result_bmi_2f).format(bmi)
        textViewCategory.text = buildString {
            append(getString(R.string.bmi_category))
            append("\n")
            append(bmiCategory(bmi))
        }

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILENAME, MODE_PRIVATE)
        sharedPreferences.edit {
            putString(SHARED_PREFERENCES_KEY_HEIGHT, editTextHeight.text.toString())
            putString(SHARED_PREFERENCES_KEY_WEIGHT, editTextWeight.text.toString())
            putString(SHARED_PREFERENCES_KEY_RESULT, textViewResult.text.toString())
            putString(SHARED_PREFERENCES_KEY_CATEGORY, textViewCategory.text.toString())
        }
    }

    private fun bmiCategory(bmi: Float): String {
        return when {
            bmi < 18.5 -> getString(R.string.underweight)
            bmi < 25 -> getString(R.string.normal_weight)
            bmi > 25 -> getString(R.string.overweight)
            else -> {"Failed to calculate BMI category"}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.optionsMenuItemLastInputs -> {
                val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILENAME, MODE_PRIVATE)
                editTextHeight.setText(sharedPreferences.getString(SHARED_PREFERENCES_KEY_HEIGHT, ""))
                editTextWeight.setText(sharedPreferences.getString(SHARED_PREFERENCES_KEY_WEIGHT, ""))

                showToast("Vloženy poslední vstupy...")
                true
            }
            R.id.optionsMenuItemSettings -> {
                showToast("Nastavení...")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_KEY_RESULT, textViewResult.text.toString())
        outState.putString(STATE_KEY_CATEGORY, textViewCategory.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textViewResult.text = savedInstanceState.getString(STATE_KEY_RESULT)
        textViewCategory.text = savedInstanceState.getString(STATE_KEY_CATEGORY)
    }


}