package com.github.anastr.speedviewapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewapp.lineargauge.ImageLinearGaugeActivity
import com.github.anastr.speedviewapp.lineargauge.ProgressiveGaugeActivity

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val screens = arrayOf(
        "1. Speed View" to SpeedViewActivity::class.java,
        "2. Deluxe Speed View" to DeluxeSpeedActivity::class.java,
        "3. Awesome Speedometer View" to AwesomeSpeedometerActivity::class.java,
        "4. Ray Speedometer View" to RayActivity::class.java,
        "5. Pointer Speedometer" to PointerActivity::class.java,
        "6. Tube Speedometer" to TubeSpeedometerActivity::class.java,
        "7. Image Speedometer" to ImageSpeedometerActivity::class.java,
        "1.. Progressive Gauge" to ProgressiveGaugeActivity::class.java,
        "2.. Image Linear Gauge" to ImageLinearGaugeActivity::class.java,
        "Work With Indicator" to WorkWithIndicatorActivity::class.java,
        "Work With Marks" to MarksActivity::class.java,
        "Work With Note" to WorkWithNoteActivity::class.java,
        "Create Speedometer Programmatically" to CreateProgrammatically::class.java,
        "Work With Start and End Degree" to StartEndDegreeActivity::class.java,
        "Work With Modes" to WorkWithModesActivity::class.java,
        "Speed Text Position" to SpeedTextPositionActivity::class.java,
        "Work With Ticks" to TickActivity::class.java,
        "SpeedView with Recycler" to RecyclerActivity::class.java,
        "Sections" to SectionActivity::class.java,
        "Fulcrum" to FulcrumActivity::class.java,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.list)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, screens.map { it.first }
        )
        listView.adapter = adapter
        listView.onItemClickListener = this
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
        val intent = Intent(this, screens[position].second)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about_item -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}