package com.stdev.shopit.presentation.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stdev.shopit.R
import com.stdev.shopit.data.SearchHistory
import com.stdev.shopit.data.SearchHistory.Companion.SEARCH_HISTORY_KEY
import com.stdev.shopit.data.model.Product
import com.stdev.shopit.data.model.ShopItem
import com.stdev.shopit.data.util.Resource
import com.stdev.shopit.databinding.FragmentSearchBinding
import com.stdev.shopit.presentation.adapter.SearchAdapter
import com.stdev.shopit.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    @Inject
    lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var adapter: SearchAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var queryDelayMillis: Long = 2000
    private val searchQueryHandler = Handler(Looper.getMainLooper())

    private lateinit var binding: FragmentSearchBinding

    private var productsList = listOf<ShopItem>()
    private var productsList2 = listOf<ShopItem>()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory : SearchHistory


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    private var isSearching = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(SearchHistoryFragment.mova_maker,Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
        binding = FragmentSearchBinding.bind(view)

        // Получаем контекст фрагмента
        val context = requireContext()

        viewModel.getAllProducts()

        viewModel.products.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    productsList = result.data!!
                    adapter.differ.submitList(result.data)

                    // Скрываем ProgressBar при успешной загрузке данных
                    binding.progressBar.visibility = View.GONE
                }

                is Resource.Loading -> {
                    if (!isSearching) {
                        // Показываем ProgressBar только при загрузке данных, если нет активного поиска
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Log.i("SearchFragment", "Loading...")
                }

                is Resource.Error -> {
                    // Скрываем ProgressBar в случае ошибки
                    binding.progressBar.visibility = View.GONE
                    Log.e("SearchFragment", "Error: ${result.message}")
                }
            }
        }

        val searchQueryHandler = Handler(Looper.getMainLooper())
        var lastQuery = ""

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // На случай, если запрос был отправлен немедленно, снимаем отложенный поиск
                searchQueryHandler.removeCallbacksAndMessages(null)
                query?.let {
                    lastQuery = it
                    performDelayedSearch(it)
                    if (productsList2.isEmpty()) {
                        Toast.makeText(context, "Товар не найден", Toast.LENGTH_SHORT).apply {
                            setGravity(Gravity.CENTER, 1000, 1000)
                        }.show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQueryHandler.removeCallbacksAndMessages(null)
                newText?.let {
                    lastQuery = it
                    searchQueryHandler.postDelayed({
                        performDelayedSearch(it)
                        if (productsList2.isEmpty()) {
                            Toast.makeText(context, "Товар не найден", Toast.LENGTH_SHORT).apply {
                                setGravity(Gravity.CENTER, 1000, 1000)
                            }.show()
                        }
                    }, 2000)
                }
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            adapter.differ.submitList(productsList)
            true
        }

        binding.searchBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchCart.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_cartFragment)
        }
        binding.searchHistory.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchHistoryFragment)
        }
        binding.searchBack.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

        binding.searchRecyclerView.adapter = adapter

        adapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(it)
            searchHistory.write(it)
            findNavController().navigate(action)
        }
    }

    private fun performDelayedSearch(query: String) {
        // Устанавливаем флаг состояния поиска в true
        isSearching = true

        // Показываем ProgressBar при начале поиска
        binding.progressBar.visibility = View.VISIBLE

        // Отменяем предыдущий поиск, если он был запущен
        searchQueryHandler.removeCallbacksAndMessages(null)

        // Выполняем поиск с задержкой
        searchQueryHandler.postDelayed({
            performSearch(query)
        }, 2000) // Задержка в 2 секунды перед выполнением поиска
    }

    private fun performSearch(query: String) {
        // Выполняем фактический поиск
        productsList2 = productsList.filter { it.title.contains(query, ignoreCase = true) }
        adapter.differ.submitList(productsList2)

        // Скрываем ProgressBar после завершения поиска
        binding.progressBar.visibility = View.GONE

        // Сбрасываем флаг состояния поиска после завершения поиска
        isSearching = false
    }
}