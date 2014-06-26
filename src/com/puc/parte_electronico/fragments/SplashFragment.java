package com.puc.parte_electronico.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.puc.parte_electronico.R;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jose on 6/26/14.
 */
public class SplashFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container);
        return view;
    }
}
