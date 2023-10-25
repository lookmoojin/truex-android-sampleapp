package com.tdg.truex_android_sampleapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
    }

    private fun viewModelObserve() = with(mainViewModel) {
        onLoginSuccess.observe(viewLifecycleOwner) {
            binding.responseTextView.text = it
        }
        onLoginFailed.observe(viewLifecycleOwner) {
            binding.responseTextView.text = it
        }
        onLoginFinish.observe(viewLifecycleOwner) {
            binding.responseTextView.text = it
        }
        onPreprodState.observe(viewLifecycleOwner) {
            binding.preprodButton.isSelected = it
            binding.prodButton.isSelected = !it
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
            if (preprodButton.isSelected == prodButton.isSelected) {
                Toast.makeText(context, "Please, select 'Preprod' or 'Prod'", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
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

        copyButton.setOnClickListener {
            Toast.makeText(context, "Copied to clipboard.", Toast.LENGTH_SHORT).show()
            context?.let { context ->
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", binding.responseTextView.text)
                clipboard.setPrimaryClip(clip)
            }
        }
    }

    private fun preprodState() = with(binding) {
        mainViewModel.savePreprodState(true)
        usernameEditText.setText("0620922456")
        passwordEditText.setText("TestPass01")
    }

    private fun prodState() = with(binding) {
        mainViewModel.savePreprodState(false)
        usernameEditText.setText("")
        passwordEditText.setText("")
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }
}