package com.example.playlistmaker.presentation.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.imageview.ShapeableImageView

class PlaylistTracksAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistTracksAdapter.TrackViewHolder>() {

    private val tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun setTracks(newList: List<Track>) {
        val diffCallback = TrackDiffCallback(tracks, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        tracks.clear()
        tracks.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val albumCover: ShapeableImageView = itemView.findViewById(R.id.album_cover)
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistAndTime: TextView = itemView.findViewById(R.id.artist_and_time)
        private val arrowIcon: ImageView = itemView.findViewById(R.id.arrow_icon)

        fun bind(track: Track) {
            trackName.text = track.trackName
            val timeStr = track.trackTimeMillis.toTrackTimeString()
            artistAndTime.text = "${track.artistName} • $timeStr"

            if (track.artworkUrl100.isNullOrEmpty()) {
                albumCover.setImageResource(R.drawable.placeholder_image)
            } else {
                Glide.with(albumCover.context)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.placeholder_image)
                    .into(albumCover)
            }

            arrowIcon.visibility = View.VISIBLE

            itemView.setOnClickListener { onTrackClick(track) }
            itemView.setOnLongClickListener {
                onTrackLongClick(track)
                true
            }
        }
    }
}

private fun Long.toTrackTimeString(): String {
    val minutes = this / 60000
    val seconds = (this / 1000) % 60
    return "$minutes:%02d".format(seconds)
}

class TrackDiffCallback(
    private val oldList: List<Track>,
    private val newList: List<Track>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].trackId == newList[newItemPosition].trackId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
