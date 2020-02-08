package org.anvilpowered.anvil.api.event.events.entity.Projectile;

import org.anvilpowered.anvil.api.Interface.entity.Projectile;
import org.anvilpowered.anvil.api.event.Cancellable;
import org.anvilpowered.anvil.api.event.events.entity.EntityEvent;

public interface ProjectileLaunchEvent extends EntityEvent, Cancellable {

    Projectile getProjectile();

    ProjectileSource getSource();

}
