package com.ecnu.pizzaexpressapplication.main.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends PizzaExpressBaseFragment {


    public PersonalFragment() {
        // Required empty public constructor
    }

    public static PersonalFragment newInstance() {
        return new PersonalFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

}
