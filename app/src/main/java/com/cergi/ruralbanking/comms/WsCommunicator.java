package com.cergi.ruralbanking.comms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.entities.ACTIVITE;
import com.cergi.ruralbanking.entities.CATEGORIE_CPTE;
import com.cergi.ruralbanking.entities.DETAILS_CPTE;
import com.cergi.ruralbanking.entities.DEVISE;
import com.cergi.ruralbanking.entities.Empreinte;
import com.cergi.ruralbanking.entities.GENRE_CPTE;
import com.cergi.ruralbanking.entities.INFO_CAISSE;
import com.cergi.ruralbanking.entities.INFO_CLIENT;
import com.cergi.ruralbanking.entities.INFO_COMPTE;
import com.cergi.ruralbanking.entities.INFO_TRANSFERT;
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
import com.cergi.ruralbanking.entities.TYPE_INTERVENANT;
import com.cergi.ruralbanking.entities.UTILISATEUR;
//import com.cergi.ruralbanking.entities.FINGERPRINTS;

public class WsCommunicator {
    static String BaseURI() {
        return Safe.getSERVER_URL() + "/DataService.svc/";
    }

    static DETAILS_CPTE ExtractDetailsCpte(JSONObject r) throws Exception {
        DETAILS_CPTE result = null;
        if (r != null) {
            result = new DETAILS_CPTE();
            result.CATAUXIL_PERIOD_REL = r.getString("CATAUXIL_PERIOD_REL");
            result.CLE_RIB = r.getString("CLE_RIB");
            result.MODARR_CODE = r.getString("MODARR_CODE");
            result.MODARR_LIBELLE = r.getString("MODARR_LIBELLE");
            result.NUM_CPTE = r.getString("NUM_CPTE");
        }
        return result;
    }

    static INFO_CLIENT ExtractInfoClient(JSONObject r) throws Exception {
        INFO_CLIENT result = null;
        if (r != null) {
            result = new INFO_CLIENT();
            result.ETCIV_MATRICULE = r.getString("ETCIV_MATRICULE");
            result.PERSPHYS_NOM = r.getString("PERSPHYS_NOM");
            result.PERSPHYS_PRENOM = r.getString("PERSPHYS_PRENOM");

            result.STATUT_CODE = r.getString("STATUT_CODE");
            result.PERSPHYS_DATENAISS = r.getString("PERSPHYS_DATENAISS");
            result.PERSPHYS_LIEUNAISS = r.getString("PERSPHYS_LIEUNAISS");
            result.PAYS_CODE = r.getString("PAYS_CODE");
            result.ACTBCEAO_CODE = r.getString("ACTBCEAO_CODE");
            result.ETCIV_INTERNET = r.getString("ETCIV_INTERNET");
            result.ETCIV_PAYS_RESIDENCE = r.getString("ETCIV_PAYS_RESIDENCE");
            result.ETCIV_VILLE_RESIDENCE = r.getString("ETCIV_VILLE_RESIDENCE");
            result.ETCIV_ADRESS_GEOG1 = r.getString("ETCIV_ADRESS_GEOG1");
            result.ETCIV_TELEPHONE = r.getString("ETCIV_TELEPHONE");

            result.PROFESS_CODE = r.getString("PROFESS_CODE");
            result.SITMATRIM_CODE = r.getString("SITMATRIM_CODE");
            result.NATURE_ID_CODE = r.getString("NATURE_ID_CODE");
            result.PERSPHYS_PIECE_ID = r.getString("PERSPHYS_PIECE_ID");
            result.PERSPHYS_PIECE_ID_DATE = r.getString("PERSPHYS_PIECE_ID_DATE");
            result.PERSPHYS_PIECE_ID_ORGAN = r.getString("PERSPHYS_PIECE_ID_ORGAN");

            result.PERSPHYS_MERE_NOM = r.getString("PERSPHYS_MERE_NOM");
            result.PERSPHYS_MERE_PRENOM = r.getString("PERSPHYS_MERE_PRENOM");
            result.PERSPHYS_MERE_DATENAISS = r.getString("PERSPHYS_MERE_DATENAISS");

            result.PERSPHYS_PERE_NOM = r.getString("PERSPHYS_PERE_NOM");
            result.PERSPHYS_PERE_PRENOM = r.getString("PERSPHYS_PERE_PRENOM");
            result.PERSPHYS_PERE_DATENAISS = r.getString("PERSPHYS_PERE_DATENAISS");

            result.FP_PHOTO = TextUtils.isEmpty(r.getString("FP_PHOTO")) ? null : Base64.decode(r.getString("FP_PHOTO"), Base64.DEFAULT);
        }
        return result;
    }

    static INFO_TRANSFERT ExtractInfoTransfert(JSONObject r) throws Exception {
        INFO_TRANSFERT result = null;
        if (r != null) {
            result = new INFO_TRANSFERT();
            result.TRANSF_OPERATEUR = r.getString("TRANSF_OPERATEUR");
            result.TRANSF_NOMEXP = r.getString("TRANSF_NOMEXP");
            result.TRANSF_PRENOMEXP = r.getString("TRANSF_PRENOMEXP");
            result.TRANSF_MONTANT = r.getString("TRANSF_MONTANT");
            result.TRANSF_DEVISE = r.getString("TRANSF_DEVISE");
            result.TRANSF_NOMBENEF = r.getString("TRANSF_NOMBENEF");
            result.TRANSF_PRENOMBENEF = r.getString("TRANSF_PRENOMBENEF");
            result.TRANSF_QUESTION = r.getString("TRANSF_QUESTION");
            result.TRANSF_REPONSE = r.getString("TRANSF_REPONSE");
            result.TRANSF_PAYSPROV = r.getString("TRANSF_PAYSPROV");
        }
        return result;
    }

