package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 05/02/14.
 */
public class STATUT {
    public String STATUT_CODE ;
    public String STATUT_LIBELLE;
    public String STATUT_LIB_ABREGE;
    public String STATUT_TYPE_PERSN;
    public String STATUT_SEXE ;
    public String CATEG_PM ;
    public String CODE_CATEGORIE;
    public String STATUT_MONETIQ ;

    @Override
    public String toString() {
        return this.STATUT_LIBELLE + " ( "+ this.STATUT_LIB_ABREGE + " )";
    }
}
