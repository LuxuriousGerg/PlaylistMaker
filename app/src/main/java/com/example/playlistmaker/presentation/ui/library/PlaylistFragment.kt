package com.example.playlistmaker.presentation.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var rvPlaylists: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: PlaylistAdapter

    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
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
        tvEmpty = view.findViewById(R.id.tvEmpty)
        val newPlaylistButton = view.findViewById<Button>(R.id.button_new_playlist)

        // Настраиваем RecyclerView
        rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PlaylistAdapter()
        rvPlaylists.adapter = adapter

        // Подписываемся на Flow из ViewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.playlistsFlow.collect { playlistList ->
                if (playlistList.isEmpty()) {
                    // Пустой список → заглушка
                    rvPlaylists.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    // Есть плейлисты → показываем список
                    rvPlaylists.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    adapter.setPlaylists(playlistList)
                }
            }
        }

        // Переход к фрагменту создания плейлиста
        newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.createPlaylistFragment)
        }
    }
}
