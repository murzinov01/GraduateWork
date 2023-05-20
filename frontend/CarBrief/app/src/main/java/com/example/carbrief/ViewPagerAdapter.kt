package com.example.carbrief

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(private val context: Context) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private var curPage: Int = 0
    private val imgArray = arrayOf(
        R.drawable.car_interior_1,
        R.drawable.car_interior_2,
        R.drawable.car_interior_3,
    )

    private val tittleArray = arrayOf(
        context.getString(R.string.onboarding_title_1),
        context.getString(R.string.onboarding_title_2),
        context.getString(R.string.onboarding_title_3),
    )

    private val descriptionArray = arrayOf(
        context.getString(R.string.onboarding_description_1),
        context.getString(R.string.onboarding_description_2),
        context.getString(R.string.onboarding_description_3),
    )

    override fun getCount(): Int {
        return tittleArray.size
    }

    fun setCurPage(page: Int) {
        if (page >= count) {
            curPage = count
        } else {
            curPage = page
        }
    }

    fun getCurPage(): Int {
        return curPage
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slider, container, false)

        val img = view.findViewById<ImageView>(R.id.carInteriorImage)
        val txtTittle = view.findViewById<TextView>(R.id.txtTittle)
        val txtDescription = view.findViewById<TextView>(R.id.txtDescription)

        img.setImageResource(imgArray[position])
        txtTittle.text = tittleArray[position]
        txtDescription.text = descriptionArray[position]

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}