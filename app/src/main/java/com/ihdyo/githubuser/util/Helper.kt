package com.ihdyo.githubuser.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class Helper {
    companion object {
        fun ImageView.setImageGlide(context: Context, url: String) {
            Glide
                .with(context)
                .load(url)
                .into(this)
        }

        fun TextView.setAndVisible(text: String?) {
            if (!text.isNullOrBlank()) {
                this.text = text
                this.visibility = View.VISIBLE
            }
        }
    }
}