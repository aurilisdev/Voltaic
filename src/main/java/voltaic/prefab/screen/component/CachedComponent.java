package voltaic.prefab.screen.component;

import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

/**
 * Simple cache for UI components that depend on a small, comparable state.
 * Rebuilds the component only when the state changes.
 */
public class CachedComponent<S> {

    private final Function<S, Component> builder;
    @Nullable
    private S lastState;
    @Nullable
    private Component cached;

    public CachedComponent(Function<S, Component> builder) {
	this.builder = builder;
    }

    @Nullable
    public Component get(S state) {
	if (cached == null || !Objects.equals(lastState, state)) {
	    lastState = state;
	    cached = builder.apply(state);
	}
	return cached;
    }

    public void invalidate() {
	lastState = null;
	cached = null;
    }

}
