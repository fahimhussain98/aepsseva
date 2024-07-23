package com.wts.aepssevaa.plansFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.wts.aepssevaa.R;
import com.wts.aepssevaa.activities.PlansActivity;
import com.wts.aepssevaa.adapters.MyPlansAdaper;
import com.wts.aepssevaa.models.PlansModel;

import java.util.ArrayList;


public class BsnlValidExtentionFragment extends Fragment {

    ArrayList<PlansModel> bsnlValidExtArrayList;

    ListView topUpList;

    MyPlansAdaper myPlansAdaper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bsnl_valid_extention, container, false);

        bsnlValidExtArrayList= PlansActivity.bsnlValidExtArrayList;
        topUpList=view.findViewById(R.id.top_up_list);
        myPlansAdaper=new MyPlansAdaper(getContext(),getActivity(),bsnlValidExtArrayList);

        topUpList.setAdapter(myPlansAdaper);

        return view;
    }
}

