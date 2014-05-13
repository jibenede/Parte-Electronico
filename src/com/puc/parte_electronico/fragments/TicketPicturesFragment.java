package com.puc.parte_electronico.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.puc.ParteElectronico.R;

/**
 * Created by jose on 5/13/14.
 */
public class TicketPicturesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_pictures, container, false);
    }
}
