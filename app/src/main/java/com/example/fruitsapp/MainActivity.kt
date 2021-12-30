package com.example.fruitsapp

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fruitsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), RecycleViewAdapter.onItemClickListener,
    RecycleViewAdapter.imageFromCacheListener {

    private lateinit var binding: ActivityMainBinding
    private var viewModel: MainActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // calling the action bar
        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            val fragment = HomeFragment.newInstance()
            replace(R.id.mainLayout, fragment)
            addToBackStack(null)
        }

        viewModel?.getErrorLiveData()?.observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onFruitItemClick(fruit: Fruit) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            val fragment = DetailsFragment.newInstance(fruit)
            replace(R.id.mainLayout, fragment)
            addToBackStack(null)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun getBitmapFromMemCache(imageKey: String): Bitmap? {
        return viewModel?.getBitmapFromMemCache(imageKey)
    }

    override fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?) {
        viewModel?.addBitmapToMemoryCache(key, bitmap)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}