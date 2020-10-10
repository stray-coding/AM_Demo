package com.coding.am_demo

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import java.io.IOException

/**
 * @author: Coding.He
 * @date: 2020/10/9
 * @emil: 229101253@qq.com
 * @des:获取assets目录下资源的工具类
 */
object AssetsTools {
    private lateinit var am: AssetManager
    private lateinit var appCtx: Context

    /**
     * 初始化AssetsTools，使用前必须初始化
     * */
    fun init(ctx: Context) {
        this.appCtx = ctx.applicationContext
        am = ctx.applicationContext.assets
    }

    /**
     * 获取assets目录下的xml布局
     * 需要以.xml结尾
     * */
    @Throws(IOException::class)
    fun getView(filename: String): View? {
        if (!filename.endsWith(".xml"))
            return null
        val name = when {
            filename.startsWith("assets/") -> filename
            else -> "assets/$filename"
        }
        return LayoutInflater.from(appCtx).inflate(am.openXmlResourceParser(name), null)
    }

    /**
     * 获取assets目录下的图片资源
     * */
    fun getBitmap(filename: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val ins = am.open(filename)
            ins.use {
                bitmap = BitmapFactory.decodeStream(ins)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    /**
     * 获取assets目录下的html路径
     * */
    fun getHtmlUrl(filename: String):String{
        return "file:///android_asset/$filename"
    }

}