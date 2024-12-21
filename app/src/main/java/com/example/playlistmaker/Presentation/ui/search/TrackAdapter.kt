package com.example.playlistmaker.Presentation.ui.search

import android.util.Log
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
import com.example.playlistmaker.data.dto.Track

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
            Log.d("TrackClick", "Track clicked: ${track.trackName}, Artist: ${track.artistName}, Duration: ${track.formatTrackTime(track.trackTimeMillis)}, Artwork: ${track.artworkUrl100}")
            onTrackClickListener?.invoke(track)
        }
    }

    override fun getItemCount() = trackList.size

    fun updateTracks(newTracks: List<Track>) {
        // Фильтруем треки с пустым названием или временем, равным нулю
        val filteredTracks = newTracks.filter { track ->
            // Добавляем проверку на null
            val isTrackNameValid = track.trackName?.isNotEmpty() == true
            val isTrackTimeValid = track.trackTimeMillis > 0

            if (!isTrackNameValid) {
                Log.e("TrackAdapter", "Track name is null or empty for track: $track")
            }
            if (!isTrackTimeValid) {
                Log.e("TrackAdapter", "Track time is zero or negative for track: $track")
            }

            isTrackNameValid && isTrackTimeValid
        }

        trackList.clear()
        trackList.addAll(filteredTracks)
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
            // Проверка на null перед присвоением значений
            trackName.text = track.trackName ?: "Неизвестный трек"
            artistAndTime.text = "${track.artistName ?: "Неизвестный артист"} • ${track.formatTrackTime(track.trackTimeMillis)}"


            // Загрузка обложки трека с помощью Glide
            Glide.with(itemView)
                .load(track.getCoverArtwork())
                .apply(RequestOptions().transform(RoundedCorners(3)))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(trackArtwork)

            // Устанавливаем фокус на текстовые поля для включения marquee
            trackName.isSelected = true
            artistAndTime.isSelected = true
        }
    }
}
