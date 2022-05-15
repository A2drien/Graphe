package graphes.types;

import graphes.IGraphe;

public abstract class Graphe implements IGraphe {
	/**
	 * @param n Num�ro du noeud � tester
	 * @return true si le noeud peut exister, false sinon
	 */
	public boolean estNoeudOK(int n) {
		return n >= 1 && n <= getNbSommets();
	}
	
	/**
	 * @param a Num�ro du noeud de d�part de l'arc � tester
	 * @param b Num�ro du noeud de destination de l'arc � tester
	 * @return true si l'arc peut exister, false sinon
	 */
	public boolean estArcOK(int a, int b) {
		return estNoeudOK(a) && estNoeudOK(b);
	}
	
	@Override
	public boolean aArc(int a, int b) {
		return getValuation(a,b) != INFINI;
	}
}