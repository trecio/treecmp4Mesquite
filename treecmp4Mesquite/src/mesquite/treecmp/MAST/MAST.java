package mesquite.treecmp.MAST;

import mesquite.lib.*;
import mesquite.lib.duties.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: HH and lyj
 * Date: 2012-06-15
 */
public class MAST extends DistanceBetween2Trees {
    private int TaxonReferenceLength = -1;
    /* taxa2Ref is used to find the refernce number for a given taxa */
    private Map<Integer, Integer> taxa2Ref;

    /* Leaves is the intersection of Leaves of the two trees */
    private int[] Leaves;
    /* LeavesCache is used to cache all visited trees' leaves */
    private Map<int[], boolean[]> LeavesCache;

    private RootedTriples R;
    private FanTriples F;
    private int[][] MastTable;
    private Map<Tree, LCATable> LCACache;

    /* using array to represent set A and C, 
     * this is much faster than using hash set or other set-like data containers
     */
    private int[][][] Set_A;
    private int[][][] Set_C;


	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from
     happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/
    public void initialize(Tree t1, Tree t2) {
    }

    public boolean startJob(String arguments, Object condition, boolean hiredByName) {
        //initializes internal data members here
        /*
		row = 1;
        col = 0;
		*/

        LeavesCache = new HashMap<int[], boolean[]>(100, 05f);
        LCACache = new HashMap<Tree, LCATable>(100, 0.5f);
        return true;
    }

    /** Returns the name of the module*/
    public String getName() {
        return "MAST(Maximum Agreement Subtree) Tree Difference";
    }

    public String getVersion() {
        return "1.0";
    }

    public String getYearReleased() {
        return "2012";
    }

    public boolean showCitation() {
        return true;
    }

    public String getPackageName() {
        return "Tree Comparison Package";
    }

    public boolean getUserChoosable() {
        return false;
    }

    public boolean isPrerelease() {
        return false;
    }

    public boolean isSubstantive() {
        return true;
    }

    public String getCitation() {
        return "\n" + getYearReleased() + ". " + getAuthors() + "\n";
    }

    public String getAuthors() {
        return "H Huang and YJ Li, University of South Florida, Tampa, FL.";
    }

    public String getExplanation() {
        return "Calculates difference according to the number of leaves in the Maximum Agreement Subtree between two trees.";
    }

    private void cleanup() {
        for (int i = 1; i < Leaves.length; i++) {
            for (int j = 1; j < Leaves.length; j++) {
                Set_A[i][j] = null;
                Set_C[i][j] = null;
                MastTable[i][j] = 0;
            }
        }
        R.cleanup();
        F.cleanup();
    }

    public void employeeQuit(MesquiteModule m) {
        iQuit();
    }

