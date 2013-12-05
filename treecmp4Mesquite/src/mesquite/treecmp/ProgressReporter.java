package mesquite.treecmp;

public interface ProgressReporter {
	void start();
	boolean isAborted();
	void setCurrentValue(int totalPairsCalculated);
}

interface AbortableProgressReporter extends ProgressReporter {
	void abort();
}
