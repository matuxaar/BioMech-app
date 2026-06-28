package com.biomech.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources
import com.biomech.core.resource.Locale

@Composable
fun LanguageScreen(
    locale: Locale = Locale.EN,
    onLocaleChanged: (Locale) -> Unit = {},
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
                    IconButton(onClick = onBack) {
                        Text("\u2190")
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        AppResources.strings.language,
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
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Locale.entries.forEach { l ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLocaleChanged(l) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = locale == l,
                                onClick = { onLocaleChanged(l) },
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (l) {
                                    Locale.EN -> "English"
                                    Locale.RU -> "Русский"
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
