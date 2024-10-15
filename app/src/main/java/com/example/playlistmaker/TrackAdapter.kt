package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackAdapter(private val trackList: ArrayList<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
    }

    override fun getItemCount() = trackList.size

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistAndTime: TextView = itemView.findViewById(R.id.artist_and_time)
        private val trackArtwork: ImageView = itemView.findViewById(R.id.album_cover)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistAndTime.text = "${track.artistName} • ${track.trackTime}"

            // Загрузка обложки трека с помощью Glide
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .fitCenter()
                .placeholder(R.drawable.placeholder_image) // Плейсхолдер на случай отсутствия интернета
                .into(trackArtwork)
        }
    }
}
