package com.example.playlistmaker.presentation.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.presentation.ui.search.TrackAdapter
import com.example.playlistmaker.presentation.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

    private val viewModel by viewModel<FavoritesViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyPlaceholder: View
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        emptyPlaceholder = view.findViewById(R.id.emptyPlaceholder)

        adapter = TrackAdapter(arrayListOf()).apply {
            setOnTrackClickListener { track ->
                // Открываем PlayerActivity при нажатии
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Подписываемся на Flow со списком избранных треков
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteTracks.collect { favorites ->
                if (favorites.isEmpty()) {
                    emptyPlaceholder.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyPlaceholder.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.updateTracks(favorites)
                }
            }
        }
        // Загружаем список избранных треков
        viewModel.loadFavorites()
    }
}
