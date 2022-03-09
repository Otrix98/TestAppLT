package com.example.testapplt.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testapplt.utils.ViewBindingFragment
import com.example.testapplt.R
import com.example.testapplt.databinding.LogInFragmentBinding
import com.example.testapplt.ui.viewmodels.LogInViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LogInFragment : ViewBindingFragment<LogInFragmentBinding>(LogInFragmentBinding::inflate){

    private val viewModel by viewModels<LogInViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPhoneMask()
        checkAndFillPhoneAndPasswordText()
        bindViewModel()
        binding.loginButton.setOnClickListener {
            logIn()
        }
    }

    private fun bindViewModel() {
        viewModel.phoneMask.observe(viewLifecycleOwner) {binding.phoneEditText.hint = it}
        viewModel.progressBarVisible.observe(viewLifecycleOwner) {binding.progressBar.isVisible = it}
        viewModel.serverConnectError.observe(viewLifecycleOwner) {showErrorMessage(it)}
        viewModel.authCheck.observe(viewLifecycleOwner) { status ->
            if (status == "true") {
                saveStringToPrefs("lastStatus", "true")
                saveStringToPrefs("phoneNumber", binding.phoneEditText.text.toString())
                saveStringToPrefs("password", binding.passwordEditText.text.toString())
                val action = LogInFragmentDirections.actionLogInFragmentToListFragment()
                findNavController().navigate(action)
            } else if (status == "false"){
                saveStringToPrefs("lastStatus", "false")
            }
        }
    }

    private fun showErrorMessage(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_SHORT).show()
    }

    private fun logIn() {
        val lastStatus: String = readFromPrefs("lastStatus", "false") as String
        val lastPhoneNumber: String? = readFromPrefs("phoneNumber", "")
        val lastPassword: String? = readFromPrefs("password", "false")
        val phoneNumber: String = binding.phoneEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        val phoneMask = binding.phoneEditText.hint.toString()
            .replace("-", "")
            .replace(" ", "")
            .replace("(", "")
            .replace(")", "")
        when {
            lastStatus == "true" && lastPassword == password && lastPhoneNumber == phoneNumber ->
                viewModel.authCheck(phoneNumber.substringAfter("+"), password)
            phoneNumber.contains(phoneMask.substringBefore("Х")) && phoneNumber.length ==
                    phoneMask.length -> viewModel.authCheck(phoneNumber.substringAfter("+"), password)
            else -> {
                Toast.makeText(requireContext(), requireContext().getString(R.string.check_data) , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndFillPhoneAndPasswordText() {
        val lastStatus: String? = readFromPrefs("lastStatus", "false")
        val phoneNumber: String? = readFromPrefs("phoneNumber", "")
        val password: String? = readFromPrefs("password", "false")
        if (lastStatus == "true") {
            binding.phoneEditText.setText(phoneNumber)
            binding.passwordEditText.setText(password)
        }
    }

    private fun saveStringToPrefs(key: String, text: String) {
            val sPref = requireActivity().getSharedPreferences(AUTH_CODE, Context.MODE_PRIVATE)
            val editor = sPref.edit()
            editor.putString(key, text)
            editor.apply()
    }

    private fun readFromPrefs(key: String, DefString: String): String? {
        val sPref = requireActivity().getSharedPreferences(AUTH_CODE, Context.MODE_PRIVATE)
        return sPref.getString(key, DefString)
    }
    companion object {
        private const val AUTH_CODE = "code_for_shared_prefs"
    }
}
