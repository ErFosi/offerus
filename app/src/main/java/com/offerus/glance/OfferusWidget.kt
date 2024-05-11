package com.offerus.glance

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.offerus.R
import java.util.prefs.Preferences

class OfferusWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            Content()
        }
    }


    @Composable
    private fun Content() {
        val prefs = currentState<Preferences>()


        Column(modifier = GlanceModifier.cornerRadius(8.dp).fillMaxSize().padding(8.dp)) {
            // Encabezado
            Box(
                modifier = GlanceModifier.fillMaxWidth()./*background(seedLight).*/padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.cornerRadius(10.dp)
                ) {


                    Text(
                        text = "Standings",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(6.dp)
                    )
                    Image(
                        provider = ImageProvider(R.drawable.refresh),
                        contentDescription = null,
                        modifier = GlanceModifier.size(24.dp)
                            .clickable(actionRunCallback<RefreshActionCallback>())
                    )
                }
            }

        }

    }
}

class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        Log.d("refresh", "refresh widget")
        val intent = Intent(context, MyAppWidgetReceiver::class.java).apply {
            action = "updateAction"
        }
        context.sendBroadcast(intent)
        OfferusWidget().updateAll(context)
    }
}