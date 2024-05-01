package com.offerus.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.ContactsContract

fun obtenerContactos(contentResolver: ContentResolver): List<Pair<String, String>> {
    val contactos = mutableListOf<Pair<String, String>>()

    val cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )

    cursor?.use { cursor ->
        val nombreIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numeroIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while (cursor.moveToNext()) {
            val nombre = cursor.getString(nombreIndex)
            val numeroTelefono = cursor.getString(numeroIndex)
            contactos.add(nombre to numeroTelefono)
        }
    }

    return contactos
}

fun crearContacto(contentResolver: ContentResolver, nombre: String, numero: String) {
    val contentValues = ContentValues().apply {
        put(ContactsContract.RawContacts.ACCOUNT_TYPE, null as String?)
        put(ContactsContract.RawContacts.ACCOUNT_NAME, null as String?)
    }

    val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
    val rawContactId = rawContactUri?.lastPathSegment?.toLongOrNull()

    rawContactId?.let {
        val contactValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, it)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nombre)
        }

        contentResolver.insert(ContactsContract.Data.CONTENT_URI, contactValues)

        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, it)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, numero)
            put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        }

        contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
    }
}