    static INFO_CAISSE ExtractInfoCaisse(JSONObject r) throws Exception {
        INFO_CAISSE result = null;

        if (r != null) {
            result = new INFO_CAISSE();
            INFO_CAISSE.LOT_FAIT = r.getInt("LOT_FAIT");
            INFO_CAISSE.LOT_INTITULE = r.getString("LOT_INTITULE");
            INFO_CAISSE.DEVISE_CODE = r.getString("DEVISE_CODE");
            INFO_CAISSE.PLANCPTA_NUM_COMPTE = r.getString("PLANCPTA_NUM_COMPTE");
            INFO_CAISSE.DEVISE_FORME = r.getString("DEVISE_FORME");
            INFO_CAISSE.DEVISE_TYPEMONNAIE = r.getString("DEVISE_TYPEMONNAIE");
        }
        return result;
    }

    static OpResult ExtractOpResult(JSONObject r) throws Exception {
        OpResult result = null;
        if (r != null) {
            result = new OpResult();
            result.Success = r.getBoolean("Success");
            result.ErrorMessage = r.getString("ErrorMessage");
        }
        return result;
    }

    static GENRE_CPTE ExtractGenreCpte(JSONObject r) throws Exception {
        GENRE_CPTE result = null;
        if (r != null) {
            result = new GENRE_CPTE();
            result.CODE = r.getString("CODE");
            result.PLANCPTA_LIBELLE = r.getString("PLANCPTA_LIBELLE");
            result.PLANCPTA_NUMCPTE = r.getString("PLANCPTA_NUMCPTE");
        }
        return result;
    }

    static CATEGORIE_CPTE ExtractCategorieCpte(JSONObject r) throws Exception {
        CATEGORIE_CPTE result = null;
        if (r != null) {
            result = new CATEGORIE_CPTE();
            result.CODE = r.getString("CODE");
            result.LIBELLE = r.getString("LIBELLE");
        }
        return result;
    }

    static DEVISE ExtractDevise(JSONObject r) throws Exception {
        DEVISE result = null;
        if (r != null) {
            result = new DEVISE();
            result.CODE = r.getString("CODE");
            result.LIBELLE = r.getString("LIBELLE");
        }
        return result;
    }

    static ACTIVITE ExtractActivite(JSONObject r) throws Exception {
        ACTIVITE result = null;

        if (r != null) {
            result = new ACTIVITE();
            result.ACTBCEAO_LIBELLE = r.getString("ACTBCEAO_LIBELLE");
            result.ACTBCEAO_CODE = r.getString("ACTBCEAO_CODE");
        }
        return result;
    }

    static PROFESSION ExtractProfession(JSONObject r) throws Exception {
        PROFESSION result = null;

        if (r != null) {
            result = new PROFESSION();
            result.PROFESS_CODE = r.getString("PROFESS_CODE");
            result.PROFESS_LIBELLE = r.getString("PROFESS_LIBELLE");
        }
        return result;
    }

    static UTILISATEUR ExtractUtilisateur(JSONObject r) throws Exception {
        UTILISATEUR result = null;

        if (r != null) {
            result = new UTILISATEUR();
            result.USER_ABREGE = r.getString("USER_ABREGE");
            result.USER_ACTIF = r.getBoolean("USER_ACTIF");
            result.USER_CODE = r.getString("USER_CODE");
            result.USER_NOM = r.getString("USER_NOM");
            result.AGENCE_CODE = r.getString("AGENCE_CODE");
            result.SERVAG_CODE = r.getString("SERVAG_CODE");
            result.USER_MOT_PASSE = "";
            result.ETCIV_MATRICULE = r.getString("ETCIV_MATRICULE");
        }
        return result;
    }

    static TYPE_INTERVENANT ExtractTypeIntervenant(JSONObject r)
            throws Exception {
        TYPE_INTERVENANT result = null;

        if (r != null) {
            result = new TYPE_INTERVENANT();
            result.TYPINTERV_CODE = r.getString("TYPINTERV_CODE");
            result.TYPINTERV_LIBELLE_TYPE = r
                    .getString("TYPINTERV_LIBELLE_TYPE");
        }
        return result;
    }

    static NATURE_PIECE_ID ExtractNaturePieceID(JSONObject r) throws Exception {
        NATURE_PIECE_ID result = null;

        if (r != null) {
            result = new NATURE_PIECE_ID();
            result.NATURE_ID_CODE = r.getString("NATURE_ID_CODE");
            result.NATURE_ID_LIBELLE = r.getString("NATURE_ID_LIBELLE");
            result.NATURE_OPERATION = r.getBoolean("NATURE_OPERATION");
        }
        return result;
    }

    static PAYS_NATIONAL ExtractPaysNational(JSONObject r) throws Exception {
        PAYS_NATIONAL result = null;

        if (r != null) {
            result = new PAYS_NATIONAL();
            result.DEVISE_CODE = r.getString("DEVISE_CODE");
            result.PAYS_CODE = r.getString("PAYS_CODE");
            result.PAYS_CODE_ISO = r.getString("PAYS_CODE_ISO");
            result.PAYS_NATION = r.getString("PAYS_NATION");
            result.PAYS_NOM = r.getString("PAYS_NOM");
        }
        return result;
    }

