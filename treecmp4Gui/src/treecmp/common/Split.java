package treecmp.common;

import java.util.BitSet;

import pal.tree.Node;

public class Split {
	public final BitSet bitSet;
	public final Node node;
	
	public Split(BitSet bitSet, Node node) {
		this.bitSet = bitSet;
		this.node = node;
	}
}
