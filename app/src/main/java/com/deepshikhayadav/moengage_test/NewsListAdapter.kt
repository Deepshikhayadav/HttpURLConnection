package com.deepshikhayadav.moengage_test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Recyclerview Adapter
class NewsListAdapter(
    val mContext: Context,
    val articles: List<NewsArticles>,
    val clickListener: OnClickListener
) : RecyclerView.Adapter<NewsListAdapter.MyViewHolder>() {

    interface OnClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = articles[position]
        holder.tvTitle.text = article.title
        holder.tvAuthor.text = article.author
        holder.tvDate.text = article.publishedAt.toString()
        // load image from Url
        Glide.with(mContext)
            .load(article.urlToImage)
            .skipMemoryCache(false)
            .into(holder.ivImage)

        holder.itemView.setOnClickListener { v: View? ->
            clickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView
        val tvDate: TextView
        val tvAuthor: TextView
        val ivImage: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tv_head)
            tvAuthor = itemView.findViewById(R.id.tv_author)
            tvDate = itemView.findViewById(R.id.tv_date)
            ivImage = itemView.findViewById(R.id.iv_img)
        }
    }
}
