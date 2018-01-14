/**
 * 
 */
package com.cergi.ruralbanking.entities;

/**
 * Created by CERGI on  27 janv. 2015
 *
 */
public class LOCALITE {
	public String LOCA_CODE;
    public String LOCA_LIBELLE;
    
    @Override
    public String toString() {
        return this.LOCA_LIBELLE;
    }
}
