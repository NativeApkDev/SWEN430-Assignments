// This file is part of the WhileLang Compiler (wlc).
//
// The WhileLang Compiler is free software; you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// The WhileLang Compiler is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE. See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public
// License along with the WhileLang Compiler. If not, see
// <http://www.gnu.org/licenses/>
//
// Copyright 2013, David James Pearce.

package whilelang.ast;

import java.util.ArrayList;
import java.util.List;

import whilelang.util.Pair;
import whilelang.util.SyntacticElement;

/**
 * <p>
 * Represents a type as denoted in a source file (a.k.a a <i>syntactic
 * type</i>). As such types come directly from source code, they may be
 * incorrect in some fashion. For example, the type <code>{void f}</code> could
 * be written by a programmer, but is invalid type and should (eventually)
 * result in a syntax error.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface Type extends SyntacticElement {

	/**
	 * Represents the special <code>void</code> type which can only be used in
	 * special circumstance (e.g. for a function return).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Void extends SyntacticElement.Impl implements Type {

		/**
		 * Construct a new <code>void</code> type.
		 *
		 * @param attributes
		 */
		public Void(Attribute... attributes) {
			super(attributes);
		}

		@Override
		public String toString() {
			return "void";
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Void;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

	/**
	 * Represents the special <code>null</code> type which indicates a lack of
	 * something (e.g. a return value).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Null extends SyntacticElement.Impl implements Type {

		/**
		 * Construct a new <code>null</code> type.
		 *
		 * @param attributes
		 */
		public Null(Attribute... attributes) {
			super(attributes);
		}

		@Override
		public String toString() {
			return "null";
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Null;
		}

		@Override
		public int hashCode() {
			return 1;
		}
	}

	/**
	 * Represents the <code>bool</code> type which contains the values
	 * <code>true</code> and <code>false</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Bool extends SyntacticElement.Impl implements
			Type {

		/**
		 * Construct a new <code>bool</code> type.
		 *
		 * @param attributes
		 */
		public Bool(Attribute... attributes) {
			super(attributes);
		}

		@Override
		public String toString() {
			return "bool";
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Bool;
		}

		@Override
		public int hashCode() {
			return 2;
		}
	}

	/**
	 * Represents the <code>int</code> type which describes the set of all
	 * integers described in 32bit twos compliment form. For example, this is
	 * identical to a Java <code>int</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Int extends SyntacticElement.Impl implements Type {

		/**
		 * Construct a new <code>int</code> type.
		 *
		 * @param attributes
		 */
		public Int(Attribute... attributes) {
			super(attributes);
		}

		@Override
		public String toString() {
			return "int";
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Int;
		}

		@Override
		public int hashCode() {
			return 3;
		}
	}

	/**
	 * Represents a named type which has yet to be expanded in the given
	 * context.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Named extends SyntacticElement.Impl implements
			Type {

		/**
		 * The name of this named type.
		 */
		private final String name;

		/**
		 * Construct a new named type.
		 *
		 * @param name       The name to use for this type.
		 * @param attributes
		 */
		public Named(String name, Attribute... attributes) {
			super(attributes);
			this.name = name;
		}

		@Override
		public String toString() {
			return getName();
		}

		/**
		 * Get the name used by this type.
		 *
		 * @return The name.
		 */
		public String getName() {
			return name;
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Named) {
				Named n = (Named) o;
				return n.name.equals(name);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}

	/**
	 * Represents the array type <code>T[]</code> which describes any sequence
	 * of zero or more values of type <code>T</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Array extends SyntacticElement.Impl implements
			Type {

		/**
		 * Represents the element type.
		 */
		private final Type element;

		/**
		 * Construct a new array type for a given element type.
		 *
		 * @param element    The element type to use.
		 * @param attributes
		 */
		public Array(Type element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}

		/**
		 * Get the element type of this list.
		 *
		 * @return The element type.
		 */
		public Type getElement() {
			return element;
		}

		@Override
		public String toString() {
			return element + "[]";
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Array) {
				Array a = (Array) o;
				return element.equals(a.element);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return element.hashCode() << 1;
		}
	}

	/**
	 * Represents a record type, such as <code>{int x, int y}</code>, which
	 * consists of one or more (named) field types. Observe that records exhibit
	 * <i>depth</i> subtyping, but not <i>width</i> subtyping.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Record extends SyntacticElement.Impl implements Type {
		/**
		 * Determines the field types and their names of this record type.
		 */
		private final ArrayList<Pair<Type,String>> fields;
		/**
		 * Construct a new record type.
		 *
		 * @param fields     The sequence of field types and their names.
		 * @param attributes
		 */
		public Record(List<Pair<Type,String>> fields, Attribute... attributes) {
			super(attributes);
			if (fields.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.fields = new ArrayList<>(fields);
		}

		/**
		 * Get the fields which make up this record type. This are stored in the
		 * order they are declared in the source file.
		 *
		 * @return The list opf field types and their names.
		 */
		public List<Pair<Type,String>> getFields() {
			return fields;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Record) {
				Record r = (Record) o;
				return fields.equals(r.fields);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return fields.hashCode();
		}

		@Override
		public String toString() {
			String r = "";

			for (int i = 0; i != fields.size(); ++i) {
				if (i != 0) {
					r = r + ",";
				}
				Pair<Type, String> field = fields.get(i);
				r = r + field.first() + " " + field.second();
			}

			return "{" + r + "}";
		}
	}

	/**
	 * Represents the unique reference type <code>&T:1</code> where <code>&T</code> represents a reference type
	 * and <code>:1</code> indicates that the reference type is unique.
	 *
	 * @author Dominic Tjiptono
	 *
	 * */
	public static class UniqueReference extends Reference implements
			Type {
		/**
		 * A constructor which initialises a unique reference type
		 *
		 * @param element    The element type to use.
		 * @param attributes: a list of attributes
		 * */
		public UniqueReference(Type element, Attribute... attributes) {
			super(element, attributes);
		}

		/**
		 * Checking for equality of this object with another object.
		 * */
		@Override
		public boolean equals(Object o) {
			if (o instanceof UniqueReference) {
				UniqueReference ur = (UniqueReference) o;
				return getElement().equals(ur.getElement());
			}
			return false;
		}


		/**
		 * Creating a method to calculate the hash code of a UniqueReference class instance.
		 *
		 * @return a hash code
		 * */
		@Override
		public int hashCode() {
			return getElement().hashCode() << 1;
		}

		/**
		 * Creating a method to dump a UniqueReference class object into a string
		 *
		 * @return a string
		 * */
		@Override
		public String toString() {
			return "&" + getElement() + ":1";
		}
	}

	/**
	 * Represents the reference type <code>&T</code> which describes a
	 * <i>pointer</i> to a variable of the given type <code>T</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Reference extends SyntacticElement.Impl implements
			Type {
		/**
		 * Represents the element type.
		 */
		private final Type element;

		/**
		 * Construct a new array type for a given element type.
		 *
		 * @param element    The element type to use.
		 * @param attributes
		 */
		public Reference(Type element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}

		/**
		 * Get the element type of this list.
		 *
		 * @return The element type.
		 */
		public Type getElement() {
			return element;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Reference) {
				Reference r = (Reference) o;
				return element.equals(r.element);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return element.hashCode() << 1;
		}

		@Override
		public String toString() {
			return "&" + element;
		}
	}

	/**
	 * Represents a union type, such as <code>int|null</code>, which consists of one
	 * or more <i>type bounds</i>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Union extends SyntacticElement.Impl implements Type {
		/**
		 * The type bounds making up this union.
		 */
		private final List<Type> bounds;

		/**
		 * Construct a union type from a given list of types.
		 *
		 * @param bounds
		 * @param attributes
		 */
		public Union(List<Type> bounds, Attribute... attributes) {
			super(attributes);
			this.bounds = new ArrayList<>(bounds);
		}

		public List<Type> getBounds() {
			return bounds;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Union) {
				Union r = (Union) o;
				return bounds.equals(r.bounds);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return bounds.hashCode();
		}

		@Override
		public String toString() {
			String r = "";
			for(int i=0;i!=bounds.size();++i) {
				if(i!=0) {
					r += "|";
				}
				r += bounds.get(i);
			}
			return r;
		}
	}
}
