package edu.gwu.tennistalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MatchAdapter(val context: Context, val matches: List<Match>, val matchClickListener: MatchClickListener) : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    // How many rows (total) do you want the adapter to render?
    override fun getItemCount(): Int {
        return matches.size
    }

    // The RecyclerView needs a "fresh" / new row, so we need to:
    // 1. Read in the XML file for the row type
    // 2. Use the new row to build a ViewHolder to return
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // A LayoutInflater is an object that knows how to read & parse an XML file
        val layoutInflater = LayoutInflater.from(parent.context)

        // Read & parse the XML file to create a new row at runtime
        // The 'inflate' function returns a reference to the root layout (the "top" view in the hierarchy) in our newly created row
        val rootLayout: View = layoutInflater.inflate(R.layout.match_card, parent, false)

        // We can now create a ViewHolder from the root view
        return ViewHolder(rootLayout)
    }

    // The RecyclerView is ready to display a new (or recycled) row on the screen, represented a our ViewHolder.
    // We're given the row position / index that needs to be rendered.
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currMatch = matches[position]
        viewHolder.tournament.text = currMatch.tournament
        viewHolder.round.text = currMatch.round_name
        viewHolder.home_player.text = currMatch.home_player
        viewHolder.away_player.text = currMatch.away_player
        viewHolder.status.text = currMatch.status
        viewHolder.home1.text = currMatch.home_set1
        viewHolder.home2.text = currMatch.home_set2
        viewHolder.home3.text = currMatch.home_set3
        viewHolder.home4.text = currMatch.home_set4
        viewHolder.home5.text = currMatch.home_set5
        viewHolder.away1.text = currMatch.away_set1
        viewHolder.away2.text = currMatch.away_set2
        viewHolder.away3.text = currMatch.away_set3
        viewHolder.away4.text = currMatch.away_set4
        viewHolder.away5.text = currMatch.away_set5


        // bold the name of the winner
        // bold sets won

        viewHolder.itemView.setOnClickListener{
            matchClickListener.onMatchClickListener(currMatch)
        }
    }

    // A ViewHolder represents the Views that comprise a single row in our list (e.g.
    // our row to display a Tweet contains three TextViews and one ImageView).
    //
    // The "rootLayout" passed into the constructor comes from onCreateViewHolder. From the root layout, we can
    // call findViewById to search through the hierarchy to find the Views we care about in our new row.
    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout) {
        val tournament: TextView = rootLayout.findViewById(R.id.tournament_title)
        val round: TextView = rootLayout.findViewById(R.id.round)
        val home_player: TextView = rootLayout.findViewById(R.id.home_name)
        val away_player: TextView = rootLayout.findViewById(R.id.away_name)
        val status: TextView = rootLayout.findViewById(R.id.match_status)
        val home1: TextView = rootLayout.findViewById(R.id.home2)
        val home2: TextView = rootLayout.findViewById(R.id.home3)
        val home3: TextView = rootLayout.findViewById(R.id.home4)
        val home4: TextView = rootLayout.findViewById(R.id.home5)
        val home5: TextView = rootLayout.findViewById(R.id.home5)
        val away1: TextView = rootLayout.findViewById(R.id.away2)
        val away2: TextView = rootLayout.findViewById(R.id.away3)
        val away3: TextView = rootLayout.findViewById(R.id.away4)
        val away4: TextView = rootLayout.findViewById(R.id.away5)
        val away5: TextView = rootLayout.findViewById(R.id.away5)

    }
}