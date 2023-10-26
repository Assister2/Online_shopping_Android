package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import cliqbuy.R
import cliqbuy.model.CommonHomeModel
import com.squareup.picasso.Picasso

class HomeTopBannerAdapter(val context: Context, val bannerList: CommonHomeModel) : PagerAdapter() {

    lateinit var iv_banner:ImageView

    override fun getCount(): Int {
        return bannerList.data!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_top_banner, null)
        iv_banner = view.findViewById(R.id.iv_banner_image)
        val viewPager = container as ViewPager
        viewPager.addView(view, 0)

            Picasso.get()
                .load(context.resources.getString(R.string.imageUrl) + bannerList.data!![position].photo)
                .placeholder(R.drawable.ic_empty_cart).into(iv_banner)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}