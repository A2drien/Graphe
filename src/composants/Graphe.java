package composants;

public class Graphe {
	protected static final int NB_POINTS = 6;
	private boolean[][] coefficients;
	
	
	/**
	 * @brief Constructeur de graphe
	 */
	public Graphe() {
		this.coefficients = new boolean[NB_POINTS][NB_POINTS];
	}
	
	
	/**
	 * @brief Ajoute un arc au graphe
	 * @param i Num�ro de la ligne
	 * @param j Num�ro de la colonne
	 */
	public void ajouterArc(int i, int j) {
		this.coefficients[i-1][j-1] = true;
	}
	
	
	/**
	 * @brief Retourne l'�tat de la matrice aux coordonn�es (i,j)
	 * @param i Num�ro de la ligne
	 * @param j Num�ro de la colonne
	 * @return true s'il y a un arc, false sinon
	 */
	public boolean aArc(int i, int j) {
		return this.coefficients[i-1][j-1];
	}
	
	
	/**
	 * @brief Retourne le nombre de successeurs
	 * @param n Num�ro du noeud
	 * @return Nombre de successeurs
	 */
	public int dOut(int n) {
		int nbDegre = 0;
		
		for (boolean ma : coefficients[n-1]) {
			nbDegre += ma ? 1 : 0;
		}
		
		return nbDegre;
	}
	
	
	/**
	 * @brief Retourne le nombre de pr�d�cesseurs
	 * @param n Num�ro du noeud
	 * @return Nombre de pr�d�cesseurs
	 */
	public int dIn(int n) {
		int nbDegre = 0;
		
		for (int i=0; i<NB_POINTS; ++i) {
			nbDegre += coefficients[i][n-1] ? 1 : 0;
		}
		
		return nbDegre;
	}
}
