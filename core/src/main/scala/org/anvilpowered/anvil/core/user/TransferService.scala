package org.anvilpowered.anvil.core.user

import cats.effect.Async

trait TransferService {
  def transfer[F[_]: Async](player: Player)(host: String, port: Int): F[Unit]
  def storeCookie[F[_]: Async](player: Player)(key: String, value: Array[Byte]): F[Unit]
}

object TransferService {
  extension (player: Player)(using tr: TransferService) {
    def transfer[F[_]: Async](host: String, port: Int): F[Unit] = tr.transfer(player)(host, port)
    def storeCookie[F[_]: Async](key: String, value: Array[Byte]): F[Unit] = tr.storeCookie(player)(key, value)
  }
}
