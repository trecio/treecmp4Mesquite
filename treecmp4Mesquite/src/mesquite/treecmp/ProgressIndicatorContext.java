package mesquite.treecmp;

import java.util.Stack;

import mesquite.lib.MesquiteProject;

public class ProgressIndicatorContext {	
	private static final Stack<AbortableProgressReporter> progressReporters = new Stack<AbortableProgressReporter>();
	
	private ProgressIndicatorContext() {}

	public static void exit() {
		final AbortableProgressReporter reporter = progressReporters.pop();
		reporter.abort();
	}

	public static ProgressReporter enterFor(MesquiteProject project,
			String title, int totalProgress) {
		final AbortableProgressReporter reporter = progressReporters.empty()
				? new ProgressIndicatorReporter(project, title, totalProgress)
				: new NullProgressReporter();
		progressReporters.add(reporter);
		return reporter;
	}
}
