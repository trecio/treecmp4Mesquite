package treetool.distance;
import treetool.tree.Tree;
import treetool.tree.ParseException;
import java.io.*;
import java.util.*;

public class QuartetDist {

  public static void main(String[] args) {
    boolean qdist = false;
    if (args.length < 2) {
      System.err.println("Provide at least 2 newick files as arguments.");
      System.err.println("\nOptions:\n");
      System.err.println("--qdist  \t -q  \t Display the Quartet Distance (default if no");
      System.err.println("         \t     \t options are given).");
      System.err.println("--qnorm  \t     \t Display the Normalized Quartet Distance.");
      System.err.println("--qsim   \t -s  \t Display the number of quartets that have the");
      System.err.println("         \t     \t same topology.");
      System.err.println("--fit    \t -f  \t Display Quartet Fit Similarity (Normalized Similarity).");
      System.err.println("--quarts \t     \t Display the number of quartets present only in tree");
      System.err.println("         \t     \t 'i' of the pair of trees '(i,j)'.");
      System.err.println("--dec D  \t -d D\t Display floating point values with D digits");
      System.err.println("         \t     \t after the decimal point. Default is 5, max is 9.");
      System.err.println("--verbose\t -v  \t Display matrix names and tree names.");
      System.err.println("--help   \t -h  \t Display this help.");
      System.exit(0);
    }

    boolean fit = false, qsim = false, qnorm = false, quarts = false, verbose = false;
    LinkedList treelist = new LinkedList();
    LinkedList namelist = new LinkedList();
    double dig = Math.pow(10,5);
    
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--qdist") || args[i].equals("-q"))
	qdist = true;
      else if (args[i].equals("--fit") || args[i].equals("-f"))
	fit = true;
      else if (args[i].equals("--qsim") || args[i].equals("-s"))
	qsim = true;
      else if (args[i].equals("--qnorm"))
	qnorm = true;
      else if (args[i].equals("--quarts"))
	quarts = true;
      else if (args[i].equals("--verbose") || args[i].equals("-v"))
	verbose = true;
      else if (args[i].equals("--dec") || args[i].equals("-d")) {
	try {
	  int tmp = Integer.parseInt(args[++i]);
	  if (tmp<0 || tmp>9)
	    throw new NumberFormatException();
	  dig = Math.pow(10,tmp);
	  System.out.println(dig);
	}
	catch (NumberFormatException nfe) {
	  System.err.println("'"+args[i]+"' is not a digit between 0 and 9.");
	  System.exit(0);
	}
      }
      else {
	try {
	  File f = new File(args[i]);
	  char[] chars = new char[(int)f.length()];
	  FileReader r = new FileReader(f);
	  r.read(chars);
	  treelist.add(new Tree(new String(chars)));
	  namelist.add(args[i]);
	}
	catch (FileNotFoundException fnf) {
	  System.err.println("File '"+args[i]+"' not found.");
	  System.exit(0);
	}
	catch (ParseException pe) {
	  System.err.println("Contents of file '"+args[i]+"' is not in newick format.");
	  System.exit(0);
	}
	catch (Exception e) {
	  System.err.println("The following error occured while reading tree from the file "+args[i]+":");
	  e.printStackTrace();
	  System.exit(0);
	}
      }
    }
    Distance d;
    
    if (!fit && !qsim && !qnorm && !quarts)
      qdist = true;
    
    d = new GeneralN2DQDist();
    
    Tree[] trees = (Tree[])treelist.toArray(new Tree[0]);
    String[][] dists = null;
    String[][] qsims  = null;
    String[][] qnorms  = null;
    String[][] fits = null;
    String[][] equarts = null;
    
    if (qdist)
      dists = new String[trees.length][trees.length];
    if (qnorm)
      qnorms = new String[trees.length][trees.length];
    if (qsim)
      qsims = new String[trees.length][trees.length];
    if (fit)
      fits = new String[trees.length][trees.length];
    if (quarts)
      equarts = new String[trees.length][trees.length];
    
