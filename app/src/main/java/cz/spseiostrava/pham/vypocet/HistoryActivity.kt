package cz.spseiostrava.pham.vypocet

import android.os.Bundle
import android.view.View
import android.widget.TextView

class HistoryActivity : AppParentActivity() {

    private companion object {
        const val SHARED_PREFERENCES_FILENAME = "general"
        const val SHARED_PREFERENCES_KEY_HEIGHT = "lastHeight"
        const val SHARED_PREFERENCES_KEY_WEIGHT = "lastWeight"
        const val SHARED_PREFERENCES_KEY_RESULT = "lastResult"
        const val SHARED_PREFERENCES_KEY_CATEGORY = "lastCategory"
    }

    private lateinit var tvHistoryHeight: TextView
    private lateinit var tvHistoryWeight: TextView
    private lateinit var tvHistoryResult: TextView
    private lateinit var tvHistoryCategory: TextView
    private lateinit var tvNoHistory: TextView
    private lateinit var historyLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setBottomMenu(R.id.bottomNavItemHistory)
        setToolbar(getString(R.string.HistoryTitle), true)

        tvHistoryHeight = findViewById(R.id.tvHistoryHeight)
        tvHistoryWeight = findViewById(R.id.tvHistoryWeight)
        tvHistoryResult = findViewById(R.id.tvHistoryResult)
        tvHistoryCategory = findViewById(R.id.tvHistoryCategory)
        tvNoHistory = findViewById(R.id.tvNoHistory)
        historyLayout = findViewById(R.id.historyLayout)

        loadHistory()
    }

    private fun loadHistory() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILENAME, MODE_PRIVATE)

        val height = sharedPreferences.getString(SHARED_PREFERENCES_KEY_HEIGHT, null)
        val weight = sharedPreferences.getString(SHARED_PREFERENCES_KEY_WEIGHT, null)
        val result = sharedPreferences.getString(SHARED_PREFERENCES_KEY_RESULT, null)
        val category = sharedPreferences.getString(SHARED_PREFERENCES_KEY_CATEGORY, null)

        // Zkontrolujeme, zda jsou data k dispozici
        if (height != null && weight != null && result != null && category != null) {
            historyLayout.visibility = View.VISIBLE
            tvNoHistory.visibility = View.GONE

            tvHistoryHeight.text = "Height: $height cm"
            tvHistoryWeight.text = "Weight: $weight kg"
            tvHistoryResult.text = result
            tvHistoryCategory.text = category
        } else {
            historyLayout.visibility = View.GONE
            tvNoHistory.visibility = View.VISIBLE
        }
    }
}
