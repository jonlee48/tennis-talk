package edu.gwu.tennistalk

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.internal.format
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class MatchActivity : AppCompatActivity(), MatchClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var selectDate: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendar: Calendar
    private lateinit var textView: TextView
    private lateinit var noResults: TextView
    private lateinit var progressBar: ProgressBar

    var year = 0
    var month = 0
    var day = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)
        setTitle(R.string.matches_title)

        calendar = Calendar.getInstance()

        textView = findViewById(R.id.match_text)
        noResults = findViewById(R.id.no_results)
        selectDate = findViewById(R.id.select_date)
        progressBar = findViewById(R.id.progressBar2)

        selectDate.setOnClickListener {
            // use date user last entered if date has changed (otherwise today's date is used)
            if (year != 0 || month != 0 || day != 0) {
                calendar[Calendar.DAY_OF_MONTH] = day
                calendar[Calendar.MONTH] = month
                calendar[Calendar.YEAR] = year
            }
            // Kotlin DatePicker code adapted from source: https://www.rrtutors.com/tutorials/kotlin-date-picker-dialog
            val datePickerDialog = DatePickerDialog(this, this, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }


        // Use today's date
        val date = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val formatedDate = formatter.format(date)

        Log.d("RankingActivity", "Set date to $formatedDate")
        val title = getString(R.string.matches_results, formatedDate)
        textView.setText(title)

        noResults.setText("")

        updateResults(formatedDate)
    }

    fun updateResults(date: String) {
        progressBar.visibility = View.VISIBLE

        //make API call
        doAsync {
            val tennisManager = TennisManager()

            // make call to news API
            val apiKey = getString(R.string.tennis_api_key)

            val matches: List<Match> = tennisManager.retrieveMatches(apiKey, date)

            runOnUiThread {
                progressBar.visibility = View.INVISIBLE
                Log.d("RankingActivity", "Updating recycler view")
                recyclerView = findViewById(R.id.ranking_recycler_viewer)

                // Sets scrolling direction to vertical
                recyclerView.layoutManager = LinearLayoutManager(this@MatchActivity)

                val adapter =
                    MatchAdapter(this@MatchActivity, matches, this@MatchActivity)
                recyclerView.adapter = adapter

                if (matches.isEmpty()) {
                    noResults.setText(R.string.no_results)

                    /*val toast = Toast.makeText(
                        this@MatchActivity,
                        "No results found.",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                    */
                }
                else {
                    noResults.setText("")
                }
            }
        }
    }

    override fun onMatchClickListener(data: Match) {
        // open up activity with match details
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        year = p1
        month = p2
        day = p3
        val monthString: String = (month+1).toString().padStart(2,'0')
        val dayString: String = day.toString().padStart(2,'0')
        val date: String = "$year-${monthString}-$dayString"
        Log.d("RankingActivity", "Set date to $date")

        val title = getString(R.string.matches_results, date)
        textView.setText(title)



        updateResults(date)
    }
}