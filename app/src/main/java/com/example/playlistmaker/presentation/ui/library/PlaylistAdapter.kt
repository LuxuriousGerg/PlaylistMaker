package com.example.playlistmaker.presentation.ui.library

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setPlaylists(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
        private val trackCount: TextView = itemView.findViewById(R.id.trackCount)

        fun bind(playlist: Playlist) {
            playlistName.text = playlist.name
            trackCount.text = "${playlist.trackCount} треков"

            // Если нет обложки - показываем заглушку
            if (playlist.coverUri.isNullOrEmpty()) {
                coverImage.setImageResource(R.drawable.playlist_picture)
            } else {
                coverImage.setImageURI(Uri.parse(playlist.coverUri))
            }
        }
    }
}
