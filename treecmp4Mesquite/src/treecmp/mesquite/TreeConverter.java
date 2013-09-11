package treecmp.mesquite;

import java.io.IOException;

import mesquite.lib.MesquiteTree;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;

public class TreeConverter {
	private interface ITreeCreator<TreeType> {
		TreeType createFrom(String repr);
	}
	
	private static class MesquiteTreeBuilder implements ITreeCreator<mesquite.lib.Tree> {
		private Taxa taxa;
		
		public MesquiteTreeBuilder() {
			this.taxa = new Taxa(0);
		}
		
		public MesquiteTreeBuilder(Taxa taxa) {
			this.taxa = taxa;
		}

		public Tree createFrom(String repr) {
			MesquiteTree t = new MesquiteTree(taxa);
			t.setPermitTaxaBlockEnlargement(true);
			t.readTree(repr);
			return t;
		}
	}
	
	private static class PalTreeBuilder implements ITreeCreator<PalFacade.Tree> {
		public PalFacade.Tree createFrom(String s) {
			PalFacade.Tree result = null;
			if (s != null) 
				try {
					result = PalFacade.readTree(s);			
				} catch (IOException e) {}
			return result;		
		}		
	}
	
	public static <InTreeType> mesquite.lib.Tree getMesquiteFrom(InTreeType inTree) {
		return convert(inTree, new MesquiteTreeBuilder());
	}
	
	public static <InTreeType> mesquite.lib.Tree getMesquiteFrom(InTreeType inTree, Taxa t) {
		return convert(inTree, new MesquiteTreeBuilder(t));
	}
	
	public static <InTreeType> PalFacade.Tree getPalFrom(InTreeType inTree) {
		return convert(inTree, new PalTreeBuilder());
	}
	
	public static String getStringFrom(mesquite.lib.Tree tree) {
		return tree.writeTreeSimpleByNames();
	}

	public static String getStringFrom(PalFacade.Tree tree) {
		return tree.toString();
	}
	
	private static <InTreeType, OutTreeType> OutTreeType convert(InTreeType tree, ITreeCreator<OutTreeType> builder) {
		String repr = "";
		if (tree instanceof String) {
			repr = (String)tree;
		} else if (tree instanceof mesquite.lib.Tree) {
			repr = getStringFrom((mesquite.lib.Tree) tree);
		} else if (tree instanceof PalFacade.Tree) {
			repr = getStringFrom((PalFacade.Tree) tree);		
		} else {
			throw new RuntimeException("Don't know what to do with a tree structure class: " + tree.getClass().getName());
		}
		
		return builder.createFrom(repr);
	}
}
