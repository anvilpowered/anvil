package org.anvilpowered.anvil.chat

import cats.effect.kernel.Async
import cats.kernel.Monoid
import cats.syntax.all.*
import net.kyori.adventure.audience.Audience as KAudience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.chat.ChatType.Bound
import net.kyori.adventure.chat.{ChatType, SignedMessage}
import net.kyori.adventure.chat.SignedMessage.Signature
import net.kyori.adventure.dialog.DialogLike
import net.kyori.adventure.identity.{Identified, Identity}
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.pointer.Pointered
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.sound.Sound.Emitter
import net.kyori.adventure.sound.{Sound, SoundStop}
import net.kyori.adventure.text.{Component, ComponentLike}
import net.kyori.adventure.title.{Title, TitlePart}

import java.util.UUID

class ForwardingAudience(val audiences: Seq[Audience]) extends Audience {
  private given Monoid[Boolean] = Monoid.instance(false, _ || _)
  override def sendMessage[F[_]: Async](message: Component): F[Boolean] = audiences.foldMapM(_.sendMessage(message))
  override def sendMessage[F[_]: Async](message: Component, boundChatType: ChatType.Bound): F[Boolean] = audiences.foldMapM(_.sendMessage(message, boundChatType))
  override def sendMessage[F[_]: Async](signedMessage: SignedMessage, boundChatType: Bound): F[Boolean] = audiences.foldMapM(_.sendMessage(signedMessage, boundChatType))
  override def deleteMessage[F[_]: Async](signature: Signature): F[Boolean] = audiences.foldMapM(_.deleteMessage(signature))
  override def sendActionBar[F[_]: Async](message: Component): F[Boolean] = audiences.foldMapM(_.sendActionBar(message))
  override def sendPlayerListHeaderAndFooter[F[_]: Async](header: Component, footer: Component): F[Boolean] = audiences.foldMapM(_.sendPlayerListHeaderAndFooter(header, footer))
  override def sendTitlePart[F[_]: Async, T](part: TitlePart[T], value: T): F[Boolean] = audiences.foldMapM(_.sendTitlePart(part, value))
  override def clearTitle[F[_]: Async]: F[Boolean] = audiences.foldMapM(_.clearTitle)
  override def resetTitle[F[_]: Async]: F[Boolean] = audiences.foldMapM(_.resetTitle)
  override def showBossBar[F[_]: Async](bar: BossBar): F[Boolean] = audiences.foldMapM(_.showBossBar(bar))
  override def hideBossBar[F[_]: Async](bar: BossBar): F[Boolean] = audiences.foldMapM(_.hideBossBar(bar))
  override def playSound[F[_]: Async](sound: Sound): F[Boolean] = audiences.foldMapM(_.playSound(sound))
  override def playSound[F[_]: Async](sound: Sound, x: Double, y: Double, z: Double): F[Boolean] = audiences.foldMapM(_.playSound(sound, x, y, z))
  override def playSound[F[_]: Async](sound: Sound, emitter: Sound.Emitter): F[Boolean] = audiences.foldMapM(_.playSound(sound, emitter))
  override def stopSound[F[_]: Async](stop: SoundStop): F[Boolean] = audiences.foldMapM(_.stopSound(stop))
  override def openBook[F[_]: Async](book: Book): F[Boolean] = audiences.foldMapM(_.openBook(book))
  override def sendResourcePacks[F[_]: Async](request: ResourcePackRequest): F[Boolean] = audiences.foldMapM(_.sendResourcePacks(request))
  override def removeResourcePacks[F[_]: Async](id: UUID, others: UUID*): F[Boolean] = audiences.foldMapM(_.removeResourcePacks(id, others*))
  override def clearResourcePacks[F[_]: Async]: F[Boolean] = audiences.foldMapM(_.clearResourcePacks)
  override def showDialog[F[_]: Async](dialog: DialogLike): F[Boolean] = audiences.foldMapM(_.showDialog(dialog))
  override def closeDialog[F[_]: Async]: F[Boolean] = audiences.foldMapM(_.closeDialog)
  override def filterAudience(filter: Audience => Boolean): Audience = ForwardingAudience(audiences.filter(filter))
  override def forEachAudience[F[_]: Async](action: Audience => F[Boolean]): F[Boolean] = audiences.foldMapM(action)
}
