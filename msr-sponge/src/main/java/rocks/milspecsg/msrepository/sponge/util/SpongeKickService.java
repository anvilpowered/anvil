package rocks.milspecsg.msrepository.sponge.util;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.api.util.KickService;
import rocks.milspecsg.msrepository.api.util.UserService;

import javax.inject.Inject;
import java.util.UUID;

public class SpongeKickService implements KickService {

    @Inject
    private UserService<User> userService;

    @Override
    public void kick(UUID userUUID, String reason) {
        userService.get(userUUID).flatMap(User::getPlayer).ifPresent(player -> player.kick(Text.of(reason)));
    }

    @Override
    public void kick(UUID userUUID) {
        kick(userUUID, "You have been kicked");
    }
}
