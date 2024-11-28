package com.payby.pos.common.helper

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.GrayscaleTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

object ImageLoaderHelper {

    fun load(context: Context, url: String, imageView: ImageView, @DrawableRes placeholderResId: Int = 0, @DrawableRes errorResId: Int = 0, width: Int = -1, height: Int = -1, isCircleCrop: Boolean = false, isSkipMemoryCache: Boolean = false) {
        var options = RequestOptions()
        if (placeholderResId != 0) {
            options = options.placeholder(placeholderResId)
        }
        if (errorResId != 0) {
            options = options.placeholder(errorResId)
        }
        if (width != -1 && height != -1) {
            options = options.override(width, height)
        }
        options = options.skipMemoryCache(isSkipMemoryCache)
        if (isCircleCrop) {
            options = options.circleCrop().transform()
        }
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    fun preload(context: Context, url: String) {
        Glide.with(context).load(url).preload()
    }

    fun loadRoundRect(context: Context, url: String, radius: Int, imageView: ImageView, @DrawableRes placeholderResId: Int = 0, @DrawableRes errorResId: Int = 0, width: Int = -1, height: Int = -1, isCircleCrop: Boolean = false) {
        var options = RequestOptions()
        if (placeholderResId != 0) {
            options = options.placeholder(placeholderResId)
        }
        if (errorResId != 0) {
            options = options.placeholder(errorResId)
        }
        if (width != -1 && height != -1) {
            options = options.override(width, height)
        }
        if (isCircleCrop) {
            options = options.circleCrop()
        }
        val centerCrop = CenterCrop()
        val roundedCornersTransformation = RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.ALL)
        val transformation = object : MultiTransformation<Bitmap>(centerCrop, roundedCornersTransformation) {}
        options = options.transform(transformation)
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    fun loadBlur(context: Context, url: String, blur: Int, sampling: Int = 4, imageView: ImageView, @DrawableRes placeholderResId: Int = 0, @DrawableRes errorResId: Int = 0, width: Int = -1, height: Int = -1, isCircleCrop: Boolean = false) {
        var options = RequestOptions()
        if (placeholderResId != 0) {
            options = options.placeholder(placeholderResId)
        }
        if (errorResId != 0) {
            options = options.placeholder(errorResId)
        }
        if (width != -1 && height != -1) {
            options = options.override(width, height)
        }
        if (isCircleCrop) {
            options = options.circleCrop()
        }
        val centerCrop = CenterCrop()
        val blurTransformation = BlurTransformation(blur, sampling)
        val transformation = object : MultiTransformation<Bitmap>(centerCrop, blurTransformation) {}
        options = options.transform(transformation)
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    fun loadBlackAndWhite(context: Context, url: String, imageView: ImageView, @DrawableRes placeholderResId: Int = 0, @DrawableRes errorResId: Int = 0, width: Int = -1, height: Int = -1, isCircleCrop: Boolean = false) {
        var options = RequestOptions()
        if (placeholderResId != 0) {
            options = options.placeholder(placeholderResId)
        }
        if (errorResId != 0) {
            options = options.placeholder(errorResId)
        }
        if (width != -1 && height != -1) {
            options = options.override(width, height)
        }
        if (isCircleCrop) {
            options = options.circleCrop()
        }
        val centerCrop = CenterCrop()
        val grayscaleTransformation = GrayscaleTransformation()
        val transformation = object : MultiTransformation<Bitmap>(centerCrop, grayscaleTransformation) {}
        options = options.transform(transformation)
        Glide.with(context).load(url).apply(options).into(imageView)
    }

}