package com.example.safebitecapstone.pages

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safebitecapstone.ListAlergenAdapter
import com.example.safebitecapstone.R
import com.example.safebitecapstone.databinding.FragmentHistoryBinding
import com.example.safebitecapstone.databinding.FragmentHomeBinding
import com.example.safebitecapstone.dummyData.Alergen

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    //    Apus kalo udah ada API
    private lateinit var rvAlergen: RecyclerView
    private val list = ArrayList<Alergen>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAlergen = binding.alergenItems
        list.addAll(getListAlergen())
        showRecyclerList()
    }

    private fun getListAlergen(): ArrayList<Alergen> {
        val dataTitle = resources.getStringArray(R.array.data_title)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)

        val listAlergen = ArrayList<Alergen>()

        for (i in dataTitle.indices) {
            val alergen = Alergen(dataTitle[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listAlergen.add(alergen)
        }
        return listAlergen
    }

    private fun showRecyclerList() {
        binding.alergenItems.layoutManager = LinearLayoutManager(activity)
        val listAlergenAdapter = ListAlergenAdapter(list)
        binding.alergenItems.adapter = listAlergenAdapter
    }

}