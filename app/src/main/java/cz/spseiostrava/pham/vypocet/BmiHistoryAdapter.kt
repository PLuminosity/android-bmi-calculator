package cz.spseiostrava.pham.vypocet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.spseiostrava.pham.vypocet.database.BmiInfoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BmiHistoryAdapter(
    private val onDelete: (BmiInfoEntity) -> Unit
) : ListAdapter<BmiInfoEntity, BmiHistoryAdapter.BmiViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd. MM. yyyy", Locale.getDefault())

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BmiInfoEntity>() {
            override fun areItemsTheSame(a: BmiInfoEntity, b: BmiInfoEntity) =
                a.bmiInfoID == b.bmiInfoID

            override fun areContentsTheSame(a: BmiInfoEntity, b: BmiInfoEntity) = a == b
        }
    }

    inner class BmiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView     = itemView.findViewById(R.id.tvItemDate)
        val tvHeight: TextView   = itemView.findViewById(R.id.tvItemHeight)
        val tvWeight: TextView   = itemView.findViewById(R.id.tvItemWeight)
        val tvBmi: TextView      = itemView.findViewById(R.id.tvItemBmi)
        val tvCategory: TextView = itemView.findViewById(R.id.tvItemCategory)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BmiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bmi_history, parent, false)
        return BmiViewHolder(view)
    }

    override fun onBindViewHolder(holder: BmiViewHolder, position: Int) {
        val item = getItem(position)
        val ctx  = holder.itemView.context

        holder.tvDate.text     = DATE_FORMAT.format(Date(item.measureDate))
        holder.tvHeight.text   = ctx.getString(R.string.history_height_fmt, item.height)
        holder.tvWeight.text   = ctx.getString(R.string.history_weight_fmt, item.weight)
        holder.tvBmi.text      = ctx.getString(R.string.history_bmi_fmt, item.bmiResult)
        holder.tvCategory.text = bmiCategory(ctx, item.bmiResult)

        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    private fun bmiCategory(ctx: android.content.Context, bmi: Float): String = when {
        bmi < 18.5f -> ctx.getString(R.string.underweight)
        bmi < 25f   -> ctx.getString(R.string.normal_weight)
        else        -> ctx.getString(R.string.overweight)
    }
}
