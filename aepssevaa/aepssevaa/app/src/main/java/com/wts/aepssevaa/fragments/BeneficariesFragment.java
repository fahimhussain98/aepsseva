package com.wts.aepssevaa.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wts.aepssevaa.R;
import com.wts.aepssevaa.activities.NewMoneyTransferActivity;
import com.wts.aepssevaa.adapters.RecipientAdapter;
import com.wts.aepssevaa.models.RecipientModel;

import java.util.ArrayList;

public class BeneficariesFragment extends Fragment {

    ArrayList<RecipientModel> recipientModelArrayList;
    RecyclerView recipientRecycler;
    String mobileNumber;
    String userId, userName;
    SharedPreferences sharedPreferences;
    String deviceId, deviceInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beneficaries, container, false);
        inhitViews(view);

        mobileNumber = NewMoneyTransferActivity.senderMobileNumber;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        userName = sharedPreferences.getString("userNameResponse", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        recipientModelArrayList = NewMoneyTransferActivity.recipientModelArrayList;

        RecipientAdapter recipientAdapter = new RecipientAdapter(getContext(), getActivity(),
                recipientModelArrayList, mobileNumber, userId, userName, deviceId, deviceInfo);
        recipientRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));
        recipientRecycler.setAdapter(recipientAdapter);

        return view;
    }

    private void inhitViews(View view) {
        recipientRecycler = view.findViewById(R.id.recipient_recycler_view);
    }
}
