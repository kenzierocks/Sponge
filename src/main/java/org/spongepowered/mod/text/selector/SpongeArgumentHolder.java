package org.spongepowered.mod.text.selector;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector3l;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.spongepowered.api.text.selector.ArgumentHolder;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.List;
import java.util.Set;

@NonnullByDefault
public class SpongeArgumentHolder<T extends ArgumentHolder<?>> implements ArgumentHolder<T> {

    private final Set<T> subtypes;

    @SuppressWarnings("unchecked")
    protected SpongeArgumentHolder() {
        if (this instanceof ArgumentType) {
            this.subtypes = ImmutableSet.of((T) this);
        } else {
            throw new IllegalStateException("Must provide subtypes if not ArgumentType");
        }
    }

    public SpongeArgumentHolder(Set<T> subtypes) {
        this.subtypes = ImmutableSet.copyOf(subtypes);
    }

    @Override
    public int getCount() {
        return this.subtypes.size();
    }

    @Override
    public Set<T> getTypes() {
        return this.subtypes;
    }

    public static class SpongeVector3<V, T> extends SpongeArgumentHolder<ArgumentType<T>> implements ArgumentHolder.Vector3<V, T> {

        private static final SetMultimap<Class<?>, Function<?, ?>> extractFunctionSets = HashMultimap.create();
        static {
            List<Class<?>> vectors = ImmutableList.<Class<?>>builder().add(Vector3i.class, Vector3l.class, Vector3f.class, Vector3d.class).build();
            for (Class<?> vec : vectors) {
                Set<Function<?, ?>> set = Sets.newLinkedHashSet();
                try {
                    set.add(SpongeSelectorFactory.methodAsFunction(vec.getDeclaredMethod("getX"), false));
                    set.add(SpongeSelectorFactory.methodAsFunction(vec.getDeclaredMethod("getY"), false));
                    set.add(SpongeSelectorFactory.methodAsFunction(vec.getDeclaredMethod("getZ"), false));
                } catch (Exception e) {
                    // should support getX/Y/Z
                    throw new AssertionError("bad vector3 type");
                }
                extractFunctionSets.asMap().put(vec, set);
            }
        }

        private final ArgumentType<T> x;
        private final ArgumentType<T> y;
        private final ArgumentType<T> z;
        private final Set<Function<V, T>> extractFunctionSet;

        @SuppressWarnings("unchecked")
        public SpongeVector3(ArgumentType<T> x, ArgumentType<T> y, ArgumentType<T> z, Class<V> vectorType) {
            super(ImmutableSet.of(x, y, z));
            this.x = x;
            this.y = y;
            this.z = z;
            this.extractFunctionSet = (Set<Function<V, T>>) (Object) extractFunctionSets.get(vectorType);
            if (this.extractFunctionSet.isEmpty()) {
                throw new IllegalStateException("Unknown vector type " + vectorType);
            }
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

        protected Set<Function<V, T>> extractFunctions() {
            return this.extractFunctionSet;
        }

    }

    public static class SpongeLimit<T extends ArgumentHolder<T>> extends SpongeArgumentHolder<T> implements ArgumentHolder.Limit<T> {

        private final T min;
        private final T max;

        public SpongeLimit(T min, T max) {
            super(ImmutableSet.of(min, max));
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
