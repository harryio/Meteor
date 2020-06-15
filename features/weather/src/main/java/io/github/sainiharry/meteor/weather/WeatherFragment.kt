package io.github.sainiharry.meteor.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import coil.api.load
import com.google.android.gms.location.LocationServices
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import io.github.sainiharry.meteor.commonfeature.EventObserver
import io.github.sainiharry.meteor.search.SearchViewModel
import io.github.sainiharry.meteor.weather.databinding.FragmentWeatherBinding
import io.github.sainiharry.meteor.weather.forecast.ForecastAdapter
import io.github.sainiharry.meteor.weatherrepository.getWeatherRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_weather.*

const val REQUEST_LOCATION_CODE = 923

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

    private val searchViewModel by activityViewModels<SearchViewModel>()

    private val weatherInfoViewModel by activityViewModels<WeatherInfoViewModel>()

    private lateinit var binding: FragmentWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION_CODE
                )
            } else {
                getLastKnownLocation()
            }
        } else {
            getLastKnownLocation()
        }
    }

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

        search_bar.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_home_to_search)
        }

        val adapter = ForecastAdapter()
        binding.recyclerView.adapter = adapter

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
            ) {
                crossfade(true)
            }
        })

        model.forecast.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        model.weather.observe(viewLifecycleOwner, Observer {
            weatherInfoViewModel.handleWeatherInfo(it)
        })

        searchViewModel.searchText.observe(viewLifecycleOwner, Observer {
            model.handleUserQuery(it)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        }
    }

    //This method should only be called after checking if the location permission is granted by the user
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener {
            if (weatherInfoViewModel.resultsFetchedForLocation.value == false) {
                weatherInfoViewModel.resultsFetchedForLocation.value = true
                model.handleUserLocation(it.latitude, it.longitude)
            }
        }
    }
}