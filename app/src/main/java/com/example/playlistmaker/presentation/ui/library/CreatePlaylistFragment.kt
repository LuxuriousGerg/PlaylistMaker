package com.example.playlistmaker.presentation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import androidx.activity.result.contract.ActivityResultContracts
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.plusIcon.setImageURI(it)
            viewModel.coverUri = it
            viewModel.hasUnsavedData = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        // Поле «Название»
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.playlistName = text.toString()
            binding.createButton.isEnabled = !text.isNullOrEmpty()
            viewModel.hasUnsavedData = !text.isNullOrEmpty()
        }

        // Поле «Описание»
        binding.descriptionEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.playlistDescription = text.toString()
            if (!text.isNullOrEmpty()) {
                viewModel.hasUnsavedData = true
            }
        }

        // Выбор обложки
        binding.coverContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Кнопка «Создать»
        binding.createButton.setOnClickListener {
            viewModel.onCreatePlaylistClicked()
        }

        // Обработчик кнопки «Назад» на тулбаре
        binding.toolbar.setNavigationOnClickListener {
            handleBackPress()
        }

        // Наблюдаем за событием успешного создания
        viewModel.playlistCreatedEvent.observe(viewLifecycleOwner) { playlistName ->
            playlistName?.let {
                Toast.makeText(
                    requireContext(),
                    "Плейлист $it создан",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }

        // Переопределяем системный Back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
        )
    }

    private fun handleBackPress() {
        if (viewModel.hasUnsavedData &&
            (viewModel.playlistName.isNotBlank() ||
                    viewModel.playlistDescription.isNotBlank() ||
                    viewModel.coverUri != null)
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
