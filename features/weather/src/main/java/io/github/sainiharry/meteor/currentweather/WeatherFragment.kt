package io.github.sainiharry.meteor.currentweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import io.github.sainiharry.meteor.currentweather.databinding.FragmentWeatherBinding
import io.github.sainiharry.meteor.currentweatherrepository.getWeatherRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WeatherFragment : BaseFragment() {

    private val viewModel by viewModels<WeatherViewModel>(factoryProducer = {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(
                    getWeatherRepository(Schedulers.io(), requireContext().applicationContext),
                    AndroidSchedulers.mainThread()) as T
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWeatherBinding.inflate(inflater, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}