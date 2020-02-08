package org.anvilpowered.anvil.spigot.module;

import com.google.inject.TypeLiteral;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.StringResult;
import org.anvilpowered.anvil.api.util.TeleportationService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.common.module.ApiCommonModule;
import org.anvilpowered.anvil.spigot.util.SpigotKickService;
import org.anvilpowered.anvil.spigot.util.SpigotStringResult;
import org.anvilpowered.anvil.spigot.util.SpigotTeleportationService;
import org.anvilpowered.anvil.spigot.util.SpigotUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ApiSpigotModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(KickService.class).to(SpigotKickService.class);
        bind(new TypeLiteral<StringResult<TextComponent, CommandSender>>() {
        }).to(SpigotStringResult.class);
        bind(TeleportationService.class).to(SpigotTeleportationService.class);
        bind(new TypeLiteral<UserService<Player, Player>>() {
        }).to(SpigotUserService.class);
    }
}
