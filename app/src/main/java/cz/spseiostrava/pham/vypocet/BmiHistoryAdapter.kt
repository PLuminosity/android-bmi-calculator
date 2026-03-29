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
        val strip: View        = itemView.findViewById(R.id.viewBmiStrip)
        val tvDate: TextView   = itemView.findViewById(R.id.tvItemDate)
        val tvBmi: TextView    = itemView.findViewById(R.id.tvItemBmi)
        val tvCategory: TextView = itemView.findViewById(R.id.tvItemCategory)
        val tvHeight: TextView = itemView.findViewById(R.id.tvItemHeight)
        val tvWeight: TextView = itemView.findViewById(R.id.tvItemWeight)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BmiViewHolder =
        BmiViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_bmi_history, parent, false)
        )

    override fun onBindViewHolder(holder: BmiViewHolder, position: Int) {
        val item = getItem(position)
        val ctx  = holder.itemView.context
        val bmi  = item.bmiResult

        // Category & color
        val (categoryStr, colorRes) = when {
            bmi < 18.5f -> Pair(ctx.getString(R.string.underweight),   R.color.bmiUnderweight)
            bmi < 25f   -> Pair(ctx.getString(R.string.normal_weight), R.color.bmiNormal)
            else        -> Pair(ctx.getString(R.string.overweight),    R.color.bmiOverweight)
        }
        val color = ctx.getColor(colorRes)

        holder.strip.setBackgroundColor(color)
        holder.tvBmi.setTextColor(color)
        holder.tvCategory.setTextColor(color)

        holder.tvDate.text     = DATE_FORMAT.format(Date(item.measureDate))
        holder.tvBmi.text      = "%.2f".format(bmi)
        holder.tvCategory.text = categoryStr
        holder.tvHeight.text   = "%.1f cm".format(item.height)
        holder.tvWeight.text   = "%.1f kg".format(item.weight)

        holder.btnDelete.setOnClickListener { onDelete(item) }
    }
}
