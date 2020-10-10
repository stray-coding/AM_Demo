package com.coding.am_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        wb_html.loadUrl(AssetsTools.getHtmlUrl("aaa.html"))
        btn_back.setOnClickListener {
            finish()
        }
    }
}