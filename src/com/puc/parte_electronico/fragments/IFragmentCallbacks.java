package com.puc.parte_electronico.fragments;

import com.puc.parte_electronico.model.TrafficTicket;

/**
 * Created by jose on 5/20/14.
 */
public interface IFragmentCallbacks {
    public void updateTicket(TrafficTicket ticket);
    public TrafficTicket getTicket();
}
