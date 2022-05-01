package algorithmes;

import java.util.HashMap;
import java.util.Map;

import exceptions.ArcN�gatifEx;
import exceptions.NoPathEx;
import graphes.IGraphe;

public class PCCDijkstra implements PCC {
	// Nombre indiquant l'absence de calcul de la distance pour le noeud concern�
	private static final int NON_CALCULE = -100;
	
	/**
	 * @brief Indique si l'algorithme de Dijkstra peut ici �tre appliqu�
	 * @param g Le graphe
	 * @return true si applicable, false sinon
	 */
	@Override
	public boolean estOK(IGraphe g) {
		for (Integer i : g)
			for (Integer j : g)
				if (g.aArc(i, j) && g.getValuation(i, j) < 0)
					return false;
		return true;
	}
	
	
	/**
	 * @brief Initialise la HashMap de longueur de chaque noeud
	 * @param g Graphe
	 * @param noeudD Noeud de d�part
	 * @return HashMap initialis�e de la longueur du chemin actuel entre le noeud
	 * 		   de d�part en ind�termin�e (sauf le noeud de d�part, fix�e � 0) et
	 * 		   le noeud en cl�
	 */
	private static Map<Integer, Integer> initialisationDistances(IGraphe g,
											Integer noeudD){
		HashMap<Integer, Integer> distances = new HashMap<>();
		
		// Rempli le tableau de distance � "l'infini"
		for (Integer i : g)
			if (i != noeudD)
				distances.put(i, NON_CALCULE);
		
		/* La longueur du chemin pour le noeud de d�part est mis � 0
		 * pour faciliter l'impl�mentation de l'algorithme
		 */
		distances.put(noeudD, 0);
		
		return distances;
	}
	
	
	/**
	 * @brief V�rifie si toutes les conditions suivantes sont r�unies :
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
	private static boolean peutRemplacerDistanceActuelle(IGraphe g, 
			Map<Integer, Integer> distances, Integer noeudP, Integer noeudS) {
		return 	g.aArc(noeudP, noeudS) && (distances.get(noeudS) ==
				NON_CALCULE || distances.get(noeudS) >
				g.getValuation(noeudP, noeudS) + distances.get(noeudP));
	}
	
	
	/**
	 * @brief V�rifie si toutes les conditions suivantes sont r�unies :
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
	private static boolean peutEtreLeProchainNoeud(Map<Integer, Integer> distances,
							Integer noeudP, Integer noeudSucc, Integer noeudSuiv) {
		return distances.get(noeudSucc) != NON_CALCULE &&
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
	 * @param noeudA : ???
	 * @return noeudSuivant, le noeud suivant
	 */
	private static Integer choixNoeudSuivant(IGraphe g, Map<Integer, Integer> distances,
							Map<Integer, Integer> predecesseurs, Integer noeudP, Integer noeudA){
		// Impossible de prendre un noeud d�j� termin�
		assert(distances.containsKey(noeudP));
		
		Integer noeudSuivant = null; // Aucun noeud suivant n'est choisi par d�faut
		
		// On parcourt tous les noeuds qui n'ont pas �t� mis de c�t�
		for (Integer noeudS : distances.keySet()) {
			if (peutRemplacerDistanceActuelle(g, distances, noeudP, noeudS)) {
				distances.put(noeudS, g.getValuation(noeudP, noeudS) + distances.get(noeudP));
				predecesseurs.put(noeudS, noeudP);
			}
			
			if (peutEtreLeProchainNoeud(distances, noeudP, noeudS, noeudSuivant)){
				noeudSuivant = noeudS;
			}
		}
		
		// Mit de c�t�, il n'est plus utile
		if (!noeudP.equals(noeudA))
			distances.remove(noeudP);
			
		return noeudSuivant;
	}
	
	
	/**
	 * @param predecesseurs HashMap des pr�d�cesseurs des noeuds en cl�
	 * @param noeudA Noeud d'arriv�e
	 * @return Le chemin du noeud de d�part au noeud d'arriv�e
	 */
	private static String affichage(Map<Integer, Integer> predecesseurs,
							Integer noeudD, Integer noeudA, Integer distance) {
		StringBuilder sb = new StringBuilder();
		
		Integer i = noeudA;
		
		while(true){
			sb.insert(0,  " ");
			sb.insert(0, i);
			if (!i.equals(noeudD))
				i = predecesseurs.get(i);
			else
				break;
		}
		return "Dijkstra" + System.lineSeparator()
		+ distance + System.lineSeparator() + sb.toString();
	}
	
	
	/**
	 * @brief Algorithme de Dijkstra
	 * @param g Graphe contenant les noeuds
	 * @param noeudD Noeud de d�part
	 * @param noeudA Noeud d'arriv�e
	 * @return Le chemin le plus court entre noeudD et noeudA
	 * @throws ArcN�gatifEx S'il existe au moins un arc n�gatif dans le graphe
	 * @throws NoPathEx Si aucun chemin entre le noeud de d�part et le noeud
	 * 					d'arriv� n'est trouv�
	 */
	@Override
	public String algorithme(IGraphe g, Integer noeudD, Integer noeudA)
											throws ArcN�gatifEx, NoPathEx {
		if (!estOK(g)) { throw new ArcN�gatifEx(); }
		
		// HashMap des longueurs de chemins pour chaque noeud
		Map<Integer, Integer> distances = initialisationDistances(g, noeudD);
		
		// HashMap de pr�d�cecesseur de chaque noeud
		Map<Integer, Integer> predecesseurs = new HashMap<>();
		
		// Noeud sur lequel sera calcul� les longueur des chemins
		Integer noeudActuel = noeudD;
		
		/* Tant que le noeud d'arriv�e n'a pas la certitude d'avoir eu le chemin
		   le plus court, poursuivre l'algorithme */
		while(noeudActuel != noeudA) {
			//System.out.println(distances + " " + predecesseurs);
			/* 
			 * Si aucun noeud n'a �t� choisi comme prochain noeud de calcul
			 * et que le noeud d'arriv�e n'a pas encore trouv� de chemin
			 * certifi� optimis�, alors il n'y a pas de chemins atteignable
			 * pour le noeud d'arriv�e
			 */
			if (noeudActuel == null)
				throw new NoPathEx();
			
			// On actualise en permanence le noeud "actuel"
			noeudActuel = choixNoeudSuivant(g, distances, predecesseurs, noeudActuel, noeudA);
		}
		return affichage(predecesseurs, noeudD, noeudA, distances.get(noeudA));
	}
}
