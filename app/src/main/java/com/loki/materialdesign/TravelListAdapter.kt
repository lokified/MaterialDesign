package com.loki.materialdesign

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.loki.materialdesign.databinding.RowPlacesBinding
import com.squareup.picasso.Picasso

class TravelListAdapter(
    private val context: Context
): RecyclerView.Adapter<TravelListAdapter.ViewHolder>() {

    lateinit var itemCLickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowPlacesBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val place = PlaceData.placeList()[position]

        place.apply {

            holder.binding.placeName.text = this.name

            Picasso.get().load(this.getImageResourceId(context)).into(holder.binding.placeImage)

            val photo = BitmapFactory.decodeResource(context.resources, this.getImageResourceId(context))

            Palette.from(photo).generate { palette ->

                val bgColor = palette?.getMutedColor(ContextCompat.getColor(context, android.R.color.black))

                holder.binding.placeNameHolder.setBackgroundColor(bgColor!!)
            }
        }
    }



    override fun getItemCount() = PlaceData.placeList().size


    fun setOnItemClickListener(clickListener: OnItemClickListener) {

        this.itemCLickListener = clickListener
    }

    inner class ViewHolder(val binding: RowPlacesBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {

            binding.placeHolder.setOnClickListener(this)
        }

        override fun onClick(p0: View?) = itemCLickListener.onItemClick(itemView, adapterPosition)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}