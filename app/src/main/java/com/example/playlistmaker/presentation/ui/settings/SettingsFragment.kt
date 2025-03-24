package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModel()

    // храним ссылку на binding
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        val toolbar = binding.toolbar
        toolbar.title = getString(R.string.settings_title)
        toolbar.navigationIcon = null
    }

    private fun setupObservers() {
        // Подписываемся на LiveData через viewLifecycleOwner
        settingsViewModel.isDarkThemeEnabled.observe(viewLifecycleOwner, Observer { isDarkMode ->
            // Прямой доступ к switchTheme
            binding.switchTheme.isChecked = isDarkMode
        })

        settingsViewModel.shareAppEvent.observe(viewLifecycleOwner, Observer { intent ->
            startActivity(Intent.createChooser(intent, null))
        })

        settingsViewModel.supportEmailEvent.observe(viewLifecycleOwner, Observer { intent ->
            startActivity(Intent.createChooser(intent, getString(R.string.contact_support)))
        })

        settingsViewModel.userAgreementEvent.observe(viewLifecycleOwner, Observer { intent ->
            startActivity(intent)
        })
    }

    private fun setupListeners() {
        // Переключатель темы
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }

        // Кнопка «Поделиться приложением»
        binding.textViewShareApp.setOnClickListener {
            settingsViewModel.shareApp(getString(R.string.share_message))
        }

        // Кнопка «Написать в поддержку»
        binding.textViewSupport.setOnClickListener {
            settingsViewModel.writeToSupport(
                email = getString(R.string.extra_email),
                subject = getString(R.string.support_subject),
                message = getString(R.string.support_email)
            )
        }

        // Кнопка «Пользовательское соглашение»
        binding.textViewTerms.setOnClickListener {
            settingsViewModel.openUserAgreement(getString(R.string.terms_url))
        }
    }

    // обнуляем binding, чтобы избежать утечек
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
