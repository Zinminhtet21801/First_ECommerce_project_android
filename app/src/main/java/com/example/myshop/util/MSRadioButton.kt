package com.example.myshop.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MSRadioButton(context : Context, attributeSet : AttributeSet) : AppCompatRadioButton(context,attributeSet) {
    init {
        applyFont()
    }

    private fun applyFont() {
        val typefaceReg = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        typeface = typefaceReg
    }
}