package com.stdev.shopit.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stdev.shopit.data.model.ShopItem
import com.stdev.shopit.databinding.SearchItemBinding

class SearchHistoryAdapter(private val products: List<ShopItem>): RecyclerView.Adapter<SearchHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return SearchHistoryViewHolder(SearchItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(products[position])
    }
}