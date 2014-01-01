package tosram;

/**
 * A representation of runestone as specified in Tower of Savior.
 * 
 * @author johnchen902
 */
public class RuneStone {

	/**
	 * The type of a runestone as specified by the game.
	 * 
	 * @author johnchen902
	 */
	public static enum Type {
		/**
		 * The red one, which has a fire sign on it.
		 */
		FIRE('r'),
		/**
		 * The blue one, which is shaped as a drop of water.
		 */
		WATER('b'),
		/**
		 * The green one, which has four signs of leaf on it.
		 */
		GREEN('g'),
		/**
		 * The yellow one, which has a cross sign on it.
		 */
		LIGHT('y'),
		/**
		 * The purple one, which has a sign of moon on it.
		 */
		DARK('d'),
		/**
		 * The pink one, which has a sign of heart on it.
		 */
		HEART('p'),
		/**
		 * The type indicate the type is unknown.
		 */
		UNKNOWN('x');
		private final char shortName;

		private Type(char s) {
			shortName = s;
		}

		/**
		 * Get a single lower-case character for the type.
		 * 
		 * @return a lower case character
		 */
		public char getShortName() {
			return shortName;
		}
	}

	/**
	 * A stone with unknown type and normal strength.
	 */
	public static final RuneStone UNKNOWN = new RuneStone(Type.UNKNOWN);

	private final Type type;
	private final boolean stronger;

	/**
	 * Create a <code>RuneStone</code> with the specified type and the specified
	 * strength.
	 * 
	 * @param type
	 *            the specified type
	 * @param stronger
	 *            <code>true</code> if stronger; <code>false</code> otherwise
	 */
	public RuneStone(Type type, boolean stronger) {
		if (type == null)
			throw new NullPointerException("type");
		this.type = type;
		this.stronger = stronger;
	}

	/**
	 * Create a <code>RuneStone</code> with the specified type and normal
	 * strength.
	 * 
	 * @param type
	 *            the specified type
	 */
	public RuneStone(Type type) {
		this(type, false);
	}

	/**
	 * Return the type of this stone
	 * 
	 * @return a <code>Type</code>
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Return whether the stone has extra strength
	 * 
	 * @return <code>true</code> if stronger; <code>false</code> otherwise
	 */
	public boolean isStronger() {
		return stronger;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RuneStone))
			return false;
		RuneStone that = (RuneStone) obj;
		return this.type == that.type && this.stronger == that.stronger;
	}

	@Override
	public int hashCode() {
		return type.ordinal() * 2 + (stronger ? 1 : 0);
	}

	@Override
	public String toString() {
		if (stronger)
			return String.valueOf(Character.toUpperCase(type.getShortName()));
		else
			return String.valueOf(type.getShortName());
	}
}
