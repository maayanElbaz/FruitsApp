package com.example.fruitsapp

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fruitsapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recycleAdapter: RecycleViewAdapter
    private var listener: RecycleViewAdapter.onItemClickListener? = null
    private var memoryListener: RecycleViewAdapter.imageFromCacheListener? = null
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var memoryCache: LruCache<String, Bitmap>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        initRecyclerView()
        initViewModel(savedInstanceState == null) // if configuration change and we have fruits list stored
        return binding.root
    }


    private fun initRecyclerView() {
        val recyclerView = binding.RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recycleAdapter = RecycleViewAdapter()
        recycleAdapter.setOnItemClickListener(listener)
        recycleAdapter.setImageFromCacheListener(memoryListener)
        recyclerView.adapter = recycleAdapter
    }

    private fun initViewModel(callToApi: Boolean) {
        viewModel.setMemoryCache(memoryCache)
        viewModel.getRecyclerListObserver(callToApi)
            .observe(viewLifecycleOwner, Observer<FruitsListModel> {
                if (it != null) {
                    recycleAdapter.setUpdateData(it.fruits)

                } else {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as MainActivity
        memoryListener = context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        memoryListener = null
    }
    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}