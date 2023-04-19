package com.example.carbrief

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class OnboardingActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var propertyRepo: PropertyRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        propertyRepo = PropertyRepo(this)

        val refreshToken = runBlocking { propertyRepo.readProperty("refreshToken") }
        println(refreshToken)
        if (true || refreshToken != null && refreshToken != "") {
            val intent = Intent(this, BottomNavigationActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val isOnboardingPassed = runBlocking { propertyRepo.readProperty("isOnboardingPassed") }
        if (isOnboardingPassed != null && isOnboardingPassed != "") {
            val intent = Intent(this, StartScreenActivity::class.java)
            startActivity(intent)
            finish()
            return
        }


        setContentView(R.layout.activity_onboarding)

        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)

        viewPager = findViewById(R.id.viewpager)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        dotsIndicator.attachTo(viewPager)

        // Btn Get Started
        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            launch {
                propertyRepo.saveProperty("isOnboardingPassed", "true")
            }
            val intent = Intent(this, StartScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Btn Next
        val btnClickNext = findViewById<Button>(R.id.btnNext)
        btnClickNext.setOnClickListener {
            var curPage = viewPagerAdapter.getCurPage()
            curPage += 1
            viewPagerAdapter.setCurPage(curPage)
            viewPager.currentItem = curPage
        }

        // Btn Skip
        val btnClickSkip= findViewById<Button>(R.id.btnSkip)
        btnClickSkip.setOnClickListener {
            launch {
                propertyRepo.saveProperty("isOnboardingPassed", "true")
            }
            val intent = Intent(this, StartScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                viewPagerAdapter.setCurPage(position)

                if(position == viewPagerAdapter.count - 1) {
                    btnGetStarted.visibility = View.VISIBLE
                    btnClickNext.visibility = View.INVISIBLE
                } else {
                    btnGetStarted.visibility = View.INVISIBLE
                    btnClickNext.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

}