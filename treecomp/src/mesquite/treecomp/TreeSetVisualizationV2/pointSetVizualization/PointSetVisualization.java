/*
 * This software is part of the Tree Set Visualization module for Mesquite,
 * written by Jeff Klingner, Fred Clarke, and Denise Edwards.
 *
 * Copyright (c) 2002 by the University of Texas
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted under the GNU Lesser General 
 * Public License, as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version, 
 * provided that this entire notice is included in all copies of any 
 * software which are or include a copy or modification of this software
 * and in all copies of the supporting documentation for such software.
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR THE UNIVERSITY OF TEXAS
 * AT AUSTIN MAKE ANY REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE 
 * MERCHANTABILITY OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 * IN NO CASE WILL THESE PARTIES BE LIABLE FOR ANY SPECIAL, INCIDENTAL, 
 * CONSEQUENTIAL, OR OTHER DAMAGES THAT MAY RESULT FROM USE OF THIS SOFTWARE.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 *	Last change:  DE   16 Apr 2003   11:18 am
 */

package mesquite.treecomp.TreeSetVisualizationV2.pointSetVizualization;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteBoolean;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.Parser;
import mesquite.lib.Snapshot;
import mesquite.treecomp.TreeSetVisualizationV2.clustering.ClusteringAlgorithmFactory;
import mesquite.treecomp.TreeSetVisualizationV2.clustering.ClustersPane;
import mesquite.treecomp.TreeSetVisualizationV2.clustering.IClusteringAlgorithm;
import mesquite.treecomp.TreeSetVisualizationV2.mds.MDS;
import mesquite.treecomp.TreeSetVisualizationV2.mds.SampledDiffMatrix;

/**
 * Class <code>PointSetVisualization</code> is an abstract superclass for
 * displaying <em>Point Sets</em>. Examples of <em>Point Sets</em> to be
 * displayed could be <em>Taxa Sets</em> or <em>Tree Sets</em>.
 */
public abstract class PointSetVisualization extends MesquiteWindow {

	/* members implementing basic functionality of MDS and point display */
	/** object that actually performs the mds calculations */
	protected MDS mds;
	/** Panel to display the points. */
	protected EmbeddingDisplayPanel embeddingDisplay;
	/**
	 * The big 2D Difference Matrix over all the trees (Computed at
	 * initialization).
	 */
	protected SampledDiffMatrix itemDiffMatrix;
	/** Point locations in the current embedding (Shared with the MDS class). */
	private SharedPoints sharedPoints;
	/** Main thread that controls the MDS loop. */
	private MDSThread mdsThread;
	/** object to handle the details of multiple selection */
	protected SelectionManager selectionManager;
	/** object which creates different clustering algorithms */
	protected final ClusteringAlgorithmFactory factory;
	/** number of items in the sample when sampled-MDS is enabled */
	private int sampleSize;
	/** Dimensionality of the MDS embedding */
	protected static int DIMENSIONS = 2;
	/** constant used to calibrate the stepSize value */
	protected static float STEPSIZE_DIVIDEND = 1.8245f;

	/* members for search animation functionality */
	/** a utility timer thread used to schedule the search animation */
	private java.util.Timer animationTimer;
	/**
	 * The task that actually does the animation, making one more item visible
	 * each time its run() is called
	 */
	private java.util.TimerTask animationTask;
	/** rate at which items appear in the search animation (items per second) */
	private final static int DEFAULT_ANIMATION_RATE = 30;

	/* members for implementing optimality coloring */
	/**
	 * A score for each tree (likelihood, parsimony, etc.) used to compute
	 * colors
	 */
	private double[] optimalityScores;
	/** one color for each point, used when optimality Coloring is activated */
	private Color[] optimalityColors;
	/** one color for each point, used when clustering coloring is activated */	
	private Color[] clusteringPointColors;
	/** a panel that displays the key to the optimality coloring */
	private ColorKey colorKey;
	/** a panel that displays the information about clusters */
	private ClustersPane clustersPane;
	/**
	 * reference to the current mesquite project, used to get the working
	 * directory to read in optimality scores
	 */
	private MesquiteProject project;
	/** color checkpoints used to define ColorGradient */
	private final static Color[] colorsForGradient = { Color.blue, Color.cyan,
			Color.green, Color.yellow, Color.red };
	/** the range of color used in optimality coloring and the corresponding key */
	private final static ColorGradient colorGradient = new ColorGradient(
			colorsForGradient);

	/*
	 * User interface elements to which a reference needs to be maintained
	 * outside of the constructor
	 */

