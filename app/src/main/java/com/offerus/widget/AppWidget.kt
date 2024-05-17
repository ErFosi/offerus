package com.offerus.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.offerus.R
import com.offerus.model.repositories.UserDataRepository
import com.offerus.ui.theme.darkScheme
import com.offerus.ui.theme.inversePrimaryLight
import com.offerus.ui.theme.lightScheme
import com.offerus.ui.theme.primaryLight
import com.offerus.widget.AppWidgetReceiver.Companion.clienteDealsKey
import com.offerus.widget.AppWidgetReceiver.Companion.logged
import com.offerus.widget.AppWidgetReceiver.Companion.pendingHostDealsKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class AppWidget: GlanceAppWidget(){

    @Composable
    private fun Content() {
        val prefs = currentState<Preferences>()
        val clienteDealsJson: String? = prefs[clienteDealsKey]
        val pendingHostDealsJson: String? = prefs[pendingHostDealsKey]
        val logged = prefs[logged]
        Log.d("Widget", "logged: $logged")
        val clienteDeals: List<UserDataRepository.DealInfo> =
            if (clienteDealsJson != null) Json.decodeFromString(clienteDealsJson) else emptyList()
        val pendingHostDeals: List<UserDataRepository.PendingDealInfo> =
            if (pendingHostDealsJson != null) Json.decodeFromString(pendingHostDealsJson) else emptyList()
        val userDataAvailable = clienteDealsJson != null && pendingHostDealsJson != null
        if ((!userDataAvailable || logged == "false")){

            Box(
                modifier = GlanceModifier.fillMaxSize().background(Color.DarkGray).cornerRadius(10.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically,

                    ) {


                        Text(
                            text = "Reload ",
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = 18.sp,
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
                    Image(
                        provider = ImageProvider(R.drawable.logorecortado),
                        contentDescription = null,
                        modifier = GlanceModifier.size(40.dp)
                    )
                    Text(
                        text = "Mantain logged in to see your deals",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.padding(8.dp)
                    )
                }

            }
            return
        }
        Column(modifier = GlanceModifier.cornerRadius(10.dp)) {

            Box(modifier = GlanceModifier.cornerRadius(10.dp)){
                Column(modifier = GlanceModifier.fillMaxSize().padding(8.dp)) {
                    Box(
                        modifier = GlanceModifier.fillMaxWidth().padding(4.dp).background(primaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {


                            Text(
                                text = "My Deals",
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,

                                    ),
                                modifier = GlanceModifier.padding(4.dp)
                            )
                            Image(
                                provider = ImageProvider(R.drawable.refresh),
                                contentDescription = null,
                                modifier = GlanceModifier.size(16.dp)
                                    .clickable(actionRunCallback<RefreshActionCallback>())
                            )
                        }
                    }
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.background(inversePrimaryLight)

                    ) {
                        item {
                            Text(
                                text = "Client Deals",
                                style = TextStyle(
                                    color = ColorProvider(Color.Black),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                ),
                                modifier = GlanceModifier.padding(8.dp)
                            )
                        }

                        clienteDeals.forEach { deal ->
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = GlanceModifier.padding(8.dp).fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${deal.nombrePeticion}",
                                        style = TextStyle(
                                            color = ColorProvider(Color.Black),
                                            fontSize = 12.sp
                                        ),
                                        modifier = GlanceModifier.padding(start = 4.dp).width(width = 200.dp)
                                    )
                                    when (deal.estado) {
                                        "pendiente" -> {

                                            Image(
                                                provider = ImageProvider(R.drawable.pending),
                                                contentDescription = "pendiente",
                                                modifier = GlanceModifier
                                                    .padding(11.dp)
                                                    .size(40.dp),
                                            )
                                        }

                                        "aceptado" -> {
                                            Image(
                                                provider = ImageProvider(R.drawable.logorecortado),
                                                contentDescription = null,
                                                modifier = GlanceModifier
                                                    .padding(11.dp)
                                                    .size(40.dp),
                                            )

                                        }

                                        "rechazado" -> {
                                            Image(
                                                provider = ImageProvider(R.drawable.logo_rojo_wdgt),
                                                contentDescription = null,
                                                modifier = GlanceModifier
                                                    .padding(11.dp)
                                                    .width(40.dp),

                                                )
                                        }
                                    } // Display icon based on estado

                                }
                            }

                        }





                        item { Spacer(modifier = GlanceModifier.height(4.dp)) }

                        item {
                            Text(
                                text = "Pending Host Deals:",
                                style = TextStyle(
                                    color = ColorProvider(Color.Black),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                modifier = GlanceModifier.padding(8.dp)
                            )
                        }


                        pendingHostDeals.forEach { deal ->
                            item {
                                Text(
                                    text = "Client: ${deal.username}, Petition: ${deal.titulo}",
                                    style = TextStyle(
                                        color = ColorProvider(Color.Black),
                                        fontSize = 12.sp
                                    ),
                                    modifier = GlanceModifier.padding(8.dp).width(width = 240.dp)
                                )
                            }

                        }
                    }

                }
                // Encabezado

            }
        }
    }

    object MyAppWidgetGlanceColorScheme {

        val colors = ColorProviders(
            light = lightScheme,
            dark = darkScheme
        )
    }
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            GlanceTheme(colors = MyAppWidgetGlanceColorScheme.colors) {
                Content()
            }
            // create your AppWidget here

        }
    }

}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetReceiverEntryPoint {
    fun userRepository(): UserDataRepository
}
class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppWidget()
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d("Widget", "metodo onUpdate")
        observeData(context)

    }

    private fun observeData(context: Context) {
        val appComponent = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetReceiverEntryPoint::class.java
        )
        val userRepo=appComponent.userRepository()
        serviceScope.launch {
            Log.d("Widget", "metodo observeData")

            // Fetch the two lists from getDatosWidget
            val (clienteDeals, pendingHostDeals) = userRepo.getDatosWidget()

            if (clienteDeals == null && pendingHostDeals == null) {
                Log.d("Widget", "Usuario is empty or not logged in")
                //agregar a logged false en el preferences

                GlanceAppWidgetManager(context).getGlanceIds(AppWidget::class.java).forEach { glanceId ->
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { preferences ->
                        preferences.toMutablePreferences().apply {
                            this[logged] = "false"
                        }
                    }
                }
                return@launch
            }

            // Serialize the lists to JSON
            val clienteDealsJson = Json.encodeToString(clienteDeals)
            val pendingHostDealsJson = Json.encodeToString(pendingHostDeals)

            // Update the widget state with the new data
            GlanceAppWidgetManager(context).getGlanceIds(AppWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { preferences ->
                    preferences.toMutablePreferences().apply {
                        this[clienteDealsKey] = clienteDealsJson
                        this[pendingHostDealsKey] = pendingHostDealsJson
                        this[logged]="true"
                    }
                }
            }

            // Update the widget
            glanceAppWidget.updateAll(context)
            Log.d("Widget", "Widget updated with new data")
        }
    }

    companion object {
        val clienteDealsKey = stringPreferencesKey("clientedeals")
        val pendingHostDealsKey = stringPreferencesKey("pendinghostdeals")
        val logged=stringPreferencesKey("logged")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("Widget", "metodo onReceive")
        observeData(context)
    }

}

class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        Log.d("refresh", "refresh widget")
        val intent = Intent(context, AppWidgetReceiver::class.java).apply {
            action = "updateAction"
        }
        context.sendBroadcast(intent)
        AppWidget().updateAll(context)
    }
}