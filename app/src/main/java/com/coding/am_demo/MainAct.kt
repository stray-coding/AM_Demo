package com.coding.am_demo

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class MainAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AssetsTools.init(this)
        try {
            val root = AssetsTools.getView("activity_main.xml")
            setContentView(root)
            root as ViewGroup
            /*获取图片资源*/
            root.background = BitmapDrawable(resources, AssetsTools.getBitmap("icon.jpg"))
            /*获取控件*/
            val text = root.findViewWithTag("tv") as TextView
            text.text = "我来了啊"
            /*解析xml资源*/
            val factory = DocumentBuilderFactory.newInstance()
            val docBuild = factory.newDocumentBuilder()
            val doc = docBuild.parse(assets.open("test.xml"))
            val persons = doc.getElementsByTagName("person")
            for (i in 0 until persons.length) {
                val element = persons.item(i) as Element
                val name = element.getAttribute("name")
                Log.d("Main", "name:${name}")
            }
            /*使用音乐资源*/
            val startBtn = root.findViewWithTag("start_btn") as Button
            val player = MediaPlayer()
            val mp3 = assets.openFd("Zhandou.mp3")
            player.setDataSource(mp3.fileDescriptor, mp3.startOffset, mp3.length)
            player.prepare()
            startBtn.setOnClickListener {
                player.start()
            }
            val stopBtn = root.findViewWithTag("stop_btn") as Button
            stopBtn.setOnClickListener {
                player.pause()
            }
            /*使用静态html*/
            val htmlBtn = root.findViewWithTag("html_btn") as Button
            htmlBtn.setOnClickListener {
                val intent = Intent(this@MainAct,WebViewAct::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}