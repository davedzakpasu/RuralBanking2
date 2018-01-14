package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 13/02/14.
 */
public class GENRE_CPTE {
    public String CODE;
    public String PLANCPTA_NUMCPTE;
    public String PLANCPTA_LIBELLE;

    @Override
    public String toString() {
        return this.PLANCPTA_LIBELLE;
    }
}
