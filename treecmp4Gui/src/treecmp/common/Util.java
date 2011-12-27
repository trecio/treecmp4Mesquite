/*
 * Copyright (c) 2005, Regents of the University of California
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.  
 *
 * * Neither the name of the University of California, Berkeley nor
 *   the names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior 
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package treecmp.common;




import java.util.Random;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.io.PrintStream;

/**
 * Provides common utilities to FOMIE programs.
 */
public class Util {
    /**
     * Initializes the random number generator using either the clock time 
     * or a fixed seed.  If the fixed seed is used, the behavior of the 
     * program will be repeatable across runs.
     *
     * @param randomize   set seed using clock time rather than fixed value
     */
    public static void initRandom(boolean randomize) {
	if (randomize) {
	    System.out.println("Using clock time as random seed.");
	    rand = new Random();
	} else {
	    System.out.println("Using fixed random seed for repeatability.");
	    rand = new Random(0xad527c2b74e10cb3L);
	}
    }

    /**
     * Uniformly sample from a set.
     */
    public static Object uniformSample(Set set) {
	if (set.isEmpty()) {
	    return null;
	}

	int index = randInt(set.size());
	Iterator setIterator = set.iterator();

	for(int counter=0; counter<index; ++counter, setIterator.next());
	return setIterator.next();
    }
	
    /**
     * Returns a pseudorandom number uniformly distributed in the range [0, 1).
     * This method must not be called before initRandom() is called.  
     */
    public static double random() {
	return rand.nextDouble(); // null pointer exception if not initialized
    }


    /**
     * Returns a pseudorandom integer sampled uniformly from {0, ..., n-1}
     * Assumes n > 0
     */
    public static int randInt(int n) {
	return (int) Math.floor(rand.nextDouble()*n);
    }

    /**
     * Returns an integer in the range {0, ..., probs.length - 1}, 
     * according to the distribution specified by probs.
     */
    public static int sampleWithProbs(double[] probs) {
	double u = random();
	double cumProb = 0;

	for (int i = 0; i < probs.length - 1; ++i) {
	    cumProb += probs[i];
	    if (u < cumProb) { // use < because random() could return 0
		return i;
	    }
	}

	return (probs.length - 1);
    }

    /**
     * Returns a list of min(<code>list.size()</code>, <code>n</code>) 
     * objects sampled without replacement from <code>list</code>.
     */
    public static List sampleWithoutReplacement(List list, int n) {
	List sampledObjs = new ArrayList();
	List sampledIndices = new LinkedList(); // sorted list of Integer
	n = Math.min(list.size(), n);
	
	for (int i = 0; i < n; ++i) {
	    int index = randInt(list.size() - i);

	    // find the index-th unsampled element of the list
	    ListIterator iter = sampledIndices.listIterator();
	    while(iter.hasNext()) {
		int alreadySampled = ((Integer) iter.next()).intValue();
		if (alreadySampled <= index) {
		    ++index; // don't count the already-sampled index
		} else {
		    iter.previous(); // so we can insert new index correctly
		    break;
		}
	    }

	    sampledObjs.add(list.get(index));
	    iter.add(new Integer(index));
	}

	return sampledObjs;
    }

    /**
     * Returns log(n! / (n-m)!), that is, the log of the product of the first 
     * m factors in the factorial of n.  
     */
    public static double logPartialFactorial(int n, int m) {
	double sum = 0;
	for (int i = n; i > n - m; i--) {
	    sum += Math.log(i);
	}
	return sum;
    }

    /**
     * Returns the factorial of n.
     */
    public static int factorial(int n) {
	int prod = 1;
	for (int i = 2; i <= n; i++) {
	    prod *= i;
	}
	return prod;
    }

    /**
     * Returns the log of the factorial of n.  This may be faster than 
     * just calling Math.log(Util.factorial(n)).
     */
    public static double logFactorial(int n) {
	return logPartialFactorial(n, n);
    }

