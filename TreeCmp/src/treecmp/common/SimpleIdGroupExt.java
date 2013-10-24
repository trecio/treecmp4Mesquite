/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.misc.Nameable;

/**
 *
 * @author Damian
 */
public class SimpleIdGroupExt implements IdGroup, Serializable, Nameable {

	private String name;
	private Identifier[] ids;
	private Map<String, Integer> indices;

	//
	// Serialization code
	//
	private static final long serialVersionUID= -4266575329910153075L;

	//serialver -classpath ./classes pal.misc.SimpleIdGroup
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
		out.writeByte(1); //Version number
		out.writeObject(name);
		out.writeObject(ids);
	}

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
		byte version = in.readByte();
		switch(version) {
			default : {
				name = (String)in.readObject();
				ids = (Identifier[])in.readObject();
				indices = new HashMap<String, Integer>(ids.length);
				for(int i = 0 ; i < ids.length ; i++) {
					indices.put(ids[i].getName(), new Integer(i));
				}
				break;
			}
		}
	}

	/**
	 * Constructor taking the size of the group.
	 */
	public SimpleIdGroupExt(int size) {
		this(size,false);

	}

	/**
	 * Constructor taking an array of strings.
	 */
	public SimpleIdGroupExt(String[] labels) {
		this(labels.length);
		for (int i = 0; i < labels.length; i++) {
			setIdentifier(i, new Identifier(labels[i]));
		}
	}

	/**
	 * Constructor taking the size of the group.
	 * @param size - the number of ids
	 * @param createIDs - if true creates default Identifiers.
	 * Otherwise leaves blank (for user to fill in)
	 */
	public SimpleIdGroupExt(int size, boolean createIDs) {

		ids = new Identifier[size];
		indices = new HashMap<String, Integer>(size);
		if(createIDs) {
			for(int i = 0 ; i < size ; i++ ) {
				setIdentifier(i, new Identifier(""+i));
			}
		}
	}

	/**
	 * Constructor taking an array of identifiers.
	 */
	public SimpleIdGroupExt(Identifier[] id) {
		this(id.length);
		for (int i = 0; i < id.length; i++) {
			setIdentifier(i, id[i]);
		}
	}

	/**
	 * Constructor taking two separate id groups and merging them.
	 */
	public SimpleIdGroupExt(IdGroup a, IdGroup b) {
		this(a.getIdCount() + b.getIdCount());

		for (int i = 0; i < a.getIdCount(); i++) {
			setIdentifier(i, a.getIdentifier(i));
		}
		for (int i = 0; i < b.getIdCount(); i++) {
			setIdentifier(i + a.getIdCount(), b.getIdentifier(i));
		}
	}

	/**
	 * Impersonating Constructor.
	 */
	public SimpleIdGroupExt(IdGroup a) {
		this(a.getIdCount());

		for (int i = 0; i < a.getIdCount(); i++) {
			setIdentifier(i, a.getIdentifier(i));
		}
	}
	/**
	 * Impersonating Constructor.
	 * @param toIgnore - will ignore the identifier at the index specified by toIgnore
	 */
	public SimpleIdGroupExt(IdGroup a, int toIgnore) {
		this((toIgnore < 0 ||toIgnore > a.getIdCount() ? a.getIdCount() : a.getIdCount()-1));
		int index = 0;
		for (int i = 0; i < a.getIdCount(); i++) {
			if(i!=toIgnore) {
				setIdentifier(index++, a.getIdentifier(i));
			}
		}
	}

	/**
	 * Returns the number of identifiers in this group
	 */
	public int getIdCount() {
		return ids.length;
	}

	/**
	 * Returns the ith identifier.
	 */
	public Identifier getIdentifier(int i) {
		return ids[i];
	}

	/**
	 * Convenience method to return the name of identifier i
	 */
	public final String getName(int i) {
		return ids[i].getName();
	}

	/**
	 * Sets the ith identifier.
	 */
	public void setIdentifier(int i, Identifier id) {
		ids[i] = id;
		indices.put(id.getName(), new Integer(i));
	}

	/**
	 * Return index of identifier with name or -1 if not found
	 */
	public int whichIdNumber(String name) {

		Integer index = (Integer)indices.get(name);
		if (index != null) {
			return index.intValue();
		}
		return -1;
	}

	/**
	 * Returns a string representation of this IdGroup in the form of
	 * a bracketed list.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for (int i = 0; i < getIdCount(); i++) {
			sb.append(getIdentifier(i) + " ");
		}
		sb.append("]");
		return new String(sb);
	}

	// implement Nameable interface

	/**
	 * Return the name of this IdGroup.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this IdGroup.
	 */
	public void setName(String n) {
		name = n;
	}
}



