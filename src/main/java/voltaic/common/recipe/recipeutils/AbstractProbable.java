package voltaic.common.recipe.recipeutils;

import voltaic.Voltaic;

abstract class AbstractProbable<T> {

    protected final T stack;
    private final double chance;

    protected AbstractProbable(T stack, double chance) {
	this.stack = stack;
	this.chance = Math.clamp(chance, 0.0, 1.0);
    }

    public final T getFullStack() {
	return stack;
    }

    public final double getChance() {
	return chance;
    }

    protected static final double nextRoll() {
	return Voltaic.RANDOM.nextDouble();
    }

    protected final boolean passesChance(double roll) {
	return roll > 1.0 - chance;
    }

    protected final boolean isGuaranteed() {
	return chance >= 1.0;
    }
}
