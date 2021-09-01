package com.example.dgtechhealthcare.nurse.view

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.nurse.presenter.AllPatientsInfoPresenter
import java.util.concurrent.CountDownLatch

class NursePatientFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var patientInfoPresenter : AllPatientsInfoPresenter
    lateinit var tempDialog: ProgressDialog
    lateinit var mCountDownTimer: CountDownTimer
    val i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nurse_patients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tempDialog = ProgressDialog(activity)
        tempDialog.setMessage("Please wait...")
        tempDialog.setCancelable(false)
        tempDialog.progress = i
        tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        tempDialog.window?.setBackgroundDrawable(ColorDrawable(Color.GRAY))


        initializeValues(view)

        recyclerView.setHasFixedSize(true)
        val layout = LinearLayoutManager(activity)
        recyclerView.layoutManager = layout

        patientInfoPresenter.displayNursePatients(recyclerView,requireActivity(),layout)

        tempDialog.show()
        mCountDownTimer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempDialog.setMessage("Please wait...")
            }

            override fun onFinish() {
                tempDialog.dismiss()
            }
        }.start()
    }

    private fun initializeValues(view: View) {
        recyclerView = view.findViewById(R.id.nursePatientRV)
        patientInfoPresenter = AllPatientsInfoPresenter(view)
    }
}