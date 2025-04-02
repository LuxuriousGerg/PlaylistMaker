package com.example.playlistmaker.presentation.ui.library

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.viewmodel.PlaylistInsideViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max

class PlaylistInsideFragment : Fragment(R.layout.fragment_playlist_inside) {

    private val viewModel by viewModel<PlaylistInsideViewModel>()
    private lateinit var tracksAdapter: PlaylistTracksAdapter
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetTracksBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var overlay: View
    private var playlistId: Long = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        val shareButton = view.findViewById<ImageButton>(R.id.share_button)
        val menuButton = view.findViewById<ImageButton>(R.id.menu_button)
        val coverImage = view.findViewById<ShapeableImageView>(R.id.playlist_cover)
        val titleText = view.findViewById<TextView>(R.id.playlist_title)
        val descText = view.findViewById<TextView>(R.id.playlist_description)
        val infoText = view.findViewById<TextView>(R.id.playlist_tracks_info)
        val menuSheet = view.findViewById<LinearLayout>(R.id.playlists_bottom_sheet)
        val menuCoverImage = view.findViewById<ImageView>(R.id.menuCoverImage)
        val menuPlaylistName = view.findViewById<TextView>(R.id.menuPlaylistName)
        val menuPlaylistTrackCount = view.findViewById<TextView>(R.id.menuPlaylistTrackCount)
        val emptyMessage = view.findViewById<TextView>(R.id.emptyMessage)
        overlay = view.findViewById(R.id.overlay)
        val topContainer = view.findViewById<ConstraintLayout>(R.id.topContainer)

        topContainer.post {
            BottomSheetBehavior.from(menuSheet).peekHeight = topContainer.height
        }
        menuSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        menuSheet.requestLayout()
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuSheet).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
            isFitToContents = false
            halfExpandedRatio = 0.5f
        }
        val bottomSheetTracks = view.findViewById<FrameLayout>(R.id.bottom_sheet_tracks)
        bottomSheetTracksBehavior = BottomSheetBehavior.from(bottomSheetTracks).apply {
            isHideable = false
        }
        topContainer.post {
            bottomSheetTracksBehavior.expandedOffset = topContainer.height
        }
        val recycler = view.findViewById<RecyclerView>(R.id.playlist_tracks_recycler_view)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        tracksAdapter = PlaylistTracksAdapter(
            onTrackClick = { track -> openPlayer(track) },
            onTrackLongClick = { showDeleteTrackDialog(it) }
        )
        recycler.adapter = tracksAdapter

        backButton.setOnClickListener { findNavController().navigateUp() }
        shareButton.setOnClickListener { sharePlaylist() }
        menuButton.setOnClickListener {
            menuSheet.visibility = View.VISIBLE
            overlay.visibility = View.VISIBLE
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        overlay.setOnClickListener { menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN }

        val shareItem = view.findViewById<TextView>(R.id.menu_item_share)
        val editItem = view.findViewById<TextView>(R.id.menu_item_edit)
        val deleteItem = view.findViewById<TextView>(R.id.menu_item_delete)

        shareItem.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }
        editItem.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val uiModel = viewModel.playlistUIFlow.value ?: return@setOnClickListener
            val domainPlaylist = Playlist(
                id = uiModel.id,
                name = uiModel.name,
                description = uiModel.description,
                coverUri = uiModel.coverUri,
                trackCount = uiModel.trackCount
            )
            val bundle = Bundle().apply { putParcelable("playlist", domainPlaylist) }
            findNavController().navigate(R.id.action_playlistInsideFragment_to_editPlaylistFragment, bundle)
        }
        deleteItem.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.playlistUIFlow.collect { p ->
                p?.let {
                    titleText.text = it.name
                    descText.text = it.description
                    infoText.text = getString(R.string.playlist_info, it.totalMinutes, it.trackCount)
                    if (!it.coverUri.isNullOrEmpty()) {
                        coverImage.setImageURI(Uri.parse(it.coverUri))
                        menuCoverImage.setImageURI(Uri.parse(it.coverUri))
                    } else {
                        coverImage.setImageResource(R.drawable.placeholder_image)
                        menuCoverImage.setImageResource(R.drawable.placeholder_image)
                    }
                    menuPlaylistName.text = it.name
                    menuPlaylistTrackCount.text = "${it.trackCount} треков"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tracksFlow.collect { tracks ->
                tracksAdapter.setTracks(tracks)
                emptyMessage.visibility = if (tracks.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        playlistId = arguments?.getLong("playlistId") ?: -1
        if (playlistId != -1L) viewModel.loadPlaylist(playlistId)
        shareButton.post {
            val coords = IntArray(2)
            shareButton.getLocationOnScreen(coords)
            val topY = coords[1]
            val margin = (8 * resources.displayMetrics.density).toInt()
            val screenH = resources.displayMetrics.heightPixels
            bottomSheetTracksBehavior.peekHeight = max(0, screenH - (topY - margin))
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(requireContext(), com.example.playlistmaker.presentation.ui.player.PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogCustom)
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setNegativeButton("Отмена") { d, _ -> d.dismiss() }
            .setPositiveButton("Удалить") { d, _ ->
                viewModel.deleteTrackFromPlaylist(playlistId, track.trackId)
                d.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val playlistName = viewModel.playlistUIFlow.value?.name ?: "Плейлист"
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogCustom)
            .setMessage("Хотите удалить плейлист «$playlistName»?")
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
                overlay.visibility = View.GONE
            }
            .setPositiveButton("ДА") { dialog, _ ->
                dialog.dismiss()
                viewModel.deletePlaylist(playlistId) {
                    findNavController().navigateUp()
                }
            }
            .show()
    }


    private fun sharePlaylist() {
        overlay.visibility = View.GONE
        val p = viewModel.playlistUIFlow.value ?: return
        val list = viewModel.tracksFlow.value
        if (list.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "В этом плейлисте нет треков для шаринга", Toast.LENGTH_SHORT).show()
            return
        }
        val sb = buildString {
            appendLine(p.name)
            if (p.description.isNotEmpty()) appendLine(p.description)
            appendLine("[${list.size}] треков")
            list.forEachIndexed { i, track ->
                appendLine("${i + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis.toTrackTimeString()})")
            }
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb)
            putExtra(Intent.EXTRA_TITLE, getString(R.string.share_item))
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_item)))
    }

}

private fun Long.toTrackTimeString(): String {
    val minutes = this / 60000
    val seconds = (this / 1000) % 60
    return "$minutes:%02d".format(seconds)
}