    public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result, MesquiteString resultString) {
        /*check the precondition first*/
        if (result == null) {
            System.out.println("You passed an uninitialized result holder to calculateNumber().");
            return;
        }

        if (t1.getTaxa() != t2.getTaxa()) {
            System.out.println("MAST only works for treesTable over the same Taxa.");
            return;
        }

        boolean[] tmpObj;
        int[] tmpLeaves;
	// using boolean array to represent sets of leaves
        boolean[] LeafSet1,LeafSet2;
        final Taxa taxa = t1.getTaxa();
        int TotalTaxon = taxa.getNumTaxa() + 1; 

	/* compute the Taxon reference system */
        if (TaxonReferenceLength == -1) {
            TaxonReferenceLength = TotalTaxon; 
            TotalTaxon = TaxonReferenceLength;		// zero is reserved
            taxa2Ref = new HashMap<Integer, Integer>(2 * TotalTaxon);
            for (int i = 1; /* zero is reserved */ i < TotalTaxon; i++) {
                Taxon taxon = taxa.getTaxon(i - 1);
                taxa2Ref.put(taxon.number, i);
            }
        }

        /*prepare to obtain leaves from t1 and t2*/
        int[] taxaOfTree1 = t1.getTerminalTaxa(t1.getRoot());
        int[] taxaOfTree2 = t2.getTerminalTaxa(t2.getRoot());
        int maxLeavesNumber = taxaOfTree1.length;
        if (taxaOfTree2.length > maxLeavesNumber)
            maxLeavesNumber = taxaOfTree2.length;

        /* LeafSet1 and LeafSet2's index range is 1 to (TaxonRefference.length-1) */
        if ((tmpObj = LeavesCache.get(taxaOfTree1)) != null) {
            LeafSet1 = tmpObj;
        } else {
            LeafSet1 = new boolean[TotalTaxon];
            for (int i = 0; i < taxaOfTree1.length; i++) {
                LeafSet1[taxa2Ref.get(taxaOfTree1[i])] = true; //mark the reference index of taxa from tree one
            }
            LeavesCache.put(taxaOfTree1, LeafSet1);
        }

        if ((tmpObj = LeavesCache.get(taxaOfTree2)) != null) {
            LeafSet2 = tmpObj;
        } else {
            LeafSet2 = new boolean[TotalTaxon];
            for (int i = 0; i < taxaOfTree2.length; i++) {
                LeafSet2[taxa2Ref.get(taxaOfTree2[i])] = true;
            }
            LeavesCache.put(taxaOfTree2, LeafSet2);
        }

        /* tmpLeaves' index range is 1 to (LeafSet1.length-1) */
        tmpLeaves = new int[LeafSet1.length];
	/* totalLeaves is the number of the intersection of Leaves of t1 and t2 */
        int totalLeaves = 1;
        for (int i = 1; i < LeafSet1.length; i++) {
            if (LeafSet1[i] && LeafSet2[i]) {
                tmpLeaves[totalLeaves] = i;
                totalLeaves++;
            }
        }
        /* Leaves is the intersection of Leaves of t1 and t2,
	 * every leaf is referencd using the global reference system.
	 * Leaves' index range is 1 to (totalLeaves-1)
	 */
        Leaves = new int[totalLeaves];
        for (int i = 1; i < Leaves.length; i++) {
            Leaves[i] = tmpLeaves[i];
        }
        tmpLeaves = null;

        Triples triples = new Triples(t1, t2, taxa2Ref, TotalTaxon);
        R = new RootedTriples(triples);
        F = new FanTriples(triples);

	// allocate heap space for Set A and C
        if (Set_A == null) {
            Set_A = new int[TotalTaxon][][];
            Set_C = new int[TotalTaxon][][];
            for (int i = 1; i < TotalTaxon; i++) {
                Set_A[i] = new int[TotalTaxon][];
                Set_C[i] = new int[TotalTaxon][];
            }
        }
        int len = Leaves.length;
        for (int i = 1; i < len - 2; i++) {
            int a,b,c;
            a = Leaves[i];
            for (int j = i + 1; j < len - 1; j++) {
                b = Leaves[j];
                for (int k = j + 1; k < len; k++) {
                    c = Leaves[k];
                    int type = triples.getTripleType(a, b, c);
                    if (type == -1) {
                        fillSetC(i, j, k);
                        fillSetC(i, k, j);
                        fillSetC(j, k, i);
                    } else if (type == c) {//SetA[i,k]=j,SetA[j,k]=i
                        fillSetA(i, j, k);
                        fillSetA(j, i, k);
                    } else if (type == b) {
                        fillSetA(i, k, j);
                        fillSetA(k, i, j);
                    } else if (type == a) {
                        fillSetA(j, k, i);
                        fillSetA(k, j, i);
                    }
                }
            }
        }

        MastTable = new int[TotalTaxon][];
        for (int i = 1; i < TotalTaxon; i++) {
            MastTable[i] = new int[TotalTaxon];
        }
        int mast = 0,u = 0;
        if (Leaves.length - 1 >= 3) {
            /* traverse every pair of leaves, return the maxinum MAST value*/
            for (int i = 1; i < Leaves.length - 1; i++) {
                for (int j = i + 1; j < Leaves.length; j++) {
                    u = _MAST(i, j);
                    if (u > mast) {
                        mast = u;
                    }
                }
            }
        } else {
	    // when the number of the intersection of leaves from t1 and t2 is less hen 3, MAST is this number
            mast = Leaves.length;
        }
        /* return the number of different leaves of t1 and t2 */
        result.setValue(maxLeavesNumber - mast);
        if (resultString != null) {
            resultString.setValue("MAST defference is: " + result.toString());
        }

        cleanup();
        return;
    }

    private void fillSetC(int i, int j, int k) {
        int[] tmp;
        if (Set_C[i][j] == null) {
            tmp = new int[Leaves.length];
            tmp[0] = 1;
            tmp[1] = k;
            Set_C[i][j] = tmp;
            Set_C[j][i] = tmp;
        } else {
            tmp = Set_C[i][j];
            tmp[0]++;
            tmp[tmp[0]] = k;
        }
    }

    private void fillSetA(int i, int j, int k) {
        int[] tmp;
        if (Set_A[i][k] == null) {
            tmp = new int[Leaves.length];
            tmp[0] = 1;
            tmp[1] = j;
            Set_A[i][k] = tmp;
        } else {
            tmp = Set_A[i][k];
            tmp[0]++;
            tmp[tmp[0]] = j;
        }
    }

    /** @param a: index in Leaves array
     * @param b: index in Leaves array
     * @return
     */
    private int _MAST(int a, int b) {
        int tmp = MastTable[a][b];
        if (tmp > 0) return tmp;

        if (a == b) {
            //MastTable[a][b] = 1;
            //MastTable[b][a] = 1;
            return 1;
        }

/*MAST(a,x*)+MAST(b,y*)*/
        int result,maxa,maxb,i;
        maxa = 1;
        maxb = 1;
/*Reserve zero*/
        int taxaRef_a,taxaRef_b;
        taxaRef_a = Leaves[a];
        taxaRef_b = Leaves[b];
        if (Set_A[a][b] != null) {
            int[] tmpSet = Set_A[a][b];
            for (i = 1; i <= tmpSet[0]; i++) {
                if ((R.contains(taxaRef_a, Leaves[tmpSet[i]], taxaRef_b))
                        && (tmp = _MAST(a, tmpSet[i])) > maxa) {
                    maxa = tmp;
                }
            }
        }
        if (Set_A[b][a] != null) {
            int[] tmpSet = Set_A[b][a];
            for (i = 1; i <= tmpSet[0]; i++) {
                if ((R.contains(taxaRef_b, Leaves[tmpSet[i]], taxaRef_a))
                        && (tmp = _MAST(b, tmpSet[i])) > maxb) {
                    maxb = tmp;
                }
            }
        }
        result = maxa + maxb;

        WeightVertex[] C = BuildSetC(a, b);
        if (C != null && C.length > 0) {
            WeightedGraph g = new WeightedGraph(C, taxaRef_a);
            result += g.weightOfMC();
        }

        MastTable[a][b] = result;
        MastTable[b][a] = result;
        return result;
    }

    /**
     * @param a: index in Leaves array
     * @param b: index in Leaves array
     * @return
     */
    private WeightVertex[] BuildSetC(int a, int b) {
        int[] tmp = Set_C[a][b];
        WeightVertex[] t = null;
        if (tmp != null) {
            t = new WeightVertex[tmp[0]];
            for (int i = 1; i <= tmp[0]; i++) {
                t[i - 1] = new WeightVertex();
                t[i - 1].leafIndex = tmp[i];
            }
        }
        return t;
    }

    private class WeightedGraph {
        private WeightVertex[] vSet;

        private int maxweight;
        private int pos;
        private int[] MW;
        private boolean cutBranch = false;

        /**
         * @param c         
         * @param taxa_a    vertex a is index in TaxonRefference
         */
        public WeightedGraph(WeightVertex[] c, int taxa_a) {
            vSet = c;

            int z,k,max,tlen;
            int u,taxa_z,leaf_z,taxa_k,leaf_k,partnerLeaf;
            WeightVertex[] t = new WeightVertex[vSet.length];

            /*go through each pair in the set C*/
            for (z = 0; z < vSet.length; z++) {
                leaf_z = vSet[z].leafIndex;
                taxa_z = Leaves[leaf_z];
                partnerLeaf = leaf_z;
                max = 1;
                /*count the weight that maximized MAST(leaf_z,leaf_z*)*/
                for (k = 0, tlen = 0; k < vSet.length; k++) {
                    leaf_k = vSet[k].leafIndex;
                    taxa_k = Leaves[leaf_k];
                    if (R.contains(taxa_z, taxa_k, taxa_a)) {
                        u = _MAST(leaf_z, leaf_k);
                        if (u > max) {
                            max = u;
                            partnerLeaf = leaf_k;
                        }
                    }
                    if (F.contains(taxa_z, taxa_k, taxa_a)) {
                        t[tlen] = vSet[k];
                        tlen++;
                    }
                }
                vSet[z].setAdjacence(t, tlen);
                vSet[z].wSum = max * tlen;
                vSet[z].weight = max;
                vSet[z].partnerLeafIndex = partnerLeaf;
            }

            for (z = 0; z < vSet.length; z++) {
                for (k = 0, vSet[z].key = vSet[z].weight; k < vSet[z].adjacence.length; k++) {
                    vSet[z].key += vSet[z].adjacence[k].wSum;
                }
            }
            SortVertexArray(vSet);

            for (z = 0; z < vSet.length; z++) {
                /* key value */
                vSet[z].key = z;
                t[z] = null;
            }
            for (z = 0; z < vSet.length; z++) {
                //SortVertexArray(vSet[z].adjacence);
                WeightVertex[] adj;
                adj = vSet[z].adjacence;
                for (int j = 0; j < adj.length; j++) {
                    t[adj[j].key] = adj[j];
                }
                int i = 0;
                for (int j = 0; j < t.length; j++) {
                    if (t[j] != null) {
                        adj[i] = t[j];
                        t[j] = null;
                        i++;
                    }
                }
            }
        }

        /* PATRIC R.J. A New Algorithm For The Maximum-Weight Clique Problem 2001*/
        public int weightOfMC() {
            WeightVertex[] t = new WeightVertex[vSet.length];
            maxweight = 0;
            MW = new int[vSet.length];
            for (pos = vSet.length - 1; pos >= 0; pos--) {
                cutBranch = false;
                //wclique(intersect(S, vSet[pos].adjacence), vSet[pos].weight);
                WeightVertex[] adj;
                int i,len;
                adj = vSet[pos].adjacence;
                for (i = 0; (i < adj.length && adj[i].key <= pos); i++) {
                }
                len = adj.length - i;
                for (int j = 0; j < len; j++, i++) {
                    t[j] = adj[i];
                }
                wclique(t, len, vSet[pos].weight);
                MW[pos] = maxweight;
            }

            MW = null;
            return maxweight;
        }

        private void wclique(WeightVertex[] workingset, int len, int w) {
            if (workingset != null && len == 0) {
                if (w > maxweight) {
                    maxweight = w;
                    if (pos + 1 < MW.length
                            && maxweight == MW[pos + 1] + vSet[pos].weight) {
                        cutBranch = true;
                    }
                    return;
                }
            }
            WeightVertex[] t = new WeightVertex[len];
            for (int k = 0; k < len; k++) {
                int j,WeightOfWorkingset = 0;
                for (j = k; j < len; j++) {
                    WeightOfWorkingset += workingset[j].weight;
                }
                if (w + WeightOfWorkingset <= maxweight) return;
                WeightVertex v = workingset[k];
                int i = v.key;
                if (w + MW[i] <= maxweight) return;
                int newlen = 0,p;
                WeightVertex[] adj;
                adj = v.adjacence;
                for (j = k + 1, p = 0; j < len; j++) {
                    while (p < adj.length && workingset[j].key > adj[p].key) p++;
                    if (p < adj.length && workingset[j].key == adj[p].key) {
                        t[newlen] = adj[p];
                        newlen++;
                        p++;
                    }
                }
                wclique(t, newlen, w + v.weight);
                //wclique(intersect(workingset, v.adjacence), w + v.weight);
                if (cutBranch) return;
            }
            return;
        }
    }

    private class WeightVertex implements Comparable {
        /*leafIndex and partnerLeafIndex are index in the Leaves array*/
        int leafIndex;
        public int partnerLeafIndex;
        int weight;
        int key;
        int wSum;
        WeightVertex[] adjacence;

        public int compareTo(Object o) {
            WeightVertex t = (WeightVertex) o;
            int r = this.key - t.key;
            if (r == 0) r = this.weight - t.weight;
            if (r == 0) r = this.leafIndex - t.leafIndex;
            return r;
        }

        /*copy references from vertex array*/
        public void setAdjacence(WeightVertex[] vertexArray, int len) {
            adjacence = new WeightVertex[len];
            for (int i = 0; i < len; i++) {
                adjacence[i] = vertexArray[i];
            }
        }
    }

    private TreeSet reorderTree = new TreeSet();

    private void SortVertexArray(WeightVertex[] vset) {
        int k;
        for (k = 0; k < vset.length; k++) {
            reorderTree.add(vset[k]);
        }
        k = 0;
        Iterator i = reorderTree.iterator();
        while (i.hasNext()) {
            WeightVertex v = (WeightVertex) i.next();
            vset[k] = v;
            k++;
        }
        reorderTree.clear();
    }

    /**
     * LCATable
     * @param t 
     * @param taxa2Ref   Taxon2Refference
     * Taxon Number TaxonRefference
     * @return  LCATable
     */
    public LCATable getInstance(Tree t, Map<Integer, Integer> taxa2Ref, int maxLen) {
        LCATable r;
        LCATable o;
        if ((o = LCACache.get(t)) != null) {
            r = o;
        } else {
            r = new LCATable(t, taxa2Ref, maxLen);
            LCACache.put(t, r);
        }
        return r;
    }

    /**
     * lazy initized
     */
    class Triples {
        private LCATable t1,t2;
        private int[][][] table;

        /**
         * precondition: tree1,tree2
         * @param tree1
         * @param tree2
         * @param taxa2Ref TaxonRefference
         * @param maxTaxon maxTaxon-1
         */
        public Triples(Tree tree1, Tree tree2, Map<Integer, Integer> taxa2Ref, int maxTaxon) {
            t1 = getInstance(tree1, taxa2Ref, maxTaxon);
            t2 = getInstance(tree2, taxa2Ref, maxTaxon);
            /*Reserve zero*/
            table = new int[maxTaxon][][];
            for (int i = 1; i < maxTaxon; i++) {
                table[i] = new int[maxTaxon][];
                for (int j = 1; j < maxTaxon; j++) {
                    table[i][j] = new int[maxTaxon];
                }
            }
        }

        public void clear() {
            t1 = null;
            t2 = null;
        }

        /**
         * precondition: i,j,k
         * @param a  TaxonRefference
         * @param b  TaxonRefference
         * @param c TaxonRefference
         * @return -1 FanTriple        */
        public int getTripleType(int a, int b, int c) {
            int result;
            if (table[a][b][c] == 0) {
                result = t1.getTripleType(a, b, c);
                if (result != t2.getTripleType(a, b, c)) {
                    result = -2;
                }
                table[a][b][c] = result;
                return result;
            } else {
                return table[a][b][c];
            }
        }
    }

    /**
     * Least Common Ancestor
     * Cache HashTable
     */
    public class LCATable {
        private int[][] table;
        private Tree tree;

        private int index;
        private int[] treeleaves;
        private Map<Integer, Integer> taxa2Ref;

        /**
         * @param t
         * @param taxa2Ref   Taxon2Refference
         * Taxon Number TaxonRefference
         * @param maxLen: maxLen-1
         */
        private LCATable(Tree t, Map<Integer, Integer> taxa2Ref, int maxLen) {
            table = new int[maxLen][];
            for (int i = 1; i < maxLen; i++) {
                table[i] = new int[maxLen];
            }
            index = 0;
            this.taxa2Ref = taxa2Ref;
            this.tree = t;
            treeleaves = new int[maxLen];
            travel(t.getRoot());
            this.taxa2Ref = null;
            treeleaves = null;
            index = 0;
        }

        /**
         * LCA
         * @param node
         * @return
         */
        private LCATable.Cluster travel(int node) {
            /*×ÓÊ÷µÄNode NumberÊý×é*/
            int[] children = tree.daughtersOfNode(node);
            if (children != null && children.length > 0) {
                /*LCAtable*/
                LCATable.Cluster[] clusters = new LCATable.Cluster[children.length];
                for (int i = 0; i < clusters.length; i++) {
                    clusters[i] = travel(children[i]);
                }
                /*cluster*/
                for (int i = 0; i < clusters.length - 1; i++) {
                    for (int j = i + 1; j < clusters.length; j++) {
                        /*cluster*/
                        for (int u = clusters[i].begin; u <= clusters[i].end; u++) {
                            for (int v = clusters[j].begin; v <= clusters[j].end; v++) {
                                setLCA(u, v, node);
                            }
                        }
                    }
                }
                return new LCATable.Cluster(clusters[0].begin, clusters[clusters.length - 1].end);
            } else {
                treeleaves[index] = node;
                index++;
                return new LCATable.Cluster(index - 1, index - 1);
            }
        }

        /**
         * @param a  Leaves 
         * @param b  Leaves 
         * @param value (Node Number in the tree)
         */
        private void setLCA(int a, int b, int value) {
            /*first map Node number to Taxon number, then map Taxon number to Index in TaxonRefference array*/
            int a1 = taxa2Ref.get(tree.taxonNumberOfNode(treeleaves[a])).intValue();
            int b1 = taxa2Ref.get(tree.taxonNumberOfNode(treeleaves[b])).intValue();
            table[a1][b1] = value;
            table[b1][a1] = value;
        }

        /**
         * precondition: a,b
         * @param a TaxonRefference ÖÐµÄË÷Òý
         * @param b TaxonRefference ÖÐµÄË÷Òý
         * @return Least Common Ancestor(Node Number in the tree)
         */
        private int getLCA(int a, int b) {
            return table[a][b];
        }

        /**
         *¸
         * precondition: a,b,c
         * @param a  TaxonRefference 
         * @param b  TaxonRefference 
         * @param c TaxonRefference 
         * @return 
         * (bc)|a (ac)|b (ab)|c
         * 
         */
        public int getTripleType(int a, int b, int c) {
            int x,y,z;
            x = getLCA(a, b);
            y = getLCA(b, c);
            z = getLCA(a, c);
            boolean b1,b2,b3;
            b1 = (x == y);
            b2 = (y == z);
            b3 = (x == z);
            if (b1 && b2 && b3) {
                return -1;
            }
            if (b1 && !b2 && !b3) {
                return b;
            }
            if (!b1 && b2 && !b3) {
                return c;
            }
            if (!b1 && !b2 && b3) {
                return a;
            }
            /* throw bug*/
            throw new RuntimeException("Bug at LCATable of Tree: " + getName() +
                    ";Nodes: " + a + ", " + b + ", " + c +
                    ";LCA values: " + x + ", " + y + ", " + z +
                    ";checks: " + b1 + ", " + b2 + ", " + b3);
        }

        class Cluster {
            int begin,end;

            public Cluster(int begin, int end) {
                this.begin = begin;
                this.end = end;
            }
        }
    }
}

