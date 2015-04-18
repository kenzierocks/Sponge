package org.spongepowered.mod.text.selector;

import com.flowpowered.math.vector.Vector3d;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.mod.util.VecHelper;

public class SelectorResolver implements ICommandSender {

    private final World world;
    private final Vec3 position;

    private SelectorResolver(Extent extent, Vector3d position) {
        this.world = (World) extent;
        this.position = position != null ? VecHelper.toVector(position) : null;
    }

    public SelectorResolver(Extent extent) {
        this(extent, null);
    }

    public SelectorResolver(Location location) {
        this(location.getExtent(), location.getPosition());
    }

    @Override
    public String getCommandSenderName() {
        return "SelectorResolver";
    }

    @Override
    public IChatComponent getDisplayName() {
        return new ChatComponentText(getCommandSenderName());
    }

    @Override
    public void addChatMessage(IChatComponent component) {
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return false;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.position);
    }

    @Override
    public Vec3 getPositionVector() {
        return this.position;
    }

    @Override
    public World getEntityWorld() {
        return this.world;
    }

    @Override
    public Entity getCommandSenderEntity() {
        return null;
    }

    @Override
    public boolean sendCommandFeedback() {
        return false;
    }

    @Override
    public void setCommandStat(CommandResultStats.Type type, int amount) {

    }

}
