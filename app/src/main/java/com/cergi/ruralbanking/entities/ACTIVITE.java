package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 05/02/14.
 */
public class ACTIVITE {
    public String ACTBCEAO_CODE;
    public String ACTBCEAO_LIBELLE;

    @Override
    public String toString() {
        return this.ACTBCEAO_LIBELLE;
    }
}
