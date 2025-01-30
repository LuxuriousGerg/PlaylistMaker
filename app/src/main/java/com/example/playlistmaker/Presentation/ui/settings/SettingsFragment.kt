package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModel()

    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)
        setupObservers()
        setupListeners(view)
    }

    private fun setupUI(view: View) {

        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.settings_title)
        toolbar.navigationIcon = null

        // SwitchMaterial
        themeSwitcher = view.findViewById(R.id.switch_theme)
    }

    private fun setupObservers() {
        // Подписываемся на LiveData через viewLifecycleOwner
        settingsViewModel.isDarkThemeEnabled.observe(viewLifecycleOwner, Observer { isDarkMode ->
            themeSwitcher.isChecked = isDarkMode
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

    private fun setupListeners(view: View) {
        // Переключатель темы
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }

        // Кнопка «Поделиться приложением»
        view.findViewById<MaterialTextView>(R.id.textView_share_app).setOnClickListener {
            settingsViewModel.shareApp(getString(R.string.share_message))
        }

        // Кнопка «Написать в поддержку»
        view.findViewById<MaterialTextView>(R.id.textView_support).setOnClickListener {
            settingsViewModel.writeToSupport(
                email = "support@example.com",
                subject = getString(R.string.support_subject),
                message = getString(R.string.support_email)
            )
        }

        // Кнопка «Пользовательское соглашение»
        view.findViewById<MaterialTextView>(R.id.textView_terms).setOnClickListener {
            settingsViewModel.openUserAgreement(getString(R.string.terms_url))
        }
    }
}