    String[] names = (String[])namelist.toArray(new String[0]);
    int longestnamelength = 0;
    for (int i = 0; i < names.length; i++) {
      if (names[i].length() > longestnamelength)
	longestnamelength = names[i].length();
    }
    for (int i = 0; i < trees.length; i++) {
      if (qdist)
	dists[i][i] = "-";
      if (qsim)
	qsims[i][i] = "-";
      if (qnorm)
	qnorms[i][i] = "-";
      if (fit)
	fits[i][i] = "-";
      if (quarts)
	equarts[i][i] = "-";
      
      for (int j = i+1; j < trees.length; j++) {
	DistResult dr = d.getMeasures(trees[i], trees[j]);
	if (qdist) {
	  dists[i][j] = "" + (dr.qdist()+dr.q1()+dr.q2());
	  dists[j][i] = "-";
	}
	if (qsim) {
	  qsims[i][j] = "" + dr.qsim();
	  qsims[j][i] = "-";
	}
	if (qnorm) {
	  qnorms[i][j] = "" + ((int)(((double)(dr.qdist()+dr.q1()+dr.q2()) /
				     (dr.qdist()+dr.qsim()+dr.q1()+dr.q2()))*dig))/dig;
	  qnorms[j][i] = "-";
	}
	if (fit) {
	  fits[i][j] = "" + ((int)(((double)dr.qsim() /
				     (dr.qdist()+dr.qsim()+dr.q1()+dr.q2()))*dig))/dig;
	  fits[j][i] = "-";
	}
	if (quarts) {
	  equarts[i][j] = "" + dr.q1();
	  equarts[j][i] = "" + dr.q2();
	}
	
      }
    }
    

    
    if (qdist && trees.length == 2 && !fit && !verbose && !qsim && !qnorm && !quarts) {
      System.out.println(dists[0][1]);
      System.exit(0);
    }
    
    if (qdist) {
      if (verbose)
	System.out.println("Quartet Distance Matrix.\n------------------------");
      printMatrix(dists, names, longestnamelength, verbose);
      System.out.println();
    }
    if (qnorm) {
      if (verbose)
	System.out.println("Normalized Quartet Distance Matrix.\n-----------------------------------");
      printFloatMatrix(qnorms, names, longestnamelength, verbose);
      System.out.println();
    }
      
    if (qsim) {
      if (verbose)
	System.out.println("Quartet Similarity Matrix.\n--------------------------");
      printMatrix(qsims, names, longestnamelength, verbose);
      System.out.println();
    }

    if (fit) {
      if (verbose)
	System.out.println("Quartet Fit Similarity Matrix (Normalized Similarity).\n------------------------------------------------------");
      printFloatMatrix(fits, names, longestnamelength, verbose);
      System.out.println();
    }
    if (quarts) {
      if (verbose) {
	System.out.println("Quartets only present in tree 'i'\nof the pair of trees '(i,j)'.");
	System.out.println("---------------------------------");
      }
      printMatrix(equarts, names, longestnamelength, verbose);
      System.out.println();
    }
  }


  public static void printMatrix(String[][] mat, String[] names, int longestnamelength, boolean verbose) {
    int[] spaces = getSpaces(mat);
    for (int i = 0; i < mat.length; i++) {
      if (verbose) {
	System.out.print(names[i]+": ");
	printSpaces(names[i], longestnamelength);
	System.out.print("\t");
      }
      for (int j = 0; j < mat.length; j++) {
	printSpaces(mat[i][j], spaces[j]);
	System.out.print(mat[i][j]);
	System.out.print( (j<mat.length-1) ? "\t" : "");
      }
      System.out.println();
    }
  }

  public static void printFloatMatrix(String[][] mat, String[] names, int longestnamelength, boolean verbose) {
    int[] spaces = getSpaces(mat);
    for (int i = 0; i < mat.length; i++) {
      if (verbose) {
	System.out.print(names[i]+": ");
	printSpaces(names[i], longestnamelength);
	System.out.print("\t");
      }
      for (int j = 0; j < mat.length; j++) {
	System.out.print(mat[i][j]);
	printSpaces(mat[i][j], spaces[j]);
	System.out.print( (j<mat.length-1) ? "\t" : "");
      }
      System.out.println();
    }
  }

  
  //Gets the number of chars of the maximal entry in each column
  private static int[] getSpaces(String[][] dists) {
    int[] spaces = new int[dists.length];
    for (int j = 0; j < dists.length; j++)
      for (int i = 0; i < dists.length; i++)
	spaces[j] = Math.max(dists[i][j].length(), spaces[j]);
    return spaces;
  }

  //Prints the right amount of spaces
  //Prepends/appends a tab if 'printtab' is set to false
  private static void printSpaces(String num, int maxsize) {
    String padding = "";
    for (int i = 0; i < maxsize - num.length(); i++)
      padding += " ";
    System.out.print(padding);
  }

}
