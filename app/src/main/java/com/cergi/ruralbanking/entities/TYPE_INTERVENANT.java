package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 06/02/14.
 */
public class TYPE_INTERVENANT {
    public String TYPINTERV_CODE;
    public String TYPINTERV_LIBELLE_TYPE;

    @Override
    public String toString() {
        return this.TYPINTERV_LIBELLE_TYPE;
    }
}
