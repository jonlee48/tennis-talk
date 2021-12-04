package edu.gwu.tennistalk

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import org.jetbrains.anko.doAsync

class MatchActivity : AppCompatActivity(), MatchClickListener {

    private lateinit var recyclerView: RecyclerView


    override fun onMatchClickListener(data: Match) {
        // open up activity with match details
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)
        setTitle(R.string.matches_title)
        updateResults()
    }

    fun updateResults() {
        //make API call
        doAsync {
            val tennisManager = TennisManager()

            // make call to news API
            val apiKey = getString(R.string.tennis_api_key)

            val matches: List<Match> = tennisManager.retrieveMatches(apiKey, "2021-11-10")

            runOnUiThread {
                if (matches.isNotEmpty()) {
                    recyclerView = findViewById(R.id.ranking_recycler_viewer)

                    // Sets scrolling direction to vertical
                    recyclerView.layoutManager = LinearLayoutManager(this@MatchActivity)

                    val adapter =
                        MatchAdapter(this@MatchActivity, matches, this@MatchActivity)
                    recyclerView.adapter = adapter
                    Log.d("RankingActivity", "Updating recycler view")
                } else {
                    val toast = Toast.makeText(
                        this@MatchActivity,
                        "No more results found.",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
            }
        }
    }
}