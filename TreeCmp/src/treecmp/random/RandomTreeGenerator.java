/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.random;

import java.util.Random;
import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.misc.SimpleIdGroup;
import pal.tree.Node;
import pal.tree.NodeFactory;
import pal.tree.NodeUtils;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeUtils;

/**
 *
 * @author Damian
 */
public class RandomTreeGenerator {

    private final IdGroup idGroup;
    private Random randomGen = new Random();


    public RandomTreeGenerator(IdGroup idGroup) {
        this.idGroup = idGroup;  
    }

    public RandomTreeGenerator(int n) {
        this.idGroup = genIdGroup(n);
    }

    public Tree generateYuleTree(boolean isRooted){
        int N = idGroup.getIdCount();
        if (N < 3){
            throw new IllegalArgumentException("IdGroup should have at least 3 ids");
        }
         if (!isRooted){
           throw new IllegalArgumentException("Not implemented yet!");
        }

        Identifier idTab[] = new Identifier[N];
        for (int i = 0; i < N; i++){
            idTab[i] = idGroup.getIdentifier(i);
        }

        generateRandomPermutation(idTab);

        Node l0 = NodeFactory.createNode(idTab[0]);
        Node l1 = NodeFactory.createNode(idTab[1]);
        Node l2 = NodeFactory.createNode(idTab[2]);

        Tree tree = genRootedThreeLeafTree (l0, l1, l2);
        for (int i = 3; i < N; i++){
            int n = tree.getExternalNodeCount();
            int randLeaf = randomGen.nextInt(n);
            Node baseNode = tree.getExternalNode(randLeaf);
            Node newLeaf = NodeFactory.createNode(idTab[i]);
            addNewNode(tree, baseNode, newLeaf);
        }
        //TODO: suppers the root for unrooted tree
       
        return tree;
    }
    
    public Tree generateYuleTree(Tree baseTree, boolean isRooted){
        int N = idGroup.getIdCount();
        if (N < 3){
            throw new IllegalArgumentException("IdGroup should have at least 3 ids");
        }
         if (!isRooted){
           throw new IllegalArgumentException("Not implemented yet!");
        }
        IdGroup baseTreeIdGroup = TreeUtils.getLeafIdGroup(baseTree);
        int nb = baseTreeIdGroup.getIdCount();
        int Nn = N-nb;
        Identifier idTab[] = new Identifier[Nn];
        int j = 0;
        for (int i = 0; i < N; i++){
            Identifier id = idGroup.getIdentifier(i);
            if (baseTreeIdGroup.whichIdNumber(id.getName()) == -1){
                idTab[j] = id;
                j++;
            }
        }
        generateRandomPermutation(idTab);
        
        Tree tree = baseTree.getCopy();
        j = 0;
        for (int i = nb; i < N; i++){
            int n = tree.getExternalNodeCount();
            int randLeaf = randomGen.nextInt(n);
            Node baseNode = tree.getExternalNode(randLeaf);
            Node newLeaf = NodeFactory.createNode(idTab[j]);
            addNewNode(tree, baseNode, newLeaf);
            j++;
        }
        //TODO: suppers the root for unrooted tree
       
        return tree;
    }

    private void addNewNode(Tree tree, Node baseNode, Node newNode){
       Node baseParentNode = baseNode.getParent();

        if (baseParentNode == null){
            throw new IllegalArgumentException("Base node shoud have a parent!");
        }

       NodeUtils.removeChild(baseParentNode, baseNode);
       Node newIntNode = NodeFactory.createNode();
       newIntNode.addChild(baseNode);
       newIntNode.addChild(newNode);
       baseParentNode.addChild(newIntNode);
       tree.createNodeList();
    }


    private <T> void generateRandomPermutation(T [] tab){
        int n = tab.length;
        T temp;
        int rand;
        for (int i = 0; i < n; i++){
            temp = tab [i];
            rand = n - 1 - randomGen.nextInt(n-i);
            tab[i] = tab[rand];
            tab[rand] = temp ;
        }
    }
    /**
     * Generates a tree ((l1,l2),(l3));
     * @param l1
     * @param l2
     * @param l3
     * @return
     */
    
    private Tree genRootedThreeLeafTree(Node l1, Node l2, Node l3){
        Tree tree = new SimpleTree();
        
        Node rootNode = NodeFactory.createNode();
        
        Node intNode = NodeFactory.createNode();
        intNode.addChild(l1);
        intNode.addChild(l2);
        rootNode.addChild(intNode);
        rootNode.addChild(l3);
        tree.setRoot(rootNode);
        tree.createNodeList();
        return tree;
    }

     private IdGroup genIdGroup(int n){
         IdGroup idGroup = new SimpleIdGroup(n);
         for (int i = 1; i <= n; i++){
             idGroup.setIdentifier(i-1, new Identifier(String.valueOf(i)));
         }
         return idGroup;
     }

}
