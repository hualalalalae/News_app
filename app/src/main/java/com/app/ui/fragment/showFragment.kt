package com.app.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.model.News
import com.app.NewsApplication
import com.app.model.NewsResponse
import com.app.R
import com.app.model.NetWorkLog
import com.app.ui.adapter.NewsAdapter
import com.app.ui.adapter.NewsAdapter.Companion.FAILED
import com.app.ui.adapter.NewsAdapter.Companion.FINISHED
import com.app.ui.adapter.NewsAdapter.Companion.HAS_MORE
import com.app.ui.adapter.showAdapter
import com.app.util.isNetworkAvailable
import com.app.util.showToast
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.litepal.LitePal
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 * 新闻碎片：newType是拼音,用于url  category是汉字,用于sql
 */
open class showFragment() : Fragment() {
    // 注意:通过构造方法为Fragment传递参数很不优雅，在Fragment restore的时候就会丢失参数。
    // 一般推荐通过将参数设置到Bundle里面，然后调用fragment.setArguments方法传递参数，这样可以保证系统恢复Fragment是还能拿到传入的参数。

    protected  lateinit var newsRecyclerView: RecyclerView

    protected  lateinit var swipeRefreshLayout: SwipeRefreshLayout

    /**
     * 成员变量 newsList 作为视图中 newsRecyclerView 的 dataSet
     */
    private  val newsList = ArrayList<News>()

    private  val newsAdapter = showAdapter(newsList, this)

    /**
     *  成员变量 isLoading 作为一个标记,保证 loadNewData() 和 loadCacheData() 这两个函数同一时间只有一个正在执行
     */
    protected var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.show_list, container, false)
        newsRecyclerView = view.findViewById(R.id.show_recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.show_refresh)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        newsRecyclerView.layoutManager = LinearLayoutManager(NewsApplication.context)
        newsRecyclerView.adapter = newsAdapter

        // 功能1:创建页面后立即获取收藏列表，并刷新到UI上
        loadNewData()

        // 功能2:下拉刷新
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#03A9F4"))
        swipeRefreshLayout.setOnRefreshListener {
            thread {
                Thread.sleep(700) // 延迟0.7秒只是为了实现视觉效果
                activity?.runOnUiThread {
                    loadNewData()
                    swipeRefreshLayout.isRefreshing = false // 让圆形进度条停下来
                }
            }
        }

        //  功能3:滑动到底部加载更多数据，数据来自本地数据库的缓存
        newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) return // 不监听向上滑动的动作，只监听向下滑动的动作
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val position = layoutManager.findLastVisibleItemPosition()
                if (position == newsAdapter.itemCount - 1) {
                    // 向下滑动到底部时，立即加载缓存数据
                    loadCacheData()
                }
            }
        })
    }

    public fun loadNewData() {
        if (isLoading) return
        isLoading = true
        //获取目前收藏了的列表
        val dataFromDatabase = getDataFromDatabase(6)

        // 刷新UI
        activity?.runOnUiThread {
            replaceDataInRecyclerView(dataFromDatabase)
            newsAdapter.footerViewStatus = HAS_MORE
            isLoading = false
        }
    }

    fun loadCacheData() {
        if (isLoading) return
        if (newsAdapter.footerViewStatus != HAS_MORE) return
        isLoading = true
        thread {
            try {
                Thread.sleep(1000) // 延迟1秒只是为了实现视觉效果
                // 注意在缓存时让越早的新闻,id越小
                val newData = getDataFromDatabase(6, minIdInNewsList() - 1)
                if (newData.isEmpty()) {
                    newsAdapter.footerViewStatus = FINISHED
                    activity?.runOnUiThread {
                        // 若数据加载完毕，则只更新最后一个列表项(即 footer_view)的UI为结束状态
                        // 这里notify了之后，newsAdapter会执行一遍onBindViewHolder重绘UI
                        newsAdapter.notifyItemChanged(newsAdapter.itemCount - 1)
                        isLoading = false
                    }
                } else {
                    // 将旧数据和新数据合并到一个新的list中
                    val list = listOf(newsList, newData).flatten()
                    activity?.runOnUiThread {
                        // 若数据加载成功，则更新整个RecyclerView
                        replaceDataInRecyclerView(list)
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                newsAdapter.footerViewStatus = FAILED
                activity?.runOnUiThread {
                    // 若数据加载失败，则只更新最后一个列表项(即 footer_view)的UI为失败状态
                    // 这里notify了之后，newsAdapter会执行一遍onBindViewHolder重绘UI
                    newsAdapter.notifyItemChanged(newsAdapter.itemCount - 1)
                    isLoading = false
                }
            }
        }
    }

    protected fun getDataFromDatabase(limitCount: Int = 6, maxId: Long = -996): List<News> {
        // 由于在保存来自网络的数据时将列表翻转了一次,而插入数据库时id是自增的,因此越旧的新闻 id越小
        return if (maxId < 0) {
            // 小于 0的id是无意义的，不拼接到 SQL中
            // 将数据库中所有category类型的所有新闻按id降序排序,从前往后取出最多limitCount条
            LitePal.where("collect=?", "1")//按照category查找项
                .order("id desc")//按照id降序排列
                .limit(6)//指定查询个数
                .find(News::class.java)// 返回的对象的类
        } else {
            // 将数据库中所有category类型且id不超过maxId的所有新闻按id降序排序,从前往后取出最多limitCount条
            LitePal.where("collect=?", "1", maxId.toString())//按照category查找项
                .order("id desc")//按照id降序排列
                .limit(6)//指定查询个数
                .find(News::class.java)// 返回的对象的类
        }
    }

    /**
     * 获取当前newsList中所有新闻中id的最小值(一定是正整数),  如果newsList为空则返回-1
     */
    protected fun minIdInNewsList(): Long {
        return if (newsList.isNullOrEmpty()) {
            -1
        } else {
            var minId = newsList[0].id
            for (i in newsList.indices) {
                val id = newsList[i].id
                if (id < minId) {
                    minId = id
                }
            }
            minId
        }
    }

    /**
     * 刷新UI操作:用 newData 替换掉 RecyclerView中所有的旧数据
     */
    @SuppressLint("NotifyDataSetChanged")
    protected fun replaceDataInRecyclerView(newData: List<News>) {
        try {
            newsList.clear()
            newsList.addAll(newData)
            newsAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}