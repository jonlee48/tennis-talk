package edu.gwu.tennistalk

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
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

    private val tours  = arrayOf(
        "ATP",
        "WTA",
    )

    private var tourIndex = 1


    override fun onPlayerClickListener(data: Player) {
        // goto player activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        setTitle(R.string.ranking_title)


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
        //make API call
        doAsync {
            val tennisManager = TennisManager()

            // make call to news API
            val apiKey = getString(R.string.tennis_api_key)

            val players: List<Player> = tennisManager.retrievePlayers(apiKey, tours[tourIndex]) //generateFakePlayers()
            //val players: List<Player> = generateFakePlayers()


            runOnUiThread {
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

    fun generateFakePlayers(): List<Player> {
        return listOf(
            Player(
                name = "N. Djokovic",
                ranking = 1,
                movement = "",
                points =10940,
                country = "Serbia",
                id = 89304
            ),
            Player(
                name = "D. Medvedev",
                ranking = 2,
                movement = "",
                points = 7640,
                country = "Russia",
                id = 86928
            ),
            Player(
                name = "A. Zverev",
                ranking = 3,
                movement = "",
                points = 6540,
                country = "Germany",
                id = 262500
            ),
            Player(
                name = "S. Tsitsipas",
                ranking = 4,
                movement = "",
                points = 6540,
                country = "Germany",
                id = 86064
            ),
            Player(
                name = "A. Rublev",
                ranking = 5,
                movement = "",
                points = 4950,
                country = "Russia",
                id = 981036
            )
        )
    }
}