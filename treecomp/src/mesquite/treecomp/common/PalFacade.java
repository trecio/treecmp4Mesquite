package mesquite.treecomp.common;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mesquite.trunk.ClassPathHacker;

import org.xeustechnologies.jcl.JarClassLoader;

public class PalFacade {
	
	public static Tree readTree(String s) throws IOException {
		try {
			return new Tree(getReadTreeMethod().invoke(null, new StringReader(s)));
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof IOException)
				throw (IOException)e.getCause();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			return null;
		}
	}
	
	public static void setModulePath(String newPath) {
		if (newPath == null)
			throw new IllegalArgumentException("newPath must not be null");
		if (!newPath.equals(path))
			synchronized (PalFacade.class) {
				path = newPath;
			}
	}
	private static String path; 
	
	private static JarClassLoader getLoader() {
		if (loader == null)
			synchronized (PalFacade.class) {
				if (loader==null) {
					try {						
						ClassPathHacker.addFile(path+"../../../../lib/jcl-core-2.2.2.jar");
						ClassPathHacker.addFile(path+"../../../../lib/log4j-1.2.16.jar");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					loader = new JarClassLoader();					
					loader.add(path+"../../../../lib/treecmp4Gui.jar");
					loader.add(path+"../../../../lib/pal-1.5.1.jar");
				}
			}
		return loader;
	}
	private static JarClassLoader loader;
	
	private static Method getReadTreeMethod() {
		if (readTreeMethod == null)
			synchronized (PalFacade.class) {
				if (readTreeMethod == null) {
					try {
						Class<?> treeToolClass = getLoader().loadClass("pal.tree.TreeTool");
						readTreeMethod = treeToolClass.getMethod("readTree", Reader.class);
					} catch (Exception e) {
						//this should not happen
						throw new RuntimeException(e);
					}
				}
			}
		return readTreeMethod;
	}
	private static Method readTreeMethod;
	
	static Class<?> getPalTreeClass() {
		if (palTreeClass == null)
			synchronized (PalFacade.class) {
				if (palTreeClass == null) {
					try {
						palTreeClass = getLoader().loadClass("pal.tree.Tree");
					} catch (ClassNotFoundException e) {
						// this should not happen
						throw new RuntimeException(e);
					}
				}		
			}
		return palTreeClass;
	}
	private static Class<?> palTreeClass;	

	public static class Tree {
		public Tree(Object treeObj) {
			this.treeObj = treeObj;
			if (toStringMethod == null)
				try {
					toStringMethod = treeObj.getClass().getMethod("toString");
				} catch (Exception e) {
					//this should not happen
					throw new RuntimeException(e);
				}
		}
		
		@Override
		public String toString() {
			try {
				return (String)toStringMethod.invoke(treeObj);
			} catch (Exception e) {
				//this should not happen
				throw new RuntimeException(e);
			}
		}

		private Method toStringMethod;
		private Object treeObj;
	}
	
	public static class TreeCmpMetric {
		public TreeCmpMetric(String className) {
			try {
				metricObject = getLoader().loadClass(className).getConstructor().newInstance();
				getDistanceMethod = metricObject.getClass().getMethod("getDistance", getPalTreeClass(), getPalTreeClass());
			} catch (Exception e) {
				//this should not happen
				throw new RuntimeException(e);
			}
		}
		
		public double getDistance(Tree t1, Tree t2) {
			
			try {
				return (Double)getDistanceMethod.invoke(metricObject, t1.treeObj, t2.treeObj);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				//this should not happen
				throw new RuntimeException(e);
			}			 
		}
		
		private Method getDistanceMethod;
		private Object metricObject;
	}
}
