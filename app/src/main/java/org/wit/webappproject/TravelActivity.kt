package org.wit.webappproject

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import timber.log.Timber.Forest.i
import timber.log.Timber.i

class TravelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        Timber.plant(Timber.DebugTree())
        i("Travel Activity started..")

        val grettingButton = findViewById<Button>(R.id.greetingButton)
        val textView = findViewById<TextView>(R.id.textView1)

        grettingButton.setOnClickListener {
            textView.text = getString(R.string.greeting_text)
        }

    }
}
