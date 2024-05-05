package com.offerus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.offerus.R
import com.offerus.ui.theme.OfferUSTheme

@Composable
fun DialogoSobreMi(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var sobreMi by rememberSaveable { mutableStateOf("") }

    Dialog(
        onDismissRequest = { onDismissRequest() },
    ){
        Card(
            modifier = Modifier
                //.width(400.dp)
                //.height(150.dp)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.sobreMi),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                modifier = Modifier.padding(20.dp),
                value = sobreMi,
                onValueChange = { sobreMi = it},
                label = { Text(stringResource(R.string.sobreMiEdit)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                //horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(onClick = {
                    onDismissRequest()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = ""// stringResource(R.string.Cancelar)
                    )
                }

                Button(onClick = {
                    onConfirmation()
                }) {
                    Text(text = "")
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = ""//stringResource(R.string.Borrar)
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewDialogoSobreMi() {
    OfferUSTheme(content = {
        DialogoSobreMi(onDismissRequest = {  }) {

        }
    })
}