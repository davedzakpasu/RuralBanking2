package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 05/02/14.
 */
public class NATURE_PIECE_ID {
    public String NATURE_ID_CODE;
    public String NATURE_ID_LIBELLE;
    public boolean NATURE_OPERATION ;

    @Override
    public String toString() {
        return this.NATURE_ID_LIBELLE;
    }
}