    static PERIODICITE ExtractPeriodicite(JSONObject r) throws Exception {
        PERIODICITE result = null;

        if (r != null) {
            result = new PERIODICITE();
            result.PERIODIC_CODE = r.getString("PERIODIC_CODE");
            result.PERIODIC_LIBELLE = r.getString("PERIODIC_LIBELLE");
            result.PERIODIC_NBRE_JOUR = r.getInt("PERIODIC_NBRE_JOUR");
            result.PERIODIC_LIBELLE_ABREGE = r.getString("PERIODIC_LIBELLE_ABREGE");
//			result.PERIODIC_NBRE_JOUR_AV_REANCE = r.getInt("PERIODIC_NBRE_JOUR_AV_REANCE");
        }
        return result;
    }

    static SECTEUR_ACTIV ExtractSecteurActiv(JSONObject r) throws Exception {
        SECTEUR_ACTIV result = null;

        if (r != null) {
            result = new SECTEUR_ACTIV();
            result.SECTACT_CODE = r.getString("SECTACT_CODE");
            result.SECTACT_LIBELLE = r.getString("SECTACT_LIBELLE");
        }
        return result;
    }

    static LOCALITE ExtractLocalite(JSONObject r) throws Exception {
        LOCALITE result = null;

        if (r != null) {
            result = new LOCALITE();
            result.LOCA_CODE = r.getString("LOCA_CODE");
            result.LOCA_LIBELLE = r.getString("LOCA_LIBELLE");
        }
        return result;
    }

    private static OBJET ExtractObjet(JSONObject r) throws Exception {
        OBJET result = null;

        if (r != null) {
            result = new OBJET();
            result.OBJPRET_CODE = r.getString("OBJPRET_CODE");
            result.OBJPRET_LIBELLE = r.getString("OBJPRET_LIBELLE");
        }
        return result;
    }

    private static TYPE_ENGAGEMENT ExtractTypeEngagement(JSONObject r) throws Exception {
        TYPE_ENGAGEMENT result = null;

        if (r != null) {
            result = new TYPE_ENGAGEMENT();
            result.CODE_TYPE_ENGAGEMENT = r.getString("CODE_TYPE_ENGAGEMENT");
            result.LIBELLE_ENGAGEMENT = r.getString("LIBELLE_ENGAGEMENT");
            result.TYPENG_TYPE_CREDIT = r.getString("TYPENG_TYPE_CREDIT");
            result.TYPENG_TYPDOSS = r.getString("TYPENG_TYPDOSS");
        }
        return result;
    }

    private static SIT_MATRIM ExtractSitMatrim(JSONObject r) throws Exception {
        SIT_MATRIM result = null;

        if (r != null) {
            result = new SIT_MATRIM();
            result.SITMATRIM_CODE = r.getString("SITMATRIM_CODE");
            result.SITMATRIM_LIBELLE = r.getString("SITMATRIM_LIBELLE");
            result.SITMATRIM_MONETIQ = r.getString("SITMATRIM_MONETIQ");
        }
        return result;
    }

    private static STATUT ExtractStatut(JSONObject r) throws Exception {
        STATUT result = null;

        if (r != null) {
            result = new STATUT();
            result.STATUT_CODE = r.getString("STATUT_CODE");
            result.STATUT_LIB_ABREGE = r.getString("STATUT_LIB_ABREGE");
            result.STATUT_LIBELLE = r.getString("STATUT_LIBELLE");
            result.STATUT_SEXE = r.getString("STATUT_SEXE");
            result.STATUT_TYPE_PERSN = r.getString("STATUT_TYPE_PERSN");
        }
        return result;
    }

    private static INFO_COMPTE ExtractInfoCompte(JSONObject r) throws Exception {
        INFO_COMPTE result = null;
        if (r != null) {
            result = new INFO_COMPTE();

            result.AGENCE_CODE = r.getString("AGENCE_CODE");
            result.CPTEAUXIL_ADR1_SPEC = r.getString("CPTEAUXIL_ADR1_SPEC");
//			result.CPTEAUXIL_CLE_RIB = r.getString("CPTEAUXIL_CLE_RIB");
            result.CPTEAUXIL_CPTGEN_ASSOC = r.getString("CPTEAUXIL_CPTGEN_ASSOC");
            result.CPTEAUXIL_DATE_FERM = r.getString("CPTEAUXIL_DAT_FERMETURE");
//			result.CPTEAUXIL_MONT_INDISP = r.getDouble("CPTEAUXIL_MONT_INDISPO");
            result.CPTEAUXIL_NUMCPT = r.getString("CPTEAUXIL_NUMCPT");
//			result.CPTEAUXIL_OPP_CPTE = r.getBoolean("CPTEAUXIL_OPP_CPTE");
            result.CPTEAUXIL_SOLDE_DISP = r.getDouble("CPTEAUXIL_SOLDE_DISPO");
            result.DEVISE_CODE = r.getString("DEVISE_CODE");
            result.ETCIV_ADRESS_GEO1 = r.getString("ETCIV_ADRESS_GEOG1");
            result.ETCIV_MATRICULE = r.getString("ETCIV_MATRICULE");
            result.ETCIV_NOMREDUIT = r.getString("ETCIV_NOMREDUIT");
//			result.GESTIONNAIRE_CODE = r.getString("GESTIONNAIRE_CODE");
//			result.MODARR_CODE = r.getString("MODARR_CODE");
            result.NATURE_ID_CODE = r.getString("NATURE_ID_CODE");
            result.PERSPHYS_PIECE_ID = r.getString("PERSPHYS_PIECE_ID");
            result.PERSPHYS_PIECE_ID_DATE = r.getString("PERSPHYS_PIECE_ID_DATE");
            result.STATUT_TYPE_PERSN = r.getString("STATUT_TYPE_PERSN");
            result.FP_PHOTO = Base64.decode(r.getString("FP_PHOTO"), Base64.DEFAULT);
            result.MessageErreur = r.getString("MessageErreur");
        }
        return result;
    }

