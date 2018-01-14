/**
 * 
 */
package com.cergi.ruralbanking.entities;

/**
 * Created by CERGI on  2 févr. 2015
 *
 */
public class OBJET {
	public String OBJPRET_CODE;
	public String OBJPRET_LIBELLE;
	
	@Override
    public String toString() {
        return this.OBJPRET_LIBELLE;
    }
}
