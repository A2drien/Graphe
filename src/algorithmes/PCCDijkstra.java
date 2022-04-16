package algorithmes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exceptions.ArcN�gatifEx;
import exceptions.NoPathEx;
import graphes.IGraph;

// Notes : les HashMap peuvent �tre optimis�es en n'ins�rant les �l�ments au fur et � mesure au lieu de le faire tous en m�me temps ???

public class PCCDijkstra {
	private static final int FIN = -1;			// Nombre indiquant la fin du calcul de la distance pour le noeud concern�
	private static final int NON_CALCULE = -100;// Nombre indiquant l'absence de calcul de la distance pour le noeud consern�
	
	
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
	
	/*
	 * 	V�rifie si toutes les conditions suivantes sont r�unies :
	 * 		- Le noeud actuellement test� n'a pas �t� mis de c�t� par l'algorithme (son chemin n'est donc pas certain d'�tre optimal)
	 * 		- Il existe un arc entre le noeud "actuel" et le noeud actuellement test�
	 *  	- L'une des conditions suivantes doit �tre remplie :
	 * 			- Aucun chemin entre le noeud de d�part et le noeud test� n'a encore �t� calcul�
	 * 			- Le chemin (en passant par le noeud "actuel") est plus optimis� que le chemin actuel
	 */
	private static boolean peutRemplacerLaDistanceActuelle(IGraph g, Map<String, Integer> distances, String noeudA, String noeudS) {
		return 	distances.get(noeudS) != FIN && g.aArc(noeudA, noeudS) && (distances.get(noeudS) == NON_CALCULE ||
					distances.get(noeudS) > g.getValeur(noeudA, noeudS) + distances.get(noeudA));
	}
	
	
	/*
	 * 	V�rifie si toutes les conditions suivantes sont r�unies :
	 *  	- L'une des conditions suivantes doit �tre remplie :
	 *  		- Aucun noeud n'a encore �t� choisi comme noeud succ�sseur au noeud "actuel"
	 *  		- Le noeud actuellement test� a une longueur de chemin inf�rieure au noeud succ�sseur (et est donc plus int�ressant)
	 *  	- Le noeud potentiellement successeur ne doit pas avoir �t� mis de c�t� par l'algorithme (on ne refait pas 2 fois les calculs)
	 *  	- La distance doit avoir �t� d�j� calcul�e (il existe bien un chemin entre le noeud de d�part et le noeud potentiellement successeur)
	 *  	- Le noeud potentiellement successeur ne peut �tre identique au noeud "actuel"
	 */
	private static boolean peutEtreLeProchainNoeud(Map<String, Integer> distances, Map<String, String> predecesseurs, String idxSucc, String idxNoeudSuivant, String idxNoeudActuel) {
		return (idxNoeudSuivant == null || distances.get(idxNoeudSuivant) > distances.get(idxSucc)) &&
				distances.get(idxSucc) != FIN && distances.get(idxSucc) != NON_CALCULE &&
					!idxNoeudActuel.equals(idxSucc);
	}
	
	
	private static String choixNoeudSuivant(IGraph g, Map<String, Integer> distances, Map<String, String> predecesseurs, String noeudActuel){
		// Impossible de prendre un noeud d�j� termin�
		assert(distances.get(noeudActuel) != FIN);
		
		String noeudSuivant = null;
		
		for (String noeudSucc : g.noeuds()) {
			if (peutRemplacerLaDistanceActuelle(g, distances, noeudActuel, noeudSucc)) {
				distances.put(noeudSucc, g.getValeur(noeudActuel, noeudSucc) + distances.get(noeudActuel));
				predecesseurs.put(noeudSucc, noeudActuel);
			}
			
			System.out.println("Noeud " + noeudSucc + " : " + distances.get(noeudSucc) + "[" + predecesseurs.get(noeudSucc) + "]");
		
			if (peutEtreLeProchainNoeud(distances, predecesseurs, noeudSucc, noeudSuivant, noeudActuel)){
				noeudSuivant = noeudSucc;
			}
		}
		
		distances.put(noeudActuel, FIN);
		
		System.out.println("   -> Choix : " + noeudSuivant + "\n");
			
		return noeudSuivant;
	}
	
	
	private static String affichage(Map<String, String> predecesseurs, String noeudD, String noeudA) {
		LinkedList<String> liste = new LinkedList<>();
		liste.addLast(noeudA);
		
		while(!liste.getLast().equals(noeudD)) {
			liste.addLast(predecesseurs.get(liste.getLast()));
		}
		
		
		StringBuilder sb = new StringBuilder();
		
		while(!liste.isEmpty()) {
			sb.append(liste.removeLast());
			
			if (!liste.isEmpty())
				 sb.append(" - ");
		}
		
		return sb.toString();
	}
	
	
	private static Map<String, Integer> initialisationDistances(IGraph g, String noeudD){
		Map<String, Integer> distances = new HashMap<>();
		
		// Rempli le tableau de distance � l'infini
		for (String i : g.noeuds()) {
			if (!i.equals(noeudD))
				distances.put(i, NON_CALCULE);
			else
				distances.put(noeudD, 0);				
		}
		
		return distances;
	}
	
	private static Map<String, String> initialisationPredecesseurs(IGraph g, String noeudD){
		Map<String, String> predecesseurs = new HashMap<>();
		
		// Indique le d�but du graphe : absance d�finitive de pr�d�cesseur
		for (String i : g.noeuds()) {
			if (!i.equals(noeudD))
				predecesseurs.put(i, noeudD);
		}
		
		return predecesseurs;
	}
	
	
	public static String algorithmeDijkstra(IGraph g, String noeudD, String noeudA) throws ArcN�gatifEx, NoPathEx {
		if (!estOK(g)) { throw new ArcN�gatifEx(); }
		
		Map<String, Integer> distances = initialisationDistances(g, noeudD);
		Map<String, String> predecesseurs = initialisationPredecesseurs(g, noeudD);
		
		String noeudActuel = noeudD;
		
		while(distances.get(noeudA) != FIN) {
			if (noeudActuel == null)
				throw new NoPathEx();
			
			noeudActuel = choixNoeudSuivant(g, distances, predecesseurs, noeudActuel);
		}
		
		return affichage(predecesseurs, noeudD, noeudA);
	}
}
