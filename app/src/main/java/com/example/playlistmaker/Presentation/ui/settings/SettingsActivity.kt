package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.viewmodel.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.settings_title)
            setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener { finish() }

        themeSwitcher = findViewById(R.id.switch_theme)
    }

    private fun setupObservers() {
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }

        settingsViewModel.shareAppEvent.observe(this) { intent ->
            startActivity(Intent.createChooser(intent, null))
        }

        settingsViewModel.supportEmailEvent.observe(this) { intent ->
            startActivity(Intent.createChooser(intent, getString(R.string.contact_support)))
        }

        settingsViewModel.userAgreementEvent.observe(this) { intent ->
            startActivity(intent)
        }
    }

    private fun setupListeners() {
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }

        findViewById<MaterialTextView>(R.id.textView_share_app).setOnClickListener {
            settingsViewModel.shareApp(getString(R.string.share_message))
        }

        findViewById<MaterialTextView>(R.id.textView_support).setOnClickListener {
            settingsViewModel.writeToSupport(
                email = "support@example.com",
                subject = getString(R.string.support_subject),
                message = getString(R.string.support_email)
            )
        }

        findViewById<MaterialTextView>(R.id.textView_terms).setOnClickListener {
            settingsViewModel.openUserAgreement(getString(R.string.terms_url))
        }
    }
}
