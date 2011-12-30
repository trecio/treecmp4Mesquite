/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.misc.SimpleIdGroup;
import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class TripleMetricOpt extends BaseMetric implements Metric {

    public double getDistance(Tree t1, Tree t2) {

        // macierze przodkow
        int[][] generational1 = getSymmetricGenerationalMatrices(t1);
        int[][] generational2 = getSymmetricGenerationalMatrices(t2);

        // pierwszy czlon wzoru na Sn
        IdGroup id1 = TreeUtils.getLeafIdGroup(t1);
        int count = id1.getIdCount();
        int Sn = count * (count-1) * (count-2) / 6;

        // drugi czlon wzoru na Sn
        for (int i = 0; i < count; i++) {
            List<Integer> wsp = getGenerationalsPatterns(generational1[i], generational2[i], i);    // tylko ich dlugosci wieksze od 1

            for (int j = 0; j < wsp.size(); j++) {
                Sn -= wsp.get(j) * (wsp.get(j) - 1) / 2;
            }
        }

        return Sn;
    }

    /**
     * Lista przejsc pozycji z jednej macierzy w druga.
     * Ignorowane sa przjscia o ilosci mniejszej od 2
     *
     * @param generational1
     * @param generational2
     * @param un tego wiersza nie rozpatrujemy - przekatna w macierzy int[][]
     * @return
     */
    private List<Integer> getGenerationalsPatterns(int[] generational1, int[] generational2, int un) {

    	final int length = generational1.length;
    	Map<Pair, Integer> genMatrixHelper = new HashMap<Pair, Integer>();
        for (int i=0; i<length; i++)
        	if (i != un) {
        		Pair patternPair = new Pair(generational1[i], generational2[i]);
        		if (!genMatrixHelper.containsKey(patternPair)) {
        			genMatrixHelper.put(patternPair, 1);
        		} else {
        			genMatrixHelper.put(patternPair, genMatrixHelper.get(patternPair)+1);
        		}
        	}
        
        List<Integer> result = new ArrayList<Integer>();        
        for (Integer i: genMatrixHelper.values())
        	if (i > 1)
        		result.add(i);
        return result;
    }
    
    private class Pair {
    	private int first;
		private int second;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + first;
			result = prime * result + second;
			return result;
		}

		@Override
		public String toString() {
			return "Pair [first=" + first + ", second=" + second + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (first != other.first)
				return false;
			if (second != other.second)
				return false;
			return true;
		}

		public Pair(int first, int second) {
    		this.first = first;
    		this.second = second; 
    	}

		private TripleMetricOpt getOuterType() {
			return TripleMetricOpt.this;
		}
    }

    /**
     * Generuje macierz, gdzie:
     * (i, j) - liczba wspólnych węzłów na ścieżce do roota,
     * a i, j - liście w drzewie
     */
    private int[][] getSymmetricGenerationalMatrices(Tree tree) {

        // utworzenie tablicy
        IdGroup leafs = TreeUtils.getLeafIdGroup(tree);
        final int count = leafs.getIdCount();
        
        String[] leafIdentifiers = Identifier.getNames(leafs);
        Arrays.sort(leafIdentifiers);
        IdGroup orderedIds = new SimpleIdGroup(leafIdentifiers);
        
        int[][] result = new int[count][count];

        for (int i = 0; i < count; i++) {
            Node nodeI = TreeUtils.getNodeByName(tree.getRoot(), leafIdentifiers[i]);
            for (int j = i + 1; j < count; j++) {
                Node nodeJ = TreeUtils.getNodeByName(tree.getRoot(), leafIdentifiers[j]);
                compute(nodeI, nodeJ, result, orderedIds);    // obliczania dla dwoch lisci
            }
        }

        return result;
    }

    /**
     * Uzupelnia macierz, porownujac 2 liscie w drzewie
     *
     * @param tree
     * @param nodeI
     * @param nodeJ
     * @param result wynikowa macierz
     */
    private void compute(Node nodeI, Node nodeJ, int[][] result, IdGroup ids) {

        // wezly do korzenia
        List<Node> rootsI = TripleMetric.getRoots(nodeI);
        List<Node> rootsJ = TripleMetric.getRoots(nodeJ);

        // usuniecie wspolnych wezlow,
        // potem wyznaczymy ilosc usunietych -> ilosc wspolnych wezlow
        int tmp = rootsI.size();
        rootsI.removeAll(rootsJ);

        // wyznaczenie indeksow
        // -1 - macierz liczy indeksy od 0        
        //int idI = Integer.parseInt(nodeI.getIdentifier().getName()) - 1;
        //int idJ = Integer.parseInt(nodeJ.getIdentifier().getName()) - 1;
        int idI = ids.whichIdNumber(nodeI.getIdentifier().getName());
        int idJ = ids.whichIdNumber(nodeJ.getIdentifier().getName());

        // usupelnienie macierzy o ilosc wspolnych wezlow do korzenia
        result[idI][idJ] = tmp - rootsI.size();
        result[idJ][idI] = tmp - rootsI.size();
    }
}
