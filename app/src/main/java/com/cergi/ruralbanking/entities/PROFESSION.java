package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 07/02/14.
 */
public class PROFESSION {
    public String PROFESS_CODE;
    public String PROFESS_LIBELLE;

    @Override
    public String toString() {
        return this.PROFESS_LIBELLE;
    }
}
