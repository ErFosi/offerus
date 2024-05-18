package com.offerus.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.offerus.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

/***********************************************************************************************
 * source: https://github.com/jchodev/jetpack-take-select-photo-image/tree/main
 * *********************************************************************************************/



/**
 * Component to show a profile picture. If 'editable', it will show a + icon to select or take a photo
 * @param uri: target url to preview
 * @param directory: stored directory
 * @param onSetUri: selected / taken uri
 * @param editable: photo can be changed?
 */
@Composable
fun ProfilePicture(
    uri: Uri? = null, //target url to preview
    directory: File? = null, // stored directory
    onSetUri : (Uri) -> Unit = {}, // selected / taken uri
    editable: Boolean // to show the + icon or not
) {
    val context = LocalContext.current
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val authority = stringResource(id = R.string.fileprovider)
    val file = context.createImageFile()

    // for takePhotoLauncher used
    fun getTempUri(): Uri? {
        return  FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            authority, file
        )
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                onSetUri.invoke(it)
            }
        }
    )

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSaved ->
            tempUri.value?.let {
                onSetUri.invoke(it)
            }
        }
    )


    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, launch takePhotoLauncher
            val tmpUri = getTempUri()
            tempUri.value = tmpUri
            takePhotoLauncher.launch(tempUri.value)
        } else {
            // Permission is denied, handle it accordingly
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet){
        MyModalBottomSheet(
            onDismiss = {
                showBottomSheet = false
            },
            onTakePhotoClick = {
                showBottomSheet = false

                val permission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, proceed to step 2
                    val tmpUri = getTempUri()
                    tempUri.value = tmpUri
                    takePhotoLauncher.launch(tempUri.value)
                } else {
                    // Permission is not granted, request it
                    cameraPermissionLauncher.launch(permission)
                }
            },
            onPhotoGalleryClick = {
                showBottomSheet = false
                imagePicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
        )
    }

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        uri?.let {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = it,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                    ,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                if (editable) {
                    IconButton(
                        onClick = { showBottomSheet = true },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .align(Alignment.BottomEnd),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = null,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .size(40.dp),
                            //.padding(bottom = SpacingSmall),
                            tint = MaterialTheme.colorScheme.tertiaryContainer,

                        )
                    }
                }
            }
        }

    }
}

@Composable
fun MyModalBottomSheet(
    onDismiss: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onPhotoGalleryClick: () -> Unit
) {
    MyModalBottomSheetContent(
        //header = "Choose Option",
        onDismiss = {
            onDismiss.invoke()
        },
        items = listOf(
            BottomSheetItem(
                title = stringResource(R.string.tomarFoto),
                icon = ImageVector.vectorResource(R.drawable.baseline_photo_camera_24),
                onClick = {
                    onTakePhotoClick.invoke()
                }
            ),
            BottomSheetItem(
                title = stringResource(R.string.select_photo),
                icon = ImageVector.vectorResource(R.drawable.baseline_image_24),
                onClick = {
                    onPhotoGalleryClick.invoke()
                }
            ),
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyModalBottomSheetContent(
    onDismiss: () -> Unit,
    //header
    header: String = stringResource(R.string.elige_opcion),

    items: List<BottomSheetItem> = listOf(),
) {
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val windowInsets = if (edgeToEdgeEnabled)
        WindowInsets(0) else BottomSheetDefaults.windowInsets

    ModalBottomSheet(
        shape = MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        ),
        onDismissRequest = { onDismiss.invoke() },
        sheetState = bottomSheetState,
        windowInsets = windowInsets
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = header,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            items.forEach {item ->
                androidx.compose.material3.ListItem(
                    modifier = Modifier.clickable {
                        item.onClick.invoke()
                    },
                    headlineContent = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                )
            }
            Spacer(modifier = Modifier.size(50.dp))
        }
    }
}

data class BottomSheetItem(
    val title: String = "",
    val icon: ImageVector,
    val onClick: () -> Unit
)

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}

fun Context.createImageFileFromBitMap(bitmap: Bitmap, username: String): Uri {
    // Create an image file name
    //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + username + "_"
    val imageFile = File(externalCacheDir, "${imageFileName}.jpg")

    // Write the bitmap to the file
    FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
    }

    return imageFile.toUri()
}

fun Context.getBipMapFromUri(uri: Uri): Bitmap? {
    return uri.let { uri ->

        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.use {
            val exif = ExifInterface(it)
            val orientation =
                exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            if (bitmap != null) {
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                val outputStream = ByteArrayOutputStream()
                return try {
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    val compressedByteArray = outputStream.toByteArray()
                    BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)
                } catch (e: IOException) {
                    Log.e("CompressImage", "Error al escribir el archivo comprimido: ${e.message}")
                    null
                } catch (e: Exception) {
                    Log.e("CompressImage", "Error al escribir el archivo comprimido: ${e.message}")
                    null
                }
            } else {
                Log.e("DecodeBitmap", "Error al decodificar el bitmap desde el URI.")
                null
            }
        }
    }
}


/*fun Context.getBipMapFromUri(uri: Uri): Bitmap? {
    return uri.let { uri ->

        val inputStream = contentResolver.openInputStream(uri)

        val exif = ExifInterface(inputStream!!)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }


        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val compressedByteArray = outputStream.toByteArray()
            BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)

        } catch (e: IOException) {
            Log.e("CompressImage", "Error al escribir el archivo comprimido: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("CompressImage", "Error al escribir el archivo comprimido: ${e.message}")
            null
        }

    }
}*/

fun Context.getFileFromUri(uri: Uri): File? {
    return uri.let { uri ->

        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()


        val outputFile = File.createTempFile("temp", null, cacheDir)

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val fileOutputStream = FileOutputStream(outputFile)
            fileOutputStream.write(outputStream.toByteArray())
            fileOutputStream.close()
            outputFile
        } catch (e: IOException) {
            Log.e("CompressImage", "Error al escribir el archivo comprimido: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("CompressImage", "Error al escribir el archivo comprimido: ${e.message}")
            null
        }

    }
}

// preview image area
@Composable
@Preview
fun PreviewImageArea() {
    val uri =
        remember { mutableStateOf<Uri?>(Uri.parse("android.resource://com.aimarsg.serietracker/drawable/baseline_adb_24")) }

    //image to show bottom sheet
    ProfilePicture(
        directory = File("images"),
        uri = uri.value,
        onSetUri = {
            uri.value = it
        },
        editable = true
    )
}

@Composable
@Preview
fun PreviewImageArea2() {
    val uri =
        remember { mutableStateOf<Uri?>(Uri.parse("android.resource://com.aimarsg.serietracker/drawable/baseline_adb_24")) }

    //image to show bottom sheet
    ProfilePicture(
        directory = File("images"),
        uri = uri.value,
        onSetUri = {
            uri.value = it
        },
        editable = false
    )
}
