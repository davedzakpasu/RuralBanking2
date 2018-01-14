/**
 * 
 */
package com.cergi.ruralbanking.entities;

/**
 * Created by CERGI on  23 janv. 2015
 *
 */
public class PERIODICITE {
	public String PERIODIC_CODE;
    public String PERIODIC_LIBELLE;
    public int PERIODIC_NBRE_JOUR;
    public String PERIODIC_LIBELLE_ABREGE;
//    public int PERIODIC_NBRE_JOUR_AV_REANCE;
    
    @Override
    public String toString() {
        return this.PERIODIC_LIBELLE;
    }
}
