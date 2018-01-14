/**
 * 
 */
package com.cergi.ruralbanking.entities;

/**
 * Created by CERGI on  27 janv. 2015
 *
 */
public class TYPE_ENGAGEMENT {
	public String CODE_TYPE_ENGAGEMENT;
    public String LIBELLE_ENGAGEMENT;
    public String TYPENG_TYPE_CREDIT;
    public String TYPENG_TYPDOSS;
    
    @Override
    public String toString() {
        return this.LIBELLE_ENGAGEMENT;
    }
}
