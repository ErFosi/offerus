package com.offerus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offerus.utils.obtenerCategorias

// circulos con el color de cada categoria
@Composable
fun CategoriasCard(nombresCategorias: String) {
    var listaCategorias = obtenerCategorias(nombresCategorias)

    obtenerCategoriasDesdeNombres(listaCategorias).let { categorias ->
        Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            for (categoria in categorias.take(3)) {
                // circulos con el color
                Card(modifier = Modifier.padding(2.dp), colors = CardDefaults.cardColors(containerColor = categoria.color.copy(alpha = 0.45f))) {
                    Row(modifier = Modifier.padding(vertical = 0.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(imageVector = ImageVector.vectorResource(categoria.icono), contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = categoria.nombre,
                            modifier = Modifier.padding(
                                top = 2.dp,
                                start = 2.dp,
                                end = 4.dp,
                                bottom = 2.dp
                            ),
                            fontSize = 12.sp,
                        )
                    }

                }
            }
        }
    }
}
