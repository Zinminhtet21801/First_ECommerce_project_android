package com.example.myshop.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSTextViewRegular(context : Context, attributeSet: AttributeSet) : AppCompatTextView(context, attributeSet) {
    init {
        applyFont()
    }
    private fun applyFont(){
        val regularTypeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        typeface = regularTypeface
    }
}