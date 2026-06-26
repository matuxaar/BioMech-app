package com.biomech.core.resource

enum class Locale(val value: Int) {
    EN(0),
    RU(1);

    companion object {
        fun fromValue(value: Int): Locale =
            entries.firstOrNull { it.value == value } ?: EN
    }
}

class Strings(private val locale: Locale) {

    val appName: String get() = when (locale) {
        Locale.EN -> "BioMech"
        Locale.RU -> "БиоМех"
    }

    val email: String get() = when (locale) {
        Locale.EN -> "Email"
        Locale.RU -> "Эл. почта"
    }

    val password: String get() = when (locale) {
        Locale.EN -> "Password"
        Locale.RU -> "Пароль"
    }

    val confirmPassword: String get() = when (locale) {
        Locale.EN -> "Confirm Password"
        Locale.RU -> "Подтвердите пароль"
    }

    val login: String get() = when (locale) {
        Locale.EN -> "Login"
        Locale.RU -> "Войти"
    }

    val register: String get() = when (locale) {
        Locale.EN -> "Register"
        Locale.RU -> "Зарегистрироваться"
    }

    val alreadyHaveAccount: String get() = when (locale) {
        Locale.EN -> "Already have an account? Login"
        Locale.RU -> "Уже есть аккаунт? Войти"
    }

    val dontHaveAccount: String get() = when (locale) {
        Locale.EN -> "Don't have an account? Register"
        Locale.RU -> "Нет аккаунта? Зарегистрироваться"
    }

    val skipExplore: String get() = when (locale) {
        Locale.EN -> "Skip — explore offline"
        Locale.RU -> "Пропустить — режим офлайн"
    }

    val passwordsDoNotMatch: String get() = when (locale) {
        Locale.EN -> "Passwords do not match"
        Locale.RU -> "Пароли не совпадают"
    }

    val myDevices: String get() = when (locale) {
        Locale.EN -> "My Devices"
        Locale.RU -> "Мои устройства"
    }

    val newDevice: String get() = when (locale) {
        Locale.EN -> "New Device"
        Locale.RU -> "Новое устройство"
    }

    val addNewDevice: String get() = when (locale) {
        Locale.EN -> "Add New Device"
        Locale.RU -> "Добавить устройство"
    }

    val training: String get() = when (locale) {
        Locale.EN -> "Training"
        Locale.RU -> "Тренировка"
    }

    val edit: String get() = when (locale) {
        Locale.EN -> "Edit"
        Locale.RU -> "Изменить"
    }

    val delete: String get() = when (locale) {
        Locale.EN -> "Delete"
        Locale.RU -> "Удалить"
    }

    val home: String get() = when (locale) {
        Locale.EN -> "Home"
        Locale.RU -> "Главная"
    }

    val profile: String get() = when (locale) {
        Locale.EN -> "Profile"
        Locale.RU -> "Профиль"
    }

    val settings: String get() = when (locale) {
        Locale.EN -> "Settings"
        Locale.RU -> "Настройки"
    }

    val nickname: String get() = when (locale) {
        Locale.EN -> "Nickname"
        Locale.RU -> "Никнейм"
    }

    val editNickname: String get() = when (locale) {
        Locale.EN -> "Edit nickname"
        Locale.RU -> "Изменить никнейм"
    }

    val devices: String get() = when (locale) {
        Locale.EN -> "Devices"
        Locale.RU -> "Устройства"
    }

    val trainings: String get() = when (locale) {
        Locale.EN -> "Trainings"
        Locale.RU -> "Тренировки"
    }

    val dashboard: String get() = when (locale) {
        Locale.EN -> "Dashboard"
        Locale.RU -> "Панель"
    }

    val dashboardDesc: String get() = when (locale) {
        Locale.EN -> "Train your device to unlock personalized insights and movement analytics here."
        Locale.RU -> "Тренируйте устройство, чтобы открыть персонализированную аналитику."
    }

    val serverConfiguration: String get() = when (locale) {
        Locale.EN -> "Server Configuration"
        Locale.RU -> "Настройки сервера"
    }

