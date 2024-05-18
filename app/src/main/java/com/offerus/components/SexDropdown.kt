package com.offerus.components

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
    selectedSexInitial:String = ""
) {
    val sexOptions = listOf(
        Pair(stringResource(R.string.hombre), 'M'),
        Pair(stringResource(R.string.mujer), 'F'),
        Pair(stringResource(R.string.otros), 'O')
    )
    var selectedSexInitial2 = ""
    when (selectedSexInitial) {
        "M" -> selectedSexInitial2 = stringResource(R.string.hombre)
        "F" -> selectedSexInitial2 = stringResource(R.string.mujer)
        "O" -> selectedSexInitial2 = stringResource(R.string.otros)
        else -> selectedSexInitial2 = ""
    }
    var selectedSex by remember { mutableStateOf(selectedSexInitial2) }
    var sexExpanded by remember { mutableStateOf(false) }


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
                    text = { Text(sexOption.first) },
                    onClick = {
                        selectedSex = sexOption.first
                        sexExpanded = false
                        onSexChange(sexOption.second)
                    }
                )
            }
        }
    }
}