package com.example.dgtechhealthcare.pharmacist.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.PharmacistInterface
import com.example.dgtechhealthcare.pharmacist.model.DescriptionData
import com.example.dgtechhealthcare.pharmacist.presenter.RequestDescriptionPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter
import kotlinx.android.synthetic.main.fragment_request_description.*

class RequestDescriptionFragment : Fragment(),PharmacistInterface.View.RequestDescription {

    var userID = ""
    var type = ""

    lateinit var reference: FirebasePresenter
    lateinit var presenter : RequestDescriptionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        userID = arguments?.getString("userID","")!!
        type = arguments?.getString("type","")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeValues(view)

        if (type.compareTo("requestHistory")==0){
            requestAcceptB.visibility = View.GONE
            requestDeclineB.visibility = View.GONE
        }

        val descriptionData = DescriptionData(requestName,requestIV,requestMed1, requestMed2,
            requestMed3, requestMed4, requestAcceptB, requestDeclineB)
        presenter.populateDescription(type,descriptionData,requireActivity(),userID)
    }

    private fun initializeValues(view: View) {
        reference = FirebasePresenter(view)
        presenter = RequestDescriptionPresenter(view)
    }

    override fun requestApproved(context: Context) {
        Toast.makeText(context,R.string.req_approved, Toast.LENGTH_LONG).show()
    }

    override fun requestDeclined(context: Context) {
        Toast.makeText(context,R.string.req_declined, Toast.LENGTH_LONG).show()
    }
}