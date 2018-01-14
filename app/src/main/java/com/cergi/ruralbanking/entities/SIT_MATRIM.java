package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 05/02/14.
 */
public class SIT_MATRIM {
    public String SITMATRIM_CODE ;
    public String SITMATRIM_LIBELLE ;
    public String SITMATRIM_MONETIQ ;

    @Override
    public String toString() {
        return this.SITMATRIM_LIBELLE;
    }
}
