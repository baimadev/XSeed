package com.holderzone.library.xseed

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.holderzone.library.XSeedClient
import com.holderzone.library.XSeedRequest
import com.holderzone.library.annotation.XSeedFuncAnnotation

class MainActivity : AppCompatActivity() {

    @XSeedFuncAnnotation(functionDesc = "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_add_request).setOnClickListener {
            for (i in 0..100) {
                XSeedClient.createRequestAndEnqueue("test")
            }

        }
    }
}