    val apiUrlTest: String get() = when (locale) {
        Locale.EN -> "API URL, Test Connection"
        Locale.RU -> "URL API, Проверка соединения"
    }

    val appearance: String get() = when (locale) {
        Locale.EN -> "Appearance"
        Locale.RU -> "Внешний вид"
    }

    val systemDefault: String get() = when (locale) {
        Locale.EN -> "System default"
        Locale.RU -> "Системная"
    }

    val light: String get() = when (locale) {
        Locale.EN -> "Light"
        Locale.RU -> "Светлая"
    }

    val dark: String get() = when (locale) {
        Locale.EN -> "Dark"
        Locale.RU -> "Тёмная"
    }

    val deviceLayout: String get() = when (locale) {
        Locale.EN -> "Device Layout"
        Locale.RU -> "Расположение"
    }

    val gridLayout: String get() = when (locale) {
        Locale.EN -> "Grid (2 columns)"
        Locale.RU -> "Сетка (2 колонки)"
    }

    val listLayout: String get() = when (locale) {
        Locale.EN -> "List (1 column)"
        Locale.RU -> "Список (1 колонка)"
    }

    val logout: String get() = when (locale) {
        Locale.EN -> "Logout"
        Locale.RU -> "Выйти"
    }

    val logoutConfirmTitle: String get() = when (locale) {
        Locale.EN -> "Logout"
        Locale.RU -> "Выход"
    }

    val logoutConfirmBody: String get() = when (locale) {
        Locale.EN -> "Are you sure you want to logout?"
        Locale.RU -> "Вы уверены, что хотите выйти?"
    }

    val cancel: String get() = when (locale) {
        Locale.EN -> "Cancel"
        Locale.RU -> "Отмена"
    }

    val save: String get() = when (locale) {
        Locale.EN -> "Save"
        Locale.RU -> "Сохранить"
    }

    val back: String get() = when (locale) {
        Locale.EN -> "< Back"
        Locale.RU -> "< Назад"
    }

    val serverConfigTitle: String get() = when (locale) {
        Locale.EN -> "Server Configuration"
        Locale.RU -> "Настройки сервера"
    }

    val serverConfigDesc: String get() = when (locale) {
        Locale.EN -> "Change the server URL to point to a running backend instance."
        Locale.RU -> "Измените URL сервера для подключения к запущенному бэкенду."
    }

    val apiUrl: String get() = when (locale) {
        Locale.EN -> "API URL"
        Locale.RU -> "URL API"
    }

    val testConnection: String get() = when (locale) {
        Locale.EN -> "Test Connection"
        Locale.RU -> "Проверить соединение"
    }

    val connected: String get() = when (locale) {
        Locale.EN -> "Connected!"
        Locale.RU -> "Подключено!"
    }

    val connectionFailed: String get() = when (locale) {
        Locale.EN -> "Connection failed. Check the URL and ensure the server is running."
        Locale.RU -> "Ошибка подключения. Проверьте URL и убедитесь, что сервер запущен."
    }

    val deviceDetails: String get() = when (locale) {
        Locale.EN -> "Device Details"
        Locale.RU -> "Информация об устройстве"
    }

    val name: String get() = when (locale) {
        Locale.EN -> "Name"
        Locale.RU -> "Название"
    }

    val type: String get() = when (locale) {
        Locale.EN -> "Type"
        Locale.RU -> "Тип"
    }

    val hardwareVersion: String get() = when (locale) {
        Locale.EN -> "Hardware Version"
        Locale.RU -> "Версия ПО"
    }

    val id: String get() = when (locale) {
        Locale.EN -> "ID"
        Locale.RU -> "ID"
    }

    val trainedActions: String get() = when (locale) {
        Locale.EN -> "Trained Actions"
        Locale.RU -> "Обученные жесты"
    }

    val close: String get() = when (locale) {
        Locale.EN -> "Close"
        Locale.RU -> "Закрыть"
    }

    val addDevice: String get() = when (locale) {
        Locale.EN -> "Add Device"
        Locale.RU -> "Добавить устройство"
    }

    val addNewDeviceSheet: String get() = when (locale) {
        Locale.EN -> "Add New Device"
        Locale.RU -> "Новое устройство"
    }

