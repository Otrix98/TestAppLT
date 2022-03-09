package com.example.testapplt.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.testapplt.utils.ViewBindingFragment
import com.example.testapplt.data.models.ListItem
import com.example.testapplt.databinding.ListFragmentBinding
import com.example.testapplt.ui.adapters.AdaptersListener
import com.example.testapplt.ui.adapters.ListAdapter
import com.example.testapplt.ui.viewmodels.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : ViewBindingFragment<ListFragmentBinding>(ListFragmentBinding::inflate), AdaptersListener{

    private val viewModel by viewModels<ListViewModel>()
    private var listAdapter = ListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sortSwitcher()
        initList()
        bindViewModel()
    }

    override fun onClickItem(item: ListItem) {
        val action = ListFragmentDirections.actionListFragmentToDetailFragment(item)
        findNavController().navigate(action)
    }

    private fun initList() {
        val itemDivider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDivider)
        listAdapter = ListAdapter()
        listAdapter.setOnClickListener(this)
        binding.recyclerView.adapter = listAdapter
        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.init()
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    private fun bindViewModel() {
        viewModel.init()
        viewModel.progressBarVisible.observe(viewLifecycleOwner) {binding.progressBar.isVisible = it}
        viewModel.serverConnectError.observe(viewLifecycleOwner) {showErrorMessage(it)}
        viewModel.itemsList.observe(viewLifecycleOwner) {
            listAdapter.updateItems(it)
            binding.recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun showErrorMessage(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_SHORT).show()
    }

    private fun sortSwitcher() {
        val serverSort = binding.toggleButton1
        val dateSort = binding.toggleButton2

        serverSort.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                dateSort.isChecked = false
                viewModel.sortSwitch("server")
            }
        }
        dateSort.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                serverSort.isChecked = false
                viewModel.sortSwitch("date")
            }
        }
        binding.retryButton.setOnClickListener {
            viewModel.init()
        }
    }


}