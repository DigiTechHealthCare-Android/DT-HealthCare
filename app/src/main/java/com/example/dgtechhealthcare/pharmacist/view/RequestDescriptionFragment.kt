package com.example.dgtechhealthcare.pharmacist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.model.DescriptionData
import com.example.dgtechhealthcare.pharmacist.presenter.RequestDescriptionPresenter
import com.example.dgtechhealthcare.utils.FirebasePresenter

class RequestDescriptionFragment : Fragment() {

    lateinit var name : TextView
    lateinit var img : ImageView
    lateinit var med1 : TextView
    lateinit var med2 : TextView
    lateinit var med3 : TextView
    lateinit var med4 : TextView
    lateinit var acceptB : Button
    lateinit var declineB : Button

    var userID = ""
    var type = ""

    val TAG = "DoctorPrescribe"

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
            acceptB.visibility = View.GONE
            declineB.visibility = View.GONE
        }

        val descriptionData = DescriptionData(name,img,med1, med2, med3, med4, acceptB, declineB)
        presenter.populateDescription(type,descriptionData,requireActivity(),userID)
    }

    private fun initializeValues(view: View) {
        name = view.findViewById(R.id.requestName)
        img = view.findViewById(R.id.requestIV)
        med1 = view.findViewById(R.id.requestMed1)
        med2 = view.findViewById(R.id.requestMed2)
        med3 = view.findViewById(R.id.requestMed3)
        med4 = view.findViewById(R.id.requestMed4)
        acceptB = view.findViewById(R.id.requestAcceptB)
        declineB = view.findViewById(R.id.requestDeclineB)

        reference = FirebasePresenter(view)
        presenter = RequestDescriptionPresenter(view)
    }

}