    /**
     * Addition in the log domain.  Returns an approximation to 
     * ln(e^a + e^b).  Just doing it naively might lead to underflow if a 
     * and b are very negative.  Without loss of generality, let b<a .  
     * If a>-10, calculates it in the standard way.  Otherwise, rewrite
     * it as a + ln(1 + e^(b-a)) and approximate that by the
     * first-order Taylor expansion to be a + (e^(b-a)).  So if b
     * is much smaller than a, there will still be underflow in the
     * last term, but in that case, the error is small relative to the
     * final answer.
     */
    public static double logSum(double a, double b) {
		
	if (a > b) {
			

	    if (b==Double.NEGATIVE_INFINITY) {
		return a;
	    }
	    else if (a>-10) {
		return Math.log(Math.exp(a)+Math.exp(b));
	    }

	    else {
		return a+Math.exp(b-a);
	    }
	}
	else {
	    if (a == Double.NEGATIVE_INFINITY) {
		return b;
	    }
	    else if (b>-10) {
		return Math.log(Math.exp(a) + Math.exp(b));
	    }
	    else {
		return b+Math.exp(a-b);
	    }
	}
    }
				
	
    /**
     * Maximum difference that we are willing to ignore between two 
     * floating-point values.  For instance, the sum of some probabilities 
     * may not be exactly 1.0, but this may just be due to floating-point 
     * inaccuracies, so we may want to consider it "close enough" to 1.0.  
     */
    public static final double TOLERANCE = 1e-10;
		
    /**
     * Returns true if the two given values differ by no more than 
     * Util.TOLERANCE.
     */
    public static boolean withinTol(double x, double y) {
	return (Math.abs(x - y) <= Util.TOLERANCE);
    }

    /**
     * Returns true if <code>x</code> is greater than <code>y</code> by 
     * at least Util.TOLERANCE.
     */
    public static boolean signifGreaterThan(double x, double y) {
	return (x - y >= Util.TOLERANCE);
    }

    /**
     * Returns true if <code>x</code> is less than <code>y</code> by 
     * at least Util.TOLERANCE.
     */
    public static boolean signifLessThan(double x, double y) {
	return (y - x >= Util.TOLERANCE);
    }

    /**
     * Prints the error message and stack trace for the given exception, 
     * and exits the program, returning code 1.
     */
    public static void fatalError(Throwable e) {
        fatalError(e, true);
    }

    /**
     * Prints the error message for the given exception, and optionally 
     * prints a stack trace.  Then exits the program with return code 1.  
     */
    public static void fatalError(Throwable e, boolean trace) {
	System.err.println("Fatal error: " + e.getMessage());
	if (trace) {
	    e.printStackTrace();
	    Throwable cause = e.getCause();
	    if (cause != null) {
		System.err.println("Cause: " + cause.getMessage());
		cause.printStackTrace();
	    }
	} 

	System.exit(1);
    }

    /**
     * Prints error message and exits.
     *
     * @param msg the error message
     * 
     */
    public static void fatalError(String msg) {
	fatalError(msg, true);
    }

    /**
     * Prints error message, optionally prints stack trace, and exits.
     *
     * @param msg the error message
     * 
     * @param trace if true, print a stack trace
     */
    
    public static void fatalError(String msg, boolean trace) {
	System.err.println("Fatal error: " + msg);
	if (trace) {
	    (new Throwable(msg)).printStackTrace();
	}

 	System.exit(1);
    }


    /**
     * Given two substrings defined by "begin" and "end" indices in some 
     * original string, returns the index of the first character that is 
     * in either of these two strings, or 0 if both strings are empty.
     * 
     * @param begin1   index of first char in substring 1
     * @param end1     one plus index of last char in substring 1
     * @param begin2   index of first char in substring 2
     * @param end2     one plus index of last char in substring 2
     */
    public static int substringPairBegin(int begin1, int end1, 
					 int begin2, int end2) {
	if (begin1 == end1) {
	    if (begin2 == end2) {
		return 0;
	    }
	    return begin2;
	}
	if (begin2 == end2) {
	    return begin1;
	}
	return Math.min(begin1, begin2);
    }

