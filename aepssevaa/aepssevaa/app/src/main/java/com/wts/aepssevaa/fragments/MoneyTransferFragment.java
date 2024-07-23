package com.wts.aepssevaa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.wts.aepssevaa.R;
import com.wts.aepssevaa.activities.MyCommissionActivity;
import com.wts.aepssevaa.adapters.CommissionAdapter;
import com.wts.aepssevaa.models.MyCommissionModel;

import java.util.ArrayList;



public class MoneyTransferFragment extends Fragment {

    ArrayList<MyCommissionModel> myCommissionModelArrayList;

    ListView listView;

    CommissionAdapter commissionAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_moneytransfer, container, false);

        myCommissionModelArrayList = MyCommissionActivity.moneyTransferCommissionList;
        listView = view.findViewById(R.id.gas_list);
        commissionAdapter = new CommissionAdapter(getContext(),getActivity(), myCommissionModelArrayList);

        listView.setAdapter(commissionAdapter);
        return view;
    }
}

