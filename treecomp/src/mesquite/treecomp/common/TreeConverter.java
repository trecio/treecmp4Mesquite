package mesquite.treecomp.common;

import java.io.IOException;

import mesquite.lib.MesquiteTree;

public class TreeConverter {
	public static <InTreeType> mesquite.lib.Tree getMesquiteFrom(InTreeType inTree) {
		return (mesquite.lib.Tree) convert(inTree, TreeType.Mesquite);
	}
	
	public static <InTreeType> PalFacade.Tree getPalFrom(InTreeType inTree) {
		return (PalFacade.Tree) convert(inTree, TreeType.Pal);
	}
	
	public static String getStringFrom(mesquite.lib.Tree tree) {
		return tree.writeTreeSimpleByNames();
	}

	public static String getStringFrom(PalFacade.Tree tree) {
		return tree.toString();
	}
	
	public static <InTreeType> Object convert(InTreeType tree, TreeType outType) {
		String repr = "";
		if (tree instanceof String)
			repr = (String)tree;
		else if (tree instanceof mesquite.lib.Tree)
			repr = getStringFrom((mesquite.lib.Tree) tree);
		else if (tree instanceof PalFacade.Tree)
			repr = getStringFrom((PalFacade.Tree) tree);		
		else
			throw new RuntimeException("Don't know what to do with a tree structure class: " + tree.getClass().getName());
		
		switch (outType) {
		case Mesquite:
			return parseMesquiteFrom(repr);
		case Pal:
			return parsePalFrom(repr);
		case StringRepresentation:
			return repr;
		default:
			throw new RuntimeException("Unsupported tree type: " + outType);
		}
	}
	
	private static mesquite.lib.Tree parseMesquiteFrom(String s) {
		return new MesquiteTree(null, s);
	}
	
	public static PalFacade.Tree parsePalFrom(String s) {
		PalFacade.Tree result = null;
		if (s != null) 
			try {
				result = PalFacade.readTree(s);			
			} catch (IOException e) {}
		return result;		
	}
	
	private enum TreeType {
		StringRepresentation,
		Mesquite,
		Pal
	}
}
