package com.biomech.feature.training

import com.biomech.domain.model.TrainingJob
import com.biomech.domain.repository.EMGRepository
import com.biomech.domain.repository.TrainingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrainingUiState(
    val jobs: List<TrainingJob> = emptyList(),
    val sessionLabels: List<String> = emptyList(),
    val selectedSessions: Set<String> = emptySet(),
)

class TrainingViewModel(
    private val trainingRepository: TrainingRepository,
    private val emgRepository: EMGRepository,
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(TrainingUiState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        scope.launch {
            val sessions = emgRepository.getSessions()
            val jobs = trainingRepository.getJobs()

            _state.value = _state.value.copy(
                sessionLabels = sessions.getOrNull()?.map { it.label } ?: emptyList(),
                jobs = jobs.getOrNull() ?: emptyList(),
            )
        }
    }

    fun toggleSession(label: String) {
        val current = _state.value.selectedSessions.toMutableSet()
        if (label in current) current.remove(label) else current.add(label)
        _state.value = _state.value.copy(selectedSessions = current)
    }

    fun startTraining() {
        // TODO: Create training job with selected session IDs
    }
}

private fun <T> com.biomech.core.common.AppResult<T>.getOrNull(): T? =
    when (this) {
        is com.biomech.core.common.AppResult.Success -> data
        is com.biomech.core.common.AppResult.Error -> null
    }
