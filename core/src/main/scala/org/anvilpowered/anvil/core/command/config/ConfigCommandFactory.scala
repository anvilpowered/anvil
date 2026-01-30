/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.core.command.config

import cats.data.{EitherT, Nested, OptionT}
import cats.effect.Async
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.command.config.Generate.createGenerate
import org.anvilpowered.anvil.core.config.ConfigurateRegistry.DiscoverResult
import org.spongepowered.configurate.serialize.TypeSerializerCollection

class ConfigCommandFactory(
    val registry: Registry,
    val discover: [F[_]] => (F: Async[F]) ?=> EitherT[F, String, Option[DiscoverResult]],
    val exporters: List[ConfigurateRegistryExporter[?]],
    val serializers: TypeSerializerCollection,
)(using KeyNamespace) {
  def create(): LiteralCommandNode[CommandSource] =
    ArgumentBuilder
      .literal[CommandSource]("config")
      .thenArg(this.createGet)
      .thenArg(this.createGenerate)
      .build()
}
