package com.offerus.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.offerus.R
import com.offerus.components.ProfilePicture
import com.offerus.ui.theme.OfferUSTheme
import java.io.File


@Composable
fun UserScreen(){
    var sobreMiExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var datosPersonalesExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            //.padding(horizontal = 50.dp)
        )
    {
        Row(
            modifier = Modifier
                .padding(0.dp)
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(0.dp)
                    .width(120.dp)
            )
            {
                // Profile picture
                var uri by remember { mutableStateOf<Uri?>(Uri.parse("")) }
                if (uri == Uri.parse("")){
                    if (true) { // TODO: check if there is internet connection
                        /*try {
                            viewModel.getProfilePicture { bitmap ->
                                if (bitmap != null) {
                                    uri = context.createImageFileFromBitMap(bitmap)
                                }
                            }
                        }catch (e: NotFoundException){
                            Log.d("Ajustes", "No se ha podido obtener la imagen de perfil")
                        }*/

                    }else{
                        /*Toast.makeText(
                            context,
                            R.string.no_internet_pic,
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }
                    uri = "android.resource://com.offerus/drawable/baseline_adb_24".toUri()
                }

                //image to show bottom sheet
                ProfilePicture(
                    directory = File("images"),
                    uri = uri,
                    onSetUri = {
                        /*if (isNetworkAvailable(context)) context.getFileFromUri(it)?.let {
                                it1 -> viewModel.subirFotoDePerfil(it1)
                            uri = it
                        }
                        else Toast.makeText(context, R.string.no_internet_pic, Toast.LENGTH_SHORT).show()*/
                    },
                    editable = true
                )
            }
            Column(
                modifier = Modifier
                    .padding(0.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "User Name", // TODO: get user name
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 30.dp)
                )
                Row {
                    RatingBar(
                        modifier = Modifier
                            .padding(bottom = 20.dp, top = 15.dp, start = 5.dp, end = 5.dp)
                            .scale(0.75F),
                        value = 3.0F,
                        style = RatingBarStyle.Fill(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outline),
                        onValueChange = {},
                        onRatingChanged = {}
                    )
                    Text(text = "(20)", modifier = Modifier.padding(top = 18.dp))
                }
            }
        }

        // Sobre mi
        Row {
            Text(
                text = "Sobre mi", // TODO: poner stringresource
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = { sobreMiExpanded = !sobreMiExpanded },
                modifier = Modifier.padding(top = 8.dp, end = 30.dp, bottom = 0.dp)
            ) {
                Icon(
                    imageVector = if (!sobreMiExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                )
            }
        }
        Divider(
            modifier = Modifier.padding(top = 0.dp, start = 30.dp, end = 30.dp, bottom = 10.dp),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        AnimatedVisibility(visible = sobreMiExpanded){
            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "aqui va el texto de sobre mi bhipavujsgbhdfgvuia sfvgoasdfvg yyaosidfvgygb asbfv ioalfvg ",
                        modifier = Modifier.width(270.dp)
                    ) // TODO: get user description
                    IconButton(
                        onClick = { /* TODO ONCLICK ABRIR DIALOGO */ },
                        modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.editar),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(0.dp)
                        )
                    }
                }
            }
        }

        // Datos personales
        Row {
            Text(
                text = "Datos personales", // TODO: poner stringresource
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 30.dp, top = 20.dp, end = 20.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = { datosPersonalesExpanded = !datosPersonalesExpanded },
                modifier = Modifier.padding(top = 8.dp, end = 30.dp, bottom = 0.dp)
            ) {
                Icon(
                    imageVector = if (!datosPersonalesExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                )
            }
        }
        Divider(
            modifier = Modifier.padding(top = 0.dp, start = 30.dp, end = 30.dp, bottom = 10.dp),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        AnimatedVisibility(
            visible = datosPersonalesExpanded,
        ){

            Card(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.width(270.dp)
                    ) {
                        Text(text = "Nombre y apellidos") // TODO: get user name
                        Text(text = "Telefono")
                        Text(text = "Email")
                        Text(text = "Edad")
                        Text(text = "Sexo")
                    }
                    IconButton(
                        onClick = { /* TODO ONCLICK ABRIR DIALOGO */ },
                        modifier = Modifier.padding(top = 0.dp, end = 0.dp, bottom = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.editar),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(0.dp)
                        )
                    }
                }
            }
        }

        // MAPA

    }
}

// preview composable function
@Preview(showBackground = true)
@Composable
fun PreviewElemento() {
    OfferUSTheme(content = {
        UserScreen()
    })
}
