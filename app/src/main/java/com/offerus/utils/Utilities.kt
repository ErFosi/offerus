package com.offerus.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.offerus.data.Deal

fun showToastOnMainThread(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

fun createDealListExample(): List<Deal> {
    val dealList = mutableListOf<Deal>()

    repeat(20) {
        val deal = Deal(
            id = it + 1,
            nota_cliente = "-1",
            nota_host = "-1",
            username_cliente = listOf("cuadron11").random(),
            username_host = listOf("cuadron11").random(),
            id_peticion = (1..100).random(),
            estado = listOf("Aceptada", "Rechazada", "Pendiente").random()
        )
        dealList.add(deal)
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