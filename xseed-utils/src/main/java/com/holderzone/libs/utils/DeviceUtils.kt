package com.holderzone.libs.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings


@SuppressLint("MissingPermission")
object DeviceUtils {

    private var deviceId:String? =null

    /**
     * 获取设备ID(设备硬件编号)
     */
    @SuppressLint("PrivateApi", "HardwareIds")
    fun getDeviceId(context: Context):String{
        if (deviceId == null) {
            deviceId = if (brand == "SUNMI"){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Build.getSerial();
                } else {
                    Build.SERIAL;
                }
            }else{
                val c = Class.forName("android.os.SystemProperties")
                val get = c.getMethod("get", String::class.java)
                val deviceId = get.invoke(c, "ro.serialno") as String?
                return if (deviceId == null || deviceId.isBlank()) getUniqueID(context) else deviceId
            }
        }
        return deviceId!!

    }


    private var uniqueID:String? =null

    /**
     * adnroidId
     */

    @SuppressLint("HardwareIds")
    fun getUniqueID(context: Context):String{
        if (uniqueID == null){
            uniqueID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
        return uniqueID!!
    }



    /**
     * 型号
     */
    val model= android.os.Build.MODEL;


    /**
     * 厂商
     */
    val carrier= android.os.Build.MANUFACTURER;


    /**
     * 品牌  商米的品牌名统一为 SUNMI
     */
    val brand: String = android.os.Build.BRAND

}