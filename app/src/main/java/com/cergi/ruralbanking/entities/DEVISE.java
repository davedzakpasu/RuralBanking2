package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 13/02/14.
 */
public class DEVISE {
    public String CODE;
    public String LIBELLE;

    @Override
    public String toString() {
        return this.CODE + " : " +this.LIBELLE;
    }
}