    val bleScan: String get() = when (locale) {
        Locale.EN -> "BLE Scan"
        Locale.RU -> "BLE Поиск"
    }

    val manual: String get() = when (locale) {
        Locale.EN -> "Manual"
        Locale.RU -> "Вручную"
    }

    val stopScanning: String get() = when (locale) {
        Locale.EN -> "Stop Scanning"
        Locale.RU -> "Остановить поиск"
    }

    val startScanning: String get() = when (locale) {
        Locale.EN -> "Start Scanning"
        Locale.RU -> "Начать поиск"
    }

    val pressScan: String get() = when (locale) {
        Locale.EN -> "Press scan to find nearby devices"
        Locale.RU -> "Нажмите поиск, чтобы найти устройства"
    }

    val deviceName: String get() = when (locale) {
        Locale.EN -> "Device Name"
        Locale.RU -> "Название устройства"
    }

    val editDevice: String get() = when (locale) {
        Locale.EN -> "Edit Device"
        Locale.RU -> "Изменить устройство"
    }

    val deleteDevice: String get() = when (locale) {
        Locale.EN -> "Delete Device"
        Locale.RU -> "Удалить устройство"
    }

    fun deleteDeviceConfirm(name: String): String = when (locale) {
        Locale.EN -> "Are you sure you want to delete $name?"
        Locale.RU -> "Вы уверены, что хотите удалить $name?"
    }

    val noDevicesFound: String get() = when (locale) {
        Locale.EN -> "No devices found"
        Locale.RU -> "Устройства не найдены"
    }

    val stopScan: String get() = when (locale) {
        Locale.EN -> "Stop Scan"
        Locale.RU -> "Остановить"
    }

    val startScan: String get() = when (locale) {
        Locale.EN -> "Start Scan"
        Locale.RU -> "Найти"
    }

    val sensor: String get() = when (locale) {
        Locale.EN -> "sensor"
        Locale.RU -> "сенсор"
    }

    val prosthetic: String get() = when (locale) {
        Locale.EN -> "prosthetic"
        Locale.RU -> "протез"
    }

    val gestureRest: String get() = when (locale) {
        Locale.EN -> "Rest"
        Locale.RU -> "Покой"
    }

    val gestureFist: String get() = when (locale) {
        Locale.EN -> "Fist"
        Locale.RU -> "Кулак"
    }

    val gestureOpen: String get() = when (locale) {
        Locale.EN -> "Open"
        Locale.RU -> "Открыть"
    }

    val gesturePinch: String get() = when (locale) {
        Locale.EN -> "Pinch"
        Locale.RU -> "Захват"
    }

    val gesturePoint: String get() = when (locale) {
        Locale.EN -> "Point"
        Locale.RU -> "Указать"
    }

    val dashboardTitle: String get() = when (locale) {
        Locale.EN -> "Dashboard"
        Locale.RU -> "Панель"
    }

    val connectedLabel: String get() = when (locale) {
        Locale.EN -> "● Connected"
        Locale.RU -> "● Подключено"
    }

    val emgSignal: String get() = when (locale) {
        Locale.EN -> "EMG Signal"
        Locale.RU -> "ЭМГ Сигнал"
    }

    val startRecording: String get() = when (locale) {
        Locale.EN -> "Start Recording"
        Locale.RU -> "Запись"
    }

    val stop: String get() = when (locale) {
        Locale.EN -> "Stop"
        Locale.RU -> "Стоп"
    }

    val goToTraining: String get() = when (locale) {
        Locale.EN -> "Go to Training"
        Locale.RU -> "К тренировкам"
    }

    val trainingTitle: String get() = when (locale) {
        Locale.EN -> "Training"
        Locale.RU -> "Тренировка"
    }

    val selectSessions: String get() = when (locale) {
        Locale.EN -> "Select sessions for training:"
        Locale.RU -> "Выберите сессии для тренировки:"
    }

    val startTraining: String get() = when (locale) {
        Locale.EN -> "Start Training"
        Locale.RU -> "Начать тренировку"
    }

    val noSessions: String get() = when (locale) {
        Locale.EN -> "No sessions available"
        Locale.RU -> "Нет доступных сессий"
    }

