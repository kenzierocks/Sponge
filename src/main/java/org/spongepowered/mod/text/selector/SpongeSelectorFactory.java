package org.spongepowered.mod.text.selector;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.command.PlayerSelector;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.ArgumentTypes;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorBuilder;
import org.spongepowered.api.text.selector.SelectorFactory;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.mod.SpongeMod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@NonnullByDefault
public class SpongeSelectorFactory implements SelectorFactory {

    @Override
    public SelectorBuilder createBuilder(SelectorType type) {
        return new SpongeSelectorBuilder(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Selector parseRawSelector(String selector) {
        if (!selector.startsWith("@")) {
            throw new IllegalArgumentException("Invalid selector " + selector);
        }
        // If multi-character types are possible, this handles it
        int argListIndex = selector.indexOf('[');
        if (argListIndex < 0) {
            argListIndex = selector.length();
        }
        String typeStr = selector.substring(1, argListIndex);
        Optional<SelectorType> type = SpongeMod.instance.getGame().getRegistry().getSelectorType(typeStr);
        if (!type.isPresent()) {
            throw new IllegalArgumentException("No type known as '" + typeStr + "'");
        }
        try {
            Map<String, String> rawMap;
            if (argListIndex == selector.length()) {
                rawMap = ImmutableMap.of();
            } else {
                rawMap = PlayerSelector.getArgumentMap(selector.substring(argListIndex + 1, selector.length() - 1));
            }
            Map<ArgumentType<?>, Argument<?>> arguments = parseArguments(rawMap);
            return new SpongeSelector(type.get(), ImmutableMap.copyOf(arguments));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid selector " + selector, e);
        }
    }

    @Override
    public ArgumentType.Limit<ArgumentType<Integer>> createScoreArgumentType(String name) {
        return null;
    }

    @Override
    public Optional<ArgumentType<?>> getArgumentType(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<ArgumentType<?>> getArgumentTypes() {
        // TODO Auto-generated method stub
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

    @SuppressWarnings("unchecked")
    public Map<ArgumentType<?>, Argument<?>> parseArguments(Map<String, String> argumentMap) {
        Map<ArgumentType<?>, Argument<?>> generated = new HashMap<ArgumentType<?>, Argument<?>>(argumentMap.size());
        Map<ArgumentType<Object>, Object> comboArgs = Maps.newHashMap();
        for (Entry<String, String> argument : argumentMap.entrySet()) {
            String argKey = argument.getKey();
            Optional<ArgumentType<?>> type = ArgumentTypes.valueOf(argKey);
            if (!type.isPresent()) {
                throw new IllegalArgumentException("Illegal argument " + argKey + "->" + argument.getValue());
            }
            ArgumentType<Object> unwrappedType = (ArgumentType<Object>) type.get();
            if (!(unwrappedType instanceof SpongeArgumentType)) {
                // TODO handle convert generally?
                throw new IllegalStateException("Cannot convert from string: " + unwrappedType);
            }
            String value = argument.getValue();
            boolean isInverted = false;
            if (value.charAt(0) == '!' && unwrappedType instanceof ArgumentType.Invertible) {
                isInverted = true;
            }
            Object converted = ((SpongeArgumentType<?>) unwrappedType).convert(argument.getValue());
            if (unwrappedType instanceof ArgumentType.Child) {
                ArgumentType<Object> parent = ((ArgumentType.Child<Object>) unwrappedType).getParentType();
                if (!comboArgs.containsKey(parent)) {
                    comboArgs.put(parent, new HashMap<String, Object>());
                }
                String vecPart;
                if (argKey.contains("x")) {
                    vecPart = "x";
                } else if (argKey.contains("y")) {
                    vecPart = "y";
                } else if (argKey.contains("z")) {
                    vecPart = "z";
                } else {
                    throw new IllegalStateException("unknown vector key " + argKey);
                }
                ((Map<String, Object>) comboArgs.get(parent)).put(vecPart, converted);
                continue;
            }
            Argument<?> resultArg;
            if (unwrappedType instanceof ArgumentType.Invertible) {
                resultArg = createArgument((ArgumentType.Invertible<Object>) unwrappedType, converted, isInverted);
            } else {
                resultArg = createArgument(unwrappedType, converted);
            }
            generated.put(unwrappedType, resultArg);
        }
        for (Entry<ArgumentType<Object>, Object> e : comboArgs.entrySet()) {
            if (e.getKey() instanceof ArgumentType.Vector3) {
                // just a sanity check, no others possible
                Object x = ((Map<String, Object>) e.getValue()).get("x");
                Object y = ((Map<String, Object>) e.getValue()).get("y");
                Object z = ((Map<String, Object>) e.getValue()).get("z");
                if (x.getClass() != y.getClass() || y.getClass() != z.getClass()) {
                    throw new IllegalStateException("differing types for vector");
                }
                Object vec;
                if (x.getClass() == Integer.class) {
                    vec = new Vector3i((Integer) x, (Integer) y, (Integer) z);
                } else if (x.getClass() == Double.class) {
                    vec = new Vector3d((Double) x, (Double) y, (Double) z);
                } else if (x.getClass() == Float.class) {
                    vec = new Vector3f((Float) x, (Float) y, (Float) z);
                }
                else {
                    throw new IllegalStateException("unknown vector type " + x.getClass());
                }
                generated.put(e.getKey(), createArgument(e.getKey(), vec));
            } else {
                throw new IllegalStateException("ArgumentType.Child has a non-Vector3 parent");
            }
        }
        return generated;
    }

}
