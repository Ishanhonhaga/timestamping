package io.ishanhonhaga.timestamp.lib

import android.annotation.SuppressLint
import android.graphics.*
import android.location.Location
import android.media.ExifInterface
import android.os.SystemClock
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.RequiresPermission
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*
import java.io.File
import java.security.Permission
import java.security.Permissions
import kotlin.coroutines.CoroutineContext

class Stamping : CoroutineScope {

    private val compositeJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + compositeJob

    private lateinit var stampInterface: StampingInterface

    @Keep
    public fun initStamping(stampMod: StampModule, stampInterface: StampingInterface) {
        addTimeStamping(stampMod)
        this.stampInterface = stampInterface
    }

    /**
     * Method to time the saved image
     */
    private fun addTimeStamping(stampMod: StampModule) {
        async {
            try {
                val filePath = stampMod.filePath
                if (!File(filePath).exists()) {
                    stampInterface.onError("File Does not exist")
                    return@async
                }
                val bmpOptions = BitmapFactory.Options()
                bmpOptions.inJustDecodeBounds = true
                BitmapFactory.decodeFile(filePath, bmpOptions)

                val bitmapHeight = bmpOptions.outHeight
                val bitmapWidth = bmpOptions.outWidth

                val finalBitmap: Bitmap = Bitmap.createBitmap(
                    bitmapWidth,
                    bitmapHeight,
                    Bitmap.Config.RGB_565
                )
                bmpOptions.inJustDecodeBounds = false

//                val initialBitmap: Bitmap =
//                    BitmapFactory.decodeByteArray(pictureResult.data, 0, pictureResult.data.size)
                val initialBitmap: Bitmap = BitmapFactory.decodeFile(filePath, bmpOptions)
                val canvas = Canvas()
                canvas.setBitmap(finalBitmap)
                canvas.drawBitmap(initialBitmap, 0f, 0f, null)

                val canvasHeight = bitmapHeight
                val canvasWidth = bitmapWidth

                val textSize = 0.02f * canvasHeight
                val paint = getPaintObject(textSize)

                val currentTime = Calendar.getInstance().time
                val simpleDateFormat = SimpleDateFormat("YYYY-MM-dd HH:mm")
                val currentDateTime = simpleDateFormat.format(currentTime)
                val currentLocation =
                    "${stampMod.location.latitude},${stampMod.location.longitude}"

                // Writing Date and Time
                canvas.drawText(
                    currentDateTime,
                    canvasWidth - paint.measureText(currentDateTime) - 15,
                    canvasHeight - 180f,
                    paint
                )

                // Writing Latitude and Longitude
                canvas.drawText(
                    currentLocation,
                    canvasWidth - paint.measureText(currentLocation) - 15,
                    canvasHeight - 135f,
                    paint

                )

                // Writing the file
                val outPutFilePath = stampMod.outputFile
                val outStream = FileOutputStream(outPutFilePath)
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)

                outStream.flush()
                outStream.close()

                // Recycling Bitmaps
                initialBitmap.recycle()
                finalBitmap.recycle()

                //Adding Exif Data
                stampMod.exifData?.let {
                    addImageMetaData(stampMod)
                }

                //Scanning Media
                scanMedia(outPutFilePath.path)

                stampInterface.onCompleted(true)
            } catch (e: Exception) {
                Log.d("TimeStamp", " Exception throwing $e")
                stampInterface.onError("$e")
            }
        }
    }

    private fun getOutputFilePath(filePath: String): String {
        return when {
            filePath.contains(".jpeg") -> return filePath.replace(".jpeg", "_01.jpeg")
            filePath.contains(".jpg") -> return filePath.replace(".jpeg", "_01.jpg")
            filePath.contains(".webp") -> return filePath.replace(".jpeg", "_01.webp")
            filePath.contains(".png") -> return filePath.replace(".jpeg", "_01.png")
            else -> return ""
        }
    }

    private fun scanMedia(filePath: String?) {
//        MediaScannerConnection.scanFile()
        Log.d("TimeStamp", "Media Scanning ")
    }


    @SuppressLint("NewApi")
    private fun addImageMetaData(
        stampMod: StampModule
    ) {
        stampMod.exifData?.let {
            val exifInterface = ExifInterface(stampMod.filePath)
            exifInterface.setAttribute(
                ExifInterface.TAG_DATETIME,
                stampMod.exifData.TAG_DATETIME ?: ""
            )
            exifInterface.setAttribute(
                ExifInterface.TAG_GPS_LONGITUDE,
                stampMod.exifData.TAG_LOCATION?.longitude.checkForNull()
            )
            exifInterface.setAttribute(
                ExifInterface.TAG_GPS_LATITUDE,
                stampMod.exifData.TAG_LOCATION?.latitude.checkForNull()
            )
            exifInterface.setAttribute(
                ExifInterface.TAG_COPYRIGHT,
                stampMod.exifData.TAG_COPYRIGHT ?: ""
            )
            exifInterface.setAttribute(
                ExifInterface.TAG_ARTIST,
                stampMod.exifData.TAG_ARTIST ?: ""
            )
            exifInterface.setAttribute(
                ExifInterface.TAG_DATETIME_DIGITIZED,
                stampMod.exifData.TAG_DATETIME_DIGITIZED ?: ""
            )
            exifInterface.setAttribute(
                ExifInterface.TAG_DATETIME_ORIGINAL,
                stampMod.exifData.TAG_DATETIME_ORIGINAL ?: ""
            )
            exifInterface.setAttribute(
                ExifInterface.TAG_ORIENTATION,
                stampMod.exifData.TAG_ORIENTATION.checkForNull()
            )
            exifInterface.saveAttributes()
        }
    }

    fun <T> T.checkForNull(): String {
        return if (this != null)
            this as String
        else
            ""
    }


    fun getPaintObject(textSize: Float): Paint {
        val paint = Paint()
        paint.textSize = textSize
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        return paint
    }

}