package com.example.playlistmaker.presentation.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.viewmodel.PlaylistViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var rvPlaylists: RecyclerView
    private lateinit var emptyContainer: LinearLayout
    private lateinit var playlistAdapter: PlaylistAdapter

    companion object {
        fun newInstance(): PlaylistFragment = PlaylistFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPlaylists = view.findViewById(R.id.rvPlaylists)
        emptyContainer = view.findViewById(R.id.emptyContainer)
        val newPlaylistButton = view.findViewById<Button>(R.id.button_new_playlist)

        rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)

        playlistAdapter = PlaylistAdapter { playlist ->
            val bundle = Bundle().apply {
                putLong("playlistId", playlist.id)
            }
            findNavController().navigate(
                R.id.action_library_fragment_to_playlistInsideFragment,
                bundle
            )
        }

        rvPlaylists.adapter = playlistAdapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.playlistsFlow.collect { playlistList ->
                if (playlistList.isEmpty()) {
                    emptyContainer.visibility = View.VISIBLE
                    rvPlaylists.visibility = View.GONE
                } else {
                    emptyContainer.visibility = View.GONE
                    rvPlaylists.visibility = View.VISIBLE
                    playlistAdapter.setPlaylists(playlistList)
                }
            }
        }

        newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.createPlaylistFragment)
        }
    }
}
