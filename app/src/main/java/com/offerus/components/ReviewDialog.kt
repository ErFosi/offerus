package com.offerus.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.R
import com.offerus.data.Deal
import com.offerus.screens.UserAvatar
import com.offerus.viewModels.MainViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun ReviewDialog(
    viewModel: MainViewModel,
    usuario: String,
    deal: Deal?,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    var valoracion by rememberSaveable { mutableStateOf(0F) }

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.valoracion),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                )

                if (deal != null) {
                    Column(modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        ValoracionUsuario(
                            nombreUsuario = deal.username_cliente,
                            nota = deal.nota_cliente,
                            viewModel = viewModel
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ValoracionUsuario(
                            nombreUsuario = deal.username_host,
                            nota = deal.nota_host,
                            viewModel = viewModel
                        )
                    }


                    val currentUserRole = when (usuario) {
                        deal.username_cliente -> "cliente"
                        deal.username_host -> "host"
                        else -> null
                    }

                    if (currentUserRole != null) {
                        val nota = if (currentUserRole == "cliente") {
                            deal.nota_cliente
                        } else {
                            deal.nota_host
                        }




                        if (nota == -1) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 8.dp)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            )
                            Text(
                                text = if (currentUserRole == "cliente") stringResource(id = R.string.valora_a) + " " + deal.username_host else stringResource(id = R.string.valora_a) + " " + deal.username_cliente,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )


                            RatingBar(
                                modifier = Modifier
                                    .padding(bottom = 20.dp)
                                    .scale(0.75F),
                                value = valoracion,
                                style = RatingBarStyle.Fill(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.outline
                                ),
                                onValueChange = {
                                    valoracion = it
                                },
                                onRatingChanged = {}
                            )
                            Row {
                                OutlinedButton(
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp),
                                    onClick = { onDismissRequest() }

                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        null
                                    )
                                }

                                Button(
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp),
                                    onClick = {
                                        if (deal.username_host == usuario) {
                                            deal.nota_host = valoracion.toInt()
                                        } else {
                                            deal.nota_cliente = valoracion.toInt()
                                        }
                                        onConfirmation()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        null
                                    )
                                }
                            }

                        }
                    }
                } else {
                    Text(text = stringResource(id = R.string.peticion_no_encontrada))
                }
            }
        }
    }
}

@Composable
fun ValoracionUsuario(nombreUsuario: String, nota: Int, viewModel: MainViewModel) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {

        Row (verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(username = nombreUsuario, viewModel = viewModel)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(nombreUsuario, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (nota != -1) {
                    RatingBar(
                        modifier = Modifier
                            .padding(0.dp),
                        value = nota.toFloat(),
                        style = RatingBarStyle.Fill(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.outline
                        ),
                        size = 15.dp,
                        spaceBetween = 2.dp,
                        onValueChange = {},
                        onRatingChanged = {}
                    )
                } else {
                    Text(stringResource(id = R.string.no_valorado))
                }
            }
        }

    }
}
