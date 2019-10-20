package io.ishanhonhaga.timestamp.lib

import android.location.Location
import androidx.annotation.Keep
import java.io.File

@Keep
data class StampModule(
    val filePath: String,
    val outputFile: File,
    val datetime: String,
    val location: LocationModule,
    val exifData: ExifModule
)

@Keep
data class LocationModule(
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Keep
data class ExifModule(
    val TAG_DATETIME: String? = "",
    val TAG_LOCATION: LocationModule? = LocationModule(),
    val TAG_COPYRIGHT: String? = "",
    val TAG_ARTIST: String? = "",
    val TAG_DATETIME_DIGITIZED: String? = "",
    val TAG_DATETIME_ORIGINAL: String? = "",
    val TAG_ORIENTATION: Int? = null
)
