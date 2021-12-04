package edu.gwu.tennistalk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MenuActivity : AppCompatActivity() {

    private lateinit var matches: Button
    private lateinit var ranking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tells Android which layout file should be used for this screen.
        setContentView(R.layout.activity_menu)

        Log.d("MenuActivity", "onCreate called!")

        ranking = findViewById(R.id.ranking_button)
        matches = findViewById(R.id.matches_button)

        ranking.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        matches.setOnClickListener {
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }
    }

}
