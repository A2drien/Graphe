package algorithmes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exceptions.ArcN�gatifEx;
import exceptions.NoPathEx;
import graphes.IGraph;

public class PCCDijkstra {
	// Nombre indiquant l'absence de calcul de la distance pour le noeud concern�
	private static final int NON_CALCULE = -100;
	
	
	private PCCDijkstra() {
		throw new IllegalStateException("Classe utilitaire");
	}
	
	
	/**
	 * @brief Indique si l'algorithme de Dijkstra peut ici �tre appliqu�
	 * @param g Le graphe
	 * @return true si applicable, false sinon
	 */
	public static boolean estOK(IGraph g) {
		for (String i : g.noeuds()) {
			for (String j : g.noeuds()) {
				if (g.aArc(i, j) && g.getValeur(i, j) < 0)
					return false;
			}
		}
		return true;
	}
	
	
	/**
	 * @brief V�rifie si toutes les conditions suivantes sont r�unies :
	 *  	- Le noeud successeur n'a pas �t� mis de c�t� par l'algorithme (son
	 * 		  chemin n'est donc pas certain d'�tre optimal)
	 * 		- Il existe un arc du noeud pr�d�cesseur au noeud successeur
	 *  	- L'une des conditions suivantes doit �tre remplie :
	 * 			- Aucun chemin entre le noeud de d�part et le noeud successeur
	 * 			  n'a encore �t� calcul�/trouv�
	 * 			- Le chemin (en passant par le noeud pr�d�cesseur) est plus
	 * 			  optimis� que le chemin actuel
	 *
	 * @param g Graphe
	 * @param distances HashMap de la longueur du chemin actuel entre le noeud
	 * 					de d�part et le noeud en cl�
	 * @param noeudP Noeud pr�d�cesseur � noeudS
	 * @param noeudS Noeud successeur � noeudP
	 * @return true si les conditions sont r�unies, false sinon
	 */
	private static boolean peutRemplacerLaDistanceActuelle(IGraph g, 
			Map<String, Integer> distances, String noeudP, String noeudS) {
		return 	distances.containsKey(noeudS) && g.aArc(noeudP, noeudS) &&
				(distances.get(noeudS) == NON_CALCULE || distances.get(noeudS) >
					g.getValeur(noeudP, noeudS) + distances.get(noeudP));
	}
	
	
	/**
	 * @brief V�rifie si toutes les conditions suivantes sont r�unies :
	 *  	- Le noeud potentiellement successeur ne doit pas avoir �t� mis de
	 * 		  c�t� par l'algorithme (on ne refait pas 2 fois les calculs)
	 *  	- La distance doit avoir �t� d�j� calcul�e (il existe bien un chemin
	 * 		  entre le noeud de d�part et le noeud potentiellement successeur)
	 *  	- Le noeud potentiellement successeur ne peut �tre identique au
	 * 		  noeud "actuel"
	 *  	- L'une des conditions suivantes doit �tre remplie :
	 *  		- Aucun noeud n'a encore �t� choisi comme noeud successeur au
	 * 			  noeud "actuel"
	 *  		- Le noeud actuellement test� a une longueur de chemin
	 * 			  inf�rieure au noeud successeur (et est donc plus int�ressant)
	 *
	 * @param distances HashMap de la longueur du chemin actuel entre le noeud
	 * 					de d�part et le noeud en cl�
	 * @param noeudP Noeud pr�d�cesseur � noeudSucc
	 * @param noeudSucc Noeud successeur � noeudP
	 * @param noeudSuiv Noeud actuellement choisi pour �tre le suivant
	 * @return true si les conditions sont r�unies, false sinon
	 */
	private static boolean peutEtreLeProchainNoeud(Map<String, Integer> distances,
							String noeudP, String noeudSucc, String noeudSuiv) {
		return distances.containsKey(noeudSucc) &&
				distances.get(noeudSucc) != NON_CALCULE &&
				!noeudP.equals(noeudSucc) && (noeudSuiv == null || 
				distances.get(noeudSuiv) > distances.get(noeudSucc));
	}
	
	
	/**
	 * @brief Choisi le noeud sur lequel sera calcul� les nouvelles longueur des
	 * 		  chemin des autres noeuds
	 * @param g Le graphe
	 * @param distances HashMap de la longueur du chemin actuel entre le noeud
	 * 					de d�part et le noeud en cl�
	 * @param predecesseurs HashMap des pr�d�cesseurs des noeuds en cl�
	 * @param noeudP Le noeud pr�c�dant (le noeud de d�part si c'est la premi�re
	 * 				 boucle, le noeudSuivant pr�c�dant sinon)
	 * @return noeudSuivant, le noeud suivant
	 */
	private static String choixNoeudSuivant(IGraph g, Map<String, Integer> distances,
							Map<String, String> predecesseurs, String noeudP){
		// Impossible de prendre un noeud d�j� termin�
		assert(distances.containsKey(noeudP));
		
		String noeudSuivant = null; // Aucun noeud suivant n'est choisi par d�faut
		
		for (String noeudS : g.noeuds()) {
			if (peutRemplacerLaDistanceActuelle(g, distances, noeudP, noeudS)) {
				distances.put(noeudS, g.getValeur(noeudP, noeudS) + distances.get(noeudP));
				predecesseurs.put(noeudS, noeudP);
			}
			
			if (peutEtreLeProchainNoeud(distances, noeudP, noeudS, noeudSuivant)){
				noeudSuivant = noeudS;
			}
		}
		
		distances.remove(noeudP);
			
		return noeudSuivant;
	}
	
	
	/**
	 * @param predecesseurs HashMap des pr�d�cesseurs des noeuds en cl�
	 * @param noeudA Noeud d'arriv�e
	 * @return Le chemin du noeud de d�part au noeud d'arriv�e
	 */
	private static String affichage(Map<String, String> predecesseurs,
							String noeudD, String noeudA) {
		StringBuilder sb = new StringBuilder();
		
		String i = noeudA;
		
		while(true){
			if (!i.equals(noeudA))
				sb.insert(0,  " - ");
			sb.insert(0, i);
			if (!i.equals(noeudD))
				i = predecesseurs.get(i);
			else
				break;
		}
		
		return sb.toString();
	}
	
	
	/**
	 * @brief Initialise la HashMap de longueur de chaque noeud
	 * @param g Graphe
	 * @param noeudD Noeud de d�part
	 * @return HashMap initialis�e de la longueur du chemin actuel entre le noeud
	 * 		   de d�part en ind�termin�e (sauf le noeud de d�part, fix�e � 0) et
	 * 		   le noeud en cl�
	 */
	private static Map<String, Integer> initialisationDistances(IGraph g,
											String noeudD){
		Map<String, Integer> distances = new HashMap<>();
		
		// Rempli le tableau de distance � "l'infini"
		for (String i : g.noeuds()) {
			if (!i.equals(noeudD))
				distances.put(i, NON_CALCULE);
		}
		
		/* La longueur du chemin pour le noeud de d�part est mis � 0
		 * pour faciliter l'impl�mentation de l'algorithme
		 */
		distances.put(noeudD, 0);
		
		return distances;
	}
	
	
	/**
	 * @brief Algorithme de Dijkstra
	 * @param g Graphe contenant les noeuds
	 * @param noeudD Noeud de d�part
	 * @param noeudA Noeud d'arriv�e
	 * @return Le chemin le plus court entre noeudD et noeudA
	 * @throws ArcN�gatifEx, pr�sence d'au moins un arc n�gatif dans le graphe, impossible de calculer n'importe quel chemin
	 * @throws NoPathEx, aucun chemin entre le noeud de d�part et le noeud d'arriv� trouv�
	 */
	public static String algorithmeDijkstra(IGraph g, String noeudD, String noeudA)
											throws ArcN�gatifEx, NoPathEx {
		if (!estOK(g)) { throw new ArcN�gatifEx(); }
		
		// HashMap des longueurs de chemins pour chaque noeud
		Map<String, Integer> distances = initialisationDistances(g, noeudD);
		
		// HashMap de pr�d�cecesseur de chaque noeud
		Map<String, String> predecesseurs = new HashMap<>();
		
		// Noeud sur lequel sera calcul� les longueur des chemins
		String noeudActuel = noeudD;
		
		/* Tant que le noeud d'arriv�e n'a pas la certitude d'avoir eu le chemin
		   le plus court, poursuivre l'algorithme */
		while(distances.containsKey(noeudA)) {
			/* Si aucun noeud n'a �t� choisi comme prochain noeud de calcul
			 * et que le noeud d'arriv�e n'a pas encore trouv� de chemin
			 * certifi� optimis�, alors il n'y a pas de chemins atteignable
			 * pour le noeud d'arriv� */
			if (noeudActuel == null) { throw new NoPathEx(); }
			
			// On actualise en permanence le noeud "actuel"
			noeudActuel = choixNoeudSuivant(g, distances, predecesseurs, noeudActuel);
		}
		
		return affichage(predecesseurs, noeudD, noeudA);
	}
}
