package com.stdev.shopit.presentation.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stdev.shopit.R
import com.stdev.shopit.data.SearchHistory
import com.stdev.shopit.data.model.Product
import com.stdev.shopit.data.model.ShopItem
import com.stdev.shopit.databinding.FragmentSearchHistoryBinding
import com.stdev.shopit.presentation.adapter.SearchAdapter
import com.stdev.shopit.presentation.adapter.SearchHistoryAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [SearchHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchHistoryFragment : Fragment() {

    private lateinit var binding: FragmentSearchHistoryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private var searchHistoryList = mutableListOf<ShopItem>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_history, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchHistoryBinding.bind(view)
        sharedPreferences = requireActivity().getSharedPreferences(mova_maker,Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
        searchHistoryList = searchHistory.read().toMutableList()
        if (searchHistoryList.isEmpty()){
            renderEmpty()
        }else{
            renderHistory()
        }
        val adapter = SearchHistoryAdapter(searchHistoryList)
        binding.historyRv.adapter = adapter
        binding.historySearchBack.setOnClickListener { // Исправлено
            findNavController().navigate(R.id.action_searchHistoryFragment_to_searchFragment)
        }
        binding.clearButton.setOnClickListener{
            searchHistory.clear()
            searchHistoryList.clear()
            adapter.notifyDataSetChanged()
            renderEmpty()
        }
    }
    fun renderEmpty(){
        binding.emptyHistoryIv.isVisible = true
        binding.emptyHistoryTv.isVisible = true
        binding.historyRv.isVisible = false
    }
    fun renderHistory(){
        binding.emptyHistoryIv.isVisible = false
        binding.emptyHistoryTv.isVisible = false
        binding.historyRv.isVisible = true
    }
    companion object{
        const val mova_maker = "playlist_maker_preferences"
    }



}