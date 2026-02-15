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

package org.anvilpowered.anvil.platform.command.config

import cats.data.{EitherT, ReaderT}
import cats.effect.Async
import fs2.io.file.{Files, Path}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.command.Command
import org.anvilpowered.anvil.command.argument.StringArgumentType
import org.anvilpowered.anvil.command.builder.ArgumentBuilder
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.{AlreadyExistsReplaceError, CommandError, CouldNotDeleteError, NotFoundError}
import org.anvilpowered.anvil.command.suggestion.Suggestions
import org.anvilpowered.anvil.command.tree.LiteralCommandNode
import org.anvilpowered.anvil.config.ConfigurateRegistry.DiscoverResult
import org.anvilpowered.anvil.config.{ConfigurateFileType, ConfigurateRegistryExporter, DefaultRegistry}
import org.anvilpowered.anvil.platform.command.CommandSource
import org.spongepowered.configurate.ConfigurateException

object Generate {
  extension (factory: ConfigCommandFactory) {
    def createGenerate: LiteralCommandNode[CommandSource] =
      ArgumentBuilder
        .literal[CommandSource]("generate")
        .executesCmd(BaseCmd(factory))
        .thenArg(
          ArgumentBuilder
            .required[CommandSource, String]("type", StringArgumentType.singleWord())
            .suggests([F[_]] =>
              (context: CommandContext[CommandSource]) =>
                (F: Async[F]) ?=>
                  Suggestions.ofSeq[F, ConfigurateRegistryExporter[?]](
                    ReaderT { text => F.pure(factory.exporters.filter(_.fileType.name.toLowerCase.startsWith(text))) },
                    _.fileType.name,
                  ),
            )
            .executesCmd(ExporterCmd(factory, force = false))
            .thenArg(
              ArgumentBuilder
                .literal[CommandSource]("--force")
                .executesCmd(ExporterCmd(factory, force = true))
                .build(),
            )
            .build(),
        )
        .build()
  }

  private class BaseCmd(factory: ConfigCommandFactory) extends Command[CommandSource] {
    override def execute[F[_]: Async as F](context: CommandContext[CommandSource]): EitherT[F, CommandError, Component] =
      EitherT.pure(
        Component
          .text()
          .append(Component.text("Please specify configuration format. Available: ", NamedTextColor.GREEN))
          .append(Component.text(factory.exporters.map(_.fileType.fullName).mkString(", "), NamedTextColor.GOLD))
          .build(),
      )
  }

  private class ExporterCmd(factory: ConfigCommandFactory, force: Boolean) extends Command[CommandSource] {
    override def execute[F[_]](context: CommandContext[CommandSource])(using F: Async[F]): EitherT[F, CommandError, Component] =
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
    )(using F: Async[F]): EitherT[F, CommandError, Component] = {
      for {
        discoverResult <- factory.discover[F](using F).leftMap[CommandError](ExportError(_))
        _ <- discoverResult match {
          case Some(result) => deleteIfExists[F](context, result, exporter.fileType, exporter.configPath)
          case None         => EitherT.liftF(F.pure(()))
        }
        _ <- exporter.exportRegistry(DefaultRegistry, factory.serializers).leftMap(ConfigurateCommandError(_))
      } yield Component
        .text()
        .append(Component.text("Generated ", NamedTextColor.GREEN))
        .append(Component.text(exporter.configPath.toString, NamedTextColor.GOLD))
        .append(Component.text("!", NamedTextColor.GREEN))
        .append(Component.newline())
        .append(Component.text("Please restart the server to apply changes.", NamedTextColor.DARK_GREEN))
        .build()
    }

    private def deleteIfExists[F[_]](
        context: CommandContext[CommandSource],
        result: DiscoverResult,
        newFileType: ConfigurateFileType[?],
        newPath: Path,
    )(using F: Async[F]): EitherT[F, CommandError, Unit] =
      if (force) for {
        deleted <- EitherT.liftF(Files.forAsync[F].deleteIfExists(result.path))
        _ <-
          if (deleted)
            EitherT.liftF(
              context.source.audience.sendMessage(
                Component
                  .text()
                  .append(Component.text("File ", NamedTextColor.YELLOW))
                  .append(Component.text(result.path.toString, NamedTextColor.GOLD))
                  .append(Component.text(" already exists! ", NamedTextColor.YELLOW))
                  .append(Component.newline())
                  .append(
                    Component.text(
                      if (newFileType == result.fileType) {
                        "Overwriting because of --force!"
                      } else {
                        s"Replacing with $newPath because of --force!"
                      },
                      NamedTextColor.YELLOW,
                    ),
                  )
                  .build(),
              ),
            )
          else EitherT.leftT(CouldNotDeleteError("file", result.path.toString))
      } yield ()
      else
        EitherT.leftT(
          AlreadyExistsReplaceError(
            "file",
            result.path.toString,
            Option.when(newFileType != result.fileType) { newPath.toString },
          ),
        )
  }

  case class ExportError(error: String) extends CommandError {
    override def text: Component = Component
      .text()
      .append(Component.text("Error occurred during configuration export:").color(NamedTextColor.RED))
      .append(Component.newline())
      .append(Component.text(error).color(NamedTextColor.RED))
      .build()
  }
  case class ConfigurateCommandError(ex: ConfigurateException) extends CommandError {
    override def text: Component = Component
      .text()
      .append(Component.text(s"A configurate exception occurred: ${ex.getClass}"))
      .append(Component.newline())
      .append(Component.text(ex.getMessage))
      .color(NamedTextColor.RED)
      .build()
  }
}
