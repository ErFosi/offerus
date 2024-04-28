package com.offerus.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
class locationUtils {

    suspend fun getLocation(context: Context): Location? {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled) {
            return null
        }
        return suspendCancellableCoroutine { continuation ->

            if (!checkPermissions(context)){
                continuation.resume(null){}
                return@suspendCancellableCoroutine
            }

            fusedLocationProviderClient.lastLocation.apply {
                if (isComplete){
                    if (isSuccessful){
                        continuation.resume(result){}
                    } else {
                        continuation.resume(null){}
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    continuation.resume(it){}
                }
                addOnFailureListener {
                    continuation.resume(null) {}
                }
                addOnCanceledListener {
                    continuation.resume(null) {}
                }
            }
        }

    }

    /*suspend fun getLocation(context: Context): Location? {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled) {
            return null
        }

        // Verificar permisos de ubicación en tiempo de ejecución
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no hay permisos, solicitarlos en tiempo de ejecución
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )

        }

        // Continuar con la obtención de la ubicación si los permisos están disponibles
        return suspendCancellableCoroutine { continuation ->
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    continuation.resume(task.result)
                } else {
                    continuation.resume(null)
                }
            }
        }
    }
       */
    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 1001
    }

    fun checkPermissions(context: Context): Boolean {
        with(NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si no hay permisos, solicitarlos en tiempo de ejecución
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSIONS_REQUEST_LOCATION
                )
                return@with
            }
            return !(ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        }
        return !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }
}