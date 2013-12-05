package mesquite.treecmp;

class NullProgressReporter implements AbortableProgressReporter {
	public void start() {
	}

	public boolean isAborted() {
		return false;
	}

	public void setCurrentValue(int totalPairsCalculated) {
	}

	public void abort() {
	}

}
