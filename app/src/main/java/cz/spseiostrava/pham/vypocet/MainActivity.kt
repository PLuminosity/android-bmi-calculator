package cz.spseiostrava.pham.vypocet

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import cz.spseiostrava.pham.vypocet.database.BmiInfoEntity
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppParentActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewResult: TextView
    private lateinit var textViewCategory: TextView

    private companion object {
        const val STATE_KEY_RESULT   = "result"
        const val STATE_KEY_CATEGORY = "category"
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

        datePicker       = findViewById(R.id.datePickerMeasure)
        editTextHeight   = findViewById(R.id.editTextHeight)
        editTextWeight   = findViewById(R.id.editTextWeight)
        buttonCalculate  = findViewById(R.id.buttonCalculate)
        textViewResult   = findViewById(R.id.textViewResult)
        textViewCategory = findViewById(R.id.textViewCategory)

        if (savedInstanceState != null) {
            textViewResult.text   = savedInstanceState.getString(STATE_KEY_RESULT)
            textViewCategory.text = savedInstanceState.getString(STATE_KEY_CATEGORY)
        }

        buttonCalculate.setOnClickListener {
            calculateAndSaveBMI()
            hideKeyboard(this)
        }

        setBottomMenu(R.id.bottomNavItemBmiCalc)
        setToolbar(getString(R.string.BmiTitle), false)
    }

    private fun calculateAndSaveBMI() {
        var heightCm = editTextHeight.text.toString().toFloatOrNull()
        val weightKg = editTextWeight.text.toString().toFloatOrNull()

        if (heightCm == null || weightKg == null || heightCm <= 54 || weightKg <= 0) {
            showToast(getString(R.string.error_invalid_height_weight))
            return
        }

        val heightM = heightCm / 100f
        val bmi     = weightKg / (heightM * heightM)

        textViewResult.text = getString(R.string.result_bmi_2f).format(bmi)
        textViewCategory.text = buildString {
            append(getString(R.string.bmi_category))
            append("\n")
            append(bmiCategory(bmi))
        }

        // Convert DatePicker selection to epoch millis
        val cal = Calendar.getInstance().apply {
            set(datePicker.year, datePicker.month, datePicker.dayOfMonth, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            db.bmiDao().insertBmiInfo(
                BmiInfoEntity(
                    profileID   = currentUserId.toInt(),
                    measureDate = cal.timeInMillis,
                    height      = heightCm,
                    weight      = weightKg,
                    bmiResult   = bmi
                )
            )
            showToast(getString(R.string.bmi_saved))
        }
    }

    private fun bmiCategory(bmi: Float): String = when {
        bmi < 18.5f -> getString(R.string.underweight)
        bmi < 25f   -> getString(R.string.normal_weight)
        else        -> getString(R.string.overweight)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.optionsMenuItemSettings -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_KEY_RESULT,   textViewResult.text.toString())
        outState.putString(STATE_KEY_CATEGORY, textViewCategory.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textViewResult.text   = savedInstanceState.getString(STATE_KEY_RESULT)
        textViewCategory.text = savedInstanceState.getString(STATE_KEY_CATEGORY)
    }
}