    public static ArrayList<DEVISE> ListDevise(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<DEVISE> result = new ArrayList<DEVISE>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_devise");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    DEVISE o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractDevise(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<GENRE_CPTE> ListGenreCpte(Context ctx, String categ) {
        String jsonString = "";
        InputStream is;

        ArrayList<GENRE_CPTE> result = new ArrayList<GENRE_CPTE>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_genre_cpte");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .key("categ").object().key("Value").value(categ)
                    .endObject().endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    GENRE_CPTE o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractGenreCpte(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<CATEGORIE_CPTE> ListCategorieCpte(Context ctx,
                                                              String matricule, String usercode) {
        String jsonString = "";
        InputStream is;

        ArrayList<CATEGORIE_CPTE> result = new ArrayList<CATEGORIE_CPTE>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_categorie_cpte");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .key("matricule").object().key("Value").value(matricule)
                    .endObject().key("usercode").object().key("Value")
                    .value(usercode).endObject().endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    CATEGORIE_CPTE o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractCategorieCpte(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static String CreerEtatCivil(Context ctx, String USER_CODE, String agence, String codetype_interv, String statut_code, String nomclient, String prenomclient, String datenaiss, String lieunaiss, String nationalite, String ACTBCEAO_CODE,
                                        String email, String paysresid, String villeresid, String ETCIV_ADRESS_GEOG, String ETCIV_TELEPHONE, String SITMATRIM_CODE, String NATURE_PIECE_ID_CODE, String PERSPHYS_PIECE_ID, String PERSPHYS_PIECE_ID_DATE, String PERSPHYS_PIECE_ID_ORGAN,
                                        String PERSPHYS_PROFESSION, String PERSPHYS_MERE_NOM, String PERSPHYS_MERE_PRENOM, String PERSPHYS_MERE_DATENAIS, String PERSPHYS_PERE_NOM, String PERSPHYS_PERE_PRENOM, String PERSPHYS_PERE_DATENAIS) {
        String jsonString = "";
        InputStream is;

        String result = "";
        try {
            HttpPost request = new HttpPost(BaseURI() + "creer_etat_civil");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("USER_CODE").object().key("Value").value(USER_CODE).endObject()
                    .key("agence").object().key("Value").value(agence).endObject()
                    .key("codetype_interv").object().key("Value").value(codetype_interv).endObject()
                    .key("statut_code").object().key("Value").value(statut_code).endObject()
                    .key("nomclient").object().key("Value").value(nomclient).endObject()
                    .key("prenomclient").object().key("Value").value(prenomclient).endObject()
                    .key("datenaiss").object().key("Value").value(datenaiss).endObject()
                    .key("lieunaiss").object().key("Value").value(lieunaiss).endObject()
                    .key("nationalite").object().key("Value").value(nationalite).endObject()
                    .key("ACTBCEAO_CODE").object().key("Value").value(ACTBCEAO_CODE).endObject()
                    .key("ETCIV_INTERNET").object().key("Value").value(email).endObject()
                    .key("paysresid").object().key("Value").value(paysresid).endObject()
                    .key("villeresid").object().key("Value").value(villeresid).endObject()
                    .key("ETCIV_ADRESS_GEOG").object().key("Value").value(ETCIV_ADRESS_GEOG).endObject()
                    .key("ETCIV_TELEPHONE").object().key("Value").value(ETCIV_TELEPHONE).endObject()
                    .key("SITMATRIM_CODE").object().key("Value").value(SITMATRIM_CODE).endObject()
                    .key("NATURE_PIECE_ID_CODE").object().key("Value").value(NATURE_PIECE_ID_CODE).endObject()
                    .key("PERSPHYS_PIECE_ID").object().key("Value").value(PERSPHYS_PIECE_ID).endObject()
                    .key("PERSPHYS_PIECE_ID_DATE").object().key("Value").value(PERSPHYS_PIECE_ID_DATE).endObject()
                    .key("PERSPHYS_PIECE_ID_ORGAN").object().key("Value").value(PERSPHYS_PIECE_ID_ORGAN).endObject()
                    .key("PERSPHYS_PROFESSION").object().key("Value").value(PERSPHYS_PROFESSION).endObject()
                    .key("PERSPHYS_MERE_NOM").object().key("Value").value(PERSPHYS_MERE_NOM).endObject()
                    .key("PERSPHYS_MERE_PRENOM").object().key("Value").value(PERSPHYS_MERE_PRENOM).endObject()
                    .key("PERSPHYS_MERE_DATENAIS").object().key("Value").value(PERSPHYS_MERE_DATENAIS).endObject()
                    .key("PERSPHYS_PERE_NOM").object().key("Value").value(PERSPHYS_PERE_NOM).endObject()
                    .key("PERSPHYS_PERE_PRENOM").object().key("Value").value(PERSPHYS_PERE_PRENOM).endObject()
                    .key("PERSPHYS_PERE_DATENAIS").object().key("Value").value(PERSPHYS_PERE_DATENAIS).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonString.length() > 2)
                result = jsonString.substring(1, jsonString.length() - 2);
            else
                return jsonString;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static OpResult MajEtatCivil(Context ctx, String matricule, String USER_CODE, String agence, String statut_code, String nomclient, String prenomclient, String datenaiss, String lieunaiss, String nationalite, String ACTBCEAO_CODE,
                                        String email, String paysresid, String villeresid, String ETCIV_ADRESS_GEOG, String ETCIV_TELEPHONE, String SITMATRIM_CODE, String NATURE_PIECE_ID_CODE, String PERSPHYS_PIECE_ID, String PERSPHYS_PIECE_ID_DATE, String PERSPHYS_PIECE_ID_ORGAN,
                                        String PERSPHYS_PROFESSION, String PERSPHYS_MERE_NOM, String PERSPHYS_MERE_PRENOM, String PERSPHYS_MERE_DATENAIS, String PERSPHYS_PERE_NOM, String PERSPHYS_PERE_PRENOM, String PERSPHYS_PERE_DATENAIS) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "update_etciv");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer()
                    .object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("matricule").object().key("Value").value(matricule).endObject()
                    .key("USER_CODE").object().key("Value").value(USER_CODE).endObject()
                    .key("agence").object().key("Value").value(agence).endObject()
                    .key("statut_code").object().key("Value").value(statut_code).endObject()
                    .key("nomclient").object().key("Value").value(nomclient).endObject()
                    .key("prenomclient").object().key("Value").value(prenomclient).endObject()
                    .key("datenaiss").object().key("Value").value(datenaiss).endObject()
                    .key("lieunaiss").object().key("Value").value(lieunaiss).endObject()
                    .key("nationalite").object().key("Value").value(nationalite).endObject()
                    .key("ACTBCEAO_CODE").object().key("Value").value(ACTBCEAO_CODE).endObject()
                    .key("ETCIV_INTERNET").object().key("Value").value(email).endObject()
                    .key("paysresid").object().key("Value").value(paysresid).endObject()
                    .key("villeresid").object().key("Value").value(villeresid).endObject()
                    .key("ETCIV_ADRESS_GEOG").object().key("Value").value(ETCIV_ADRESS_GEOG).endObject()
                    .key("ETCIV_TELEPHONE").object().key("Value").value(ETCIV_TELEPHONE).endObject()
                    .key("SITMATRIM_CODE").object().key("Value").value(SITMATRIM_CODE).endObject()
                    .key("NATURE_PIECE_ID_CODE").object().key("Value").value(NATURE_PIECE_ID_CODE).endObject()
                    .key("PERSPHYS_PIECE_ID").object().key("Value").value(PERSPHYS_PIECE_ID).endObject()
                    .key("PERSPHYS_PIECE_ID_DATE").object().key("Value").value(PERSPHYS_PIECE_ID_DATE).endObject()
                    .key("PERSPHYS_PIECE_ID_ORGAN").object().key("Value").value(PERSPHYS_PIECE_ID_ORGAN).endObject()
                    .key("PERSPHYS_PROFESSION").object().key("Value").value(PERSPHYS_PROFESSION).endObject()
                    .key("PERSPHYS_MERE_NOM").object().key("Value").value(PERSPHYS_MERE_NOM).endObject()
                    .key("PERSPHYS_MERE_PRENOM").object().key("Value").value(PERSPHYS_MERE_PRENOM).endObject()
                    .key("PERSPHYS_MERE_DATENAIS").object().key("Value").value(PERSPHYS_MERE_DATENAIS).endObject()
                    .key("PERSPHYS_PERE_NOM").object().key("Value").value(PERSPHYS_PERE_NOM).endObject()
                    .key("PERSPHYS_PERE_PRENOM").object().key("Value").value(PERSPHYS_PERE_PRENOM).endObject()
                    .key("PERSPHYS_PERE_DATENAIS").object().key("Value").value(PERSPHYS_PERE_DATENAIS).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());
            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static OpResult HistoEmpreintes(Context ctx, String matricule) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "histo_fingerprints");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer()
                    .object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("matricule").object().key("Value").value(matricule).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());
            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }


