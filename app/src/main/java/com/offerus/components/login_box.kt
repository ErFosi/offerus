package com.offerus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalWearMaterialApi::class,
    ExperimentalWearMaterialApi::class
)
@Composable
fun LoginBox() {
    val swipeableState = rememberSwipeableState(initialValue = 1)

    val anchors = mapOf(0f to 2, 300f to 1)
    val coroutineScope = rememberCoroutineScope()

    val onOptionSelected: (Int) -> Unit = { option ->
        coroutineScope.launch {
            swipeableState.animateTo(option)
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                )
        ) {
            OptionSelector(
                swipeableState.currentValue,
                onOptionSelected= onOptionSelected
            )
        }
        Spacer(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.onPrimary)
        )
        Box(
            modifier = Modifier
                .weight(0.75f)
                .padding(8.dp)
                .clipToBounds()
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                )
        ) {
            this@Column.AnimatedVisibility(
                visible = swipeableState.currentValue == 1,
                enter = slideInHorizontally(initialOffsetX = { -1000 }),
                exit = slideOutHorizontally(targetOffsetX = { -1000 })
            ) {
                LoginFieldView()
            }
            this@Column.AnimatedVisibility(
                visible = swipeableState.currentValue == 2,
                enter = slideInHorizontally(initialOffsetX = { 1000 }),
                exit = slideOutHorizontally(targetOffsetX = { 1000 })
            ) {
                RegisterFieldView()
            }
        }



        Button(
            onClick = { /* TO DO */ },
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Log in",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )


                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Enviar",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun OptionSelector(selectedOption: Int, onOptionSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        TextButton(
            onClick = { onOptionSelected(1) },
            shape = RectangleShape
        ) {
            Text(
                "Login",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                ),
                textDecoration = if (selectedOption == 1) TextDecoration.Underline else TextDecoration.None
            )
        }
        TextButton(
            onClick = { onOptionSelected(2) },
            shape = RectangleShape
        ) {
            Text(
                "Register",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                ),
                textDecoration = if (selectedOption == 2) TextDecoration.Underline else TextDecoration.None
            )
        }
    }
}
