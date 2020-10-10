[篇幅太长不想看，直接看总结](#总结)

总所周知，Android中Activity加载布局的方式常用的有以下几种：
```
setContentView(View view)    
setContentView(@LayoutRes int layoutResID)
```
View的加载方式可通过下列方式加载：
```
View.inflate(Context context, @LayoutRes int resource, ViewGroup root)
LayoutInflater.from(Context context).inflate(@LayoutRes int resource, @Nullable ViewGroup root)
LayoutInflater.from(Context context).inflate(XmlPullParser parser, @Nullable ViewGroup root)
```
由于Android的特殊机制，assets和raw目录下的文件不会被编译（即不能通过R.xx.id访问），所以我们只能采用LayoutInflater.from(Context context).inflate(XmlPullParser parser, @Nullable ViewGroup root)方法来访问其中的xml布局。
所以我们接着来看如何获取到XmlPullParset对象，通过context.assets可获取到AssetManager对象，而AssetManager则可以通过openXmlResourceParser(@NonNull String fileName)获取到XmlResourceParser对象

所以通过上面的分析我们可得出下列代码：
```
    fun getView(ctx: Context, filename: String): View? {
        return LayoutInflater.from(ctx).inflate(am.openXmlResourceParser(filename), null)
    }
```
1.当我们兴高采烈的写好demo，实机运行时，会遇到第一个坑：
程序抛出了FileNotFoundException的异常
```
java.io.FileNotFoundException: activity_main.xml
```
通过查阅资料后你发现原来要在文件名的前面加上"assets/"的前缀

2.这时你修改了你的代码，要对文件前缀进行判断
```
    fun getView(ctx: Context, filename: String): View? {
        var name = filename
        if(!filename.startsWith("assets/")){
            name = "assets/$filename"
        }
        return LayoutInflater.from(ctx).inflate(am.openXmlResourceParser(name), null)
    }
```
修改完代码后，你紧接着开始了第二波测试，却发现程序又抛出了异常：
```
java.io.FileNotFoundException: Corrupt XML binary file
```
这个错误则代表这你的xml布局文件格式不对，放入到assets目录下的xml文件应该是编译后的文件（即apk中xml文件）如下图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009161006275.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2Mzc4ODM2,size_16,color_FFFFFF,t_70#pic_center)

3.于是你将你的apk中的layout/activity_main.xml拷贝到工程的assets目录下，开始了第三波测试：
这时你发现APK运行正常，但是你冥冥中发现了一丝不对劲，你发现你即使能拿到该布局所对应的ViewGroup,却发现并不能通过findViewById(id)方法来获取到子View,于是你开始查看ViewGroup的源码，机智的你发现了如下方法：
```
public final <T extends View> T findViewWithTag(Object tag) {
        if (tag == null) {
            return null;
        }
        return findViewWithTagTraversal(tag);
    }
```
该方法可以通过设置的tag，来获取到对应的子View

4.于是你在xml中为子View设置好tag后，写好代码，开始了第四波测试
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009161502987.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2Mzc4ODM2,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009161602796.png#pic_center)
这时候你查看手机上的APP，发现textView显示的字符发生了改变:
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009161832650.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2Mzc4ODM2,size_16,color_FFFFFF,t_70#pic_center)
## <h2 id="总结">入坑指南</h2>

 1. **java.io.FileNotFoundException: activity_main.xml**   xml布局文件名需加前缀"**assets/**"
 2. **java.io.FileNotFoundException: Corrupt XML binary file**   xml布局文件需要放入**编译后的xml**，如果只是普通的xml文件，则**不需要**
 3. 在xml中对子View设置**tag**，通过ViewGroup的findViewWithTag(tag)方法即可获取到子View
 4. 使用html网页 **"file:///android_asset/$filename**" filename为assets目录下的文件路径

工具类源码：
```
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
```
[demo项目地址](https://github.com/stray-coding/AM_Demo)
