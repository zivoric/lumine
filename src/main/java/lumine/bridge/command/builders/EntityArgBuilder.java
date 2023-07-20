package lumine.bridge.command.builders;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lumine.bridge.command.ArgumentBuilder;
import lumine.bridge.entity.BridgeEntity;
import lumine.bridge.entity.BridgePlayer;
import lumine.command.argument.types.EntityArg;
import lumine.entity.Entity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class EntityArgBuilder extends ArgumentBuilder<EntityArg, EntityArgumentType, List<? extends Entity>, EntitySelector> {
    public EntityArgBuilder(EntityArg arg) {
        super(arg);
    }

    @Override
    public Class<EntitySelector> getBrigadierClass() {
        return EntitySelector.class;
    }

    public EntityArgBuilder() {
        super();
    }

    @Override
    public EntityArgumentType toBrigadier() {
        if (getArgument().onlyPlayers()) {
            if (getArgument().allowMultiple())
                return EntityArgumentType.players();
            else
                return EntityArgumentType.player();
        } else {
            if (getArgument().allowMultiple())
                return EntityArgumentType.entities();
            else
                return EntityArgumentType.entity();
        }
    }

    @Override
    public EntityArg fromBrigadier(String identifier, EntityArgumentType argument) {
        JsonObject object = new JsonObject();
        EntityArgumentType.Serializer serializer = new EntityArgumentType.Serializer();
        serializer.writeJson(serializer.getArgumentTypeProperties(argument), object);
        boolean allowMultiple = object.get("amount").getAsString().equals("single");
        boolean onlyPlayers = object.get("type").getAsString().equals("players");
        if (onlyPlayers) {
            if (allowMultiple)
                return EntityArg.players(identifier);
            else
                return EntityArg.player(identifier);
        } else {
            if (allowMultiple)
                return EntityArg.entities(identifier);
            else
                return EntityArg.entity(identifier);
        }
    }

    @Override
    public List<? extends Entity> convertValue(EntitySelector selector, CommandContext<ServerCommandSource> context) {
        try {
            if (getArgument().onlyPlayers()) {
                if (getArgument().allowMultiple())
                    return selector.getPlayers(context.getSource()).stream().map(BridgePlayer::new).toList();
                else
                    return List.of(new BridgePlayer(selector.getPlayer(context.getSource())));
            } else {
                if (getArgument().allowMultiple())
                    return selector.getEntities(context.getSource()).stream().map(BridgeEntity::new).toList();
                else
                    return List.of(new BridgeEntity<net.minecraft.entity.Entity>(selector.getPlayer(context.getSource())));
            }
        } catch (CommandSyntaxException e) {
            return null;
        }
    }
}
