package com.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.R
import com.app.ui.fragment.HomeFragment
import com.app.ui.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val fragmentList = listOf(HomeFragment(), UserFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val contentViewPager = findViewById<ViewPager>(R.id.content_view_pager)
        //设置 fragment页面的缓存数量 , 这里设置成缓存所有的页面！！！！！
        contentViewPager.offscreenPageLimit = fragmentList.size
        contentViewPager.adapter = Adapter(supportFragmentManager)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        // 给底部导航栏的菜单项添加点击事件
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                // smoothScroll=false这个参数能解决切换时的多页闪烁问题
                R.id.nav_home -> contentViewPager.setCurrentItem(0, false)
                R.id.nav_video -> contentViewPager.setCurrentItem(1, false)
                R.id.nav_user -> contentViewPager.setCurrentItem(2, false)
            }
            false
        }
//        val forceOffline = findViewById<Button>(R.id.forceOffline)
//        forceOffline.setOnClickListener {
//            val intent = Intent()
//            intent.action = "thisFORCE_OFFLINE"
//            sendBroadcast(intent)
//            Log.d("MainActivity", "onClick: sended.")
//        }
        // 当页面切换时，将对应的底部导航栏菜单项设置为选中状态
        contentViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p: Int, pOffset: Float, pOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                // 将对应的底部导航栏菜单项设置为选中状态
                bottomNav.menu.getItem(position).isChecked = true
            }
        })

        //TODO 点击左上角头像跳转到“我的”页面
        val id = intent.getIntExtra("id", 0)
        if (id == 2) {
            contentViewPager.setCurrentItem(2, false)
            false
        }
    }

    inner class Adapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }
    }

}