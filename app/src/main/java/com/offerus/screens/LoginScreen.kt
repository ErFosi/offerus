package com.offerus.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.offerus.components.AnimatedAppName
import com.offerus.components.AnimatedLogo
import com.offerus.components.LoginBox
import com.offerus.ui.theme.OfferUSTheme
import com.offerus.ui.theme.primaryLight


@Composable
fun Login() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(primaryLight)

    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.82f),  // Aplica el 75% del ancho aquí
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.weight(0.15f)) {
                AnimatedLogo()
            }


            Card(modifier = Modifier.weight(0.7f)) {
                LoginBox()
            }


            Box(modifier = Modifier.weight(0.15f)) {
                AnimatedAppName()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLogInScreen() {
    OfferUSTheme {
        Login()
    }
}



