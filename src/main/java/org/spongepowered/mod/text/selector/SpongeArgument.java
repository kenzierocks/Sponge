package org.spongepowered.mod.text.selector;

import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class SpongeArgument<T> implements Argument<T> {

    private final ArgumentType<T> type;
    private final T value;
    private final String plain;

    public SpongeArgument(ArgumentType<T> type, T value, String plain) {
        this.type = type;
        this.value = value;
        this.plain = plain;
    }

    @Override
    public ArgumentType<T> getType() {
        return this.type;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public String toPlain() {
        return this.plain;
    }

}
