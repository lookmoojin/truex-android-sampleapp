import android.view.View
import android.view.ViewGroup
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

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewmodelObserve()
    }

    private fun viewmodelObserve() {
        mainViewModel.onLoginSuccess.observe(viewLifecycleOwner) {
            binding.textViewResponse.text = it
        }
    }

    private fun initView() = with(binding) {
        loginButton.setOnClickListener {
            // login
            mainViewModel.login()
        }

        nextButton.setOnClickListener {
