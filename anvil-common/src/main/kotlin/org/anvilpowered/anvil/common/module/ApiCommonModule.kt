package org.anvilpowered.anvil.common.module

import com.google.inject.AbstractModule
import com.google.inject.Provider
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.entity.RestrictionService
import org.anvilpowered.anvil.api.messaging.RedisService
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.anvil.common.entity.CommonRestrictionService
import org.anvilpowered.anvil.common.messaging.CommonRedisService
import org.anvilpowered.anvil.common.util.CommonTimeFormatService

open class ApiCommonModule : AbstractModule() {

    override fun configure() {
        bind(TimeFormatService::class.java).to(CommonTimeFormatService::class.java)
        bind(RedisService::class.java).to(CommonRedisService::class.java)
        bind(RestrictionService::class.java).toProvider(
            Provider {
                Anvil.getEnvironment().injector.getInstance(CommonRestrictionService::class.java)
            }
        )
    }
}
