package com.example.dgtechhealthcare.pharmacist

import android.os.Bundle
import android.view.*
import android.widget.Adapter
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dgtechhealthcare.R
import com.example.dgtechhealthcare.pharmacist.view.RequestFragment

class RequestTabbedActivity : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_request_tabbed,container,false)
        val viewPager = view.findViewById<ViewPager>(R.id.view_pager)
        setupViewPager(viewPager)
        val tabs = view.findViewById<TabLayout>(R.id.tabs)
        val s = tabs.selectedTabPosition
        Toast.makeText(activity,"$s",Toast.LENGTH_LONG).show()
        tabs.setupWithViewPager(viewPager)

        return view
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(RequestFragment(),"Requests")
        adapter.addFragment(HistoryFragment(),"History")
        viewPager?.adapter = adapter
    }

    class Adapter(fm : FragmentManager) : FragmentPagerAdapter(fm){
        val mFragmentList = arrayListOf<Fragment>()
        val mFragmentTitleList = arrayListOf<String>()
        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        fun addFragment(fragment: Fragment,title:String){
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }


}