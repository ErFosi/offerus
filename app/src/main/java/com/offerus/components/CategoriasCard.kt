package com.offerus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
        LazyRow(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { categoria ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = categoria.color.copy(alpha = 0.45f)),
                    modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(categoria.icono),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = categoria.nombre,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

