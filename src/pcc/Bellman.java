package pcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.CircuitAbsorbantEx;
import exceptions.NoPathEx;
import graphes.IGraphe;
import graphes.IPCC;

public class Bellman implements IPCC {
	/**
	 * @brief Suppression des noeud avec pas ou plus de predecesseurs
	 * @param g Graphe
	 * @param listePredecesseurs HashMap des pr�decesseurs de chaque noeud
	 * @param listeNoeudsTriesTemporaire Liste des noeuds venant d'�tre tri�s
	 * @param listeNoeudsParNiveau Liste des noeuds tri�s par niveau
	 */
	private static void nettoyagePredecesseurs(IGraphe g, Map<Integer, List<Integer>> listePredecesseurs, List<Integer> listeNoeudsTriesTemporaire, List<Integer> listeNoeudsParNiveau) {
		for (Integer i : g) {
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
	private static void suppressionPredecesseurs(IGraphe g, Map<Integer, List<Integer>> listePredecesseurs, List<Integer> listeNoeudsTriesTemporaire) {
		for (Integer i : g) {
			for (Integer j : listeNoeudsTriesTemporaire) {
				/* Si un noeud venant d'�tre tri� est encore pr�sent en tant que pr�d�cesseur,
				 * alors le supprimer de la liste des pr�decesseurs */
				if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).contains(j))
					listePredecesseurs.get(i).remove(j);
			}
		}
	}
	
