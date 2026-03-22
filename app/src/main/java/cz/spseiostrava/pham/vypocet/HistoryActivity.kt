package cz.spseiostrava.pham.vypocet

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.spseiostrava.pham.vypocet.database.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryActivity : AppParentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoHistory: TextView
    private lateinit var adapter: BmiHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setBottomMenu(R.id.bottomNavItemHistory)
        setToolbar(getString(R.string.HistoryTitle), true)

        recyclerView = findViewById(R.id.recyclerViewHistory)
        tvNoHistory  = findViewById(R.id.tvNoHistory)

        adapter = BmiHistoryAdapter { item ->
            // Delete on swipe/button tap
            lifecycleScope.launch {
                AppDatabase.getInstance(this@HistoryActivity)
                    .bmiDao()
                    .deleteBmiInfo(item)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        observeHistory()
    }

    private fun observeHistory() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            db.bmiDao()
                .getBmiInfoForProfile(currentUserId.toInt())
                .collectLatest { items ->
                    adapter.submitList(items)
                    if (items.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        tvNoHistory.visibility  = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        tvNoHistory.visibility  = View.GONE
                    }
                }
        }
    }
}
