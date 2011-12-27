/* * SimpleNodeExt.java * * Created on 31 marzec 2007, 13:35 * * To change this template, choose Tools | Template Manager * and open the template in the editor. */package treecmp;import pal.misc.*;import java.io.*;import pal.io.*;import java.util.Hashtable;import java.util.Enumeration;import pal.tree.SimpleNode;import pal.tree.Node;/** * * @author VOX */public class SimpleNodeExt extends SimpleNode implements Node {    public int label = 0;    public int level = 0;    /** Creates a new instance of SimpleNodeExt */    public SimpleNodeExt() {        super();    }    public SimpleNodeExt(String name, double branchLength) {        super(name, branchLength);    }    /**     * Constructor     * @param children     * @param branchLength     * @throws IllegalArgumentException if only one child!     */    protected SimpleNodeExt(Node[] children, double branchLength) {        super(children, branchLength);    }    protected SimpleNodeExt(Node[] children) {        super(children);    }    /** constructor used to clone a node and all children */    public SimpleNodeExt(Node n) {        this(n, true);        ;    }    public SimpleNodeExt(Node n, boolean keepIds) {        init(n, keepIds);        for (int i = 0; i < n.getChildCount(); i++) {            addChild(new SimpleNodeExt(n.getChild(i), keepIds));        }    }    public SimpleNodeExt(Node n, LabelMapping lm) {        init(n, true, lm);        for (int i = 0; i < n.getChildCount(); i++) {            addChild(new SimpleNodeExt(n.getChild(i), lm));        }    }}