package com.tdg.truex_android_sampleapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tdg.truex_android_sampleapp.databinding.FragmentMainBinding
import com.tdg.truex_android_sampleapp.injections.HomeComponent
import javax.inject.Inject

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeComponent.getInstance().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModelObserve()
        preprodState()
    }

    private fun viewModelObserve() = with(mainViewModel) {
        onLoginSuccess.observe(viewLifecycleOwner) {
            binding.textViewResponse.text = it
        }
        onLoginFailed.observe(viewLifecycleOwner) {
            binding.textViewResponse.text = it
        }
    }

    private fun initView() = with(binding) {
        preprodButton.setOnClickListener {
            preprodState()
        }

        prodButton.setOnClickListener {
            prodState()
        }

        loginButton.setOnClickListener {
            mainViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                preprodButton.isSelected
            )
            hideKeyboard(this.root)
        }

        nextButton.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToMenuFragment()
            )
        }
    }

    private fun preprodState() = with(binding) {
        preprodButton.isSelected = true
        prodButton.isSelected = false
        usernameEditText.setText("0620922456")
        passwordEditText.setText("TestPass01")
    }

    private fun prodState() = with(binding) {
        preprodButton.isSelected = false
        prodButton.isSelected = true
        usernameEditText.setText("")
        passwordEditText.setText("")
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }
}