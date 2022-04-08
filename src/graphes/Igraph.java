package graphes;

public interface Igraph {
	/**
	 * @brief Retourne le nombre de noeuds
	 * @return Nombre de noeuds
	 */
	int getNbNoeuds();
	

	/**
	 * @brief Ajoute un arc au graphe
	 * @param i Num�ro de la ligne
	 * @param j Num�ro de la colonne
	 */
	void ajouterArc(int i, int j, int valeur);
	
	
	int valeurArc(int i, int j);
	
	
	/**
	 * @brief Retourne l'�tat de la matrice aux coordonn�es (i,j)
	 * @param i Num�ro de la ligne
	 * @param j Num�ro de la colonne
	 * @return true s'il y a un arc, false sinon
	 */
	default boolean aArc(int i, int j) {
		return valeurArc(i, j) != 0;
	}
	
	
	/**
	 * @brief Retourne le nombre de successeurs
	 * @param n Num�ro du noeud
	 * @return Nombre de successeurs
	 */
	default int dOut(int n) {
		int nbDegre = 0;
		
		for (int i=0; i<getNbNoeuds(); ++i) {
			nbDegre += aArc(n, i+1) ? 1 : 0;
		}
		
		return nbDegre;
	}
	
	
	/**
	 * @brief Retourne le nombre de pr�d�cesseurs
	 * @param n Num�ro du noeud
	 * @return Nombre de pr�d�cesseurs
	 */
	default int dIn(int n) {
		int nbDegre = 0;
		
		for (int i=0; i<getNbNoeuds(); ++i) {
			nbDegre += aArc(i+1, n) ? 1 : 0;
		}
		
		return nbDegre;
	}
}
