package org.spongepowered.mod.text.selector;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.lang.reflect.Method;
import java.util.Map;

@NonnullByDefault
public class SpongeArgumentType<T> extends SpongeArgumentHolder<ArgumentType<T>> implements ArgumentType<T> {

    private static final Map<Class<?>, Function<String, ?>> converters = Maps.newHashMap();
    static {
        converters.put(String.class, Functions.<String>identity());
    }

    @SuppressWarnings("unchecked")
    private static <T> Function<String, T> getConverter(final Class<T> type) {
        if (!converters.containsKey(type)) {
            try {
                final Method valueOf = type.getMethod("valueOf", String.class);
                converters.put(type, SpongeSelectorFactory.<String, T>methodAsFunction(valueOf, true));
            } catch (NoSuchMethodException ignored) {
                // TODO handle category types here
                throw new NotImplementedException("No @CategoryType support");
            } catch (SecurityException ignored) {
            }
        }
        return (Function<String, T>) converters.get(type);
    }

    private final String key;
    private final Function<String, T> converter;

    public SpongeArgumentType(String key, Class<T> type) {
        this(key, getConverter(type));
    }

    public SpongeArgumentType(String key, Function<String, T> converter) {
        this.key = key;
        this.converter = converter;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    protected T convert(String s) {
        return this.converter.apply(s);
    }

    public static class Invertible<T> extends SpongeArgumentType<T> implements ArgumentType.Invertible<T> {

        public Invertible(String key, Class<T> type) {
            super(key, type);
        }

        public Invertible(String key, Function<String, T> converter) {
            super(key, converter);
        }

    }
}
