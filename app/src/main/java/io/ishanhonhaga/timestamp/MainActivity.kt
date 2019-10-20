package io.ishanhonhaga.timestamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import io.ishanhonhaga.timestamp.lib.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), StampingInterface {

    val TAG: String = MainActivity::class.java.simpleName

    override fun onError(errorMsg: String) {
        Log.d(TAG, "error msg $errorMsg")
    }

    override fun onCompleted(success: Boolean) {
        Log.d(TAG, "success state $success")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val outputFilePath = Environment.getExternalStorageDirectory().toString() + "/Camera"
        val outputFileName = "test_101.jpg"

        var stampModule = StampModule(
            filePath = "/storage/self/primary/DCIM/Camera/IMG_20180608_233210.jpg",
            outputFile = File(outputFilePath, outputFileName),
            datetime = "dateTime",
            location = LocationModule(),
            exifData = ExifModule()
        )

        btn_timestamp.setOnClickListener {
            StampingActivity.stamp(stampModule, this)
        }

    }
}
