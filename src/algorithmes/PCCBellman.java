package algorithmes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exceptions.CircuitAbsorbantEx;
import exceptions.NoPathEx;
import graphes.IGraph;

public class PCCBellman {
	private PCCBellman() {
		throw new IllegalStateException("Classe utilitaire");
	}

	// Penser � utiliser fonction recursive pour la suppression de predecesseur
	// Penser � v�rifier boucle foreach avec listePredecesseur
	
	/**
	 * @brief
	 * @param g Graphe
	 * @param listePredecesseurs HashMap des pr�decesseurs de chaque noeud
	 * @param listeNoeudsTries Liste vide, contiendra la liste des noeuds tri�s
	 * @param triParNiveau 
	 * @param nbNoeudTriee Nombre de noeuds tri�s
	 * @return
	 */
	private static int nettoyagePredecesseurs(IGraph g, Map<String, List<String>> listePredecesseurs, List<String> listeNoeudsTries, List<String> triParNiveau, int nbNoeudTriee) {
		for (String i : g) {
			if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).isEmpty()) {
				listeNoeudsTries.add(i);
				listePredecesseurs.remove(i);
				nbNoeudTriee++;
				triParNiveau.add(i);
			}
		}
		return nbNoeudTriee;
	}
	
	
	/**
	 * @brief On supprime les noeuds de la liste de la liste de
	 * pr�desesseurs de tous les autres noeuds
	 * @param g Graphe
	 * @param listePredecesseurs HashMap des pr�decesseurs de chaque noeud
	 * @param listeNoeudsTries Liste des noeuds venant d'�tre tri�s
	 */
	private static void suppressionPredecesseurs(IGraph g, Map<String, List<String>> listePredecesseurs, List<String> listeNoeudsTries) {
		for (String i : g) {
			for (String j : listeNoeudsTries) {
				/* Si un noeud venant d'�tre tri� est encore pr�sent en tant que pr�d�cesseur,
				 * alors le supprimer de la liste des pr�decesseurs */
				if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).contains(j)) {
					listePredecesseurs.get(i).remove(j);
				}
			}
		}
	}
	

	public static boolean estOK(IGraph g) {
		// Liste des noeuds tri�es par niveau
		List<String> triParNiveau = new ArrayList<>();
		
		// Liste de pr�decesseur de chaque noeud
		Map<String, List<String>> listePredecesseurs = listePredecesseurs(g);
		
		int nbNoeudTriee = 0;	// Nombre de noeuds tri�e
				
		// Tant qu'il existe des noeuds non tri�s
		while(nbNoeudTriee < g.getNbNoeuds()) { 
			// Liste temporaire de noeuds venant d'�tre attribu�
			ArrayList<String> listeNoeudsTries = new ArrayList<>();
			
			// Detection des noeuds de niveau sup�rieur
			int tmp = nettoyagePredecesseurs(g, listePredecesseurs, listeNoeudsTries, triParNiveau, nbNoeudTriee);
			
			// Si aucun noeud n'a �t� tri�, c'est la preuve d'un circuit
			if (tmp - nbNoeudTriee == 0)
				return false;
			
			nbNoeudTriee = tmp;// Actualisation du nombre de noeuds
			
			// Suppression de la liste des pr�decesseurs des noeuds venant d'�tre tri�s
			suppressionPredecesseurs(g, listePredecesseurs, listeNoeudsTries);
		}
		// Si l'algorithme est parvenu jusque l�, c'est la preuve de l'abscence de circuit.
		return true;
	}
	
	
	private static Map<String, List<String>> listePredecesseurs(IGraph g){
		/*
		 * Tableau de nbNoeuds dont chaque case comporte la liste de
		 * pr�decesseur du noeud correspodnant
		 */
		Map<String, List<String>> predecesseurs = new HashMap<>();
		
		// Insersion des pr�d�cesseurs
		for (String noeudSucc: g) {
			predecesseurs.put(noeudSucc, new ArrayList<>());
			for (String noeudPrec : g) {
				if (g.aArc(noeudPrec, noeudSucc))
					predecesseurs.get(noeudSucc).add(noeudPrec);
			}
		}
		
		return predecesseurs;
	}
	
	
	private static int planB(IGraph g, Map<String, List<String>> listePredecesseurs, List<String> triParNiveau, List<String> listeNoeudsTries, String noeudD, String noeudA, int nbNoeudTriee) throws NoPathEx{
		for (String i : g) {
			if (listePredecesseurs.containsKey(i)) {
				for (String j : triParNiveau) {
					if (listePredecesseurs.get(i).contains(j) && !j.equals(noeudD)) {
						listePredecesseurs.get(i).remove(j);
					}
				}
				
				if (listePredecesseurs.get(i).isEmpty()) {
					if (noeudA.equals(i)) { throw new NoPathEx(); }
					
					listeNoeudsTries.add(i);
					listePredecesseurs.remove(i);
					nbNoeudTriee++;
				}
			}
		}
		if (triParNiveau.contains(noeudA)) { throw new NoPathEx(); }
		triParNiveau.clear();
		triParNiveau.add(noeudD);
		System.out.println("zidh");
		return nbNoeudTriee;
	}
	
	
	/**
	 * 
	 * @param g
	 * @param distances
	 * @param triParNiveau
	 * @param noeudD
	 * @throws NoPathEx
	 */
	private static void triParNiveau(IGraph g, Map<String, Integer> distances, List<String> triParNiveau, String noeudD, String noeudA) throws NoPathEx{

		// Contient la liste de pr�decesseur de chaque noeud
		Map<String, List<String>> listePredecesseurs = listePredecesseurs(g);
		
		int nbNoeudTriee = 0;	// Nombre de noeuds tri�e
		
		distances.put(noeudD, 0);
		
		// On supprime les pr�d�cesseurs qui sont d�j� tri�s
		while(nbNoeudTriee < g.getNbNoeuds()) { 
			ArrayList<String> listeNoeudsTries = new ArrayList<>();
			
			nbNoeudTriee = nettoyagePredecesseurs(g, listePredecesseurs, listeNoeudsTries, triParNiveau, nbNoeudTriee);
			
			System.out.println(listePredecesseurs + " " + listeNoeudsTries + " " + triParNiveau);
			
			if (listeNoeudsTries.contains(noeudD)) {
				nbNoeudTriee = planB(g, listePredecesseurs, triParNiveau, listeNoeudsTries, noeudD, noeudA, nbNoeudTriee);
			}
			
			suppressionPredecesseurs(g, listePredecesseurs, listeNoeudsTries);
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
		if (!estOK(g)) { throw new CircuitAbsorbantEx(); }
		
		Map<String, Integer> distances = new HashMap<>();
		Map<String, String> predecesseurs = new HashMap<>();
		List<String> triParNiveau = new ArrayList<>();
		
		triParNiveau(g, distances, triParNiveau, noeudD, noeudA);
		for (String noeudS : triParNiveau) {
			int idx = 0;
			for (String noeudP = triParNiveau.get(idx); !noeudP.equals(noeudS); noeudP = triParNiveau.get(++idx)) {
				if (peutRemplacerDistanceActuelle(g, distances, noeudS, noeudP)) {
					distances.put(noeudS, g.getValeur(noeudP, noeudS) + distances.get(noeudP));
					predecesseurs.put(noeudS, noeudP);
				}
			}
		}
		
		return affichage(g, predecesseurs, noeudD, noeudA);
	}


	private static String affichage(IGraph g, Map<String, String> predecesseurs, String idxNoeudDepart, String idxNoeudArrivee) {
		StringBuilder sb = new StringBuilder();
		
		String i = idxNoeudArrivee;
		
		while(i != null){
			if (!i.equals(idxNoeudArrivee))
				sb.insert(0,  " - ");
			sb.insert(0, i);
			i = predecesseurs.get(i);
		}
		
		return sb.toString();
	}
}
