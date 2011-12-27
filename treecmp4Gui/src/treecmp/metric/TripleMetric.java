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
public class TripleMetric extends BaseMetric implements Metric {

    public double getDistance(Tree t1, Tree t2) {

        // generowanie wszystkich trojek
        List<Triple> triples1 = getTriples(t1);
        List<Triple> triples2 = getTriples(t2);

        // usuwanie identycznych
        triples1.removeAll(triples2);

        // zwracamy unikatowe
        return triples1.size();
    }

    /**
     * Opisuje trojke wierzcholkow.
     * Sa one posortowane w kolejnosci ulatwiajacej porownywanie.
     */
    private class Triple {
        private int v1, v2, v3;

        private Triple(int v1, int v2, int v3, List<Node> roots1, List<Node> roots2, List<Node> roots3) {

            // kopie
            List<Node> tmp1_2 = new ArrayList<Node>(roots1);
            List<Node> tmp1_3 = new ArrayList<Node>(roots1);
            List<Node> tmp2_3 = new ArrayList<Node>(roots2);

            // usuwanie wspolnych wezlow - wezly na drodze do glownego korzenia
            tmp1_2.removeAll(roots2);
            tmp1_3.removeAll(roots3);
            tmp2_3.removeAll(roots3);

            // ilosc usunietych wezlow
            // (ilosc poczatkowa - ilosc po usunieciu)
            int s1_2 = roots1.size() - tmp1_2.size();
            int s1_3 = roots1.size() - tmp1_3.size();
            int s2_3 = roots2.size() - tmp2_3.size();

            // wyszukanie najdluzszej sciezki
            int max = Math.max(s1_2, Math.max(s1_2, s2_3));

            // liscie z najdluzsza wspolna sciezka, tworza zawsze dwojke, np.
            //          /\
            //         /  \
            //        /   /\
            //       1   2  3
            // tutaj chodzi o 2 i 3
            // ta dwojka zapisywana jest w v2 i v3,
            // przy czym w v2 jest mniejszy, w v3 wiekszy sposrod v2 i v3
            // ostatni lisc w v1
            // taki format zapisu ulatwia porownywanie trojek miedzy drzewami
            if(max == s1_2)
            {
                this.v1 = v3;
                this.v2 = Math.min(v1, v2);
                this.v3 = Math.max(v1, v2);
            }
            else if(max == s1_3)
            {
                this.v1 = v2;
                this.v2 = Math.min(v1, v3);
                this.v3 = Math.max(v1, v3);
            }
            else if(max == s2_3)
            {
                this.v1 = v1;
                this.v2 = Math.min(v2, v3);
                this.v3 = Math.max(v2, v3);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Triple other = (Triple) obj;
            if (this.v1 != other.v1) {
                return false;
            }
            if (this.v2 != other.v2) {
                return false;
            }
            if (this.v3 != other.v3) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 61 * hash + this.v1;
            hash = 61 * hash + this.v2;
            hash = 61 * hash + this.v3;
            return hash;
        }
    }

    /**
     * Sciezka od wezla do korzenia
     * @param node
     * @return
     */
    public static List<Node> getRoots(Node node) {

        List<Node> result = new ArrayList<Node>();

        while ((node = node.getParent()) != null) {
            result.add(node);
        }

        return result;
    }

    /**
     * Pobranie wszystkich mozliwych trojek z drzewa
     * @param t1
     * @return
     */
    private List<Triple> getTriples(Tree t1) {

        List<Triple> result = new ArrayList<Triple>();

        // liscie drzewa
        IdGroup id1 = TreeUtils.getLeafIdGroup(t1);

        // generowanie trojek (i, j, k) po wszyskich lisciach
        for(int i=0; i<id1.getIdCount(); i++)
        {
            int v1 = Integer.parseInt(id1.getIdentifier(i).getName());
            Node n1 = TreeUtils.getNodeByName(t1.getRoot(), id1.getIdentifier(i).getName());
            List<Node> rootI = getRoots(n1);

            for(int j=i+1; j<id1.getIdCount() && i!=j; j++)
            {
                int v2 = Integer.parseInt(id1.getIdentifier(j).getName());
                Node n2 = TreeUtils.getNodeByName(t1.getRoot(), id1.getIdentifier(j).getName());
                List<Node> rootJ = getRoots(n2);

                for(int k=j+1; k<id1.getIdCount() && j!=k; k++)
                {
                    int v3 = Integer.parseInt(id1.getIdentifier(k).getName());
                    Node n3 = TreeUtils.getNodeByName(t1.getRoot(), id1.getIdentifier(k).getName());
                    List<Node> rootK = getRoots(n3);

                    // dodanie nowej trojki
                    result.add(new Triple(v1, v2, v3, rootI, rootJ, rootK));
                }
            }
        }

        return result;
    }

}