    /**
     * Returns the substring of <code>str</code> from 
     * <code>substringPairBegin(begin1, end1, begin2, end2)</code> to 
     * <code>substringPairEnd(begin1, end1, begin2, end2)</code>.
     */
    public static String spannedSubstring(String str, int begin1, int end1, 
					  int begin2, int end2) {
	return str.substring(substringPairBegin(begin1, end1, begin2, end2), 
			     substringPairEnd(begin1, end1, begin2, end2));
    }

    /**
     * Given two substrings defined by "begin" and "end" indices in some 
     * original string, returns one plus the index of the last character that 
     * is in one of these two strings, or 0 if both strings are empty.
     * 
     * @param begin1   index of first char in substring 1
     * @param end1     one plus index of last char in substring 1
     * @param begin2   index of first char in substring 2
     * @param end2     one plus index of last char in substring 2
     */
    public static int substringPairEnd(int begin1, int end1, 
				       int begin2, int end2) {
	if (begin1 == end1) {
	    if (begin2 == end2) {
		return 0;
	    }
	    return end2;
	}
	if (begin2 == end2) {
	    return end1;
	}
	return Math.max(end1, end2);
    }

    /**
     * Given a string, returns a version of that string where all letters 
     * have been converted to lower case, and all characters that are not 
     * letters or digits have been removed.
     */
    public static String normalize(String input) {
	StringBuffer output = new StringBuffer();
	for (int i = 0; i < input.length(); i++) {
	    char c = input.charAt(i);
	    if (Character.isLetterOrDigit(c)) {
		output.append(Character.toLowerCase(c));
	    }
	}
	return output.toString();
    }

    /**
     * Returns an unmodifiable list equal to the concatenation of the two 
     * given lists.
     */
    public static List concat(List list1, List list2) {
	return new ConcatenationList(list1, list2);
    }

    /**
     * Nested class for implementing the <code>concat</code> method.
     */
    private static class ConcatenationList extends AbstractList {
	
	ConcatenationList(List list1, List list2) {
	    this.list1 = list1;
	    this.list2 = list2;
	}

	public int size() {
	    return (list1.size() + list2.size());
	}

	public Object get(int index) {
	    if (index < list1.size()) {
		return list1.get(index);
	    } 
	    return list2.get(index - list1.size());
	}

	private List list1;
	private List list2;
    }

    /**
     * Returns an unmodifiable collection equal to the union of the two given 
     * collections, which are assumed to be disjoint.
     */
    public static Collection disjointUnion(Collection s1, Collection s2) {
	return new DisjointUnionCollection(s1, s2);
    }

    /**
     * Nested class for implementing the <code>disjointUnion</code> method.
     */
    private static class DisjointUnionCollection extends AbstractCollection {
	DisjointUnionCollection(Collection s1, Collection s2) {
	    this.s1 = s1;
	    this.s2 = s2;
	}

	public int size() {
	    return (s1.size() + s2.size());
	}

	public boolean contains(Object o) {
	    return (s1.contains(o) || s2.contains(o));
	}

	public Iterator iterator() {
	    return new DisjointUnionIterator();
	}

	private class DisjointUnionIterator implements Iterator {
	    public boolean hasNext() {
		return (s1iter.hasNext() || s2iter.hasNext());
	    }

	    public Object next() {
		if (s1iter.hasNext()) {
		    return s1iter.next();
		} else if (s2iter.hasNext()) {
		    return s2iter.next();
		}
		throw new NoSuchElementException();
	    }

	    public void remove() {
		throw new UnsupportedOperationException
		    ("Can't remove from DisjointUnionSet.");
	    }
	    
	    private Iterator s1iter = s1.iterator();
	    private Iterator s2iter = s2.iterator();
	}

	private Collection s1;
	private Collection s2;
    }

    /**
     * Returns an unmodifiable set equal to the intersection of the two 
     * given sets.
     */
    public static Set intersection(Set s1, Set s2) {
	return new IntersectionSet(s1, s2);
    }

    /**
     * Nested class for implementing the <code>intersection</code> method.
     */
    private static class IntersectionSet extends AbstractSet {
	
