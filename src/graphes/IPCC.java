package graphes;

import java.util.List;

import exceptions.NoPathEx;

public interface IPCC {
	/**
	 * @param g Graphe � v�rifier
	 * @return true si le graphe est compatible avec l'algorithme, false sinon
	 */
	boolean estOK(IGraphe g);
	
	/**
	 * @param g Graphe 
	 * @param noeudDepart Noeud de d�part de l'algorithme
	 * @param noeudArrivee Noeud d'arriv�e de l'algorithme
	 * @param chemin[inout] Chemin utilis� par l'algorithme
	 * @return Distance la plus courte entre le noeud de d�part et d'arriv�e
	 * @throws NoPathEx Aucun chemin entre noeudDepart et noeudArrivee
	 */
	int pc(IGraphe g, Integer noeudDepart, Integer noeudArrivee, List<Integer> chemin) throws NoPathEx;
}
