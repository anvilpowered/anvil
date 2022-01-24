package org.anvilpowered.anvil.base.plugin

import java.nio.file.Path

/*
Modeled after https://github.com/LuckPerms/LuckPerms/blob/664cad21a9c24954503a78b44ae218d13353a465/common/src/main/java/me/lucko/luckperms/common/plugin/classpath/ClassPathAppender.java#L33
 */
interface ClassPathAppender : AutoCloseable {

    fun addJarToPath(path: Path)
}