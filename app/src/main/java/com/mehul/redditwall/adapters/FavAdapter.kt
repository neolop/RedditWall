package com.mehul.redditwall.adapters


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mehul.redditwall.R
import com.mehul.redditwall.activities.MainActivity
import com.mehul.redditwall.activities.SettingsActivity
import com.mehul.redditwall.activities.WallActivity
import com.mehul.redditwall.favorites.FavImage
import com.mehul.redditwall.objects.BitURL

class FavAdapter(private val con: Context, lis: ArrayList<BitURL>) : RecyclerView.Adapter<FavAdapter.FavViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(con)
    private var favList: List<FavImage?> = ArrayList()
    private var favs = lis
    private val width: Int
    private val height: Int
    private val scale: Int

    init {
        val displayMetrics = DisplayMetrics()
        (con as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        scale = (con.getSharedPreferences(MainActivity.SharedPrefFile, Context.MODE_PRIVATE)
                .getInt(SettingsActivity.LOAD_SCALE, 2) + 1) * 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val itemView = inflater.inflate(R.layout.card_image, parent, false)
        return FavViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val current = favs[position]
        holder.bindTo(current)
        holder.itemView.apply {
            isLongClickable = true
            isClickable = true
        }
        holder.itemView.setOnClickListener {
            //launch wall activity
            val wallIntent = Intent(con, WallActivity::class.java)
            wallIntent.apply {
                putExtra(WallActivity.WALL_URL, current.url)
                putExtra(WallActivity.GIF, current.hasGif())
                putExtra(WallActivity.INDEX, position)
                putExtra(WallActivity.FROM_FAV, true)
                putExtra(WallActivity.LIST, WallActivity.listToJson(favs))
                putExtra(WallActivity.FAV_LIST, favList[position]?.favName)
            }
            con.startActivity(wallIntent)
        }
    }

    fun setFavs(favs: ArrayList<BitURL>, favLists: List<FavImage?>) {
        this.favs = favs
        this.favList = favLists
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return favs.size
    }

    fun getFavAtPosition(position: Int): BitURL {
        return favs[position]
    }

    fun getFavs(): ArrayList<BitURL> {
        return favs
    }

    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img: ImageView = itemView.findViewById(R.id.image_holder)

        fun bindTo(saved: BitURL) {
            val url = saved.url
            if (saved.hasGif()) {
                Glide.with(con).asGif().load(url).override(width / scale, height / 4).centerCrop().into(img)
            } else {
                img.setImageBitmap(saved.getImg())
            }
        }
    }
}