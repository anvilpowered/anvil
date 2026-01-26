package org.anvilpowered.anvil.core.platform

import cats.effect.Async
import cats.data.OptionT

trait Locatable[-T] {
  def isLocal(obj: T): Boolean
  def locate[F[_]: Async](obj: T): OptionT[F, Server]
}

object Locatable {
  extension [T: Locatable as ls](obj: T) {
    def isLocal: Boolean = ls.isLocal(obj)
    def locate[F[_]: Async]: OptionT[F, Server] = ls.locate(obj)
  }
}
