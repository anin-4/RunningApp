package com.example.runningapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runningapp.R
import com.example.runningapp.database.RunningAppEntity
import com.example.runningapp.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunFragmentAdapter:RecyclerView.Adapter<RunFragmentAdapter.RunFragmentViewHolder>() {

    var items= listOf<RunningAppEntity>()
    set(value){
        field=value
        notifyDataSetChanged()
    }

    class RunFragmentViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunFragmentViewHolder {
        return RunFragmentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_run,
                parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: RunFragmentViewHolder, position: Int) {
        val run = items[position]
        holder.itemView.apply {
            Glide.with(this).load(run.image).into(findViewById(R.id.ivRunImage))

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            findViewById<TextView>(R.id.tvDate)?.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.avgSpeedRunKMH}km/h"
            findViewById<TextView>(R.id.tvAverageSpeed)?.text = avgSpeed

            val distanceInKm = "${run.distanceCoveredM / 1000f}km"
            findViewById<TextView>(R.id.tvDistance)?.text = distanceInKm

            findViewById<TextView>(R.id.tvTime)?.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned = "${run.caloriesBurnt}kcal"
            findViewById<TextView>(R.id.tvCalories)?.text = caloriesBurned

        }
    }


    override fun getItemCount() = items.size

}