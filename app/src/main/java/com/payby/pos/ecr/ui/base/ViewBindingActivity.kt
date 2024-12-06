package com.payby.pos.ecr.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.payby.pos.ecr.ui.BaseActivity
import java.lang.reflect.ParameterizedType

abstract class ViewBindingActivity<T : ViewBinding> : BaseActivity() {

    abstract fun init()

    lateinit var activity: Activity

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle ? ) {
        super.onCreate(savedInstanceState)
        activity = this


        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<T>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as T
            setContentView(binding.root)
        }
        binding.apply { init() }
    }

}