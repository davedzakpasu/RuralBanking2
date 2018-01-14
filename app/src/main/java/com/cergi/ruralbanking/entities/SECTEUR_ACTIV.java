/**
 * 
 */
package com.cergi.ruralbanking.entities;

/**
 * Created by CERGI on  27 janv. 2015
 *
 */
public class SECTEUR_ACTIV {
	public String SECTACT_CODE;
    public String SECTACT_LIBELLE;
    
    @Override
    public String toString() {
        return this.SECTACT_LIBELLE;
    }
}
