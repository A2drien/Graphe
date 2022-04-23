package algorithmes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.CircuitAbsorbantEx;
import exceptions.NoPathEx;
import graphes.IGraph;

public class PCCBellman {
	private PCCBellman() {
		throw new IllegalStateException("Classe utilitaire");
	}
	
	/**
	 * @brief
	 * @param g Graphe
	 * @param listePredecesseurs HashMap des pr�decesseurs de chaque noeud
	 * @param listeNoeudsTriesTemporaire Liste vide, contiendra la liste des noeuds tri�s
	 * @param listeNoeudsParNiveau 
	 * @param nbNoeudTriee Nombre de noeuds tri�s
	 * @return
	 */
	private static void nettoyagePredecesseurs(IGraph g, Map<String, List<String>> listePredecesseurs, List<String> listeNoeudsTriesTemporaire, List<String> listeNoeudsParNiveau) {
		for (String i : g) {
			if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).isEmpty()) {
				listeNoeudsTriesTemporaire.add(i);
				listePredecesseurs.remove(i);
				listeNoeudsParNiveau.add(i);
			}
		}
	}
	
	
	/**
	 * @brief On supprime les noeuds de la liste de la liste de
	 * pr�desesseurs de tous les autres noeuds
	 * @param g Graphe
	 * @param listePredecesseurs HashMap des pr�decesseurs de chaque noeud
	 * @param listeNoeudsTriesTemporaire Liste des noeuds venant d'�tre tri�s
	 */
	private static void suppressionPredecesseurs(IGraph g, Map<String, List<String>> listePredecesseurs, List<String> listeNoeudsTriesTemporaire) {
		for (String i : g) {
			for (String j : listeNoeudsTriesTemporaire) {
				/* Si un noeud venant d'�tre tri� est encore pr�sent en tant que pr�d�cesseur,
				 * alors le supprimer de la liste des pr�decesseurs */
				if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).contains(j))
					listePredecesseurs.get(i).remove(j);
			}
		}
	}
	

	public static boolean estOK(IGraph g) {
		List<String> listeNoeudsParNiveau = new ArrayList<>();// Liste des noeuds tri�es par niveau
		Map<String, List<String>> listePredecesseurs = listePredecesseurs(g);// Liste de pr�decesseur de chaque noeud
				
		// Tant qu'il existe des noeuds non tri�s
		while(listeNoeudsParNiveau.size() < g.getNbNoeuds()) { 
			ArrayList<String> listeNoeudsTriesTemporaire = new ArrayList<>();// Liste temporaire de noeuds venant d'�tre attribu�
			
			int nbNoeudTriee = listeNoeudsParNiveau.size();
			
			// Detection des noeuds de niveau sup�rieur
			nettoyagePredecesseurs(g, listePredecesseurs, listeNoeudsTriesTemporaire, listeNoeudsParNiveau);
			
			// Si aucun noeud n'a �t� tri�, c'est la preuve d'un circuit
			if (listeNoeudsParNiveau.size() == nbNoeudTriee)
				return false;
			
			// Suppression de la liste des pr�decesseurs des noeuds venant d'�tre tri�s
			suppressionPredecesseurs(g, listePredecesseurs, listeNoeudsTriesTemporaire);
		}
		// Si l'algorithme est parvenu jusque l�, c'est la preuve de l'abscence de circuit.
		return true;
	}
	
	
	private static Map<String, List<String>> listePredecesseurs(IGraph g){
		Map<String, List<String>> predecesseurs = new HashMap<>();
		// Insersion des pr�d�cesseurs
		for (String noeudSucc: g) {
			predecesseurs.put(noeudSucc, new ArrayList<>());
			for (String noeudPrec : g)
				if (g.aArc(noeudPrec, noeudSucc))
					predecesseurs.get(noeudSucc).add(noeudPrec);
		}
		return predecesseurs;
	}
	
	
	/**
	 * 
	 * @param g
	 * @param distances
	 * @param listeNoeudsParNiveau
	 * @param noeudD
	 * @throws NoPathEx
	 */
	private static void triParNiveau(IGraph g, Map<String, Integer> distances, List<String> listeNoeudsParNiveau, String noeudD, String noeudA) throws NoPathEx{
		// Contient la liste de pr�decesseur de chaque noeud
		Map<String, List<String>> listePredecesseurs = listePredecesseurs(g);
		distances.put(noeudD, 0);
		
		listePredecesseurs.remove(noeudD);
		
		// Tant qu'il reste des ArrayList vides : 
		while(listePredecesseurs.containsValue(new ArrayList<>())) {
			for (String i : g) {
				if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).isEmpty()) {
					if (i.equals(noeudA))
						throw new NoPathEx();
					suppressionRecursive(g, listePredecesseurs, i, noeudA);
				}
			}
		}
		
		listePredecesseurs.put(noeudD, new ArrayList<>());
		
		int nbNoeudTrieeMax = listePredecesseurs.size();
		
		// Tant qu'il existe des noeuds non tri�s
		while(listeNoeudsParNiveau.size() < nbNoeudTrieeMax) { 
			ArrayList<String> listeNoeudsTriesTemporaire = new ArrayList<>(); // Liste temporaire de noeuds venant d'�tre attribu�
			
			// Detection des noeuds de niveau sup�rieur
			nettoyagePredecesseurs(g, listePredecesseurs, listeNoeudsTriesTemporaire, listeNoeudsParNiveau);
			
			// Suppression de la liste des pr�decesseurs des noeuds venant d'�tre tri�s
			suppressionPredecesseurs(g, listePredecesseurs, listeNoeudsTriesTemporaire);
		}
	}
	
	
	private static void suppressionRecursive(IGraph g, Map<String, List<String>> listePredecesseurs, String noeudASupprimer, String noeudA) {
		assert(listePredecesseurs.get(noeudASupprimer).isEmpty());
		
		listePredecesseurs.remove(noeudASupprimer);
		
		// Parcourir la liste de pr�decesseurs de noeuds.
		for (String i : g) {
			// Si le noeud � supprimer existe en tant que pr�decesseur, le supprimer
			if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).contains(noeudASupprimer))
				listePredecesseurs.get(i).remove(noeudASupprimer);
			
			if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).contains(noeudASupprimer)) {
				if (i.equals(noeudA))
					throw new NoPathEx();
				listePredecesseurs.remove(i); // Supprimer le noeud de la HashMap
				suppressionRecursive(g, listePredecesseurs, i, noeudA);
			}
		}
	}
	

	/*
	 * 	V�rifie si toutes les conditions suivantes sont r�unies :
	 * 		- Il existe un arc entre le noeud "actuel" et le noeud actuellement test�
	 *  	- L'une des conditions suivantes doit �tre remplie :
	 * 			- Aucun chemin avec le noeud successeur n'a encore �t� calcul�
	 * 			- Le chemin test� est plus optimis� que le chemin actuel
	 */
	private static boolean peutRemplacerDistanceActuelle(IGraph g, Map<String, Integer> distances, String noeudS, String noeudP) {
		return g.aArc(noeudP, noeudS) && (!distances.containsKey(noeudS) || 
				distances.get(noeudS) > g.getValeur(noeudP, noeudS) + 
				distances.get(noeudP));
	}
	
	
	public static String algorithmeBellman(IGraph g, String noeudD, String noeudA) throws CircuitAbsorbantEx, NoPathEx {
		if (!estOK(g))
			throw new CircuitAbsorbantEx();
		
		Map<String, Integer> distances = new HashMap<>();
		Map<String, String> predecesseurs = new HashMap<>();
		List<String> listeNoeudsParNiveau = new ArrayList<>();
		
		triParNiveau(g, distances, listeNoeudsParNiveau, noeudD, noeudA);
		
		for (String noeudS : listeNoeudsParNiveau) {
			int idx = 0;
			for (String noeudP = listeNoeudsParNiveau.get(idx); !noeudP.equals(noeudS); noeudP = listeNoeudsParNiveau.get(++idx)) {
				if (peutRemplacerDistanceActuelle(g, distances, noeudS, noeudP)) {
					distances.put(noeudS, g.getValeur(noeudP, noeudS) + distances.get(noeudP));
					predecesseurs.put(noeudS, noeudP);
				}
			}
		}
		return affichage(predecesseurs, noeudA);
	}


	private static String affichage(Map<String, String> predecesseurs, String noeudA) {
		StringBuilder sb = new StringBuilder();
		String noeud = noeudA;
		
		while(noeud != null){
			if (!noeud.equals(noeudA))
				sb.insert(0,  " - ");
			sb.insert(0, noeud);
			noeud = predecesseurs.get(noeud);
		}
		return sb.toString();
	}
}
