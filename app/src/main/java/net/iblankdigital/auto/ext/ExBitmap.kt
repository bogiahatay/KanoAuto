package net.iblankdigital.auto.ext

import android.graphics.Bitmap
import android.graphics.Color
import net.iblankdigital.auto.utils.MLog
import kotlin.math.abs

fun Bitmap.cut(xStart: Int, yStart: Int, width: Int, height: Int): Bitmap {
    return Bitmap.createBitmap(this, xStart, yStart, width, height)
}



fun Bitmap.isColor(xStart: Int, yStart: Int, width: Int, height: Int, color: Int): Boolean {
    val xEnd = xStart + width
    val yEnd = yStart + height

    for (x in xStart until xEnd) {
        for (y in yStart until yEnd) {
            if (this.getPixel(x, y) != color) {
                return false
            }
        }
    }
    return true
}

fun Bitmap.isSameOneColor(xStart: Int, yStart: Int, width: Int, height: Int): Boolean {
    val xEnd = xStart + width
    val yEnd = yStart + height

    val rootColor = this.getPixel(xStart, yStart)

    for (x in xStart until xEnd) {
        for (y in yStart until yEnd) {
            val newColor = this.getPixel(x, y)
            if (!isSameColor(rootColor, newColor)) {
                return false
            }
        }
    }
    return true
}

fun isSameColor(color1: Int, color2: Int): Boolean {

    // get red/green/blue int values of hex1
    val r1 = Color.red(color1)
    val g1 = Color.green(color1)
    val b1 = Color.blue(color1)

    // get red/green/blue int values of hex2
    val r2 = Color.red(color2)
    val g2 = Color.green(color2)
    val b2 = Color.blue(color2)

    // calculate differences between reds, greens and blues
    var r = 255F - abs(r1 - r2).toFloat()
    var g = 255F - abs(g1 - g2).toFloat()
    var b = 255F - abs(b1 - b2).toFloat()

    // limit differences between 0 and 1
    r /= 255F
    g /= 255F
    b /= 255F

    // 0 means different colors, 1 means same colors
    val sameColor: Float = (r + g + b) / 3F
    val booleanSameColor = (1F - sameColor) < 0.1F
    if (!booleanSameColor) {
        MLog.e("Different: $sameColor  $booleanSameColor")
    }
    return booleanSameColor
}

fun Bitmap.havePercentColor(xStart: Int, yStart: Int, width: Int, height: Int, color: Int, percent: Int): Boolean {
    var have = 0
    val needHave = percent * 100 / (width * height)

    var notHave = 0
    val notNeedHave = (100 - percent) * 100 / (width * height)

    val xEnd = xStart + width
    val yEnd = yStart + height

    for (x in xStart until xEnd) {
        for (y in yStart until yEnd) {
            if (this.getPixel(x, y) == color) {
                have++
                if (have >= needHave) {
                    return true
                }
            } else {
                notHave++
                if (notHave >= notNeedHave) {
                    return false
                }
            }
        }
    }
    return false
}

fun Bitmap.compareColor(color: Int): Boolean {
    for (x in 0 until width) {
        for (y in 0 until height) {
            if (this.getPixel(x, y) != color) {
                return false
            }
        }
    }
    return true
}
