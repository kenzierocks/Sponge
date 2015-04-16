package org.spongepowered.mod.text.selector;

import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class SpongeArgument<T> implements Argument<T> {

    public static class Invertible<T> extends SpongeArgument<T> implements Argument.Invertible<T> {

        private final boolean inverted;

        public Invertible(ArgumentType<T> type, T value, String plain, boolean inverted) {
            super(type, value, plain);
            this.inverted = inverted;
        }

        @Override
        String getEqualitySymbols() {
            return isInverted() ? "!=" : "=";
        }

        @Override
        public boolean isInverted() {
            return this.inverted;
        }

        @Override
        public Argument.Invertible<T> invert() {
            return new SpongeArgument.Invertible<T>(this.getType(), this.getValue(), this.toPlain(), !this.isInverted());
        }

    }

    private final ArgumentType<T> type;
    private final T value;
    private final String plain;

    public SpongeArgument(ArgumentType<T> type, T value, String plain) {
        this.type = type;
        this.value = value;
        this.plain = plain;
    }

    String getEqualitySymbols() {
        return "=";
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