	IntersectionSet(Set s1, Set s2) {
	    this.s1 = s1;
	    this.s2 = s2;
	}

	public int size() {
	    Set smaller = (s1.size() <= s2.size()) ? s1 : s2;
	    Set larger = (smaller == s1) ? s2 : s1;
	    
	    int size = 0;
	    for (Iterator iter = smaller.iterator(); iter.hasNext(); ) {
		if (larger.contains(iter.next())) {
		    ++size;
		}
	    }
	    return size;
	}

	public boolean contains(Object obj) {
	    return (s1.contains(obj) && s2.contains(obj));
	}

	public Iterator iterator() {
	    return new IntersectionSetIterator();
	}

	private class IntersectionSetIterator implements Iterator {
	    IntersectionSetIterator() {
		Set smaller = (s1.size() <= s2.size()) ? s1 : s2;
		Set larger = (smaller == s1) ? s2 : s1;
		
		smallerIter = smaller.iterator();
		nextObj = findNext();
	    }

	    public boolean hasNext() {
		return (nextObj != null);
	    }
	    
	    public Object next() {
		if (nextObj == null) {
		    throw new NoSuchElementException();
		}

		Object toReturn = nextObj;
		nextObj = findNext();
		return toReturn;
	    }

	    public void remove() {
		throw new UnsupportedOperationException
		    ("Tried to remove element from IntersectionSet.");
	    }
	    
	    private Object findNext() {
		while (smallerIter.hasNext()) {
		    Object obj = smallerIter.next();
		    if (larger.contains(obj)) {
			return obj;
		    }
		}

		return null;
	    }

	    private Iterator smallerIter;
	    private Set larger;
	    private Object nextObj;
	}

	private Set s1;
	private Set s2;
    }

