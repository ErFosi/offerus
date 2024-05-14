package com.offerus.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.offerus.R


enum class BiometricAuthenticationStatus {
    AUTHENTICATED,
    NO_CREDENTIALS,
    CREDENTIALS_ERROR,
    ERROR,
    NOT_AUTHENTICATED_YET

}
enum class DeviceBiometricsSupport {
    SUPPORTED,
    NOT_CONFIGURED,
    UNSUPPORTED
}
class BiometricAuthManager(
    context: Context, onAuthenticationSucceeded: () -> Unit,
) {

    /*------------------------------------------------
    |              Manager's Properties              |
    ------------------------------------------------*/

    private val biometricManager = BiometricManager.from(context)

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback =
        // Con "object" definimos un singleton que hereda de BiometricPrompt.AuthenticationCallback
        object : BiometricPrompt.AuthenticationCallback() {

            // On authentication succeeded
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthenticationSucceeded()
            }

            // On authentication failed
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
            }

            // On Error
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                // If the error is known we print only the error string (that represents pretty good the error)
                if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    Toast.makeText(context, "$errString", Toast.LENGTH_SHORT).show()
                }

                // Else indicate that is an error and then the string.
                else if (errorCode != BiometricPrompt.ERROR_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // Prompt data showed to the user
    private val biometricPromptInfo: BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_auth_prompt_title))
            .setSubtitle(context.getString(R.string.biometric_auth_prompt_text))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .build()


    // Prompt generation
    private val biometricPrompt: BiometricPrompt =
        BiometricPrompt(context as FragmentActivity, ContextCompat.getMainExecutor(context), authenticationCallback)


    /*------------------------------------------------
    |                    Methods                     |
    ------------------------------------------------*/

    // Method to ask for authentication
    fun submitBiometricAuthorization() {
        biometricPrompt.authenticate(biometricPromptInfo)
    }


    /**
     * Method to check device's current support for biometrics
     * It maps [BiometricManager]'s canAuthenticate returned int to a [DeviceBiometricsSupport] instance.
     */
    fun checkBiometricSupport(): DeviceBiometricsSupport {
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> DeviceBiometricsSupport.SUPPORTED
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> DeviceBiometricsSupport.NOT_CONFIGURED
            else -> DeviceBiometricsSupport.UNSUPPORTED
        }
    }


    companion object {
        //Static method to send the user to settings in order to enroll a biometric authentication.
        fun makeBiometricEnroll(context: Context) {
            val intent: Intent = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG)
                }
                Build.VERSION.SDK_INT == Build.VERSION_CODES.P -> {
                    @Suppress("DEPRECATION")
                    Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                }
                else -> {
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                }
            }
            try {
                context.startActivity(intent)
            } catch (error: ActivityNotFoundException) {
                context.startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }
    }
}