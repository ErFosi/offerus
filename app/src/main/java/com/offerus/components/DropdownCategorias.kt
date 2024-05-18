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
import com.offerus.data.CATEGORIAS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCategorias(
    onCategoriaChange: (String) -> Unit,
    categoria: String
) {
    val context = LocalContext.current
    val idiomas = arrayOf(stringResource(id = R.string.gratis), stringResource(id = R.string.deporte), stringResource(id = R.string.hogar), stringResource(id = R.string.entretenimiento), stringResource(id = R.string.academico), stringResource(id = R.string.online), stringResource(id = R.string.otros))
    var expanded by remember { mutableStateOf(false) }
    var cat2=""
    when (categoria) {
        "gratis" -> cat2= stringResource(id = R.string.gratis)
        "deporte" -> cat2 = stringResource(id = R.string.deporte)
        "hogar" -> cat2 = stringResource(id = R.string.hogar)
        "entretenimiento" -> cat2 = stringResource(id = R.string.entretenimiento)
        "academico" -> cat2 = stringResource(id = R.string.academico)
        "online" -> cat2 = stringResource(id = R.string.online)
        "otros" -> cat2 = stringResource(id = R.string.otros)
        "Todas las categorías" -> cat2 = stringResource(id = R.string.all_categories)
        "" -> cat2 = stringResource(id = R.string.all_categories)
        else -> cat2 = stringResource(id = R.string.all_categories)

    }
    var selectedText by remember {
        mutableStateOf(cat2)
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
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                CATEGORIAS.forEach { item ->
                    val selected = stringResource(id =item.nombreMostrar )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id =item.nombreMostrar )) },
                        onClick = {
                            // selectedText = item
                            expanded = false
                            selectedText = selected
                            onCategoriaChange(item.nombre)
                        }
                    )
                }
                val sel = stringResource(id = R.string.all_categories)
                DropdownMenuItem(
                    text = { Text(text = stringResource(id =R.string.all_categories )) },
                    onClick = {
                        // selectedText = item
                        expanded = false
                        selectedText = sel
                        onCategoriaChange("")
                    }
                )
            }
        }
    }
}