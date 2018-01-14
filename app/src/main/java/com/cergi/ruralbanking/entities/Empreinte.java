/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cergi.ruralbanking.entities;

import java.io.Serializable;

/**
 * 
 * @author st-homme
 */
public class Empreinte implements Serializable {
	private static final long serialVersionUID = 1L;
	public static String mainGauche = "G";
	public static String mainDroite = "D";
	public static String pouce = "1";
	public static String index = "2";
	public static String majeur = "3";
	public static String annulaire = "4";
	public static String auriculaire = "5";
	
	private Integer id;

	private String typeDoigt;

	private String typeMain;

	private byte[] imageFinger;

	public Empreinte() {
	}

	public Empreinte(Integer id) {
		this.id = id;
	}

	public Empreinte(String typeDoigt, String typeMain) {
		super();
		this.typeDoigt = typeDoigt;
		this.typeMain = typeMain;
	}

	public Empreinte(String typeDoigt, String typeMain, byte[] imageFinger) {
		super();
		this.typeDoigt = typeDoigt;
		this.typeMain = typeMain;
		this.imageFinger = imageFinger;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTypeDoigt() {
		return typeDoigt;
	}

	public void setTypeDoigt(String typeDoigt) {
		this.typeDoigt = typeDoigt;
	}

	public String getTypeMain() {
		return typeMain;
	}

	public void setTypeMain(String typeMain) {
		this.typeMain = typeMain;
	}

	public byte[] getImageFinger() {
		return imageFinger;
	}

	public void setImageFinger(byte[] imageProduit) {
		this.imageFinger = imageProduit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((typeDoigt == null) ? 0 : typeDoigt.hashCode());
		result = prime * result
				+ ((typeMain == null) ? 0 : typeMain.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Empreinte other = (Empreinte) obj;
		if (typeDoigt == null) {
			if (other.typeDoigt != null)
				return false;
		} else if (!typeDoigt.equals(other.typeDoigt))
			return false;
		if (typeMain == null) {
			if (other.typeMain != null)
				return false;
		} else if (!typeMain.equals(other.typeMain))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return typeMain + "  " + typeDoigt;
	}

}
