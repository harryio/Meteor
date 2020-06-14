package io.github.sainiharry.metero.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import io.github.sainiharry.metero.news.databinding.FragmentNewsBinding

class NewsFragment : BaseFragment() {

    // TODO: 14/06/20 Get from factory
    private val model by viewModels<NewsViewModel>()

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
}