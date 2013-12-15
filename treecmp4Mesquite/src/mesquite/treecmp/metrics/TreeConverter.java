package mesquite.treecmp.metrics;

import java.io.IOException;

import mesquite.lib.MesquiteTree;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.treecmp.Utils;

public class TreeConverter {
	private interface ITreeCreator<TreeType> {
		TreeType createFrom(String repr);
	}
	
	private static class MesquiteTreeBuilder implements ITreeCreator<mesquite.lib.Tree> {
		private final Taxa taxa;
		
		public MesquiteTreeBuilder() {
			this.taxa = new Taxa(0);
		}
		
		public MesquiteTreeBuilder(Taxa taxa) {
			this.taxa = taxa;
		}

		public Tree createFrom(String repr) {
			final MesquiteTree t = new MesquiteTree(taxa);
			t.setPermitTaxaBlockEnlargement(true);
			t.readTree(repr);
			return t;
		}
	}
	
	private static class PalTreeBuilder implements ITreeCreator<PalFacade.Tree> {
		public PalFacade.Tree createFrom(String s) {
			if (s == null) {
				return null;
			} 
			try {
				return PalFacade.readTree(s);			
			} catch (IOException e) {
				return null;
			}		
		}		
	}

	public static <InTreeType> mesquite.lib.Tree getMesquiteFrom(InTreeType inTree) {
		return convert(inTree, new MesquiteTreeBuilder(), false);
	}
	
	public static <InTreeType> mesquite.lib.Tree getMesquiteFrom(InTreeType inTree, Taxa t) {
		return convert(inTree, new MesquiteTreeBuilder(t), false);
	}
	
	public static <InTreeType> PalFacade.Tree getPalFrom(InTreeType inTree, boolean requiresBranchLengths) {
		return convert(inTree, new PalTreeBuilder(), requiresBranchLengths);
	}
	
	public static String getStringFrom(mesquite.lib.Tree tree, boolean requiresBranchLengths) {
		if (requiresBranchLengths) {
			tree = Utils.setUnassignedBranchLengthsToOne(tree);
		}
		return tree.writeTreeSimpleByNames();
	}

	public static String getStringFrom(PalFacade.Tree tree) {
		return tree.toString();
	}
	
	private static <InTreeType, OutTreeType> OutTreeType convert(InTreeType tree, ITreeCreator<OutTreeType> builder, boolean requiresBranchLengths) {
		String repr = "";
		if (tree instanceof String) {
			repr = (String)tree;
		} else if (tree instanceof mesquite.lib.Tree) {
			repr = getStringFrom((mesquite.lib.Tree) tree, requiresBranchLengths);
		} else if (tree instanceof PalFacade.Tree) {
			repr = getStringFrom((PalFacade.Tree) tree);		
		} else {
			throw new RuntimeException("Don't know what to do with a tree structure class: " + tree.getClass().getName());
		}
		
		return builder.createFrom(repr);
	}
}
