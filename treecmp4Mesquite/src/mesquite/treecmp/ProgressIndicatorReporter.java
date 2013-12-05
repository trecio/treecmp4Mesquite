package mesquite.treecmp;

import mesquite.lib.MesquiteProject;
import mesquite.lib.ProgressIndicator;

class ProgressIndicatorReporter implements AbortableProgressReporter {
	private final ProgressIndicator progressIndicator;

	public ProgressIndicatorReporter(MesquiteProject project, String title,
			int totalProgress) {
		progressIndicator = new ProgressIndicator(project, title, totalProgress);
	}

	public void start() {
		progressIndicator.start();
	}

	public boolean isAborted() {
		return progressIndicator.isAborted();
	}

	public void setCurrentValue(int currentValue) {
		progressIndicator.setCurrentValue(currentValue);
	}

	public void abort() {
		progressIndicator.goAway();
	}
	
}
