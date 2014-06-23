package com.puc.parte_electronico.model;

import android.content.Context;

/**
 * Created by jose on 5/27/14.
 */
public abstract class Validator<T> {
    public abstract String getErrorMessage(Context context);
    public abstract boolean validate(T object);

}
