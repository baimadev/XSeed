package com.holderzone.library

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2021/12/29
 */
object XseedFileUtils {

    private const val file ="./log.txt"

    var filepath =""

    fun init(str:String){
        filepath = str
    }

    /**
     * 文件数据写入（如果文件夹和文件不存在，则先创建，再写入）
     * @param filePath
     * @param content
     * @param flag true:如果文件存在且存在内容，则内容换行追加；false:如果文件存在且存在内容，则内容替换
     */
    fun fileLinesWrite(content: String?) {
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val time = dateFormat.format(date)
        var fw: FileWriter? = null
        try {
            val file = File("${filepath}/log.txt")
            //如果文件夹不存在，则创建文件夹
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            fw = if (!file.exists()) { //如果文件不存在，则创建文件,写入第一行内容
                file.createNewFile()
                FileWriter(file)
            } else {
                //如果文件存在,则追加或替换内容
                FileWriter(file, true)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val pw = fw?.let { PrintWriter(it) }
        pw?.println("${time}->>>>$content")
        pw?.flush()
        try {
            fw?.flush()
            pw?.close()
            fw?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}