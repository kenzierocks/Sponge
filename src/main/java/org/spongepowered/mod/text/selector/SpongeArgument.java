package org.spongepowered.mod.text.selector;

import org.spongepowered.api.CatalogType;

import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class SpongeArgument<T> implements Argument<T> {

    public static class Invertible<T> extends SpongeArgument<T> implements Argument.Invertible<T> {

        private final boolean inverted;

        public Invertible(ArgumentType.Invertible<T> type, T value, boolean inverted) {
            super(type, value);
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
            return new SpongeArgument.Invertible<T>((ArgumentType.Invertible<T>) this.getType(), this.getValue(), !this.isInverted());
        }

    }

    private static String toSelectorArgument(Object val) {
        if (val instanceof CatalogType) {
            return ((CatalogType) val).getId();
        }
        return String.valueOf(val);
    }

    private final ArgumentType<T> type;
    private final T value;

    public SpongeArgument(ArgumentType<T> type, T value) {
        this.type = type;
        this.value = value;
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
        return this.type.getKey() + getEqualitySymbols() + toSelectorArgument(getValue());
    }

}
