package com.tdg.truex_android_sampleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tdg.login.base.ViewModelFactory
import com.tdg.login.presentation.LoginViewModel
import com.tdg.truex_android_sampleapp.databinding.FragmentMainBinding
import com.tdg.truex_android_sampleapp.injections.MainComponent
import javax.inject.Inject

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
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
        observeLoginViewModel()
        initView()
    }

    private fun initView() = with(binding) {
        loginButton.setOnClickListener {
            loginViewModel.login()
        }

        nextButton.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToMenuFragment()
            )
        }
    }

    private fun observeLoginViewModel() = with(loginViewModel) {
        onShowErrorToast().observe(viewLifecycleOwner) {
        }
    }
}