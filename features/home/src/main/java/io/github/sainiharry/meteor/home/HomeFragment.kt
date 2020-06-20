package io.github.sainiharry.meteor.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.transition.MaterialFadeThrough
import io.github.sainiharry.meteor.commonfeature.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController =
            Navigation.findNavController(view.findViewById(R.id.home_nav_host_fragment))
        bottom_nav.setupWithNavController(navController)
    }
}