    public static OpResult EnregistrerEmpreintes(Context ctx, String matricule,
                                                 Empreinte emp) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "save_fingerprints");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer()
                    .object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("matricule").object().key("Value").value(matricule).endObject()
                    .key("FP").object().key("Value").value(Base64.encodeToString(emp.getImageFinger(), Base64.DEFAULT)).endObject()
                    .key("type_main").object().key("Value").value(emp.getTypeMain()).endObject()
                    .key("type_doigt").object().key("Value").value(emp.getTypeDoigt()).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());
            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static OpResult EnregistrerPhoto(Context ctx, String matricule, byte[] img) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "save_photo");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("matricule").object().key("Value").value(matricule).endObject()
                    .key("photo").object().key("Value").value(Base64.encodeToString(img, Base64.DEFAULT)).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());
            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }
        return result;
    }

    public static ArrayList<TYPE_INTERVENANT> ListTypeIntervenant(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<TYPE_INTERVENANT> result = new ArrayList<TYPE_INTERVENANT>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_type_intervenant");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    TYPE_INTERVENANT o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractTypeIntervenant(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<ACTIVITE> ListActivite(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<ACTIVITE> result = new ArrayList<ACTIVITE>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_activite");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    ACTIVITE o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractActivite(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<PROFESSION> ListProfession(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<PROFESSION> result = new ArrayList<PROFESSION>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_profession");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    PROFESSION o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractProfession(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<STATUT> ListStatut(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<STATUT> result = new ArrayList<STATUT>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_statut");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    STATUT o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractStatut(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<SIT_MATRIM> ListSitMatrim(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<SIT_MATRIM> result = new ArrayList<SIT_MATRIM>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_sit_matrim");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    SIT_MATRIM o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractSitMatrim(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<PAYS_NATIONAL> ListPays(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<PAYS_NATIONAL> result = new ArrayList<PAYS_NATIONAL>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_pays");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    PAYS_NATIONAL o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractPaysNational(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<NATURE_PIECE_ID> ListNaturePieceId(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<NATURE_PIECE_ID> result = new ArrayList<NATURE_PIECE_ID>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "list_nature_piece");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    NATURE_PIECE_ID o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractNaturePieceID(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<SECTEUR_ACTIV> ListSecteurActiv(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<SECTEUR_ACTIV> result = new ArrayList<SECTEUR_ACTIV>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "liste_sectact");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    SECTEUR_ACTIV o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractSecteurActiv(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<LOCALITE> ListLocalite(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<LOCALITE> result = new ArrayList<LOCALITE>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "liste_localite");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    LOCALITE o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractLocalite(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<OBJET> ListObjet(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<OBJET> result = new ArrayList<OBJET>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "liste_objets");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    OBJET o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractObjet(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject, "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<TYPE_ENGAGEMENT> ListTypeEngagement(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<TYPE_ENGAGEMENT> result = new ArrayList<TYPE_ENGAGEMENT>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "liste_type_engagement");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    TYPE_ENGAGEMENT o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractTypeEngagement(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<PERIODICITE> ListPeriodicite(Context ctx) {
        String jsonString = "";
        InputStream is;

        ArrayList<PERIODICITE> result = new ArrayList<PERIODICITE>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "liste_periodicite");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    PERIODICITE o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractPeriodicite(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject,
                    "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }


    public static UTILISATEUR Login(Context ctx, String username,
                                    String password) {
        Safe.Error = false;
        UTILISATEUR result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "Login");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .key("username").object().key("Value").value(username.trim()).endObject()
                    .key("password").object().key("Value").value(password.trim()).endObject().endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            int contentLength = (int) responseEntity.getContentLength();
            char[] buffer = new char[contentLength];
            InputStream stream = responseEntity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);

            int hasRead = 0;
            while (hasRead < contentLength) {
                hasRead += reader.read(buffer, hasRead, contentLength - hasRead);
            }

            stream.close();

            JSONObject r = null;
            if (buffer.length > 0)
                r = new JSONObject(new String(buffer));

            if (r != null) {
                result = ExtractUtilisateur(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static INFO_CAISSE InfoCaisse(String user_code, String servag_code) {
        Safe.Error = false;
        INFO_CAISSE result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "InfoCaisse");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .key("user_code").object().key("Value").value(user_code)
                    .endObject().key("servag_code").object().key("Value")
                    .value(servag_code).endObject().endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            int contentLength = (int) responseEntity.getContentLength();
            char[] buffer = new char[contentLength];
            InputStream stream = responseEntity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);

            int hasRead = 0;
            while (hasRead < contentLength)
                hasRead += reader
                        .read(buffer, hasRead, contentLength - hasRead);

            stream.close();

            JSONObject r = null;
            if (buffer.length > 0)
                r = new JSONObject(new String(buffer));

            if (r != null) {
                result = ExtractInfoCaisse(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static DETAILS_CPTE GetDetailsCompte(Context ctx, String matricule,
                                                String categ, String agencecode) {
        Safe.Error = false;
        DETAILS_CPTE result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "get_details_cpte");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .key("matricule").object().key("Value").value(matricule)
                    .endObject().key("categ").object().key("Value")
                    .value(categ).endObject().key("agencecode").object()
                    .key("Value").value(agencecode).endObject().endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            int contentLength = (int) responseEntity.getContentLength();
            char[] buffer = new char[contentLength];
            InputStream stream = responseEntity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);

            int hasRead = 0;
            while (hasRead < contentLength)
                hasRead += reader
                        .read(buffer, hasRead, contentLength - hasRead);

            stream.close();

            JSONObject r = null;
            if (buffer.length > 0)
                r = new JSONObject(new String(buffer));

            if (r != null) {
                result = ExtractDetailsCpte(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static OpResult CreerCompte(Context ctx, String modearrete,
                                       String categ, String agencecode, String cpte, String genrecode,
                                       String devise, String lbl_CPTGEN_ASSOC, String txt_LIBELLE_SPECIF,
                                       String lbl_RIB, String user_code) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "creer_compte");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object().key("APIkey")
                    .object().key("Value").value(Safe.APIkey).endObject()
                    .key("modearrete").object().key("Value").value(modearrete)
                    .endObject().key("categ").object().key("Value")
                    .value(categ).endObject().key("agencecode").object()
                    .key("Value").value(agencecode).endObject().key("cpte")
                    .object().key("Value").value(cpte).endObject()
                    .key("genrecode").object().key("Value").value(genrecode)
                    .endObject().key("devise").object().key("Value")
                    .value(devise).endObject().key("lbl_CPTGEN_ASSOC").object()
                    .key("Value").value(lbl_CPTGEN_ASSOC).endObject()
                    .key("txt_LIBELLE_SPECIF").object().key("Value")
                    .value(txt_LIBELLE_SPECIF).endObject().key("lbl_RIB")
                    .object().key("Value").value(lbl_RIB).endObject()
                    .key("user_code").object().key("Value").value(user_code)
                    .endObject().endObject();
            StringEntity entity = new StringEntity(json.toString());
            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static INFO_COMPTE InfoCompte(Context ctx, String num_compte, byte[] emp, String mat_caissier, String vSchema, String devise) {
        Safe.Error = false;
        INFO_COMPTE result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "InfoCompte");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("num_compte").object().key("Value").value(num_compte).endObject()
                    .key("FP").object().key("Value").value(Base64.encodeToString(emp, Base64.DEFAULT)).endObject()
                    .key("mat_caissier").object().key("Value").value(mat_caissier).endObject()
                    .key("vSchema").object().key("Value").value(vSchema).endObject()
                    .key("devise").object().key("Value").value(devise).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            int contentLength = (int) responseEntity.getContentLength();
            char[] buffer = new char[contentLength];
            InputStream stream = responseEntity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);

            int hasRead = 0;
            while (hasRead < contentLength)
                hasRead += reader
                        .read(buffer, hasRead, contentLength - hasRead);

            stream.close();

            JSONObject r = null;
            if (buffer.length > 0)
                r = new JSONObject(new String(buffer));

            if (r != null) {
                result = ExtractInfoCompte(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static INFO_CLIENT InfoClient(Context ctx, String matricule) {
        Safe.Error = false;
        INFO_CLIENT result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "InfoClient");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("etciv_matricule").object().key("Value").value(matricule).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            int contentLength = (int) responseEntity.getContentLength();
            char[] buffer = new char[contentLength];
            InputStream stream = responseEntity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);

            int hasRead = 0;
            while (hasRead < contentLength)
                hasRead += reader.read(buffer, hasRead, contentLength - hasRead);

            stream.close();

            JSONObject r = null;
            if (buffer.length > 0)
                r = new JSONObject(new String(buffer));

            if (r != null) {
                result = ExtractInfoClient(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static INFO_TRANSFERT InfoTransfer(Context ctx, int code_transf) {
        Safe.Error = false;
        INFO_TRANSFERT result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "InfoTransfer");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("code_transf").object().key("Value").value(code_transf).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            int contentLength = (int) responseEntity.getContentLength();
            char[] buffer = new char[contentLength];
            InputStream stream = responseEntity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);

            int hasRead = 0;
            while (hasRead < contentLength)
                hasRead += reader.read(buffer, hasRead, contentLength - hasRead);
//			String buffer = EntityUtils.toString(responseEntity);
            stream.close();

            JSONObject r = null;
            if (buffer.length > 0)
                r = new JSONObject(new String(buffer));

            if (r != null) {
                result = ExtractInfoTransfert(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }
        return result;
    }


    public static OpResult ValiderOperation(Context ctx, String num_compte, int montant, String nom_deposant, String adresse,
                                            String date_traitement, String device, String reference, String devise, String libelle_cpte, int vTypeCaisse, String vEcran,
                                            String user_code, String nature_id_code) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "OperationsEspeces");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("num_compte").object().key("Value").value(num_compte).endObject()
                    .key("montant").object().key("Value").value(montant).endObject()
                    .key("nom_deposant").object().key("Value").value(nom_deposant).endObject()
                    .key("adresse").object().key("Value").value(adresse).endObject()
                    .key("date_traitement").object().key("Value").value(date_traitement).endObject()
                    .key("device").object().key("Value").value(device).endObject()
                    .key("reference").object().key("Value").value(reference).endObject()
                    .key("devise").object().key("Value").value(devise).endObject()
                    .key("libelle_cpte").object().key("Value").value(libelle_cpte).endObject()
                    .key("vTypeCaisse").object().key("Value").value(vTypeCaisse).endObject()
                    .key("vEcran").object().key("Value").value(vEcran).endObject()
                    .key("user_code").object().key("Value").value(user_code).endObject()
                    .key("nature_id_code").object().key("Value").value(nature_id_code).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static OpResult ValiderDemande(Context ctx, String num_depot, String etciv_matricule, String agence_code, int montant, String type_engag, String obj_dem,
                                          String localite, String sect_act, String periodic, int duree, String date_visite, String comments, String user_code) {

        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "DemandeCredit");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("num_depot").object().key("Value").value(num_depot).endObject()
                    .key("etciv_matricule").object().key("Value").value(etciv_matricule).endObject()
                    .key("agence_code").object().key("Value").value(agence_code).endObject()
                    .key("montant").object().key("Value").value(montant).endObject()
                    .key("type_engag").object().key("Value").value(type_engag).endObject()
                    .key("obj_dem").object().key("Value").value(obj_dem).endObject()
                    .key("localite").object().key("Value").value(localite).endObject()
                    .key("sect_act").object().key("Value").value(sect_act).endObject()
                    .key("periodic").object().key("Value").value(periodic).endObject()
                    .key("duree").object().key("Value").value(duree).endObject()
                    .key("date_visite").object().key("Value").value(date_visite).endObject()
                    .key("comments").object().key("Value").value(comments).endObject()
                    .key("user_code").object().key("Value").value(user_code).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static OpResult ValiderTransfer(Context ctx, String operateur, int code_transf, String nom_recep, String prenom_recep, int montant, String devise, String pays, String ville, String nom_env,
                                           String prenom_env, String adress, String tel, String bp, String profess, String motif, String question, String reponse, String user_code) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "MoneyTransfer");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("operateur").object().key("Value").value(operateur).endObject()
                    .key("code_transf").object().key("Value").value(code_transf).endObject()
                    .key("nom_recep").object().key("Value").value(nom_recep).endObject()
                    .key("prenom_recep").object().key("Value").value(prenom_recep).endObject()
                    .key("montant").object().key("Value").value(montant).endObject()
                    .key("devise").object().key("Value").value(devise).endObject()
                    .key("pays").object().key("Value").value(pays).endObject()
                    .key("ville").object().key("Value").value(ville).endObject()
                    .key("nom_env").object().key("Value").value(nom_env).endObject()
                    .key("prenom_env").object().key("Value").value(prenom_env).endObject()
                    .key("adress").object().key("Value").value(adress).endObject()
                    .key("tel").object().key("Value").value(tel).endObject()
                    .key("bp").object().key("Value").value(bp).endObject()
                    .key("profess").object().key("Value").value(profess).endObject()
                    .key("motif").object().key("Value").value(motif).endObject()
                    .key("question").object().key("Value").value(question).endObject()
                    .key("reponse").object().key("Value").value(reponse).endObject()
                    .key("user_code").object().key("Value").value(user_code).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static OpResult ValiderTransferReceive(Context ctx, String operateur, String mtcn, String nom_dest, String prenom_dest, int montant, String nom_exp,
                                                  String prenom_exp, String adress_exp, String tel_exp, String bp_exp, String profess_exp, String motif_envoi, String question, String reponse, String user_code) {
        String jsonString = "";
        InputStream is;

        Safe.Error = false;
        OpResult result = null;
        try {
            HttpPost request = new HttpPost(BaseURI() + "TransferReceive");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("operateur").object().key("Value").value(operateur).endObject()
                    .key("mtcn").object().key("Value").value(mtcn).endObject()
                    .key("nom_dest").object().key("Value").value(nom_dest).endObject()
                    .key("prenom_dest").object().key("Value").value(prenom_dest).endObject()
                    .key("montant").object().key("Value").value(montant).endObject()
                    .key("nom_exp").object().key("Value").value(nom_exp).endObject()
                    .key("prenom_exp").object().key("Value").value(prenom_exp).endObject()
                    .key("adress_exp").object().key("Value").value(adress_exp).endObject()
                    .key("tel_exp").object().key("Value").value(tel_exp).endObject()
                    .key("bp_exp").object().key("Value").value(bp_exp).endObject()
                    .key("profess_exp").object().key("Value").value(profess_exp).endObject()
                    .key("motif_envoi").object().key("Value").value(motif_envoi).endObject()
                    .key("question").object().key("Value").value(question).endObject()
                    .key("reponse").object().key("Value").value(reponse).endObject()
                    .key("user_code").object().key("Value").value(user_code).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONObject r = new JSONObject(jsonString);
            if (r != null) {
                result = ExtractOpResult(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Safe.Error = true;
        }

        return result;
    }

    public static String ReferenceOperation(Context ctx, String user_code) {
        String jsonString = "";
        InputStream is;

        String result = "";
        try {
            HttpPost request = new HttpPost(BaseURI() + "ReferenceOperation");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("user_code").object().key("Value").value(user_code)
                    .endObject().endObject();

            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            result = jsonString.substring(1, jsonString.length() - 2);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject, "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static String NumeroDepot(Context ctx) {
        String jsonString = "";
        InputStream is;

        String result = "";
        try {
            HttpPost request = new HttpPost(BaseURI() + "NumeroDepot");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .endObject();

            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            result = jsonString.substring(1, jsonString.length() - 2);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject, "Une erreur est survenue. Veuillez re-essayer plus tard.",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<INFO_CLIENT> ListeClient(Context ctx, String nom) {
        String jsonString = "";
        InputStream is;

        ArrayList<INFO_CLIENT> result = new ArrayList<INFO_CLIENT>();
        try {
            HttpPost request = new HttpPost(BaseURI() + "ListeClient");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer json = new JSONStringer().object()
                    .key("APIkey").object().key("Value").value(Safe.APIkey).endObject()
                    .key("nom_client").object().key("Value").value(nom).endObject()
                    .endObject();
            StringEntity entity = new StringEntity(json.toString());

            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            is = responseEntity.getContent();

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonString = sb.toString();
            } catch (Exception e) {
            }

            JSONArray r = new JSONArray(jsonString);
            if (r != null) {
                for (int i = 0; i < r.length(); i++) {
                    INFO_CLIENT o;
                    JSONObject j = r.getJSONObject(i);
                    o = ExtractInfoClient(j);
                    result.add(o);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Safe.MasterObject, "Une erreur est survenue. Veuillez re-essayer plus tard.", Toast.LENGTH_LONG).show();
        }
        return result;
    }


    // private static byte[] getByte(String main, String doigt,Empreinte emp){
    // if(emp.getTypeMain().equalsIgnoreCase(main)){
    // if(emp.getTypeDoigt().equalsIgnoreCase(doigt)){
    // return emp.getImageFinger();
    // }
    // }
    // return new byte[]{0};
    // }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // private static String getByteString(String main, String
    // doigt,List<Empreinte> list){
    // for(Empreinte emp:list){
    // if(emp.getTypeMain().equalsIgnoreCase(main)){
    // if(emp.getTypeDoigt().equalsIgnoreCase(doigt)){
    // return bytesToHex(emp.getImageFinger());
    // }
    // }
    // }
    // return "";
    // }
}
