package com.hkm.userhub.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.hkm.userhub.R
import com.hkm.userhub.db.UserRepository
import com.hkm.userhub.entitiy.User
import io.realm.Realm
import kotlinx.android.synthetic.main.item_user.view.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {
    companion object {
        var mWidgetItems = ArrayList<Bitmap>()
    }

    private lateinit var rvFavorites: RecyclerView

    @SuppressLint("InflateParams")
    override fun onDataSetChanged() {
        val realm = Realm.getDefaultInstance()
        val userRepository = UserRepository(realm)

        var list: ArrayList<User>

        val view = (mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.item_widget, null)
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        realm.use {
            list = userRepository.getAllFavorite()
            for (user in list) {
                with(view) {
                    this.tv_username.text = user.username
                    this.img_avatar.setImageBitmap(getBitmapFromURL(user.avatar))
                }
                mWidgetItems.add(getBitmapFromView(view) as Bitmap)
            }
        }
    }

    override fun onCreate() {
        rvFavorites = RecyclerView(mContext)
        rvFavorites.visibility = View.GONE
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            FavoriteBannerWidget.EXTRA_ITEM to position
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.sv_favorite, fillInIntent)

        return rv
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )

        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)

        //Get the view's background
        val bgDrawable: Drawable = view.background
        bgDrawable.draw(canvas)

        // draw the view on the canvas
        view.draw(canvas)

        //return the bitmap
        return returnedBitmap
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}