	/** a text field to display the stress of the current embedding */
	private Label stressDisplayLabel;
	/*
	 * The following five interface elements are package-accessible so that the
	 * Mesquite wrapper class, TreeSetVisualization, can control them in
	 * response to Mesquite Commands.
	 */
	private TextField stepSizeField;
	private TextField animationRateField;
	private Checkbox sampleCheckbox;
	private Choice coloringChoice;
	private TextField sampleSizeField;

	/** overall panel containing everything */
	protected Panel pointSetPanel;

	/* Window layout constants */
	/** height of the window in pixels */
	private static int WINDOW_HEIGHT = 643;
	/** width of the window in pixels */
	private static int WINDOW_WIDTH = 500;

	/**
	 * Constructor for initializing objects
	 * 
	 *@param ownerModule
	 *            A reference to the MesquiteModule that utilizes the window.
	 *@param numItems
	 *            the number of items to be visualized (trees, taxa, etc.)
	 */
	public PointSetVisualization(MesquiteModule ownerModule, int numItems, ClusteringAlgorithmFactory clusteringFactory) {
		super(ownerModule, true); // true means the Mesquite info bar will be
									// shown in this window
		final int numberOfItems = numItems; // local constructor copy for
											// accessibilty from inner classes
		factory = clusteringFactory;
		project = ownerModule.getProject();
		
		sampleSize = numberOfItems / 10 + 1; // initial sample size; sampling is
												// off initially
		sharedPoints = new SharedPoints(numberOfItems, DIMENSIONS);
		itemDiffMatrix = new SampledDiffMatrix(numberOfItems);
		mds = new MDS(itemDiffMatrix, DIMENSIONS, STEPSIZE_DIVIDEND
				/ numberOfItems);
		sharedPoints.setPoints(mds.getEmbedding());
		selectionManager = new SelectionManager(numberOfItems, this);
		embeddingDisplay = new EmbeddingDisplayPanel(selectionManager,
				sharedPoints);
		selectionManager.setEmbeddingDisplay(embeddingDisplay);
		selectionManager.setColorKey(null);

		/* Set up the MDS loop control thread */
		mdsThread = new MDSThread(mds, sharedPoints, this);
		// set the MDS thread to a lower priority than the parent (UI) thread to
		// ensure a snappy interface
		mdsThread.setPriority(Thread.currentThread().getPriority() - 1);
		// Start the MDSthread here, on initialization. waitFlag==true will
		// cause it to suspend
		// before doing any calculations. It will resume when notify() is called
		// in response
		// to a push of the start button.
		mdsThread.waitFlag = true;
		mdsThread.start();

		// Set up the timer thread used for tree search animation
		animationTimer = new java.util.Timer();

		// We will read the optimality scores file and compute the colors only
		// when asked.
		optimalityColors = null;
		optimalityScores = null;

		/*
		 * ############################################################### ##
		 * User interface definitions. Buttons, textfields, panels, etc. ## are
		 * intitialized, and user interface reactions are defined
		 * #################################################################
		 */
		/*
		 * button used to start and suspend the MDS caluclations. final so it
		 * can refer to itself
		 */
		final Button startStopButton = new Button("Start MDS");
		startStopButton.setActionCommand("start");
		startStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("start")) {
					// Start MDS
					synchronized (mdsThread) {
						// Set the mds thread to run at a lower priority than
						// the user interface thread.
						if (mdsThread.getPriority() >= Thread.currentThread()
								.getPriority()) {
							mdsThread.setPriority(Thread.currentThread()
									.getPriority() - 1);
						}
						mdsThread.waitFlag = false;
						mdsThread.notify();
					}
					startStopButton.setActionCommand("stop");
					startStopButton.setLabel("Stop MDS");
				} else {
					// Stop MDS
					mdsThread.waitFlag = true;// signal the mds thread to wait()
												// at its next oportunity
					startStopButton.setActionCommand("start");
					startStopButton.setLabel("Start MDS");
				}
				startStopButton.repaint();

