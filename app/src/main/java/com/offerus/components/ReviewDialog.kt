package com.offerus.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.data.Deal

@SuppressLint("UnrememberedMutableState")
@Composable
fun ReviewDialog(
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
                    text = "Valoración del servicio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (deal != null) {
                    ValoracionUsuario(
                        nombreUsuario = deal.username_cliente,
                        nota = deal.nota_cliente,
                        label = "${deal.username_cliente}"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ValoracionUsuario(
                        nombreUsuario = deal.username_host,
                        nota = deal.nota_host,
                        label = "${deal.username_host}"
                    )

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
                            Text(
                                text = if (currentUserRole == "cliente") "Valora a ${deal.username_host}" else "Valora a ${deal.username_cliente}",
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

                            IconButton(
                                onClick = {
                                    if (deal.username_host == usuario) {
                                        deal.nota_host = valoracion.toInt()
                                    } else {
                                        deal.nota_cliente = valoracion.toInt()
                                    }
                                    onConfirmation()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Aceptar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                } else {
                    Text(text = "Peticion no encontrada")
                }
            }
        }
    }
}

@Composable
fun ValoracionUsuario(nombreUsuario: String, nota: Int, label: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            Text("Aún no ha valorado")
        }
    }
}
