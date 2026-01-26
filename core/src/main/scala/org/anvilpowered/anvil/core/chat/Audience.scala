package org.anvilpowered.anvil.core.chat

import cats.effect.kernel.Async
import cats.kernel.Monoid
import cats.syntax.all.*
import net.kyori.adventure.audience.Audience as KAudience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.chat.ChatType.Bound
import net.kyori.adventure.chat.SignedMessage.Signature
import net.kyori.adventure.chat.{ChatType, SignedMessage}
import net.kyori.adventure.dialog.DialogLike
import net.kyori.adventure.identity.{Identified, Identity}
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.pointer.Pointered
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.sound.Sound.Emitter
import net.kyori.adventure.sound.{Sound, SoundStop}
import net.kyori.adventure.text.{Component, ComponentLike}
import net.kyori.adventure.title.{Title, TitlePart}
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.foo

import java.util.UUID

trait Audience extends Pointered {

  /** Send a system message.
    */
  def sendMessage[F[_]: Async](message: Component): F[Boolean]

  /** Disguised Player Messages */

  def sendMessage[F[_]: Async](message: Component, boundChatType: ChatType.Bound): F[Boolean]

  /** Signed Player Messages */

  def sendMessage[F[_]: Async](signedMessage: SignedMessage, boundChatType: ChatType.Bound): F[Boolean]

  def deleteMessage[F[_]: Async as F](signedMessage: SignedMessage): F[Boolean] =
    if (signedMessage.canDelete) {
      deleteMessage(signedMessage.signature())
    } else {
      F.pure(false)
    }

  def deleteMessage[F[_]: Async](signature: SignedMessage.Signature): F[Boolean]

  /* ========== Action Bar ========== */

  def sendActionBar[F[_]: Async](message: Component): F[Boolean]

  /* ========== Player List ========== */

  def sendPlayerListHeader[F[_]: Async](header: Component): F[Boolean] =
    sendPlayerListHeaderAndFooter(header, Component.empty())

  def sendPlayerListFooter[F[_]: Async](footer: Component): F[Boolean] =
    sendPlayerListHeaderAndFooter(Component.empty(), footer)

  def sendPlayerListHeaderAndFooter[F[_]: Async](header: Component, footer: Component): F[Boolean]

  /* ========== Titles ========== */

  def showTitle[F[_]: Async as F](title: Title): F[Boolean] =
    for {
      a <- Option(title.times()).fold(F.pure(false))(sendTitlePart(TitlePart.TIMES, _))
      b <- sendTitlePart(TitlePart.SUBTITLE, title.subtitle())
      c <- sendTitlePart(TitlePart.TITLE, title.title())
    } yield a && b && c

  def sendTitlePart[F[_]: Async, T](part: TitlePart[T], value: T): F[Boolean]

  def clearTitle[F[_]: Async]: F[Boolean]

  def resetTitle[F[_]: Async]: F[Boolean]

  /* ========== Boss Bars ========== */

  def showBossBar[F[_]: Async](bar: BossBar): F[Boolean]

  def hideBossBar[F[_]: Async](bar: BossBar): F[Boolean]

  /* ========== Sounds ========== */

  def playSound[F[_]: Async](sound: Sound): F[Boolean]

  def playSound[F[_]: Async](sound: Sound, x: Double, y: Double, z: Double): F[Boolean]

  def playSound[F[_]: Async](sound: Sound, emitter: Sound.Emitter): F[Boolean]

  def stopSound[F[_]: Async](sound: Sound): F[Boolean] = stopSound(sound.asStop())

  def stopSound[F[_]: Async](stop: SoundStop): F[Boolean]

  /* ========== Books ========== */

  def openBook[F[_]: Async](book: Book): F[Boolean]

  /* ========== Resource Packs ========== */

  def sendResourcePacks[F[_]: Async](request: ResourcePackRequest): F[Boolean]

