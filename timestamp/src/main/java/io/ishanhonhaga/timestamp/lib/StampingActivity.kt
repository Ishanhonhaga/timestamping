package io.ishanhonhaga.timestamp.lib

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity

@Keep
class StampingActivity : AppCompatActivity() {

    companion object {
        fun stamp(stampModule: StampModule, stampingInterface: StampingInterface) {
            val stamping = Stamping()
            stamping.initStamping(stampModule, stampingInterface)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }


}