package com.offerus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.offerus.data.CATEGORIAS
import com.offerus.data.Categoria
import com.offerus.utils.obtenerCategorias

// circulos con el color de cada categoria
@Composable
fun CategoriasCirculos(nombresCategorias: String) {
    var listaCategorias = obtenerCategorias(nombresCategorias)

    obtenerCategoriasDesdeNombres(listaCategorias).let { categorias ->
        Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            for (categoria in categorias.take(3)) {
                // circulos con el color
                CategoriaCirculo(categoria.color.copy(alpha = 0.45f))
            }
        }
    }
}

fun obtenerCategoriasDesdeNombres(listaNombresCategorias: List<String>): List<Categoria> {
    val categorias = mutableListOf<Categoria>()
    for (nombreCategoria in listaNombresCategorias) {
        val categoria = CATEGORIAS.find { it.nombre == nombreCategoria }
        categoria?.let {
            categorias.add(it)
        }
    }
    return categorias
}

@Composable
fun CategoriaCirculo(color: Color) {
    Box(
        modifier = Modifier
            .size(15.dp)
            .background(color, shape = CircleShape)
            .fillMaxWidth()
    )
}