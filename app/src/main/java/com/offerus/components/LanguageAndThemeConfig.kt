package com.offerus.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.offerus.Idioma
import com.offerus.R
import com.offerus.ui.theme.OfferUSTheme

@Composable
fun languageSwitcher(
    size: Dp = 70.dp,
    parentShape: Shape = CircleShape,
    onLanguageSelected: (Idioma) -> Unit,
    idiomaSeleccionado: Idioma,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(modifier = Modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape),
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        )
        // LANGUAGE SELECTOR
        {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxSize().padding(0.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.idioma),
                        contentDescription = stringResource(R.string.SeleccionarIdioma),
                        tint = MaterialTheme.colorScheme.primary,

                        )
                    Text(
                        if (size<70.dp) idiomaSeleccionado.codigo else idiomaSeleccionado.name,
                        Modifier.fillMaxWidth().padding(start = 5.dp).align(Alignment.CenterVertically),
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    Idioma.entries.forEach { idioma ->
                        DropdownMenuItem(
                            text = { Text(idioma.name) },
                            onClick = {
                                expanded = false
                                onLanguageSelected(idioma)
                            }
                        )
                    }
                }
            }
        }
    }
}


/**
 * ThemeSwitcher composable to switch between dark and light theme
 * @param darkTheme: current theme
 * @param size: size of the switcher
 * @param iconSize: size of the icons
 * @param padding: padding of the switcher
 * @param borderWidth: border width of the switcher
 * @param parentShape: shape of the parent container
 * @param toggleShape: shape of the toggle switch
 * @param animationSpec: animation specification
 * @param onClick: callback to switch theme
 */

@Composable
fun ThemeSwitcher(
    darkTheme: Boolean = false,
    size: Dp = 70.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = if (darkTheme) 0.dp else size,
        animationSpec = animationSpec
    )

    Box(modifier = Modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape)
        .clickable { onClick( ) }
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                /*.border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = parentShape
                )*/
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = R.drawable.dark),
                    contentDescription = "Theme Icon",
                    tint = if (darkTheme) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = R.drawable.day),
                    contentDescription = "Theme Icon",
                    tint = if (darkTheme) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.tertiaryContainer
                )
            }
        }
    }
}

// preview the icon
@Composable
@Preview
fun ThemeSwitcherPreview() {
    OfferUSTheme(darkTheme = true) {
        var darkTheme by remember { mutableStateOf(true) }
        ThemeSwitcher(darkTheme = darkTheme, onClick = { darkTheme = !darkTheme })
    }

}
@Composable
@Preview
fun LanguageSwitcherPreview() {
    OfferUSTheme {
        languageSwitcher(
            onLanguageSelected = {},
            idiomaSeleccionado = Idioma.Castellano,
            size = 40.dp
        )
    }

}