    /**
     * Returns the number of lines in the given file.  This is the number 
     * of times that BufferedReader's readLine method can be called on 
     * this file before it returns null.
     */
    public static int getNumLines(File file) throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(file));
	int numLines = 0;
	while (reader.readLine() != null) {
	    ++numLines;
	}
	return numLines;
    }

    /**
     * Returns an iterator over the integers in the range from
     * <code>lower</code> to <code>upper</code>, inclusive.  The
     * iterator returns integers in ascending order.  If
     * <code>lower</code> is greater than <code>upper</code>, the 
     * iterator has no elements.  
     *
     * @return Iterator over Integer
     */
    public static Iterator getIntegerRangeIterator(int lower, int upper) {
	return new IntRangeIterator(lower, upper);
    }

    /**
     * Nested class for implementing getIntegerRangeIterator.
     */
    private static class IntRangeIterator implements Iterator {
	IntRangeIterator(int lower, int upper) {
	    this.upper = upper;
	    if (lower <= upper) {
		nextInt = new Integer(lower);
	    }
	}

	public boolean hasNext() {
	    return (nextInt != null);
	}

	public Object next() {
	    if (nextInt == null) {
		throw new NoSuchElementException();
	    }
	    
	    Integer toReturn = nextInt;
	    if (nextInt.intValue() < upper) {
		nextInt = new Integer(nextInt.intValue() + 1);
	    } else {
		// Note that we don't increment nextInt in this case, 
		// so we won't get overflow if upper is Integer.MAX_VALUE.
		nextInt = null;
	    }
	    return toReturn;
	}

	public void remove() {
	    throw new UnsupportedOperationException
		("Can't remove from IntRangeIterator");
	}
	
	Integer nextInt = null;
	int upper;
    }

    /**
     * Returns an iterator over the integers greater than or equal to
     * <code>lower</code>, in ascending order.  This iterator uses the
     * mathematical rather than the computational notion of an
     * integer, so its <code>hasNext</code> method never returns
     * false, even when it has already iterated over
     * <code>Integer.MAX_VALUE</code>.  If this iterator's
     * <code>next</code> method is called enough times, it will
     * eventually throw an ArithmeticException indicating that the
     * next integer cannot be represented.
     *
     * @return Iterator over Integer
     */
    public static Iterator getAscendingIntegerIterator(int lower) {
	return new AscendingIntIterator(lower);
    }

    private static class AscendingIntIterator implements Iterator {
	AscendingIntIterator(int lower) {
	    nextInt = new Integer(lower);
	}

	public boolean hasNext() {
	    return true;
	}

	public Object next() {
	    if (nextInt == null) {
		throw new ArithmeticException
		    ("Next integer in ascending order is not representable.");
	    }
	    
	    Integer toReturn = nextInt;
	    if (nextInt.intValue() < Integer.MAX_VALUE) {
		nextInt = new Integer(nextInt.intValue() + 1);
	    } else {
		nextInt = null;
	    }
	    return toReturn;
	}

	public void remove() {
	    throw new UnsupportedOperationException
		("Can't remove from AscendingIntIterator");
	}
	
	Integer nextInt;
    }

    /**
     * Returns an iterator over the integers less than or equal to
     * <code>upper</code>, in descending order.  This iterator uses the
     * mathematical rather than the computational notion of an
     * integer, so its <code>hasNext</code> method never returns
     * false, even when it has already iterated over
     * <code>Integer.MIN_VALUE</code>.  If this iterator's
     * <code>next</code> method is called enough times, it will
     * eventually throw an ArithmeticException indicating that the
     * next integer cannot be represented.
     *
     * @return Iterator over Integer
     */
    public static Iterator getDescendingIntegerIterator(int upper) {
	return new DescendingIntIterator(upper);
    }

    private static class DescendingIntIterator implements Iterator {
	DescendingIntIterator(int upper) {
	    nextInt = new Integer(upper);
	}

	public boolean hasNext() {
	    return true;
	}

	public Object next() {
	    if (nextInt == null) {
		throw new ArithmeticException
		    ("Next integer in descending order is not representable.");
	    }
	    
	    Integer toReturn = nextInt;
	    if (nextInt.intValue() > Integer.MIN_VALUE) {
		nextInt = new Integer(nextInt.intValue() - 1);
	    } else {
		nextInt = null;
	    }
	    return toReturn;
	}

	public void remove() {
	    throw new UnsupportedOperationException
		("Can't remove from DescendingIntIterator");
	}
	
	Integer nextInt;
    }

    /**
     * Returns an iterator over all integers, in order by magnitude,
     * with positive integers coming before negative integers of the
     * same magnitude.  The iterator uses the mathematical rather than
     * computational notion of an integer, so its <code>hasNext</code>
     * method always returns true, even when
     * <code>Integer.MAX_VALUE</code> has already been returned (note
     * that <code>Integer.MAX_VALUE</code> has a smaller magnitude
     * than <code>Integer.MIN_VALUE</code>, so it will be reached
     * first).  If the iterator's <code>next</code> method is called
     * enough times, it will eventually throw an
     * <code>ArithmeticException</code> indicating that the next
     * integer is not representable.
     *
     * @return Iterator over Integer
     */
    public static Iterator getIntegerIterator() {
	return new IntIterator();
    }

    private static class IntIterator implements Iterator {
	IntIterator() {
	    nextInt = new Integer(0);
	}

	public boolean hasNext() {
	    return true;
	}

	public Object next() {
	    if (nextInt == null) {
		throw new ArithmeticException
		    ("Next integer by magnitude is not representable.");
	    }

	    Integer toReturn = nextInt;
	    int inverse = -nextInt.intValue();
	    if (inverse >= 0) {
		// Next integer will be positive; increase magnitude
		if (inverse < Integer.MAX_VALUE) { // don't exceed MAX_VALUE
		    nextInt = new Integer(inverse + 1);
		} else {
		    nextInt = null;
		}
	    } else {
		// Next integer will be negative; same magnitude as previous.  
		// Don't need to worry about MIN_VALUE here because 
		// its magnitude is >= MAX_VALUE.
		nextInt = new Integer(inverse);
	    }
	    return toReturn;
	}
	
	public void remove() {
	    throw new UnsupportedOperationException
		("Can't remove from IntSet.");
	}
	
	Integer nextInt;
    }

    private static Random rand;
}

