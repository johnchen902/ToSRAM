package tosram.strategy;

/**
 * A strategy which return random quality.
 * 
 * @author johnchen902
 */
public class RandomQualityStrategy extends FilterSolutionStrategy {

	/**
	 * Create a <code>RandomQualityStrategy</code> with the specified filtered
	 * strategy.
	 * 
	 * @param next
	 *            the filtered strategy
	 */
	public RandomQualityStrategy(SolutionStrategy next) {
		super(next);
	}

	@Override
	public double getQuality() {
		return 0.5 * Math.random() + 0.5 * super.getQuality();
	}

}
