package com.offerus.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.offerus.model.repositories.UserDataRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyAppWidgetReceiver() : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = OfferusWidget()
    private val coroutineScope = MainScope()
    @Inject
    lateinit var userDataRepository: UserDataRepository
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("Widget", "metodo onUpdate")
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        observeData(context)
    }

    private fun observeData(context: Context) {
        coroutineScope.launch {
            Log.d("Widget", "metodo observeData")
            delay(500)

            GlanceAppWidgetManager(context).getGlanceIds(OfferusWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { widgetDataStore ->
                    widgetDataStore.toMutablePreferences().apply {
                        //this[globalStandings] = Json.encodeToString(standings)
                    }
                }
            }

            glanceAppWidget.updateAll(context)
            Log.d("Widget", "se actualiza")
        }
    }

    companion object {
        val globalStandings = stringPreferencesKey("standings") // key para guardar los datos entiendo
    }

}