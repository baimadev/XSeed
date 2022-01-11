package com.holderzone.library.xseed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.holderzone.library.annotation.XSeedFuncAnnotation

class MainActivity : AppCompatActivity() {

    @XSeedFuncAnnotation(functionDesc = "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}