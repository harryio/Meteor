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
import io.github.sainiharry.meteor.repositories.news.getNewsRepository
import io.github.sainiharry.meteor.weather.WeatherInfoViewModel
import io.github.sainiharry.metero.news.databinding.FragmentNewsBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewsFragment : BaseFragment() {

    private val model by viewModels<NewsViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NewsViewModel(
                    getNewsRepository(Schedulers.io(), requireContext().applicationContext),
                    AndroidSchedulers.mainThread()
                ) as T
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
        binding.recyclerView.setHasFixedSize(true)
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