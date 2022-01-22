package com.holderzone.library.xseed

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.holderzone.libs.XSeedClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        XSeedClient.executeMethodLog("test")

        findViewById<Button>(R.id.bt_add_request).setOnClickListener {
            //bug
            val list = ArrayList<Int>()
            val d = list[5]
        }

    }

//    @XSeedFuncAnnotation(functionDesc = "测试方法", tag = "debug",isSeedParam = true)
//    fun testMethod(param:String , intParam:Int){
//
//    }
}