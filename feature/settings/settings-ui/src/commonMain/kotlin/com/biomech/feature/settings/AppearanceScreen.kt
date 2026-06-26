package com.biomech.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.common.ThemeMode
import com.biomech.core.resource.AppResources

@Composable
fun AppearanceScreen(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChanged: (ThemeMode) -> Unit = {},
    useGridLayout: Boolean = true,
    onGridLayoutChanged: (Boolean) -> Unit = {},
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 16.dp, top = 6.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onBack) {
                        Text(AppResources.strings.back)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        AppResources.strings.appearance,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                HorizontalDivider()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppResources.strings.appearance,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onThemeModeChanged(mode) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = { onThemeModeChanged(mode) },
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (mode) {
                                    ThemeMode.SYSTEM -> AppResources.strings.systemDefault
                                    ThemeMode.LIGHT -> AppResources.strings.light
                                    ThemeMode.DARK -> AppResources.strings.dark
                                },
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppResources.strings.deviceLayout,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGridLayoutChanged(true) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = useGridLayout,
                            onClick = { onGridLayoutChanged(true) },
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(AppResources.strings.gridLayout)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGridLayoutChanged(false) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = !useGridLayout,
                            onClick = { onGridLayoutChanged(false) },
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(AppResources.strings.listLayout)
                    }
                }
            }
        }
    }
}
