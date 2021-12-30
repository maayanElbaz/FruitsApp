package com.example.fruitsapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fruitsapp.databinding.FragmentDetailsBinding
import java.text.NumberFormat
import java.util.*


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private var memoryListener: RecycleViewAdapter.imageFromCacheListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            var name = it.getString(NAME)
            binding.name.text = name
            binding.price.text =  "₪"+ it.getString(PRICE)
            binding.description.text = it.getString(DESCRIPTION)
            setImage(name)
        }
    }

    private fun setImage( name: String?) {

        binding.image.setImageBitmap(null)

        if (name != null) {
            val imageBitmap = memoryListener?.getBitmapFromMemCache(name)
            binding.image.setImageBitmap(imageBitmap)
        } else {
            binding.image.setImageResource(R.drawable.baseline_hide_image_blue_grey_400_24dp)
        }
    }

    companion object {
        const val NAME = "name"
        const val IMAGE = "image"
        const val DESCRIPTION = "description"
        const val PRICE = "price"
        @JvmStatic
        fun newInstance(fruit: Fruit): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putString(NAME, fruit.name)
            args.putString(IMAGE, fruit.image)
            args.putString(DESCRIPTION, fruit.description)
            args.putString(PRICE, fruit.price.toString())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        memoryListener = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        memoryListener = null
    }
}