package edu.gwu.tennistalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(val context: Context, val players: List<Player>, val playerClickListener: PlayerClickListener) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    // How many rows (total) do you want the adapter to render?
    override fun getItemCount(): Int {
        return players.size
    }

    // The RecyclerView needs a "fresh" / new row, so we need to:
    // 1. Read in the XML file for the row type
    // 2. Use the new row to build a ViewHolder to return
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // A LayoutInflater is an object that knows how to read & parse an XML file
        val layoutInflater = LayoutInflater.from(parent.context)

        // Read & parse the XML file to create a new row at runtime
        // The 'inflate' function returns a reference to the root layout (the "top" view in the hierarchy) in our newly created row
        val rootLayout: View = layoutInflater.inflate(R.layout.player, parent, false)

        // We can now create a ViewHolder from the root view
        return ViewHolder(rootLayout)
    }

    // The RecyclerView is ready to display a new (or recycled) row on the screen, represented a our ViewHolder.
    // We're given the row position / index that needs to be rendered.
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currPlayer = players[position]
        viewHolder.title.text = currPlayer.title
        viewHolder.source.text = currPlayer.source
        viewHolder.content.text = currPlayer.content


        viewHolder.itemView.setOnClickListener{
            playerClickListener.onPlayerClickListener(currPlayer)
        }
    }

    // A ViewHolder represents the Views that comprise a single row in our list (e.g.
    // our row to display a Tweet contains three TextViews and one ImageView).
    //
    // The "rootLayout" passed into the constructor comes from onCreateViewHolder. From the root layout, we can
    // call findViewById to search through the hierarchy to find the Views we care about in our new row.
    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout) {
        val title: TextView = rootLayout.findViewById(R.id.article_title)
        val source: TextView = rootLayout.findViewById(R.id.article_source)
        val content: TextView = rootLayout.findViewById(R.id.article_content)
        val icon: ImageView = rootLayout.findViewById(R.id.article_icon)
    }
}