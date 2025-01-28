package com.example.playlistmaker.presentation.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.viewmodel.LibraryViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonBack = view.findViewById<ImageButton>(R.id.back_button)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        // Назад
        buttonBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Адаптер с двумя фрагментами
        val fragments = listOf(
            FavoritesFragment(),
            PlaylistFragment()
        )
        val adapter = LibraryPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        // Привязываем вкладки к ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorites_tab_title)
                1 -> tab.text = getString(R.string.playlists_tab_title)
            }
        }.attach()

        // Задаём цвет текста вкладок (неактивных и активной)
        val normalColor = ContextCompat.getColor(requireContext(), R.color.buttonText2)
        // Если хотим, чтобы активная вкладка тоже имела тот же цвет, используем тот же normalColor:
        val selectedColor = normalColor

        tabLayout.setTabTextColors(normalColor, selectedColor)
    }
}