  def removeResourcePacks[F[_]: Async as F](request: ResourcePackRequest): F[Boolean] = {
    val infos = request.packs()
    if (infos.isEmpty) {
      F.pure(false)
    } else if (infos.size() == 1) {
      removeResourcePacks(infos.get(0).id())
    } else {
      val otherIds = infos
        .stream()
        .skip(1)
        .map[UUID](_.id())
        .toArray[UUID](_ => new Array[UUID](infos.size() - 1))
      removeResourcePacks(infos.get(0).id(), otherIds*)
    }
  }

  def removeResourcePacks[F[_]: Async as F](ids: Iterable[UUID]): F[Boolean] = {
    val it = ids.iterator
    if (!it.hasNext) {
      F.pure(false)
    } else {
      val id = it.next()
      val others = it.toArray
      removeResourcePacks(id, others*)
    }
  }

  def removeResourcePacks[F[_]: Async](id: UUID, others: UUID*): F[Boolean]

  def clearResourcePacks[F[_]: Async]: F[Boolean]

  /* ========== Dialogs ========== */

  def showDialog[F[_]: Async](dialog: DialogLike): F[Boolean]

  def closeDialog[F[_]: Async]: F[Boolean]

  /* ========== Filtering and Iteration ========== */

  def filterAudience(filter: Audience => Boolean): Audience

  def forEachAudience[F[_]: Async](action: Audience => F[Boolean]): F[Boolean]
}

object Audience {

  implicit def anvilComponentLikeAsComponent(componentLike: ComponentLike): Component = componentLike.asComponent()
  implicit def anvilIdentifiedAsIdentity(identified: Identified): Identity = identified.identity()

  def fromKyori(audience: KAudience): Audience = new Audience {

    override def sendMessage[F[_]: Async as F](message: Component): F[Boolean] = F.delay {
      audience.sendMessage(message)
      true
    }

    override def sendMessage[F[_]: Async as F](message: Component, boundChatType: Bound): F[Boolean] = F.delay {
      audience.sendMessage(message, boundChatType)
      true
    }

    override def sendMessage[F[_]: Async as F](signedMessage: SignedMessage, boundChatType: Bound): F[Boolean] = F.delay {
      audience.sendMessage(signedMessage, boundChatType)
      true
    }

    override def deleteMessage[F[_]: Async as F](signature: Signature): F[Boolean] = F.delay {
      audience.deleteMessage(signature)
      true
    }

    override def sendActionBar[F[_]: Async as F](message: Component): F[Boolean] = F.delay {
      audience.sendActionBar(message)
      true
    }

    override def sendPlayerListHeaderAndFooter[F[_]: Async as F](header: Component, footer: Component): F[Boolean] = F.delay {
      audience.sendPlayerListHeaderAndFooter(header, footer)
      true
    }

    override def sendTitlePart[F[_]: Async as F, T](part: TitlePart[T], value: T): F[Boolean] = F.delay {
      audience.sendTitlePart(part, value)
      true
    }

    override def clearTitle[F[_]: Async as F]: F[Boolean] = F.delay {
      audience.clearTitle()
      true
    }

    override def resetTitle[F[_]: Async as F]: F[Boolean] = F.delay {
      audience.resetTitle()
      true
    }

    override def showBossBar[F[_]: Async as F](bar: BossBar): F[Boolean] = F.delay {
      audience.showBossBar(bar)
      true
    }

    override def hideBossBar[F[_]: Async as F](bar: BossBar): F[Boolean] = F.delay {
      audience.hideBossBar(bar)
      true
    }

    override def playSound[F[_]: Async as F](sound: Sound): F[Boolean] = F.delay {
      audience.playSound(sound)
      true
    }

    override def playSound[F[_]: Async as F](sound: Sound, x: Double, y: Double, z: Double): F[Boolean] = F.delay {
      audience.playSound(sound, x, y, z)
      true
    }

    override def playSound[F[_]: Async as F](sound: Sound, emitter: Emitter): F[Boolean] = F.delay {
      audience.playSound(sound, emitter)
      true
    }

    override def stopSound[F[_]: Async as F](stop: SoundStop): F[Boolean] = F.delay {
      audience.stopSound(stop)
      true
    }

    override def openBook[F[_]: Async as F](book: Book): F[Boolean] = F.delay {
      audience.openBook(book)
      true
    }

    override def sendResourcePacks[F[_]: Async as F](request: ResourcePackRequest): F[Boolean] = F.delay {
      audience.sendResourcePacks(request)
      true
    }

    override def removeResourcePacks[F[_]: Async as F](id: UUID, others: UUID*): F[Boolean] = F.delay {
      audience.removeResourcePacks(id, others*)
      true
    }

    override def clearResourcePacks[F[_]: Async as F]: F[Boolean] = F.delay {
      audience.clearResourcePacks()
      true
    }

    override def showDialog[F[_]: Async as F](dialog: DialogLike): F[Boolean] = F.delay {
      audience.showDialog(dialog)
      true
    }

    override def closeDialog[F[_]: Async as F]: F[Boolean] = F.delay {
      audience.closeDialog()
      true
    }

    override def filterAudience(filter: Audience => Boolean): Audience =
      if (filter(this)) this else empty

    override def forEachAudience[F[_]: Async as F](action: Audience => F[Boolean]): F[Boolean] = action(this)
  }