class RootedTriples {
    private MAST.Triples triples;

    public RootedTriples(MAST.Triples t) {
        triples = t;
    }

    /**
     * precondition: ref_a,ref_b,ref_c 
     * note: 
     * @param ref_a µÚÒ»Æ¬Ê÷Ò¶ÔÚÖ÷³ÌÐòµÄÊý×é TaxonRefference 
     * @param ref_b µÚ¶þÆ¬Ê÷Ò¶ÔÚÖ÷³ÌÐòµÄÊý×é TaxonRefference 
     * @param ref_c µÚÈýÆ¬Ê÷Ò¶ÔÚÖ÷³ÌÐòµÄÊý×é TaxonRefference 
     * @return ( ref_a, ref_b | ref_c )
     * 
     */
    public boolean contains(int ref_a, int ref_b, int ref_c) {
        if (ref_a == ref_b || ref_b == ref_c || ref_c == ref_a) return false;
        if (triples.getTripleType(ref_a, ref_b, ref_c) == ref_c) {
            return true;
        } else {
            return false;
        }
    }

    public void cleanup() {
        triples.clear();
        triples = null;
    }
}

class FanTriples {
    private MAST.Triples triples;

    public FanTriples(MAST.Triples t) {
        triples = t;
    }

    /**
     * precondition: ref_a,ref_b,ref_c
     * note:
     * @param ref_a TaxonRefference
     * @param ref_b TaxonRefference
     * @param ref_c TaxonRefference 
     * @return ( ref_a, ref_b, ref_c )
     * 
     */
    public boolean contains(int ref_a, int ref_b, int ref_c) {
        if (ref_a == ref_b || ref_b == ref_c || ref_c == ref_a) return false;
        if (triples.getTripleType(ref_a, ref_b, ref_c) == -1) {
            return true;
        } else {
            return false;
        }
    }

    public void cleanup() {
        triples.clear();
        triples = null;
    }
}
