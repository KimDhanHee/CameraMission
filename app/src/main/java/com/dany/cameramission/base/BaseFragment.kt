package com.dany.cameramission.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

class BaseFragment<VDB : ViewDataBinding>(
  @LayoutRes
  private val layoutResId: Int,
) : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = DataBindingUtil.inflate<VDB>(inflater, layoutResId, container, false).run {
    lifecycleOwner = this@BaseFragment

    bindingVM()
    bindingViewData()
    setEventListener()
    root
  }

  protected open fun VDB.bindingVM() {}
  protected open fun VDB.bindingViewData() {}
  protected open fun VDB.setEventListener() {}
}