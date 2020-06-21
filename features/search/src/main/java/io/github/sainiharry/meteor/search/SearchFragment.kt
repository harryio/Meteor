package io.github.sainiharry.meteor.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import io.github.sainiharry.meteor.commonfeature.EventObserver
import io.github.sainiharry.meteor.commonfeature.hideKeyboard
import io.github.sainiharry.meteor.commonfeature.showKeyboard
import io.github.sainiharry.meteor.search.databinding.FragmentSearchBinding

class SearchFragment : BaseFragment() {

    private val model by searchViewModel()

    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.loadSearchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.model = model
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchText.showKeyboard()
        binding.searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                model.handleSearchDone()
                true
            } else {
                false
            }
        }
        binding.searchInputLayout.setEndIconOnClickListener {
            model.handleSearchDone()
        }
        binding.searchInputLayout.setStartIconOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = SearchAdapter(model)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        model.navigateBackEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
        model.recentSearchQueries.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}