package cz.spseiostrava.pham.vypocet

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import cz.spseiostrava.pham.vypocet.database.BmiInfoEntity
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppParentActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var editTextHeight: TextInputEditText
    private lateinit var editTextWeight: TextInputEditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewResult: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var cardResult: MaterialCardView

    private companion object {
        // Keys for onSaveInstanceState
        const val KEY_RESULT      = "result"
        const val KEY_CATEGORY    = "category"
        const val KEY_HEIGHT      = "height"
        const val KEY_WEIGHT      = "weight"
        const val KEY_DATE_YEAR   = "date_year"
        const val KEY_DATE_MONTH  = "date_month"
        const val KEY_DATE_DAY    = "date_day"
        const val KEY_CARD_VISIBLE = "card_visible"
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
        cardResult       = findViewById(R.id.cardResult)

        // Restore saved instance state
        savedInstanceState?.let { state ->
            editTextHeight.setText(state.getString(KEY_HEIGHT))
            editTextWeight.setText(state.getString(KEY_WEIGHT))
            textViewResult.text   = state.getString(KEY_RESULT)
            textViewCategory.text = state.getString(KEY_CATEGORY)
            if (state.getBoolean(KEY_CARD_VISIBLE, false)) {
                cardResult.visibility = View.VISIBLE
            }
            // Restore DatePicker – only if values were saved
            val year  = state.getInt(KEY_DATE_YEAR,  -1)
            val month = state.getInt(KEY_DATE_MONTH, -1)
            val day   = state.getInt(KEY_DATE_DAY,   -1)
            if (year != -1) datePicker.updateDate(year, month, day)
        }

        buttonCalculate.setOnClickListener {
            calculateAndSaveBMI()
            hideKeyboard(this)
        }

        setBottomMenu(R.id.bottomNavItemBmiCalc)
        setToolbar(getString(R.string.BmiTitle), false)
    }

    private fun calculateAndSaveBMI() {
        val heightCm = editTextHeight.text.toString().toFloatOrNull()
        val weightKg = editTextWeight.text.toString().toFloatOrNull()

        if (heightCm == null || weightKg == null || heightCm <= 54 || weightKg <= 0) {
            showToast(getString(R.string.error_invalid_height_weight))
            return
        }

        val heightM = heightCm / 100f
        val bmi     = weightKg / (heightM * heightM)

        textViewResult.text   = "%.2f".format(bmi)
        textViewCategory.text = bmiCategory(bmi)
        cardResult.visibility = View.VISIBLE

        // Color the result by category
        val categoryColor = when {
            bmi < 18.5f -> getColor(R.color.bmiUnderweight)
            bmi < 25f   -> getColor(R.color.bmiNormal)
            else        -> getColor(R.color.bmiOverweight)
        }
        textViewResult.setTextColor(categoryColor)
        textViewCategory.setTextColor(categoryColor)

        val cal = Calendar.getInstance().apply {
            set(datePicker.year, datePicker.month, datePicker.dayOfMonth, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        lifecycleScope.launch {
            AppDatabase.getInstance(this@MainActivity).bmiDao().insertBmiInfo(
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

    // ── Instance state ────────────────────────────────────────────────────────

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_RESULT,       textViewResult.text.toString())
        outState.putString(KEY_CATEGORY,     textViewCategory.text.toString())
        outState.putString(KEY_HEIGHT,       editTextHeight.text.toString())
        outState.putString(KEY_WEIGHT,       editTextWeight.text.toString())
        outState.putInt(KEY_DATE_YEAR,       datePicker.year)
        outState.putInt(KEY_DATE_MONTH,      datePicker.month)
        outState.putInt(KEY_DATE_DAY,        datePicker.dayOfMonth)
        outState.putBoolean(KEY_CARD_VISIBLE, cardResult.visibility == View.VISIBLE)
    }

    // ── Toolbar menu ──────────────────────────────────────────────────────────

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.optionsMenuItemSettings -> { logout(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
