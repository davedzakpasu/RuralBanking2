package com.cergi.ruralbanking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.cergi.ruralbanking.entities.ACTIVITE;
import com.cergi.ruralbanking.entities.CATEGORIE_CPTE;
import com.cergi.ruralbanking.entities.DETAILS_CPTE;
import com.cergi.ruralbanking.entities.DEVISE;
import com.cergi.ruralbanking.entities.Empreinte;
import com.cergi.ruralbanking.entities.GENRE_CPTE;
import com.cergi.ruralbanking.entities.LOCALITE;
import com.cergi.ruralbanking.entities.NATURE_PIECE_ID;
import com.cergi.ruralbanking.entities.OBJET;
import com.cergi.ruralbanking.entities.OpResult;
import com.cergi.ruralbanking.entities.PAYS_NATIONAL;
import com.cergi.ruralbanking.entities.PERIODICITE;
import com.cergi.ruralbanking.entities.PROFESSION;
import com.cergi.ruralbanking.entities.SECTEUR_ACTIV;
import com.cergi.ruralbanking.entities.SIT_MATRIM;
import com.cergi.ruralbanking.entities.STATUT;
import com.cergi.ruralbanking.entities.TYPE_ENGAGEMENT;
import com.cergi.ruralbanking.entities.UTILISATEUR;
import com.cergi.ruralbanking.fingerprints.MyApplication;

/**
 * Created by Thierry on 05/02/14.
 */
@SuppressLint("SimpleDateFormat")
public class Safe {
	public static String ServerIP = "";
	static SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	public static final String APIkey = "3cb6f8f2-e7e1-44b6-bed7-9050224aa674";

	public static Activity LoginScreenActivity = null;
	public static Boolean Error = false;
	public static UTILISATEUR CurrentUser = null;
	public static Activity MasterObject = null;
	public static MyApplication application;
	public static boolean start = false;

	public static List<PAYS_NATIONAL> ListPays = null;
	public static List<NATURE_PIECE_ID> ListNaturePieceId = null;
	public static List<STATUT> ListStatut = null;
	public static List<SIT_MATRIM> ListSitMatrim = null;
	public static List<ACTIVITE> ListActivite = null;
//	public static List<TYPE_INTERVENANT> ListTypeIntervenant = null;
	public static List<PROFESSION> ListProfession = null;
	public static List<Empreinte> ListEmpreinte = null;
	public static List<PERIODICITE> ListPeriodicite = null;
	public static List<SECTEUR_ACTIV> ListSecteurActiv = null;
	public static List<LOCALITE> ListLocal = null;
	public static List<OBJET> ListObjets = null;
	public static List<TYPE_ENGAGEMENT> ListTypeEngagement = null;
//	public static List<FINGERPRINTS> ListFingerprint = null;
	public static byte[] Photo = null;
	public static List<DEVISE> ListDevise = null;

	public static List<CATEGORIE_CPTE> ListCategorieCpte = null;
	public static List<GENRE_CPTE> ListGenre = null;
	public static DETAILS_CPTE DetailsCpte = null;

	public static View ViewIdentite = null;
	public static View ViewInfoGene = null;
	public static View ViewInfoDigit = null;

	public static String Matricule = "";
	public static String NomReduit = "";
	public static OpResult AccountOpenResult = null;
	public static OpResult CashDepositResult = null;
	public static OpResult TransferSend = null;
	public static OpResult TransferReceive = null;
	public static OpResult CashWithdrawResult = null;
	public static OpResult SaveDemandeResult = null;
	public static OpResult FingerprintsSaveResult = null;
	public static OpResult FingerprintsHistoResult = null;
	public static OpResult SavePhotoResult = null;

	public static void LoadConfig() {
		SharedPreferences sharedPref = LoginScreenActivity.getPreferences(Context.MODE_PRIVATE);
		String server_ip = sharedPref.getString("server_ip", Safe.ServerIP);
		ServerIP = server_ip;
	}

	public static String getSERVER_URL() {
		return "http://" + ServerIP + "/ruralbanking_ws";
	}

	public static Date stringToDate(String sDate)
			throws java.text.ParseException {
		return formatter.parse(sDate);
	}

	public static Date JsonDateToDate(String jsonDate) {
		// "/Date(1321867151710)/"
		int idx1 = jsonDate.indexOf("(");
		int idx2 = jsonDate.indexOf("+");
		String s = jsonDate.substring(idx1 + 1, idx2);
		long l = Long.valueOf(s);
		return new Date(l);
	}
}
