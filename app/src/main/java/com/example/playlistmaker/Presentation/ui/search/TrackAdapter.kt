package com.example.playlistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(private val trackList: ArrayList<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var onTrackClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onTrackClickListener?.invoke(track)
        }
    }

    override fun getItemCount() = trackList.size

    fun updateTracks(newTracks: List<Track>) {
        trackList.clear()
        trackList.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun setOnTrackClickListener(listener: (Track) -> Unit) {
        onTrackClickListener = listener
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistAndTime: TextView = itemView.findViewById(R.id.artist_and_time)
        private val trackArtwork: ImageView = itemView.findViewById(R.id.album_cover)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistAndTime.text = "${track.artistName} • ${track.formatTrackTime(track.trackTimeMillis)}"

            Glide.with(itemView)
                .load(track.getCoverArtwork())
                .apply(RequestOptions().transform(RoundedCorners(8)))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(trackArtwork)

            trackName.isSelected = true
            artistAndTime.isSelected = true
        }
    }
}
