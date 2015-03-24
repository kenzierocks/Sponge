package org.spongepowered.mod.text.selector;

import com.google.common.base.Optional;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class SpongeArgumentType<T> implements ArgumentType<T> {

    private final Optional<String> key;

    public SpongeArgumentType(Optional<String> key) {
        this.key = key;
    }

    @Override
    public Optional<String> getKey() {
        return this.key;
    }

    public static class Invertible<T> extends SpongeArgumentType<T> implements ArgumentType.Invertible<T> {

        public Invertible(Optional<String> key) {
            super(key);
        }

    }

    public static class Vector3<V, T> extends SpongeArgumentType<V> implements ArgumentType.Vector3<V, T> {

        private final ArgumentType<T> x;
        private final ArgumentType<T> y;
        private final ArgumentType<T> z;

        public Vector3(ArgumentType<T> x, ArgumentType<T> y, ArgumentType<T> z) {
            super(Optional.<String>absent());
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public ArgumentType<T> x() {
            return this.x;
        }

        @Override
        public ArgumentType<T> y() {
            return this.y;
        }

        @Override
        public ArgumentType<T> z() {
            return this.z;
        }

    }

    public static class Limit<T extends ArgumentType<?>> implements ArgumentType.Limit<T> {

        private final T min;
        private final T max;

        public Limit(T min, T max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public T minimum() {
            return this.min;
        }

        @Override
        public T maximum() {
            return this.max;
        }

    }
}
