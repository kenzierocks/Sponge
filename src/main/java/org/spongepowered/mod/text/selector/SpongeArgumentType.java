package org.spongepowered.mod.text.selector;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.mod.SpongeMod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@NonnullByDefault
public class SpongeArgumentType<T> implements ArgumentType<T> {

    private static final Map<Class<?>, Function<String, ?>> converters = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    private static <T> Function<String, T> getConverter(final Class<T> type) {
        if (!converters.containsKey(type)) {
            try {
                final Method valueOf = type.getMethod("valueOf", String.class);
                converters.put(type, new Function<String, T>() {

                    @Override
                    public T apply(String input) {
                        try {
                            return (T) valueOf.invoke(null, input);
                        } catch (IllegalAccessException e) {
                            SpongeMod.instance.getLogger().debug(valueOf + " wasn't public", e);
                            converters.remove(type);
                            return null;
                        } catch (IllegalArgumentException e) {
                            SpongeMod.instance.getLogger().debug(valueOf + " didn't want a String", e);
                            converters.remove(type);
                            return null;
                        } catch (InvocationTargetException e) {
                            throw Throwables.propagate(e.getCause());
                        }
                    }

                });
            } catch (NoSuchMethodException ignored) {
                // TODO handle category types here
                throw new NotImplementedException("No @CategoryType support");
            } catch (SecurityException ignored) {
            }
        }
        return (Function<String, T>) converters.get(type);
    }

    private Function<String, T> converter;

    public SpongeArgumentType(Class<T> type) {
        this(getConverter(type));
    }

    public SpongeArgumentType(Function<String, T> converter) {
        this.converter = converter;
    }

    protected T convert(String s) {
        return this.converter.apply(s);
    }

    public static class Invertible<T> extends SpongeArgumentType<T> implements ArgumentType.Invertible<T> {

        public Invertible(Class<T> type) {
            super(type);
        }
        
        public Invertible(Function<String, T> converter) {
            super(converter);
        }

    }

    public static class Vector3<V, T> extends SpongeArgumentType<V> implements ArgumentType.Vector3<V, T> {

        private final ArgumentType.Child<T> x;
        private final ArgumentType.Child<T> y;
        private final ArgumentType.Child<T> z;

        public Vector3(ArgumentType.Child<T> x, ArgumentType.Child<T> y, ArgumentType.Child<T> z, Class<V> type) {
            super(type);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3(ArgumentType.Child<T> x, ArgumentType.Child<T> y, ArgumentType.Child<T> z, Function<String, V> converter) {
            super(converter);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public ArgumentType.Child<T> x() {
            return this.x;
        }

        @Override
        public ArgumentType.Child<T> y() {
            return this.y;
        }

        @Override
        public ArgumentType.Child<T> z() {
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
