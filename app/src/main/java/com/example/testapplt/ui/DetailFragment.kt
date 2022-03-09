package com.example.testapplt.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.testapplt.utils.ViewBindingFragment
import com.example.testapplt.R
import com.example.testapplt.databinding.DetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : ViewBindingFragment<DetailFragmentBinding>(DetailFragmentBinding::inflate) {

    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInfo()
        binding.retryButton.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
            findNavController().navigate(action)
        }
    }

   private fun setInfo() {
        with(binding) {
            barTextView.text = args.item.title
            titleTextView.text = args.item.title
            descriptionTextView.text = args.item.text
        }

       Glide.with(requireContext())
           .load("http://dev-exam.l-tech.ru/" + args.item.image)
           .error(R.drawable.no_image)
           .into(binding.imageView)
    }
}