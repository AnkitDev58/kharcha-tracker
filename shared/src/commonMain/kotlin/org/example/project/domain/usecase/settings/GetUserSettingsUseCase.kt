package org.example.project.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.UserSettings
import org.example.project.domain.repository.UserSettingsRepository

class GetUserSettingsUseCase(private val repository: UserSettingsRepository) {
    operator fun invoke(): Flow<UserSettings> = repository.getUserSettings()
}
