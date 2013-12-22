package mesquite.treecmp.clustering.TreeClustering;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteString;
import mesquite.lib.Tree;
import mesquite.lib.TreeVector;
import mesquite.lib.Trees;
import mesquite.lib.duties.DistanceBetween2Trees;
import mesquite.treecmp.Utils;

import org.apache.commons.lang.StringUtils;

public class PrecomputedDistanceBetween2Trees extends DistanceBetween2Trees {
	private DistanceBetween2Trees distance;
	private Map<Pair<Tree, Tree>, Double> cache = Collections.emptyMap();
	private MesquiteProject project;
	private Trees trees;

	@Override
	public void calculateNumber(Tree t1, Tree t2, MesquiteNumber result,
			MesquiteString resultString) {
		final Double value = getDistance(t1, t2);
		result.setValue(value);
	}

	@Override
	public String getName() {
		return distance.getName();
	}

	@Override
	public void initialize(Tree t1, Tree t2) {
	}

	public void persist() {
		final MesquiteFile cacheFile = tryGuessCacheFileName(project, trees,
				distance);
		final double[][] distances = toMatrix(trees);
		if (cacheFile != null) {
			cacheFile.openWriting(true);
			try {
				Utils.exportToFile(distances, cacheFile);
			} finally {
				cacheFile.closeWriting();
			}
		}
	}

	public boolean prepare(MesquiteProject project, DistanceBetween2Trees distance, Trees trees) {
		this.distance = distance;
		this.project = project;
		this.trees = trees;
		final MesquiteFile cacheFile = tryGuessCacheFileName(project, trees, distance);
		final double[][] distances;
		if (cacheFile != null && MesquiteFile.fileExists(cacheFile.getPath())) {
			if (cacheFile.openReading()) {
				final double[][] readDistances;
				try {
					readDistances = readFrom(cacheFile);
				} finally {
					cacheFile.closeReading();
				}
				distances = verify(readDistances, distance, trees)
						? readDistances
						: Utils.calculateDistanceMatrix(distance, trees, project);
			} else {
				distances = Utils.calculateDistanceMatrix(distance, trees, project);
			}
		} else {
			distances = Utils.calculateDistanceMatrix(distance, trees, project);
		}
		cache = toMap(trees, distances);
		
		return true;
	}

	@Override
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
		return true;
	}

	private double getDistance(Tree t1, Tree t2) {
		final Double value = cache.get(new Pair<Tree, Tree>(t1, t2));
		final MesquiteNumber number = new MesquiteNumber();
		if (value != null) {
			return value;
		} else {
			distance.initialize(t1, t2);
			distance.calculateNumber(t1, t2, number, null);
			return number.getDoubleValue();
		}
	}

	private double[][] readFrom(MesquiteFile file) {
		final List<double[]> results = new ArrayList<double[]>();
		String line;
		while (!StringUtils.isBlank(line = file.readNextDarkLine())) {
			final StringTokenizer tokenizer = new StringTokenizer(line);
			final double[] row = new double[tokenizer.countTokens()];
			for (int i = 0; i < row.length; i++) {
				row[i] = Double.parseDouble(tokenizer.nextToken());
			}
			results.add(row);
		}
		return results.toArray(new double[0][]);
	}

	private Map<Pair<Tree, Tree>, Double> toMap(Trees trees,
			final double[][] distances) {
		final Map<Pair<Tree, Tree>, Double> result = new HashMap<Pair<Tree, Tree>, Double>();
		for (int i = 0; i < trees.size(); i++) {
			final Tree t1 = trees.getTree(i);
			for (int j = 0; j < trees.size(); j++) {
				final Tree t2 = trees.getTree(j);
				result.put(new Pair<Tree, Tree>(t1, t2),
						distances[t1.getFileIndex()][t2.getFileIndex()]);
			}
		}
		return result;
	}

	private double[][] toMatrix(Trees trees) {
		final int numberOfTrees = trees.size();
		final double[][] result = new double[numberOfTrees][];
		for (int i = 0; i < trees.size(); i++) {
			final Tree t1 = trees.getTree(i);
			result[i] = new double[numberOfTrees];
			for (int j = 0; j < trees.size(); j++) {
				final Tree t2 = trees.getTree(j);
				result[t1.getFileIndex()][t2.getFileIndex()] = getDistance(t1,
						t2);
			}
		}
		return result;
	}

	private static boolean treeVectorsEqualOrderInsensitive(Trees left,
			Trees right) {
		final int numberOfTrees = left.size();
		if (numberOfTrees != right.size()) {
			return false;
		}
		for (int i = 0; i < numberOfTrees; i++) {
			final Tree leftTree = left.getTree(i);
			final Tree rightTree = right.getTree(i);
			if (leftTree != rightTree) {
				return false;
			}
		}
		return true;
	}

	private static MesquiteFile tryGuessCacheFileName(MesquiteProject project,
			Trees trees, DistanceBetween2Trees distance) {
		final int numberOfProjectTreeVectors = project
				.getNumberOfFileElements(TreeVector.class);
		for (int i = 0; i < numberOfProjectTreeVectors; i++) {
			final TreeVector projectTreeVector = (TreeVector) project
					.getFileElement(TreeVector.class, i);
			if (treeVectorsEqualOrderInsensitive(trees, projectTreeVector)) {
				final String filePath = projectTreeVector.getFile().getPath()
						+ ".precomputed." + distance.getClass().getSimpleName()
						+ ".csv";
				final File file = new File(filePath);
				return MesquiteFile.newFile(file.getParent() + "/",
						file.getName());
			}
		}
		return null;
	}

	private boolean verify(double[][] distances,
			DistanceBetween2Trees distance, Trees trees) {
		MesquiteNumber number = new MesquiteNumber();
		MesquiteString string = new MesquiteString();
		for (int i = trees.size() - 1; i > 1; i--) {
			final Tree t1 = trees.getTree(i);
			final Tree t2 = trees.getTree(i - 1);
			distance.calculateNumber(t1, t2, number, string);
			if (distances[t1.getFileIndex()][t2.getFileIndex()] != number
					.getDoubleValue()) {
				return false;
			}
		}
		return true;
	}

}
