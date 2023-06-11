package com.github.anastr.speedviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.anastr.speedviewapp.RecyclerActivity.RVAdapter.MyViewHolder
import com.github.anastr.speedviewlib.Speedometer
import java.util.Random

class RecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val speeds: MutableList<Int> = ArrayList()
        for (i in 0..99) speeds.add(Random().nextInt(99) + 1)
        recyclerView.adapter = RVAdapter(speeds)
    }

    class RVAdapter internal constructor(private val speeds: List<Int>) :
        RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
            val v =
                LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view, viewGroup, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // set speed at 0 without animation (to start from this position).
            holder.speedometer.setSpeedAt(0f)
            holder.speedometer.speedTo(speeds[position].toFloat())
        }

        override fun getItemCount(): Int {
            return speeds.size
        }

        class MyViewHolder(itemView: View) : ViewHolder(itemView) {
            var speedometer: Speedometer

            init {
                speedometer = itemView.findViewById(R.id.speedometer)
            }
        }
    }
}