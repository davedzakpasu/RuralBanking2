package com.cergi.ruralbanking.entities;

/**
 * Created by Thierry on 05/02/14.
 */
public class PAYS_NATIONAL
{
    public String PAYS_CODE ;
    public String DEVISE_CODE;
    public String PAYS_NATION ;
    public String PAYS_NOM ;
    public String PAYS_UEMOA ;
    public String PAYS_CODE_ISO ;
    public String CODE_REG ;
    public String PAYS_NUM_ISO ;

    @Override
    public String toString() {
        return this.PAYS_NOM;
    }
}

