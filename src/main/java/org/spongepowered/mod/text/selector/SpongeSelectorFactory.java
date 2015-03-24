package org.spongepowered.mod.text.selector;

import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorBuilder;
import org.spongepowered.api.text.selector.SelectorFactory;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class SpongeSelectorFactory implements SelectorFactory {

    @Override
    public SelectorBuilder createBuilder(SelectorType type) {
        return new SpongeSelectorBuilder(type);
    }

    @Override
    public Selector parseRawSelector(String selector) {
        return null;
    }

    @Override
    public ArgumentType.Limit<ArgumentType<Integer>> createScoreArgumentType(String name) {
        return null;
    }

    @Override
    public ArgumentType<String> createArgumentType(String key) {
        return null;
    }

    @Override
    public <T> ArgumentType<T> createArgumentType(String key, Class<T> type) {
        return null;
    }

    @Override
    public <T> Argument<T> createArgument(ArgumentType<T> type, T value) {
        return null;
    }

    @Override
    public <T> Argument.Invertible<T> createArgument(ArgumentType.Invertible<T> type, T value, boolean inverted) {
        return null;
    }

    @Override
    public Argument<?> parseArgument(String argument) throws IllegalArgumentException {
        return null;
    }

}
