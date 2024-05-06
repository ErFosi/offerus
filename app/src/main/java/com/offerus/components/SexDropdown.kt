package com.offerus.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.offerus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SexDropdown(
    onSexChange: (Char) -> Unit,
) {
    var sexExpanded by remember { mutableStateOf(false) }
    var selectedSex by remember { mutableStateOf("") }
    val sexOptions = listOf(
        stringResource(R.string.hombre), stringResource(R.string.mujer), stringResource(
            R.string.otros)
    )

    //val backgroundColor = MaterialTheme.colorScheme.secondary

    ExposedDropdownMenuBox(
        expanded = sexExpanded,
        onExpandedChange = { sexExpanded = !sexExpanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { sexExpanded = !sexExpanded }
                .menuAnchor(),
            readOnly = true,
            value = selectedSex,
            onValueChange = { },
            label = { Text(stringResource(R.string.sex)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded)
            }
        )
        ExposedDropdownMenu(
            expanded = sexExpanded,
            onDismissRequest = { sexExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            sexOptions.forEach { sexOption ->
                DropdownMenuItem(
                    text = { Text(sexOption) },
                    onClick = {
                        selectedSex = sexOption
                        sexExpanded = false
                        Log.d("Sexo", sexOption)
                        onSexChange(sexOption.first())
                    }
                )
            }
        }
    }
}