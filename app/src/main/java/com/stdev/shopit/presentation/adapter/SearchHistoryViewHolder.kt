package com.stdev.shopit.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stdev.shopit.data.model.Product
import com.stdev.shopit.data.model.ShopItem
import com.stdev.shopit.databinding.SearchItemBinding

class SearchHistoryViewHolder(private val binding: SearchItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(model: ShopItem){
        Glide.with(binding.searchItemImage)
            .load(model.image)
            .into(binding.searchItemImage)
        binding.searchItemTitle.text = model.title
        binding.searchItemPrice.text = "USD ${model.price}"
        binding.searchItemRating.text = "${model.rating.rate}"
        binding.searchItemReview.text = "${model.rating.count} Reviews"
    }
}