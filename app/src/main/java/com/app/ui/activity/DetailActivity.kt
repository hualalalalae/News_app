package com.app.ui.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.app.R
import com.app.model.News
import org.litepal.LitePal


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 设置标题栏
        val toolbar = findViewById<Toolbar>(R.id.detail_tool_bar)
        setSupportActionBar(toolbar)

        //获取当前的事件的收藏状态,初始化收藏图标
        var collects=findViewById<ImageView>(R.id.collects)
        var choice= intent.getIntExtra("collect=",0)
        Log.d("collect1",choice.toString())
        if(choice.equals(1)){
            collects.setImageResource(R.drawable.collecting)
        }else if(choice.equals(0)){
            collects.setImageResource(R.drawable.collect)
        }
        choice= intent.getIntExtra("collect=",0)
        Log.d("collect2",choice.toString())


        val bitmap = collects.drawable//获取当前的图片
        var ItemId = intent.getLongExtra("id=",0)
        Log.d("id",ItemId.toString())
        //收藏的点击事件
        collects.setOnClickListener {
            if(bitmap.equals(R.drawable.collect)){
                collects.setImageResource(R.drawable.collecting)
                val values = ContentValues()
                values.put("collect", 1)
                LitePal.update(News::class.java,values,ItemId)
            }
            else{
                collects.setImageResource(R.drawable.collect)
                val values = ContentValues()
                values.put("collect", 0)
                LitePal.update(News::class.java,values, ItemId)
            }
        }
        // 去掉默认的标题
        title = ""
        // 设置标题的居中文字
        val realTitle = findViewById<TextView>(R.id.detail_real_title)
        realTitle.text = intent.getStringExtra("news_from=")
        // 设置系统自带的home按钮(小箭头)可见
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 打开新闻网页
        val webView = findViewById<WebView>(R.id.news_web_view)
        val url = intent.getStringExtra("url=")
        if (url != null) {
            webView.loadUrl(url)
        }
    }

    // 给Activity加载标题栏的菜单项
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //写一个menu的资源文件.然后创建就行了.
        menuInflater.inflate(R.menu.detail_tool_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //给系统自带的home按钮(小箭头)添加点击事件：销毁本页面返回上一级
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


}