				// System.out.println("Start/Stop pressed. Priority = " +
				// Thread.currentThread().getPriority());
			}
		});

		/* button to randomize the embedding (to start MDS over) */
		Button scrambleButton = new Button("Scramble");
		scrambleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mds.randomize_nodes();
				sharedPoints.setPoints(mds.getEmbedding());
				embeddingDisplay.repaint();
			}
		});

		/*
		 * field to show the current step size and make it adjustable. final so
		 * that it can be referred to by other interface elements (sampling &
		 * plus/minus buttons)
		 */
		stepSizeField = new TextField(7) {
			public void setText(String text) { // a new definition of setText()
												// used to truncate the
												// displayed text
				if (text.length() > 8) {
					if (text.charAt(text.length() - 3) == 'E') { // scientific
																	// notation,
																	// insignificant
																	// figs in
																	// the
																	// middle
						super.setText(text.substring(0, 5)
								+ text.substring(text.length() - 3, text
										.length()));
					} else { // not scientific notation, insignificant figs at
								// the end
						super.setText(text.substring(0, 8));
					}
				} else {
					super.setText(text);
				}
			}
		};
		stepSizeField
				.setText(Float.toString(STEPSIZE_DIVIDEND / numberOfItems));
		stepSizeField.setBackground(Color.white);
		stepSizeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * First, check to see if they entered a legal step size. For
				 * now, that just means its a number. Any number.
				 */
				String enteredText = stepSizeField.getText();
				float newValue;
				try {
					newValue = Float.parseFloat(enteredText);
				} catch (NumberFormatException nfe) {
					newValue = mds.getStepSize();
				}
				if (newValue < 0) {
					newValue = 0;
				}
				mds.setStepSize(newValue);
				stepSizeField.setText(Float.toString(newValue));
				stepSizeField.selectAll();
			}
		});

		/* a small '+' button to increase the step size a little */
		Button incStepSizeButton = new Button("+");
		incStepSizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float d = mds.getStepSize();
				d *= 1.1;
				stepSizeField.setText(Float.toString(d));
				stepSizeField.selectAll();
				mds.setStepSize(d);
			}
		});

		/* a small '-' button to decrease the step size a little */
		Button decStepSizeButton = new Button("-");
		decStepSizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float d = mds.getStepSize();
				d *= (1 / (1.1));
				stepSizeField.setText(Float.toString(d));
				stepSizeField.selectAll();
				mds.setStepSize(d);
			}
		});

		/*
		 * a text field where the user can set the sample size. Only enabled if
		 * sampling is enabled.
		 */
		sampleSizeField = new TextField("0", 5);
		sampleSizeField.setBackground(Color.white);
		sampleSizeField.setEnabled(false);
		sampleSizeField.setEditable(false);
		sampleSizeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// First, check to see if they entered a legal sample size.
				boolean legalEntry = true;
				int newValue = 0;
				try {
					newValue = Integer.parseInt(sampleSizeField.getText());
				} catch (NumberFormatException nfe) {
					legalEntry = false;
				}
				if (newValue < 1
						|| newValue > itemDiffMatrix.getNumberOfItems()) {
					legalEntry = false;
				}
				if (legalEntry) {
					sampleSize = newValue;
					sampleSizeField.setText(Integer.toString(newValue));
					itemDiffMatrix.sampleByPoint(newValue);
					// get sample set
					BitSet sampleSet = new BitSet(itemDiffMatrix
							.getNumberOfItems());
					for (int i = 0; i < itemDiffMatrix.getNumberOfItems(); ++i) {
						if (itemDiffMatrix.pointInSample(i)) {
							sampleSet.set(i);
						}
					}
					// send sample set to embedding display
					embeddingDisplay.setSample(sampleSet);
					embeddingDisplay.repaint();
					mds.setStepSize(STEPSIZE_DIVIDEND / newValue);
					stepSizeField.setText(Float.toString(STEPSIZE_DIVIDEND
							/ newValue));
				} else {
					// Illegal input; reset the field to contain the old sample
					// size
					sampleSizeField.setText(Integer.toString(sampleSize));
				}
				sampleSizeField.selectAll();
			}
		});

		/* a check boxed used to enable/disable sampling. */
		sampleCheckbox = new Checkbox("Sampled MDS", false);
		sampleCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				BitSet sampleSet = new BitSet(itemDiffMatrix.getNumberOfItems());
				if (sampleCheckbox.getState()) {// Box is checked
					itemDiffMatrix.sampleByPoint(sampleSize);
					sampleSizeField.setEnabled(true);
					sampleSizeField.setEditable(true);
					sampleSizeField.setText(Integer.toString(sampleSize));
					sampleSizeField.selectAll();
					for (int i = 0; i < itemDiffMatrix.getNumberOfItems(); ++i) {
						if (itemDiffMatrix.pointInSample(i)) {
							sampleSet.set(i);
						}
					}
					mds.setStepSize(STEPSIZE_DIVIDEND / sampleSize);
					stepSizeField.setText(Float.toString(STEPSIZE_DIVIDEND
							/ sampleSize));
				} else { // Box is unchecked
					itemDiffMatrix.disableSampling();
					sampleSizeField.setEnabled(false);
					sampleSizeField.setEditable(false);
					// for (int i = 0; i < sampleSet.length(); ++i) {
					// sampleSet.clear(i);
					// }
					mds.setStepSize(STEPSIZE_DIVIDEND / numberOfItems);
					stepSizeField.setText(Float.toString(STEPSIZE_DIVIDEND
							/ numberOfItems));
				}
				embeddingDisplay.setSample(sampleSet);
				embeddingDisplay.repaint();
			}
		});

		/* Panel that holds the MDS Controls and the Embedding Display Panel. */
		// declared here so that optimalityColoringCheckbox can refer to it
		final Panel mainPanel = new Panel(new BorderLayout(0, 0));
		
		coloringChoice = new Choice();
		final String NONE = "None";
		final String OPTIMALITY = "Optimality";
		final String CLUSTERS = "Clusters";
		coloringChoice.add(NONE);
		coloringChoice.add(OPTIMALITY);
		coloringChoice.add(CLUSTERS);
		coloringChoice.addItemListener(new ItemListener() {			
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {					
					setNoColoring(e.getItem() == NONE);
					setOptimalityColoring(e.getItem() == OPTIMALITY);	
					setClusteringColoring(e.getItem() == CLUSTERS);
				}
			}

			private void setOptimalityColoring(boolean state) {
				if (state
						&& (optimalityColors == null)) {	// This is the first time
														// coloring has been
														// activated
					readOptimalityScores(numberOfItems);
					if (optimalityScores != null) {
						colorKey = new ColorKey(optimalityScores,
								colorGradient, selectionManager);
						selectionManager.setColorKey(colorKey);
					}
				}
				// If score reading fails, the checkbox will be set back to
				// false and optimalityColors will
				// still be null, so the following lines won't have any effect.
				embeddingDisplay
						.setPointColoring(state);
				embeddingDisplay.setPointColors(optimalityColors);
				embeddingDisplay.repaint();
				if (colorKey != null) {
					if (state) {
						mainPanel.add(colorKey, BorderLayout.EAST);
					} else {
						mainPanel.remove(colorKey);
					}
					// took out a call to old selectionChanged() method here;
					// not sure what it was for
					organizeDisplay();
				}
			}
			
			public void setClusteringColoring(boolean state) {
				if (!state) {
					if (clustersPane != null) {
						mainPanel.remove(clustersPane);
						organizeDisplay();
					}
					return;
				}
				
				if (clusteringPointColors == null) {
					IClusteringAlgorithm clusterer = factory.create();		
					if (clusterer == null) {
						coloringChoice.select(0);
						return;
					}
					
					Collection<Collection<Integer>> clusters = clusterer.computeClusters(itemDiffMatrix);

					Color[] clusterColors = setupClusterColors(clusters);
					clustersPane = new ClustersPane(clusters, clusterColors, selectionManager);
				}
				mainPanel.add(clustersPane, BorderLayout.EAST);
				embeddingDisplay.setPointColoring(true);
				embeddingDisplay.setPointColors(clusteringPointColors);
				embeddingDisplay.repaint();
				
				organizeDisplay();
			}
			
			public void setNoColoring(boolean state) {
				embeddingDisplay.setPointColoring(false);
				embeddingDisplay.setPointColors(null);
			}
		});

		/* Used to control the rate of the tree search animation */
		animationRateField = new TextField(Integer
				.toString(DEFAULT_ANIMATION_RATE), 4);
		animationRateField.setBackground(Color.white);

		/* Button used to start the tree search animation */
		Button animateButton = new Button("Animate Tree Search");
		animateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// In case an animation is already underway, kill it.
				animationTimer.cancel();
				animationTimer = new java.util.Timer();

				// Make a new task to run the animation
				animationTask = new java.util.TimerTask() {
					int runCount = 0;
					final int lastRun = numberOfItems;

					// The run method is called periodically by the timer
					// thread.
					public void run() {
						if (runCount < lastRun) {
							// Make one more point visible and call for a
							// re-draw.
							embeddingDisplay.setLastShown(runCount);
							embeddingDisplay.repaint();
							if (colorKey != null) {
								colorKey.setLastShown(runCount);
								colorKey.repaint();
							}
							runCount++;
						} else {
							// Remove the current task from execution ability by
							// the timer.
							cancel();
							animationRateField.setEnabled(true);
							animationRateField.setEditable(true);
						}
					}
				};

				boolean illegalRate = false;
				int animationRate = DEFAULT_ANIMATION_RATE;
				try {
					animationRate = Integer.parseInt(animationRateField
							.getText());
				} catch (NumberFormatException nfe) {
					illegalRate = true;
				}
				if (illegalRate || animationRate < 1 || animationRate > 1000) {
					animationRate = DEFAULT_ANIMATION_RATE;
					animationRateField.setText(Integer
							.toString(DEFAULT_ANIMATION_RATE));
				}

				// Start the animation -- It will kill itself when it is
				// finished.
				animationTimer.scheduleAtFixedRate(animationTask, 0,
						1000 / animationRate);
				// The rate cannot be changed while animation is running.
				animationRateField.setEnabled(false);
				animationRateField.setEditable(false);
			}
		});

		stressDisplayLabel = new Label("", Label.LEFT) {
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				if (d.width <= 60) {
					d.width = 60;
				}
				return d;
			}
		};

		// Build the organization of the control panel
		Panel sssPanel = new Panel(new GridLayout(2, 1));
		sssPanel.add(startStopButton);
		sssPanel.add(scrambleButton);
		Panel sssFlowPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		sssFlowPanel.add(sssPanel);

		Panel stressPanel = new Panel(new GridLayout(2, 1));
		stressPanel.add(new Label("Stress:", Label.CENTER));
		stressPanel.add(stressDisplayLabel);
		Panel stressFlowPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 0,
				0));
		stressFlowPanel.add(stressPanel);

		Panel plusMinusPanel = new Panel(new GridLayout(2, 1));
		plusMinusPanel.add(incStepSizeButton);
		plusMinusPanel.add(decStepSizeButton);
		Panel stepSizePanel = new Panel(new GridLayout(2, 1));
		stepSizePanel.add(new Label("Step Size:", Label.CENTER));
		stepSizePanel.add(stepSizeField);
		Panel stepSizeLocalFlow = new Panel();
		stepSizeLocalFlow.add(stepSizePanel);
		Panel stepSizeBorderPanel = new Panel(new BorderLayout());
		stepSizeBorderPanel.add(stepSizeLocalFlow, BorderLayout.CENTER);
		stepSizeBorderPanel.add(plusMinusPanel, BorderLayout.EAST);
		Panel stepSizeFlowPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 0,
				0));
		stepSizeFlowPanel.add(stepSizeBorderPanel);

		Panel samplePanel = new Panel(new GridLayout(2, 1));
		samplePanel.add(sampleCheckbox);
		Panel sampleSizePanel = new Panel(new FlowLayout(FlowLayout.CENTER, 0,
				0));
		sampleSizePanel.add(new Label("Sample Size:", Label.RIGHT));
		sampleSizePanel.add(sampleSizeField);
		samplePanel.add(sampleSizePanel);
		Panel sampleFlowPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
		sampleFlowPanel.add(samplePanel);

		Panel leftMdsControlsPanel = new Panel(new FlowLayout(
				FlowLayout.CENTER, 0, 0));
		leftMdsControlsPanel.add(sssFlowPanel);
		leftMdsControlsPanel.add(stressFlowPanel);

		Panel mdsControlsPanel = new Panel(new BorderLayout(3, 1));
		mdsControlsPanel.add(leftMdsControlsPanel, BorderLayout.WEST);
		mdsControlsPanel.add(sampleFlowPanel, BorderLayout.CENTER);
		mdsControlsPanel.add(stepSizeFlowPanel, BorderLayout.EAST);

		Component mdsLabel = new Component() {
			public Dimension getPreferredSize() {
				return new Dimension(18, 0);
			}

			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(getBackground());
				g2.fillRect(0, 0, getBounds().width, getBounds().height);
				g2.setColor(Color.white);
				g2.setFont(new Font(g2.getFont().getFontName(), Font.BOLD, 16));
				java.awt.geom.Rectangle2D rec = g2.getFont().getStringBounds(
						"MDS", g2.getFontRenderContext());
				int x = 15;
				int y = (getHeight() + rec.getBounds().width) / 2;
				java.awt.geom.AffineTransform savedTransformation = g2
						.getTransform();
				g2.transform(java.awt.geom.AffineTransform.getRotateInstance(
						-Math.PI / 2, x, y));
				g2.drawString("MDS", x, y);
				g2.setTransform(savedTransformation);
			}
		};
		mdsLabel.setBackground(Color.black);

		Panel overallMdsPanel = new Panel(new BorderLayout(0, 1));
		overallMdsPanel.add(mdsLabel, BorderLayout.WEST);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 5, 0, 5); // (top,left,bottom,right)
		Panel marginBagPanel = new Panel(new GridBagLayout());
		marginBagPanel.add(mdsControlsPanel, c);
		marginBagPanel.setBackground(getBackground());

		c.insets = new Insets(1, 0, 1, 1); // (top,left,bottom,right)
		Panel mdsBagPanel = new Panel(new GridBagLayout());
		mdsBagPanel.setBackground(Color.black);
		mdsBagPanel.add(marginBagPanel, c);

		overallMdsPanel.add(mdsBagPanel, BorderLayout.CENTER);

		Panel bottomControlsPanel = new Panel(new FlowLayout(FlowLayout.CENTER,
				10, 1));
		bottomControlsPanel.add(coloringChoice);
		Panel animationFlowPanel = new Panel(new FlowLayout(FlowLayout.CENTER,
				3, 3));
		animationFlowPanel.add(animateButton);
		animationFlowPanel.add(new Label("rate:", Label.RIGHT));
		animationFlowPanel.add(animationRateField);
		animationFlowPanel.add(new Label("items/sec", Label.LEFT));
		bottomControlsPanel.add(animationFlowPanel);

		Panel overallControlsPanel = new Panel(new BorderLayout(0, 0));
		overallControlsPanel = new Panel(new BorderLayout(0, 0));
		overallControlsPanel.add(overallMdsPanel, BorderLayout.CENTER);
		overallControlsPanel.add(bottomControlsPanel, BorderLayout.SOUTH);

		/* for layout with the postscript button */

		mainPanel.add(embeddingDisplay, BorderLayout.CENTER);
		mainPanel.add(overallControlsPanel, BorderLayout.NORTH);

		Panel everythingPanel = new Panel(new BorderLayout(0, 3));
		everythingPanel.add(mainPanel, BorderLayout.CENTER);
		everythingPanel.add(selectionManager, BorderLayout.SOUTH);

		Panel borderBagPanel = new Panel();
		borderBagPanel.setLayout(new GridBagLayout());
		// an insets object is (top,left,bottom,right)
		c.insets = new Insets(10, 10, 3, 10);
		borderBagPanel.add(everythingPanel, c);

		pointSetPanel = new Panel();
		pointSetPanel.setLayout(new GridLayout(1, 1));
		pointSetPanel.add(borderBagPanel);

		addToWindow(pointSetPanel);

		resetTitle();
		setWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocation(20, 50);
		toFront();
	}

	public Snapshot getSnapshot(MesquiteFile file) {
		Snapshot windowSnapshot = super.getSnapshot(file);
		windowSnapshot.addLine("setAnimationRate "
				+ animationRateField.getText());
		windowSnapshot.addLine("setColoring "
				+ coloringChoice.getSelectedIndex());
		windowSnapshot.addLine("setSampling "
				+ MesquiteBoolean.toOffOnString(sampleCheckbox.getState()));
		windowSnapshot.addLine("setSampleSize " + sampleSizeField.getText());
		windowSnapshot.addLine("setStepSize " + stepSizeField.getText());
		return windowSnapshot;
	}

	public Object doCommand(String commandName, String arguments,
			CommandChecker checker) {
		/*
		 * Commands to set the state of MDS and the window. To avoid duplication
		 * of code and of error checking, these five commands are implemented by
		 * mimicking user input. They manipulate user interface elements and
		 * then fire off action events on those elements to induce the needed
		 * state changes in the module.
		 */
		Parser parser = new Parser();
		if (checker.compare(this.getClass(),
				"turns optimality coloring on or off", "[on; off]",
				commandName, "setColoring")) {
			coloringChoice.select(parser.getFirstToken(arguments));
			ItemListener optimalityColoringListener = (ItemListener) coloringChoice
					.getListeners(ItemListener.class)[0];
			if (optimalityColoringListener != null) {
				optimalityColoringListener.itemStateChanged(null);
			}
			return null;
		} else if (checker.compare(this.getClass(), "turns sampling on or off",
				"[on; off]", commandName, "setSampling")) {
			sampleCheckbox.setState(MesquiteBoolean.fromOffOnString(parser
					.getFirstToken(arguments)));
			ItemListener samplingListener = (ItemListener) sampleCheckbox
					.getListeners(ItemListener.class)[0];
			if (samplingListener != null) {
				samplingListener.itemStateChanged(null);
			}
			return null;
		} else if (checker.compare(this.getClass(), "sets the sample size",
				"[sample size]", commandName, "setSampleSize")) {
			sampleSizeField.setText(parser.getFirstToken(arguments));
			ActionListener sampleSizeListener = (ActionListener) sampleSizeField
					.getListeners(ActionListener.class)[0];
			if (sampleSizeListener != null) {
				sampleSizeListener.actionPerformed(null);
			}
			return null;
		} else if (checker.compare(this.getClass(), "sets the step size",
				"[step size]", commandName, "setStepSize")) {
			stepSizeField.setText(parser.getFirstToken(arguments));
			ActionListener stepSizeListener = (ActionListener) stepSizeField
					.getListeners(ActionListener.class)[0];
			if (stepSizeListener != null) {
				stepSizeListener.actionPerformed(null);
			}
			return null;
		} else if (checker.compare(this.getClass(), "sets the animation rate",
				"[animation rate]", commandName, "setAnimationRate")) {
			animationRateField.setText(parser.getFirstToken(arguments));
			/* take no action - we don't want to actually start an animation. */
			return null;
		} else {
			/*
			 * All other commands are passed to our superclass (MesquiteWindow)
			 * for handling.
			 */
			return super.doCommand(commandName, arguments, checker);
		}
	}

	public void resetNumberOfItems(int newNumberOfItems) {
		sampleSize = newNumberOfItems / 10 + 1; // new default sample size;
		sharedPoints.resetNumberOfPoints(newNumberOfItems);
		itemDiffMatrix.resetNumberOfItems(newNumberOfItems);
		mds.resetNumberOfItems(newNumberOfItems);
		mds.setStepSize(STEPSIZE_DIVIDEND / newNumberOfItems);
		sharedPoints.setPoints(mds.getEmbedding());
		selectionManager.resetNumberOfItems(newNumberOfItems, this);
		embeddingDisplay.resetNumberOfItems(newNumberOfItems);
		embeddingDisplay.repaint();
	}

	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	/**
	 * Called when the Points Set Module is cleaning up. It asks the MDS Thread
	 * to exit cleanly
	 */
	public void haltThreads() {
		mdsThread.exitFlag = true;
	}

	/**
	 * Computes the contents of the big difference matrix.
	 * 
	 * @return true if the calulation was completed, false if it is cancelled by
	 *         the user before completion.
	 */
	public abstract boolean computeDM();

	protected abstract void newSelection(java.util.BitSet selection,
			String selectionName);

	protected abstract void updateSelection(java.util.BitSet selection,
			String selectionName);

	protected abstract void removeSelection(String selectionName);

	protected abstract void activateSelection(String selectionName);

	/** This is called when the window is resized by the user. */
	public void windowResized() {
		organizeDisplay();
	}

	/**
	 * This is called after the window is resized to layout the new window size
	 * and its components.
	 */
	public void organizeDisplay() {
		pointSetPanel.setBounds(0, 0, getWidth(), getHeight());
		pointSetPanel.validate();
		pointSetPanel.repaint();
		// For layout debugging:
		// pointSetPanel.list();
	}

	/**
	 * This method set the stress size of the current embedding.
	 * 
	 *@param stress
	 *            The indicated size of the stress field.
	 */
	protected void setStress(float stress) {
		stressDisplayLabel.setText(Float.toString(stress));
		stressDisplayLabel.repaint();
	}

	/**
	 * Called every time the MDS Thread completes an iteration of MDS. Updates
	 * the stress display and tells the embedding display to repaint itself (by
	 * accessing the shared points to get the new embedding put there by MDS)
	 */
	protected void mds_iteration_complete() {
		setStress(sharedPoints.getStress());
		embeddingDisplay.repaint();
	}

	private void readOptimalityScores(int numberOfScores) {
		optimalityScores = new double[numberOfScores];
		java.io.FileReader input;

		FileDialog scoresFileDialog = new FileDialog(new Frame());
		scoresFileDialog
				.setTitle("Which optimality scores do you want to use?\n "
						+ "You need to pick a file formated like a MrBayes .p file, "
						+ "a tab-separated layout with one line of column headings and "
						+ "the optimality scores in the second column.  It must have at "
						+ " least as many entries as there are trees you want to color.");
		scoresFileDialog.setMode(FileDialog.LOAD);
		scoresFileDialog.setVisible(true);
		/*
		 * at this point, the file dialog becomes visible and the user chooses a
		 * file
		 */
		boolean userPushedCancel = (scoresFileDialog.getFile() == null);
		boolean inputSuccess = false;

		if (!userPushedCancel) {
			java.io.File scoresFile = new java.io.File(scoresFileDialog
					.getDirectory(), scoresFileDialog.getFile());
			try {
				// System.out.print("Opening " + scoresFile + "...");
				input = new java.io.FileReader(scoresFile);// open scores file
				Scanner s = new Scanner(input);
				s.useLocale(Locale.US);
				// System.out.println("success!");
				s.nextLine();
				// System.out.println("Skipped column headings.");
				
				Locale.setDefault(Locale.US);
				Pattern pattern = Pattern.compile("[^\t]+");
				for (int i = 0; i < numberOfScores; ++i) {
					String skip = s.next(pattern);
					// System.out.print("skiped first column...");
					// read in the score
					optimalityScores[i] = s.nextDouble();
					// System.out.print("read score: " + currentScore + "...");
					// skip the rest of the line
					
					skip = s.nextLine();
					// System.out.println("skipped the rest of the line");
				}
				input.close();
				inputSuccess = true;
			} catch (java.io.FileNotFoundException excep) {
				System.out
						.println("Couldn't open file with optimality scores: "
								+ excep.getMessage());
			} catch (NumberFormatException excep) {
				System.out.println("Error parsing optimality value: "
						+ excep.getMessage());
			} catch (java.io.IOException excep) {
				System.out
						.println("Error reading character of optimality scores file: "
								+ excep.getMessage());
			}
		} // if user didn't press cancel

		if (inputSuccess) {
			// Translate optimality scores into colors
			optimalityColors = new Color[optimalityScores.length];

			// Find the maximum and minimum optimalities
			double max = optimalityScores[0];
			double min = optimalityScores[0];
			for (int i = 0; i < optimalityScores.length; ++i) {
				max = Math.max(optimalityScores[i], max);
				min = Math.min(optimalityScores[i], min);
			}

			// Transform range of scores to [0..1] and translate them to colors
			double transformedScore;
			for (int i = 0; i < optimalityScores.length; ++i) {
				transformedScore = optimalityScores[i] - min;// translate
				if (max > min) {
					transformedScore /= (max - min);// scale
				}
				optimalityColors[i] = colorGradient
						.computeColor(transformedScore);
			}
		} else {
			optimalityScores = null;
			coloringChoice.select(0);
		}
	}// readOptimalityScores
	
	private Color[] setupClusterColors(Collection<Collection<Integer>> clusters) {
			
		clusteringPointColors = new Color[itemDiffMatrix.getNumberOfItems()];
		Color[] clusterColors = new Color[clusters.size()];
		
		double colorFactor = 1 / Math.E;
		int i=0;
		for (Collection<Integer> cluster : clusters) {
			clusterColors[i] = colorGradient.computeColor(colorFactor);
			for (Integer pointIndex : cluster)
				clusteringPointColors[pointIndex] = clusterColors[i];
			
			colorFactor += 1 / Math.E;
			if (colorFactor >= 1)
				colorFactor -= 1;
			i++;
		}
		
		return clusterColors;
	}

	public void saveAsPostscript() {
		FileDialog psFileDialog = new FileDialog(new Frame());
		psFileDialog.setTitle("Where do you want to save the postscript?");
		psFileDialog.setMode(FileDialog.SAVE);
		psFileDialog.setVisible(true);
		/*
		 * at this point, the file dialog becomes visible and the user chooses a
		 * file
		 */

		boolean userPushedCancel = (psFileDialog.getFile() == null);
		if (!userPushedCancel) {
			try {
				/*
				 * Open the output file and tell the embedding display to draw
				 * into it
				 */
				java.io.File postscriptFile = new java.io.File(psFileDialog
						.getDirectory(), psFileDialog.getFile());
				java.io.PrintWriter postscriptOutputStream = new java.io.PrintWriter(
						new java.io.FileWriter(postscriptFile));
				embeddingDisplay.drawInPostscript(postscriptOutputStream);
				postscriptOutputStream.close();
			} catch (java.io.IOException excep) {
				System.out.println("Trouble opening file to write postscript");
			}
		}
	}

	/*
	 * This routine uses the Java API to print the embedding. We abandoned it
	 * because the output was rasterized.
	 */
	/*
	 * public void printAsPostscript() { java.awt.print.PrinterJob printJob =
	 * java.awt.print.PrinterJob.getPrinterJob(); printJob.setPrintable(new
	 * java.awt.print.Printable() { public int print (Graphics g,
	 * java.awt.print.PageFormat pageFormat, int page) {
	 * embeddingDisplay.paint(g); if (page == 0) { return
	 * java.awt.print.Printable.PAGE_EXISTS; } else { return
	 * java.awt.print.Printable.NO_SUCH_PAGE; } } }); java.awt.print.Paper
	 * tempPaper = new java.awt.print.Paper(); java.awt.print.PageFormat
	 * pageFormat = new java.awt.print.PageFormat();
	 * pageFormat.setPaper(tempPaper); if (printJob.printDialog()) { try {
	 * printJob.print(); } catch (java.awt.print.PrinterException excep) {
	 * System.out.println("Printer exception: " + excep.getMessage()); } } }
	 */
}





