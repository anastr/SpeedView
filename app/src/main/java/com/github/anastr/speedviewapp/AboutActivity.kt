package com.github.anastr.speedviewapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        button_github_profile.setOnClickListener { openUrl("https://github.com/anastr") }
        button_linked_in.setOnClickListener { openUrl("https://linkedin.com/in/anas-altair") }
        button_github_project.setOnClickListener { openUrl("https://github.com/anastr/SpeedView") }
    }

    private fun openUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW).apply { this.data = Uri.parse(url) })
}
