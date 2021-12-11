package edu.gwu.tennistalk

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import org.jetbrains.anko.doAsync

class RankingActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, PlayerClickListener {

    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private val tours  = arrayOf(
        "ATP",
        "WTA",
    )

    private var tourIndex = 1


    override fun onPlayerClickListener(data: Player) {
        // open up news about player on tennis.com
        val name = data.name.replace(" ", "+")
        val link = "https://www.tennis.com/search?search=&q=$name"

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        setTitle(R.string.ranking_title)

        progressBar = findViewById(R.id.progressBar3)

        // Setup spinner
        spinner = findViewById(R.id.spinner)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tours)
        spinner.setAdapter(spinnerAdapter)

        // Shared Preferences for saving/restoring category
        val preferences: SharedPreferences = getSharedPreferences("tennis-talk", Context.MODE_PRIVATE)
        val savedCategory = preferences.getString("CATEGORY","")

        if (savedCategory.isNullOrBlank()) {
            spinner.setSelection(1)
        } else {
            spinner.setSelection(savedCategory.toInt())
        }
        spinner.onItemSelectedListener = this

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val preferences: SharedPreferences = getSharedPreferences("tennis-talk", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("CATEGORY", p2.toString())
        editor.apply()

        tourIndex = p2

        updateResults()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    fun updateResults() {
        progressBar.visibility = View.VISIBLE

        //make API call
        doAsync {
            val tennisManager = TennisManager()

            // make call to news API
            val apiKey = getString(R.string.tennis_api_key)

            val players: List<Player> = tennisManager.retrievePlayers(apiKey, tours[tourIndex]) //generateFakePlayers()
            //val players: List<Player> = generateFakePlayers()


            runOnUiThread {
                progressBar.visibility = View.INVISIBLE

                if (players.isNotEmpty()) {
                    recyclerView = findViewById(R.id.ranking_recycler_viewer)

                    // Sets scrolling direction to vertical
                    recyclerView.layoutManager = LinearLayoutManager(this@RankingActivity)

                    val adapter =
                        PlayerAdapter(this@RankingActivity, players, this@RankingActivity)
                    recyclerView.adapter = adapter
                    Log.d("RankingActivity", "Updating recycler view")
                } else {
                    val toast = Toast.makeText(
                        this@RankingActivity,
                        "No more results found.",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
            }
        }
    }
}