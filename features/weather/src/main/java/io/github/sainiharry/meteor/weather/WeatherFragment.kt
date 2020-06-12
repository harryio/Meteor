package io.github.sainiharry.meteor.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import io.github.sainiharry.meteor.commonfeature.EventObserver
import io.github.sainiharry.meteor.currentweatherrepository.getWeatherRepository
import io.github.sainiharry.meteor.weather.databinding.FragmentWeatherBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WeatherFragment : BaseFragment() {

    private val model by viewModels<WeatherViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(
                    getWeatherRepository(Schedulers.io(), requireContext().applicationContext),
                    AndroidSchedulers.mainThread()
                ) as T
            }
        }
    })

    private lateinit var binding: FragmentWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        binding.model = model
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refresher.setOnRefreshListener {
            model.refresh()
        }

        model.loading.observe(viewLifecycleOwner, EventObserver { loading ->
            if (binding.refresher.isRefreshing != loading) {
                binding.refresher.isRefreshing = loading
            }
        })
        model.error.observe(viewLifecycleOwner, defaultErrorHandler())

        model.weather.observe(viewLifecycleOwner, Observer {
            binding.weatherIcon.load(
                getString(R.string.weather_icon_url, it.icon)
            )
        })
    }
}