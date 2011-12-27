/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treecmp.metric;

import java.util.ArrayList;
import java.util.List;
import pal.misc.IdGroup;
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
     * Opisuje ilosc przejsc i -> j, w jednym wierszu dwoch macierzy
     * np. i = 2, j = 3, count = 4
     *      oznacza, ze 2 w jednej macierzy zamienilo sie w 3, 4 razy
     */
    private class GenerationalPattern {

        int i, j, Count;

        public GenerationalPattern(int i, int j) {
            this.i = i;
            this.j = j;
            Count = 1;
        }

        /**
         * Rowne, gdy i i j sa identyczne
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final GenerationalPattern other = (GenerationalPattern) obj;

            return (this.i == other.i) && (this.j == other.j);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + this.i;
            hash = 17 * hash + this.j;
            return hash;
        }

        /*@Override
        public String toString()
        {
        return "("+i+", "+j+") => "+count;
        }*/
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

        List<Integer> result = new ArrayList<Integer>();
        List<GenerationalPattern> data = new ArrayList<GenerationalPattern>();

        // wszystkie mozliwe przejscia generational1[i] -> generational2[i],
        // poprzez wszystkie mozliwe i
        for (int i = 0; i < generational1.length; i++) {
            if (i != un) {
                GenerationalPattern m = new GenerationalPattern(generational1[i], generational2[i]);
                if (data.contains(m)) {
                    int index = data.indexOf(m);
                    data.get(index).Count++;    // juz istnieje, wiec zwiekszamy ilosc wzorca
                } else {
                    data.add(m);    // dodajemy nowy, poniewaz jeszcze taki wzorzec nie istnieje
                }
            }
        }

        for (int i = 0; i < data.size(); i++) {
            GenerationalPattern m = data.get(i);
            if (m.Count > 1) {
                result.add(m.Count);
            }
        }

        return result;
    }

    /**
     * Generuje macierz, gdzie:
     * (i, j) - liczba wspólnych węzłów na ścieżce do roota,
     * a i, j - liście w drzewie
     */
    private int[][] getSymmetricGenerationalMatrices(Tree tree) {

        // utworzenie tablicy
        IdGroup id1 = TreeUtils.getLeafIdGroup(tree);        
        int count = id1.getIdCount();
        int[][] result = new int[count][count];

        for (int i = 0; i < count; i++) {
            Node nodeI = TreeUtils.getNodeByName(tree.getRoot(), id1.getIdentifier(i).getName());
            for (int j = i + 1; j < count; j++) {
                Node nodeJ = TreeUtils.getNodeByName(tree.getRoot(), id1.getIdentifier(j).getName());
                compute(nodeI, nodeJ, result, id1);    // obliczania dla dwoch lisci
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