  val empty: Audience = new Audience {
    override def sendMessage[F[_]: Async as F](message: Component): F[Boolean] = F.pure(false)
    override def sendMessage[F[_]: Async as F](message: Component, boundChatType: Bound): F[Boolean] = F.pure(false)
    override def sendMessage[F[_]: Async as F](signedMessage: SignedMessage, boundChatType: Bound): F[Boolean] = F.pure(false)
    override def deleteMessage[F[_]: Async as F](signature: Signature): F[Boolean] = F.pure(false)
    override def sendActionBar[F[_]: Async as F](message: Component): F[Boolean] = F.pure(false)
    override def sendPlayerListHeaderAndFooter[F[_]: Async as F](header: Component, footer: Component): F[Boolean] = F.pure(false)
    override def sendTitlePart[F[_]: Async as F, T](part: TitlePart[T], value: T): F[Boolean] = F.pure(false)
    override def clearTitle[F[_]: Async as F]: F[Boolean] = F.pure(false)
    override def resetTitle[F[_]: Async as F]: F[Boolean] = F.pure(false)
    override def showBossBar[F[_]: Async as F](bar: BossBar): F[Boolean] = F.pure(false)
    override def hideBossBar[F[_]: Async as F](bar: BossBar): F[Boolean] = F.pure(false)
    override def playSound[F[_]: Async as F](sound: Sound): F[Boolean] = F.pure(false)
    override def playSound[F[_]: Async as F](sound: Sound, x: Double, y: Double, z: Double): F[Boolean] = F.pure(false)
    override def playSound[F[_]: Async as F](sound: Sound, emitter: Emitter): F[Boolean] = F.pure(false)
    override def stopSound[F[_]: Async as F](stop: SoundStop): F[Boolean] = F.pure(false)
    override def openBook[F[_]: Async as F](book: Book): F[Boolean] = F.pure(false)
    override def sendResourcePacks[F[_]: Async as F](request: ResourcePackRequest): F[Boolean] = F.pure(false)
    override def removeResourcePacks[F[_]: Async as F](id: UUID, others: UUID*): F[Boolean] = F.pure(false)
    override def clearResourcePacks[F[_]: Async as F]: F[Boolean] = F.pure(false)
    override def showDialog[F[_]: Async as F](dialog: DialogLike): F[Boolean] = F.pure(false)
    override def closeDialog[F[_]: Async as F]: F[Boolean] = F.pure(false)
    override def filterAudience(filter: Audience => Boolean): Audience = this
    override def forEachAudience[F[_]: Async as F](action: Audience => F[Boolean]): F[Boolean] = F.pure(false)
  }
}

trait WithAudience[-T] {
  def audience(obj: T): Audience
}

object WithAudience {
  extension [T: WithAudience as wa](obj: T) {
    def audience: Audience = wa.audience(obj)
  }
}
