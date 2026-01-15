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

import cats.data.EitherT
import cats.effect.Async
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.config.{ConfigurateRegistry, ConfigurateRegistryExporter, DefaultRegistry, fullName}
import org.anvilpowered.anvil.core.kbrig.Command
import org.anvilpowered.anvil.core.kbrig.argument.StringArgumentType
import org.anvilpowered.anvil.core.kbrig.builder.ArgumentBuilder
import org.anvilpowered.anvil.core.kbrig.context.*
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandError, NotFoundError}
import org.anvilpowered.anvil.core.kbrig.tree.LiteralCommandNode

import java.nio.file.Files

object Generate {
  extension (factory: ConfigCommandFactory) {
    def createGenerate: LiteralCommandNode[CommandSource] =
      ArgumentBuilder
        .literal[CommandSource]("generate")
        .executes(BaseCmd(factory))
        .thenArg(
          ArgumentBuilder
            .required[CommandSource, String]("type", StringArgumentType.singleWord())
            .suggests([F[_]] =>
              (context, builder) =>
                (F: Async[F]) ?=>
                  factory.exporters
                    .filter(_.fileType.name.toLowerCase.startsWith(builder.remainingLowerCase))
                    .foreach(exporter => builder.suggest(exporter.fileType.name))
                  builder.build(),
            )
            .executes(ExporterCmd(factory, force = false))
            .thenArg(
              ArgumentBuilder
                .literal[CommandSource]("--force")
                .executes(ExporterCmd(factory, force = true))
                .build(),
            )
            .build(),
        )
        .build()
  }

  private class BaseCmd(factory: ConfigCommandFactory) extends Command[CommandSource] {
    override def execute[F[_]](context: CommandContext[CommandSource])(using F: Async[F]): EitherT[F, CommandError, Int] =
      EitherT.liftF[F, CommandError, Int](F.delay {
        context.source.sendMessage(
          Component
            .text()
            .append(Component.text("Please specify configuration format. Available: ", NamedTextColor.GREEN))
            .append(Component.text(factory.exporters.map(_.fileType.fullName).mkString(", "), NamedTextColor.GOLD))
            .build(),
        )
        0
      })
  }

  private class ExporterCmd(factory: ConfigCommandFactory, force: Boolean) extends Command[CommandSource] {
    override def execute[F[_]](context: CommandContext[CommandSource])(using F: Async[F]): EitherT[F, CommandError, Int] =
      for {
        targetType <- context.extract[F, String]("type")
        exporter <- factory.exporters.find(_.fileType.name == targetType) match {
          case Some(exporter) => EitherT.pure[F, CommandError](exporter)
          case None           => EitherT.leftT(NotFoundError("configurate exporter type", targetType))
        }
        result <- executeExport[F](context, exporter)
      } yield result

    private def executeExport[F[_]](
        context: CommandContext[CommandSource],
        exporter: ConfigurateRegistryExporter[?],
    )(using F: Async[F]): EitherT[F, CommandError, Int] = {
      val newType = exporter.fileType
      val newPath = exporter.configPath.toString

      val discoverResult = factory.discover[F].leftMap(ExportError(_))

//      return for {
//        existingType = discoverResult.fileType
//        existingPath = discoverResult.path.toString
//        res <- // recover
//      } yield 1

      if (configurateRegistry != null) {
        if (force) {
          context.source.sendMessage(
            Component
              .text()
              .append(Component.text("File ", NamedTextColor.YELLOW))
              .append(Component.text(existingPath, NamedTextColor.GOLD))
              .append(Component.text(" already exists! ", NamedTextColor.YELLOW))
              .append(Component.newline())
              .append(
                Component.text(
                  if (existingType == newType) {
                    "Overwriting because of --force!"
                  } else {
                    s"Replacing with $newPath because of --force!"
                  },
                  NamedTextColor.YELLOW,
                ),
              )
              .build(),
          )
          if (!Files.deleteIfExists(configurateRegistry.path)) {
            context.source.sendMessage(
              Component
                .text()
                .append(Component.text("File ", NamedTextColor.RED))
                .append(Component.text(existingPath, NamedTextColor.GOLD))
                .append(Component.text(" could not be deleted!", NamedTextColor.RED))
                .build(),
            )
          }
        } else {
          context.source.sendMessage(
            Component
              .text()
              .append(Component.text("File ", NamedTextColor.RED))
              .append(Component.text(existingPath, NamedTextColor.GOLD))
              .append(Component.text(" already exists!", NamedTextColor.RED))
              .append(Component.newline())
              .append(Component.text("Use --force to ", NamedTextColor.RED))
              .append(
                if (existingType == newType) {
                  Component.text("overwrite.", NamedTextColor.RED)
                } else {
                  Component
                    .text()
                    .append(Component.text("replace with ", NamedTextColor.RED))
                    .append(Component.text(newPath, NamedTextColor.GOLD))
                    .append(Component.text(".", NamedTextColor.RED))
                },
              )
              .build(),
          )
          throw CommandError("File already exists")
        }
      }

//      exporter.export(DefaultRegistry, factory.serializers)
      context.source.sendMessage(
        Component
          .text()
          .append(Component.text("Generated ", NamedTextColor.GREEN))
          .append(Component.text(newPath, NamedTextColor.GOLD))
          .append(Component.text("!", NamedTextColor.GREEN))
          .append(Component.newline())
          .append(Component.text("Please restart the server to apply changes.", NamedTextColor.DARK_GREEN))
          .build(),
      )
    }
  }

  case class ExportError(error: String) extends CommandError {
    override def text: Component = Component
      .text()
      .append(Component.text("Error occurred during configuration export:").color(NamedTextColor.RED))
      .append(Component.newline())
      .append(Component.text(error).color(NamedTextColor.RED))
      .build()
  }
}
