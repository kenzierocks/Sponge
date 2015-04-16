package org.spongepowered.mod.text.selector;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorBuilder;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.util.List;

@NonnullByDefault
public class SpongeSelector implements Selector {

    protected final SelectorType type;
    protected final ImmutableMap<ArgumentType<?>, Argument<?>> arguments;

    private final String plain;

    public SpongeSelector(SelectorType type, ImmutableMap<ArgumentType<?>, Argument<?>> arguments) {
        this.type = type;
        this.arguments = arguments;
        this.plain = buildString();
    }

    @Override
    public SelectorType getType() {
        return this.type;
    }

    @Override @SuppressWarnings("unchecked")
    public <T> Optional<T> get(ArgumentType<T> type) {
        Argument<T> argument = (Argument<T>) this.arguments.get(type);
        return argument != null ? Optional.of(argument.getValue()) : Optional.<T>absent();
    }

    @Override @SuppressWarnings("unchecked")
    public <T> Optional<Argument<T>> getArgument(ArgumentType<T> type) {
        return Optional.fromNullable((Argument<T>) this.arguments.get(type));
    }

    @Override
    public List<Argument<?>> getArguments() {
        return this.arguments.values().asList();
    }

    @SuppressWarnings("unchecked")
    private List<Entity> resolve(ICommandSender resolver) {
        return (List<Entity>) PlayerSelector.matchEntities(resolver, this.plain, Entity.class);
    }

    @Override
    public List<Entity> resolve(Extent extent) {
        return resolve(new SelectorResolver(extent));
    }

    @Override
    public List<Entity> resolve(Location location) {
        return resolve(new SelectorResolver(location));
    }

    @Override
    public String toPlain() {
        return this.plain;
    }

    @Override
    public SelectorBuilder builder() {
        return null;
    }

    private String buildString() {
        StringBuilder result = new StringBuilder();

        result.append('@').append(this.type.getId());

        if (!this.arguments.isEmpty()) {
            result.append('[');
            for (Argument<?> argument : this.arguments.values()) {
                if (argument.getType().getKey().isPresent()) {
                    result.append(argument.getType().getKey().get()).append('=').append(argument.getValue());
                } else {
                    
                }
            }
            result.append(']');
        }

        return result.toString();
    }

}
