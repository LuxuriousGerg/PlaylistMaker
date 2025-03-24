package com.example.playlistmaker.presentation.ui.player

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class BottomSheetPlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistAdapter.ViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setPlaylists(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return ViewHolder(view, onPlaylistClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount() = playlists.size

    class ViewHolder(
        itemView: View,
        private val onPlaylistClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
        private val trackCount: TextView = itemView.findViewById(R.id.trackCount)

        fun bind(playlist: Playlist) {
            playlistName.text = playlist.name
            trackCount.text = "${playlist.trackCount} треков"

            if (playlist.coverUri.isNullOrEmpty()) {
                coverImage.setImageResource(R.drawable.playlist_picture)
            } else {
                coverImage.setImageURI(Uri.parse(playlist.coverUri))
            }

            itemView.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }
    }
}
