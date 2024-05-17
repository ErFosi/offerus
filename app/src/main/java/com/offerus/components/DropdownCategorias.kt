package com.offerus.components

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.offerus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCategorias(
    onCategoriaChange: (String) -> Unit,
    categoria: String
) {
    val context = LocalContext.current
    val idiomas = arrayOf(stringResource(id = R.string.gratis), stringResource(id = R.string.deporte), stringResource(id = R.string.hogar), stringResource(id = R.string.entretenimiento), stringResource(id = R.string.academico), stringResource(id = R.string.online), stringResource(id = R.string.otros))
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember {
        mutableStateOf("Categoria")
    }

    if (categoria.contains(",")){
        selectedText = stringResource(id = R.string.all_categories)
    } else {
        selectedText = categoria
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {onCategoriaChange(it)},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                idiomas.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            // selectedText = item
                            expanded = false
                            selectedText = item
                            onCategoriaChange(item)
                        }
                    )
                }
            }
        }
    }
}