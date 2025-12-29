package com.example.transcribeassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.transcribeassistant.data.refresh.AppRefreshManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Lightweight ViewModel that bridges AppRefreshManager to Composables.
 * Enables dependency injection in Compose via hiltViewModel().
 */
@HiltViewModel
class RefreshManagerViewModel @Inject constructor(
    val appRefreshManager: AppRefreshManager
) : ViewModel()
