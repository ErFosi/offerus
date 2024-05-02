package com.offerus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.offerus.R

@Composable
fun AnimatedAppName() {
    var nameVisible by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        nameVisible = true
    }

    AnimatedVisibility(
        visible = nameVisible,
        enter = slideInVertically(

            initialOffsetY = { +500 },

            animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
        ) + fadeIn(

            animationSpec = tween(durationMillis = 1800)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -500 },

            animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 1800)
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.offerus_con_slogan),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .size(180.dp)
        )
    }
}