    val trainingHistory: String get() = when (locale) {
        Locale.EN -> "Training History"
        Locale.RU -> "История тренировок"
    }

    val noTrainingJobs: String get() = when (locale) {
        Locale.EN -> "No training jobs yet"
        Locale.RU -> "Пока нет тренировок"
    }

    val offlineTap: String get() = when (locale) {
        Locale.EN -> "Offline — tap to configure server"
        Locale.RU -> "Офлайн — нажмите, чтобы настроить сервер"
    }

    fun version(v: String): String = when (locale) {
        Locale.EN -> "v$v"
        Locale.RU -> "в$v"
    }

    fun rssi(value: Int): String = "$value dBm"

    fun accuracy(pct: Int): String = when (locale) {
        Locale.EN -> "$pct% accuracy"
        Locale.RU -> "$pct% точность"
    }

    fun jobAccuracy(pct: Int): String = when (locale) {
        Locale.EN -> "Accuracy: $pct%"
        Locale.RU -> "Точность: $pct%"
    }

    fun jobId(id: String): String = when (locale) {
        Locale.EN -> "Job ${id.take(8)}"
        Locale.RU -> "Задача ${id.take(8)}"
    }

    fun status(label: String): String = when (locale) {
        Locale.EN -> "Status: $label"
        Locale.RU -> "Статус: $label"
    }

    val streaming: String get() = when (locale) {
        Locale.EN -> "Real-time Prediction"
        Locale.RU -> "Прогнозирование в реальном времени"
    }

    val streamingDesc: String get() = when (locale) {
        Locale.EN -> "WebSocket connection for live gesture recognition"
        Locale.RU -> "WebSocket для распознавания жестов в реальном времени"
    }

    val enableStreaming: String get() = when (locale) {
        Locale.EN -> "Enable real-time prediction"
        Locale.RU -> "Включить прогнозирование"
    }

    val streamingConnected: String get() = when (locale) {
        Locale.EN -> "Connected"
        Locale.RU -> "Подключено"
    }

    val streamingDisconnected: String get() = when (locale) {
        Locale.EN -> "Disconnected"
        Locale.RU -> "Отключено"
    }

    val language: String get() = when (locale) {
        Locale.EN -> "Language"
        Locale.RU -> "Язык"
    }

    val predictionLabel: String get() = when (locale) {
        Locale.EN -> "PREDICTION"
        Locale.RU -> "ПРОГНОЗ"
    }

    val record: String get() = when (locale) {
        Locale.EN -> "Record"
        Locale.RU -> "Запись"
    }

    val recordingLabel: String get() = when (locale) {
        Locale.EN -> "Recording"
        Locale.RU -> "Идёт запись"
    }

    val recordingComplete: String get() = when (locale) {
        Locale.EN -> "Recording complete"
        Locale.RU -> "Запись завершена"
    }

    val upload: String get() = when (locale) {
        Locale.EN -> "Upload"
        Locale.RU -> "Загрузить"
    }

    val myFiles: String get() = when (locale) {
        Locale.EN -> "My Files"
        Locale.RU -> "Мои файлы"
    }

    val noFiles: String get() = when (locale) {
        Locale.EN -> "No training files yet"
        Locale.RU -> "Пока нет файлов"
    }

    val saveRecording: String get() = when (locale) {
        Locale.EN -> "Save Recording"
        Locale.RU -> "Сохранить запись"
    }

    val sessions: String get() = when (locale) {
        Locale.EN -> "Sessions"
        Locale.RU -> "Сессии"
    }

    val files: String get() = when (locale) {
        Locale.EN -> "Files"
        Locale.RU -> "Файлы"
    }

    val deleteFile: String get() = when (locale) {
        Locale.EN -> "Delete File"
        Locale.RU -> "Удалить файл"
    }

    fun fileSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }

    fun deviceTypeLabel(type: String): String = when (locale) {
        Locale.EN -> type.lowercase().replaceFirstChar { it.uppercase() }
        Locale.RU -> when (type.lowercase()) {
            "sensor" -> "Сенсор"
            "prosthetic" -> "Протез"
            else -> type
        }
    }
}
