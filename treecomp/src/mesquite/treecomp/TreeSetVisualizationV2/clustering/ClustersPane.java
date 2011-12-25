package mesquite.treecomp.TreeSetVisualizationV2.clustering;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.BitSet;
import java.util.Collection;

import mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization.SelectionManager;
import mesquite.treecomp.common.AwtUtils;

public class ClustersPane extends Panel {
	
	private static final long serialVersionUID = -5559088537068603905L;

	public ClustersPane(Collection<Collection<Integer>> clusters, Color[] clusterColors,
			SelectionManager selectionManager) {
		this.clusters = (Collection<Integer>[]) new Collection[clusters.size()];
		this.clusters = clusters.toArray(this.clusters);
		
		this.clusterColors = clusterColors;
		this.selectionManager = selectionManager;		
		
		initialize();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREFERRED_WIDTH, 0);
	}

	private final int PREFERRED_WIDTH = 90;

	private Collection<Integer>[] clusters;	

	private Color[] clusterColors;

	// reference to the selection manager is used to find which points are in
	// the active selection and what their icon is
	private SelectionManager selectionManager;
	
	void initialize() {
		setLayout(new BorderLayout());
		Label clustersLabel = new Label("Clusters:");
		
		add(clustersLabel, BorderLayout.NORTH);
		
		ScrollPane clustersScrollPane = new ScrollPane();
		
		Panel clustersPane = new Panel();
		clustersPane.setLayout(new GridLayout(clusters.length, 2));		

		for (int i=0; i<clusters.length; i++) {
			Label colorPane = new Label(" ");
			colorPane.setBackground(clusterColors[i]);
					
			Label clusterLabel = new Label("C"+i);
			clusterLabel = AwtUtils.HyperlinkBehaviour(clusterLabel, new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					Component sender = (Component)e.getSource();
					int clusterId = Integer.parseInt(sender.getName());
					BitSet selection = bitSetFrom(clusters[clusterId]);
					selectionManager.selectionEvent(selection, 0);
				}
			});
			clusterLabel.setName(""+i);
			
						
			clustersPane.add(colorPane);
			clustersPane.add(clusterLabel);			
		}		
		
		clustersScrollPane.add(clustersPane);
		add(clustersScrollPane, BorderLayout.WEST);
	}
	
	private BitSet bitSetFrom(Collection<Integer> cluster) {
		BitSet result = new BitSet();
		for (Integer index : cluster) 
			result.set(index);
		return result;
	}
}