	@Override
	public boolean estOK(IGraphe g) {
		List<Integer> listeNoeudsParNiveau = new ArrayList<>();// Liste des noeuds tri�es par niveau
		Map<Integer, List<Integer>> listePredecesseurs = listePredecesseurs(g);// Liste de pr�decesseur de chaque noeud
				
		// Tant qu'il existe des noeuds non tri�s
		while(listeNoeudsParNiveau.size() < g.getNbSommets()) { 
			ArrayList<Integer> listeNoeudsTriesTemporaire = new ArrayList<>();// Liste temporaire de noeuds venant d'�tre attribu�
			
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
	
	/**
	 * @param g Graphe
	 * @return Retourne les pr�decesseurs de chaque noeud
	 */
	private static Map<Integer, List<Integer>> listePredecesseurs(IGraphe g){
		Map<Integer, List<Integer>> predecesseurs = new HashMap<>();
		// Insersion des pr�d�cesseurs
		for (Integer noeudSucc : g) {
			predecesseurs.put(noeudSucc, new ArrayList<>());
			for (Integer noeudPrec : g)
				if (g.aArc(noeudPrec, noeudSucc))
					predecesseurs.get(noeudSucc).add(noeudPrec);
		}
		return predecesseurs;
	}
	
	/**
	 * @brief Trie par niveau les noeuds
	 * @param g Graphe
	 * @param distances La distance du noeud de d�part pour chaque noeud
	 * @param listeNoeudsParNiveau Liste des noeuds tri�e par niveau
	 * @param noeudD Noeud de d�part
	 * @param noeudA Noeud d'arriv�e
	 * @throws NoPathEx
	 */
	private static void triParNiveau(IGraphe g, Map<Integer, Integer> distances, List<Integer> listeNoeudsParNiveau, Integer noeudD, Integer noeudA) throws NoPathEx{
		// Contient la liste de pr�decesseur de chaque noeud
		Map<Integer, List<Integer>> listePredecesseurs = listePredecesseurs(g);
		distances.put(noeudD, 0);
		
		// Suppression les pr�decesseurs inutiles
		preSuppresionPredecesseurs(g, noeudD, noeudA, listePredecesseurs);
		
		int nbNoeudTrieeMax = listePredecesseurs.size();
		
		// Tant qu'il existe des noeuds non tri�s
		while(listeNoeudsParNiveau.size() < nbNoeudTrieeMax) { 
			ArrayList<Integer> listeNoeudsTriesTemporaire = new ArrayList<>(); // Liste temporaire de noeuds venant d'�tre attribu�
			
			// Detection des noeuds de niveau sup�rieur
			nettoyagePredecesseurs(g, listePredecesseurs, listeNoeudsTriesTemporaire, listeNoeudsParNiveau);
			
			// Suppression de la liste des pr�decesseurs des noeuds venant d'�tre tri�s
			suppressionPredecesseurs(g, listePredecesseurs, listeNoeudsTriesTemporaire);
		}
	}

	/**
	 * @brief Supprime tous les pr�decesseurs, jusqu'au noeud de d�part
	 * @param g Graphe
	 * @param noeudD Noeud de d�part
	 * @param noeudA Noeud d'arriv�e
	 * @param listePredecesseurs Liste des pr�decesseurs de chaque noeud
	 */
	private static void preSuppresionPredecesseurs(IGraphe g, Integer noeudD, Integer noeudA,
			Map<Integer, List<Integer>> listePredecesseurs) throws NoPathEx {
		/* On supprimer temporairement le noeud de d�part pour que
		 * ses successeurs ne soient pas supprim�s*/
		listePredecesseurs.remove(noeudD);
		
		// Tant qu'il reste des noeud sans pr�d�cesseurs : 
		while(listePredecesseurs.containsValue(new ArrayList<>())) {
			for (Integer noeudASupprimer : g) {
				// Si le noeud n'a pas ou plus de pr�decesseurs
				if (listePredecesseurs.containsKey(noeudASupprimer) && listePredecesseurs.get(noeudASupprimer).isEmpty()) {
					/* Si le noeud en question est le noeud de d�part,
					 * cela signifie qu'il n'existe aucun chemin entre
					 * lui et le noeud de d�part*/
					if (noeudASupprimer.equals(noeudA))
						throw new NoPathEx();
					
					listePredecesseurs.remove(noeudASupprimer);
					
					// Parcourir la liste de pr�decesseurs de noeuds.
					for (Integer i : g)
						// Si le noeud � supprimer existe en tant que pr�decesseur, le supprimer
						if (listePredecesseurs.containsKey(i) && listePredecesseurs.get(i).contains(noeudASupprimer))
							listePredecesseurs.get(i).remove(noeudASupprimer);
				}
			}
		}
		
		// Reins�rer le noeud de d�part
		listePredecesseurs.put(noeudD, new ArrayList<>());
	}
	

	/*
	 * 	V�rifie si toutes les conditions suivantes sont r�unies :
	 * 		- Il existe un arc entre le noeud "actuel" et le noeud actuellement test�
	 *  	- L'une des conditions suivantes doit �tre remplie :
	 * 			- Aucun chemin avec le noeud successeur n'a encore �t� calcul�
	 * 			- Le chemin test� est plus optimis� que le chemin actuel
	 */
	private static boolean peutRemplacerDistanceActuelle(IGraphe g, Map<Integer, Integer> distances, Integer noeudS, Integer noeudP) {
		return g.aArc(noeudP, noeudS) && (!distances.containsKey(noeudS) || 
				distances.get(noeudS) > g.getValuation(noeudP, noeudS) + 
				distances.get(noeudP));
	}
	
	@Override
	public int pc(IGraphe g, Integer noeudD, Integer noeudA, List<Integer> chemin) throws CircuitAbsorbantEx, NoPathEx {
		if (!estOK(g))
			throw new CircuitAbsorbantEx();
		
		Map<Integer, Integer> distances = new HashMap<>();
		Map<Integer, Integer> predecesseurs = new HashMap<>();
		List<Integer> listeNoeudsParNiveau = new ArrayList<>();
		
		triParNiveau(g, distances, listeNoeudsParNiveau, noeudD, noeudA);
		
		for (Integer noeudS : listeNoeudsParNiveau) {
			for (int idx = 0, noeudP = listeNoeudsParNiveau.get(idx); noeudP != noeudS; noeudP = listeNoeudsParNiveau.get(++idx)) {
				if (peutRemplacerDistanceActuelle(g, distances, noeudS, noeudP)) {
					distances.put(noeudS, g.getValuation(noeudP, noeudS) + distances.get(noeudP));
					predecesseurs.put(noeudS, noeudP);
				}	
			}
		}
		
		Integer noeud = noeudA;
		
		while(noeud != null) {
			chemin.add(0, noeud);
			noeud = predecesseurs.get(noeud);
		}
		
		return distances.get(noeudA);
	}
}
