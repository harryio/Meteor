package io.github.sainiharry.meteor.currentweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import io.github.sainiharry.meteor.currentweather.databinding.FragmentCurrentWeatherBinding

class CurrentWeatherFragment : BaseFragment() {

    // TODO: 11/06/20 Get this from factory
    val viewModel by viewModels<CurrentWeatherViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}