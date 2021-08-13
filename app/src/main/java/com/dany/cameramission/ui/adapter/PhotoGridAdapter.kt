package com.dany.cameramission.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dany.cameramission.R
import com.dany.cameramission.base.BaseViewHolder
import com.dany.cameramission.databinding.LayoutAddItemBinding
import com.dany.cameramission.databinding.LayoutPhotoItemBinding
import com.dany.cameramission.model.PhotoGridItem
import com.dany.cameramission.model.PhotoGridType

class PhotoGridAdapter(
  private val onClickAdd: () -> Unit = {},
  private val onClickPhoto: (photo: Bitmap?) -> Unit = {},
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var itemList = listOf(
    PhotoGridItem(type = PhotoGridType.ADD)
  )
    set(value) {
      field = value.plus(PhotoGridItem(type = PhotoGridType.ADD))

      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
    when (viewType) {
      PhotoGridType.ADD.ordinal -> AddItemViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.layout_add_item, parent, false))
      else -> PhotoItemViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.layout_photo_item, parent, false))
    }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is PhotoItemViewHolder) {
      holder.bindItem(itemList[position])
    }
  }

  override fun getItemViewType(position: Int): Int = when (position) {
    itemList.lastIndex -> PhotoGridType.ADD.ordinal
    else -> PhotoGridType.ITEM.ordinal
  }

  override fun getItemCount(): Int = itemList.size

  inner class PhotoItemViewHolder(view: View) : BaseViewHolder<LayoutPhotoItemBinding>(view) {
    private var photo: Bitmap? = null

    init {
      binding.viewPhoto.setOnClickListener {
        onClickPhoto(photo)
      }
    }

    fun bindItem(item: PhotoGridItem) {
      photo = item.bitmap

      Glide.with(binding.viewPhoto)
        .load(item.bitmap)
        .into(binding.viewPhoto)
    }
  }

  inner class AddItemViewHolder(view: View) : BaseViewHolder<LayoutAddItemBinding>(view) {
    init {
      binding.viewAdd.setOnClickListener {
        onClickAdd()
      }
    }
  }
}