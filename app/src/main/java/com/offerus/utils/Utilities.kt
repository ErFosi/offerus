package com.offerus.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.offerus.model.database.entities.Deal

fun showToastOnMainThread(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

fun createDealList(): List<Deal> {
    val dealList = mutableListOf<Deal>()
    for (i in 0..10) {
        dealList.add(Deal(i, i, "user$i", "jose", "", false))
    }
    return dealList
}

//intent implicito email
fun enviarEmail(context: Context, destinatario: String, asunto: String, cuerpo: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "message/rfc822"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
    intent.putExtra(Intent.EXTRA_SUBJECT, asunto)
    intent.putExtra(Intent.EXTRA_TEXT, cuerpo)

    try {
        context.startActivity(Intent.createChooser(intent, "Enviar correo electrónico"))
    } catch (e: ActivityNotFoundException) {
        showToastOnMainThread(context, "No hay clientes de correo electrónico instalados.")
    }
}