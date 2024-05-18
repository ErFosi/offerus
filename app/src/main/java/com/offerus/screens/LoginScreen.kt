package com.offerus.screens



import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.offerus.Idioma
import com.offerus.components.AnimatedAppName
import com.offerus.components.AnimatedLogo
import com.offerus.components.LoginBox
import com.offerus.components.ThemeSwitcher
import com.offerus.components.languageSwitcher
import com.offerus.ui.theme.primaryLight
import com.offerus.viewModels.MainViewModel


@Composable
fun Login(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val booleanState by mainViewModel.tema.collectAsState(initial = true)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(primaryLight)

    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.82f)
                .verticalScroll(rememberScrollState()),  // Aplica el 75% del ancho aquí
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var height by rememberSaveable { mutableStateOf(false) }
            val heightdp by animateDpAsState(
                if (height) 460.dp else 345.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Box(modifier = Modifier
                .height(150.dp)
                .padding(top = 50.dp)) {
                AnimatedLogo()
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(modifier = Modifier.height(heightdp.coerceAtLeast(300.dp))) {
                    Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp)){
                        LoginBox(
                            mainViewModel,
                            navController
                        ) {
                            height = it
                        }
                    }

                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 30.dp)
                        .width(200.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    languageSwitcher(
                        idiomaSeleccionado = mainViewModel.idioma.collectAsState(initial = Idioma.Castellano).value,
                        onLanguageSelected = { it: Idioma ->
                            mainViewModel.updateIdioma(
                                it,
                                context = context
                            )
                        },
                        size = 40.dp
                    )
                    ThemeSwitcher(
                        darkTheme = booleanState,
                        onClick = {
                            if (booleanState) {
                                mainViewModel.updateTheme(false)
                            } else {
                                mainViewModel.updateTheme(true)
                            }
                        },
                        size = 40.dp
                    )
                }
            }


            Box(modifier = Modifier
                .height(100.dp)
                .padding(bottom = 50.dp)) {
                AnimatedAppName()
            }
        }
    }
}

/*
@SuppressLint("CoroutineCreationDuringComposition")
@Preview(showBackground = true)
@Composable
fun PreviewLogInScreen() {
    OfferUSTheme {
        Login()
    }
}

 */

