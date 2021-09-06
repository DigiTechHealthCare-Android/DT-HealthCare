package com.example.dgtechhealthcare.doctorPrescribeMedicine

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pushNotification.*
import com.example.dgtechhealthcare.utils.FirebasePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_doctor_prescribe_medicine.*

const val TOPIC = "/topics/myTopic2"

class DoctorPrescribeMedicineFragment : Fragment(), AdapterView.OnItemSelectedListener,
    DoctorPrescribeMedicineContract.View {

    lateinit var reference : FirebasePresenter
    lateinit var presenter : DoctorPrescribeMedicinePresenter

    var patientID :String? = ""
    var userType : String? = ""

    val nameList = arrayListOf<String>()
    var choice = ""
    var fromDoctor = ""
    val newHM = HashMap<String,String>()

    lateinit var pharmaChoice : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_prescribe_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reference = FirebasePresenter(view)
        presenter = DoctorPrescribeMedicinePresenter(view)
        patientID = arguments?.getString("patientID","")

        if(patientID.isNullOrEmpty()){
            userType = reference.currentUserId
        } else {
            userType = patientID
        }

        val data = PrescrideMedicine(morningMed, afternoonMed, eveningMed, nightMed, addMedicineB,
            morningCheckBox, afternoonCheckBox, eveningCheckBox, nightCheckBox)

        presenter.checkAccountType(userType,data)
        presenter.populateView(data,userType)

        val builder = AlertDialog.Builder(activity)
        val dialogLayout = layoutInflater.inflate(R.layout.choose_pharmacy_layout,null)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val pharmaSpinner = dialogLayout.findViewById<Spinner>(R.id.pharmaNameSpinner)
        pharmaChoice = dialogLayout.findViewById<TextView>(R.id.pharmaNameChoiceTV)
        addMedicineB.setOnClickListener {
            if(addMedicineB.text.toString().compareTo("Request prescription")==0){

                if (!morningMed.text.isNullOrEmpty() || !afternoonMed.text.isNullOrEmpty() || !eveningMed.text.isNullOrEmpty() || !nightMed.text.isNullOrEmpty())
                {
                    reference.pharmaReference.child("pharmacyNames").addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (data in snapshot.children){
                                nameList.add(data.key.toString())
                                newHM.put(data.key.toString(),data.value.toString())
                            }
                            val adapter = ArrayAdapter<String>(it.context,R.layout.support_simple_spinner_dropdown_item,nameList)
                            pharmaSpinner.adapter = adapter
                            pharmaSpinner.onItemSelectedListener = this@DoctorPrescribeMedicineFragment
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })

                    with(builder){
                        setTitle("Choose your preferred pharmacy")
                        setPositiveButton("Request"){dialog,which ->
                            presenter.sendRequest(choice,requireActivity())
                            requireActivity()?.supportFragmentManager?.popBackStack()
                        }
                        setNegativeButton("Cancel"){ _, _ ->}
                    }
                    builder.show()
                } else Toast.makeText(activity,R.string.prescription_empty,Toast.LENGTH_LONG).show()
            }
            else if(addMedicineB.text.toString().compareTo("Prescribe Medicine")==0) {
                if (!morningMed.text.isNullOrEmpty() || !afternoonMed.text.isNullOrEmpty() || !eveningMed.text.isNullOrEmpty() || !nightMed.text.isNullOrEmpty())
                {
                    val data = PrescrideMedicine(morningMed, afternoonMed, eveningMed, nightMed,
                        addMedicineB, morningCheckBox, afternoonCheckBox, eveningCheckBox, nightCheckBox)
                    presenter.prescribeMedicine(patientID,userType,requireActivity(),data)
                    Toast.makeText(activity,R.string.prescription_given,Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity,R.string.prescription_empty,Toast.LENGTH_LONG).show()
                }
            }
            else if(addMedicineB.text.toString().compareTo("Update Status")==0) {
                val data = PrescrideMedicine(morningMed, afternoonMed, eveningMed, nightMed,
                    addMedicineB, morningCheckBox, afternoonCheckBox, eveningCheckBox, nightCheckBox)
                presenter.nurseStatus(patientID,data,requireActivity())
            }
        }
    }

    override fun onDestroyView() {
        if (view != null) {
            val parentViewGroup = requireView().parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        super.onDestroyView()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val name = parent?.selectedItem
        for(h in newHM){
            if(h.key.toString().compareTo(name.toString())==0) choice = h.value.toString()
        }
        pharmaChoice.setText(name.toString())
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun requestSentMessage(context: Context) {
        Toast.makeText(context,R.string.request_sent, Toast.LENGTH_SHORT).show()
    }

    override fun statusUpdateMessage(context: Context) {
        Toast.makeText(context, R.string.status_update, Toast.LENGTH_LONG).show()
    }

    override fun checkBoxMessage(context: Context) {
        Toast.makeText(context, R.string.select_checkbox, Toast.LENGTH_LONG).show()
    }
}