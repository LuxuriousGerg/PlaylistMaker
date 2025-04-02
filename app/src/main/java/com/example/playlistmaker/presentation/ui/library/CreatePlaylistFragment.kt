package com.example.playlistmaker.presentation

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import org.koin.androidx.viewmodel.ext.android.viewModel


open class CreatePlaylistFragment : Fragment() {

    protected var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    protected val createViewModel by viewModel<CreatePlaylistViewModel>()

    protected val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.coverImageView.setImageURI(it)
            binding.coverImageView.visibility = View.VISIBLE
            binding.plusIcon.visibility = View.GONE

            createViewModel.coverUri = it
            createViewModel.hasUnsavedData = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.new_playlist)

        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            createViewModel.playlistName = text.toString()
            binding.createButton.isEnabled = !text.isNullOrEmpty()
            createViewModel.hasUnsavedData = !text.isNullOrEmpty()
        }

        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            createViewModel.playlistDescription = text.toString()
            if (!text.isNullOrEmpty()) {
                createViewModel.hasUnsavedData = true
            }
        }

        binding.coverContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.createButton.setOnClickListener {
            createViewModel.onCreatePlaylistClicked()
        }

        binding.toolbar.setNavigationOnClickListener {
            handleBackPress()
        }

        createViewModel.playlistCreatedEvent.observe(viewLifecycleOwner) { playlistName ->
            playlistName?.let {
                Toast.makeText(requireContext(), "Плейлист $it создан", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
        )
    }

    protected fun handleBackPress() {
        if (createViewModel.hasUnsavedData &&
            (createViewModel.playlistName.isNotBlank() ||
                    createViewModel.playlistDescription.isNotBlank() ||
                    createViewModel.coverUri != null)
        ) {
            showDiscardDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showDiscardDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
