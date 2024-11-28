package com.payby.pos.common.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.argb
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.payby.pos.common.extension.valid
import java.util.Hashtable

object CodeHelper {

    fun generateQRCodeWithIMile(content: String, width: Int, height: Int): Bitmap {
        return generateQRCode(content, width, height, 163, 207, 255)
    }

    fun generateQRCodeWithPayBy(content: String, width: Int, height: Int): Bitmap {
        return generateQRCode(content, width, height, 67, 198, 172)
    }

    fun generateQRCode(content: String, width: Int, height: Int, rx: Int, gx: Int, bx: Int): Bitmap {
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.MARGIN] = 0
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        // 图像数据转换, 使用了矩阵转换
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        val pixels = IntArray(width * height)
        // 渐变色 从上到下绘制
        for (y in 0 until height) {
            for (x in 0 until width) {
                val bool = bitMatrix.get(x, y)
                if (bool) {
                    val r = rx - (rx - 25.0) / height * (y + 1)
                    val g = gx - (gx - 22.0) / height * (y + 1)
                    val b = bx - (bx - 84.0) / height * (y + 1)
                    val red = r.toInt()
                    val green = g.toInt()
                    val blue = b.toInt()
                    val colorInt = argb(255, red, green, blue)
                    // 修改二维码的颜色, 可以分别制定二维码和背景的颜色 // 0x000000 : 0xffffff
                    val boo = bitMatrix.get(x, y)
                    pixels[y * width + x] = if (boo) colorInt else 1644116
                } else {
                    pixels[y * width + x] = 0X00FFFFFF // 背景颜色
                }
            }
        }
        // 生成二维码图片的格式, 使用ARGB_8888
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    fun generateQRCode(content: String, width: Int, height: Int, color: Int = Color.BLACK, background: Int = Color.WHITE): Bitmap {
        val options = HmsBuildBitmapOption.Creator().setBitmapMargin(1).setBitmapColor(color).setBitmapBackgroundColor(background).create()
        return ScanUtil.buildBitmap(content, HmsScan.QRCODE_SCAN_TYPE, width, height, options)
    }

    fun generateCode(content: String, width: Int, height: Int, type: Int, color: Int = Color.BLACK, background: Int = Color.WHITE): Bitmap {
        val options = HmsBuildBitmapOption.Creator().setBitmapMargin(1).setBitmapColor(color).setBitmapBackgroundColor(background).create()
        return ScanUtil.buildBitmap(content, type, width, height, options)
    }

    fun decodeWithBitmap(context: Context, bitmap: Bitmap): Pair<String, Int> {
        val options = HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create()
        val hmsScans = ScanUtil.decodeWithBitmap(context, bitmap, options)
        if (hmsScans != null && hmsScans.isNotEmpty() && hmsScans[0] != null && hmsScans[0].getOriginalValue() != null && hmsScans[0].getOriginalValue().valid) {
            val hmsScan = hmsScans[0]
            val scanType = hmsScan.getScanType()
            val scanTypeForm = hmsScan.getScanTypeForm()
            val originalValue = hmsScan.getOriginalValue()
            Log.e("Scan", "scanType: $scanType")
            Log.e("Scan", "scanTypeForm: $scanTypeForm")
            Log.e("Scan", "originalValue: $originalValue")
            return Pair(originalValue, scanType)
        }
        return Pair("", 0);
    }

}