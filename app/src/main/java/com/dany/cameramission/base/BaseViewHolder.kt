package com.dany.cameramission.base

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<VDB : ViewDataBinding>(view: View) : RecyclerView.ViewHolder(view) {
  open val binding = DataBindingUtil.bind<VDB>(view)!!
}