package org.spongepowered.mod.text.selector;

import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class SpongeSelectorType implements SelectorType {

    private final String id;

    public SpongeSelectorType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

}
