package cz.spseiostrava.pham.vypocet

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryActivity : AppParentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: BmiHistoryAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private companion object {
        const val KEY_SCROLL_STATE = "scroll_state"
    }

    /** Saved scroll position — applied once the first list is submitted. */
    private var pendingScrollState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setBottomMenu(R.id.bottomNavItemHistory)
        setToolbar(getString(R.string.HistoryTitle), true)

        recyclerView = findViewById(R.id.recyclerViewHistory)
        emptyState   = findViewById(R.id.tvNoHistory)

        pendingScrollState = savedInstanceState?.getParcelable(KEY_SCROLL_STATE)

        adapter = BmiHistoryAdapter { item ->
            lifecycleScope.launch {
                AppDatabase.getInstance(this@HistoryActivity).bmiDao().deleteBmiInfo(item)
            }
        }

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        observeHistory()
    }

    private fun observeHistory() {
        lifecycleScope.launch {
            AppDatabase.getInstance(this@HistoryActivity)
                .bmiDao()
                .getBmiInfoForProfile(currentUserId.toInt())
                .collectLatest { items ->
                    adapter.submitList(items) {
                        // Restore scroll position after the list is drawn
                        pendingScrollState?.let {
                            layoutManager.onRestoreInstanceState(it)
                            pendingScrollState = null
                        }
                    }

                    if (items.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        emptyState.visibility   = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        emptyState.visibility   = View.GONE
                    }
                }
        }
    }

    // ── Instance state ────────────────────────────────────────────────────────

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save RecyclerView scroll position
        outState.putParcelable(KEY_SCROLL_STATE, layoutManager.onSaveInstanceState())
    }
}
