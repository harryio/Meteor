package io.github.sainiharry.metero.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import io.github.sainiharry.meteor.commonfeature.EventObserver
import io.github.sainiharry.meteor.weather.WeatherInfoViewModel
import io.github.sainiharry.metero.news.databinding.FragmentNewsBinding
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.get

class NewsFragment : BaseFragment() {

    private val model by viewModels<NewsViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NewsViewModel(get(), Dispatchers.Main.immediate) as T
            }
        }
    })

    private val weatherInfoViewModel by activityViewModels<WeatherInfoViewModel>()

    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        binding.model = model
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.loading.observe(viewLifecycleOwner, EventObserver { loading ->
            if (binding.refresher.isRefreshing != loading) {
                binding.refresher.isRefreshing = loading
            }
        })
        model.error.observe(viewLifecycleOwner, defaultErrorHandler())

        val adapter = NewsAdapter(model)
        binding.recyclerView.adapter = adapter

        binding.refresher.setOnRefreshListener {
            model.refresh()
        }

        model.news.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        model.viewNewsEvent.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
            requireActivity().startActivity(intent)
        })

        weatherInfoViewModel.weather.observe(viewLifecycleOwner, Observer {
            model.handleCountry(it.country)
        })
    }
}