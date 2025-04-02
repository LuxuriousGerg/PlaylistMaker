package com.example.playlistmaker.presentation.ui.library

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.core.widget.doOnTextChanged
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.CreatePlaylistFragment
import com.example.playlistmaker.presentation.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    private val editViewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.title = getString(R.string.edit_playlist)
        binding.createButton.text = getString(R.string.save_playlist)


        val playlist = arguments?.getParcelable<Playlist>("playlist")
        playlist?.let {
            binding.nameEditText.setText(it.name)
            binding.descriptionEditText.setText(it.description)


            if (!it.coverUri.isNullOrEmpty()) {
                val uri = Uri.parse(it.coverUri)
                binding.coverImageView.setImageURI(uri)
                binding.coverImageView.visibility = View.VISIBLE
                binding.plusIcon.visibility = View.GONE
            }


            editViewModel.playlistName = it.name
            editViewModel.playlistDescription = it.description
            editViewModel.coverUri = it.coverUri?.let { str -> Uri.parse(str) }


            editViewModel.playlistId = it.id
        }


        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            editViewModel.playlistName = text.toString()
            binding.createButton.isEnabled = !text.isNullOrEmpty()
            editViewModel.hasUnsavedData = !text.isNullOrEmpty()
        }

        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            editViewModel.playlistDescription = text.toString()
            if (!text.isNullOrEmpty()) {
                editViewModel.hasUnsavedData = true
            }
        }

        binding.coverContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.createButton.setOnClickListener {
            if (binding.nameEditText.text.toString().isNotBlank()) {
                editViewModel.onSavePlaylistClicked()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        editViewModel.playlistCreatedEvent.observe(viewLifecycleOwner) { updatedName ->
            updatedName?.let {
                findNavController().navigateUp()
            }
        }
    }
}
