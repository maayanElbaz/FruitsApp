package com.example.fruitsapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fruitsapp.RecycleViewAdapter.MyViewHolder
import com.example.fruitsapp.databinding.FragmentHomeBinding
import com.example.fruitsapp.databinding.FruitItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import com.example.fruitsapp.R.drawable
import java.net.URL
import com.google.android.material.shape.CornerFamily

import android.R
import android.content.res.Resources


class RecycleViewAdapter : RecyclerView.Adapter<MyViewHolder>(){
    private var fruitsList: ArrayList<Fruit> = ArrayList()
    private var mListener: onItemClickListener? = null
    private var imageMemoryListenerListener: imageFromCacheListener? = null


    interface onItemClickListener {
        fun onFruitItemClick(fruit: Fruit)
    }

    interface imageFromCacheListener {
        fun getBitmapFromMemCache(imageKey: String): Bitmap?
        fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?)
    }

    fun setUpdateData(items: ArrayList<Fruit>) {
        this.fruitsList = items
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: onItemClickListener?) {
        mListener = listener
    }

    fun setImageFromCacheListener(listener: imageFromCacheListener?) {
        imageMemoryListenerListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FruitItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(fruitsList[position], mListener)
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
     return fruitsList.size
    }

    inner class MyViewHolder (private val viewBind: FruitItemBinding, context: Context) :
        RecyclerView.ViewHolder(viewBind.root), View.OnClickListener {

        init {
            viewBind.fruit.setOnClickListener(this)
        }
        fun bind(fruit: Fruit, listener: onItemClickListener?) {
            viewBind.apply {
                title.text = fruit.name
                image.setImageBitmap(null)
                setImage(fruit)
            }
        }

        override fun onClick(v: View?) {
            mListener?.onFruitItemClick(fruitsList[adapterPosition])
        }

        private fun FruitItemBinding.setImage(fruit: Fruit){
            if (fruit.image != null) {

                val imageFromCache =
                    imageMemoryListenerListener?.getBitmapFromMemCache(fruit.name)
                if (imageFromCache == null) {
                    GlobalScope.launch(Dispatchers.IO) {

                        val imageUrl = URL(fruit.image)

                        val httpConnection = imageUrl.openConnection() as HttpURLConnection
                        httpConnection.doInput = true
                        httpConnection.connect()

                        val inputStream = httpConnection.inputStream
                        val bitmapImage = BitmapFactory.decodeStream(inputStream)
                        imageMemoryListenerListener?.addBitmapToMemoryCache(
                            fruit.name,
                            bitmapImage
                        )
                        launch(Dispatchers.Main) {
                            val radius: Float = 40F
                            image.shapeAppearanceModel = image.shapeAppearanceModel
                                .toBuilder()
                                .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                                .build()
                            image.setImageBitmap(bitmapImage)
                        }
                    }
                } else {
                    image.setImageBitmap(imageFromCache)
                }

            } else {
                image.setImageResource(drawable.baseline_hide_image_blue_grey_400_24dp)
            }
        }

    }

}