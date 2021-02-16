/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.common.anvilnet.communicator

import com.google.common.io.ByteStreams
import com.google.inject.Inject
import jetbrains.exodus.util.LightByteArrayOutputStream
import org.anvilpowered.anvil.common.anvilnet.CommonAnvilNetService
import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket
import org.anvilpowered.anvil.common.anvilnet.packet.data.PlayerData
import org.slf4j.Logger
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

abstract class BaseCommunicator protected constructor(
  name: String,
  private val connectionType: ConnectionType
) : Communicator {

  companion object {
    const val CHANNEL_NAME = "anvilnet"
    const val CHANNEL_NAME_NAMESPACED = "anvilnet:anvilnet"
  }

  @Inject
  protected lateinit var logger: Logger

  @Inject
  private lateinit var packetTranslator: CommonPacketTranslator

  @Inject
  private lateinit var sequenceGenerator: CommonSequenceGenerator

  @Inject
  protected lateinit var broadcastNetwork: BroadcastNetwork

  @Inject
  protected lateinit var pluginMessageNetwork: PluginMessageNetwork

  protected var listener: ((AnvilNetPacket) -> Unit)? = null

  /**
   * The 32 bit id for this node. The initial (invalid) value 0 should be updated as
   * early as possible.
   */
  @JvmField
  protected var nodeId = 0
  private val prefix: String = "[AnvilNet] [$name] "

  override fun setListener(nodeId: Int, listener: ((AnvilNetPacket) -> Unit)?) {
    this.nodeId = nodeId
    this.listener = listener
  }

  /**
   * Implementations should check [NetworkPath.shouldForwardTo]
   *
   * @param header The [NetworkHeader] of the message
   * @param data   The entire message to send
   */
  protected abstract fun send(header: NetworkHeader, data: ByteArray): Boolean

  override fun send(target: Int, type: Int, packet: AnvilNetPacket): Boolean {
    val outputStream = LightByteArrayOutputStream(512)
    packet.write(ByteStreams.newDataOutput(outputStream))
    val header = NetworkHeader(sequenceGenerator.next(), nodeId, target, type, outputStream.size(), connectionType)
    header.path.setForwardingType(packet.getContainer(PlayerData::class)?.userUUID)
    logger.info("[Send] $header")
    return send(header, getData(header, outputStream))
  }

  /**
   * @return The UTF-8 encoded default AnvilNet channel name
   */
  protected val channel: ByteArray
    get() = CHANNEL_NAME.toByteArray(StandardCharsets.UTF_8)

  /**
   * @param target The nodeId of the target node
   * @return The UTF-8 encoded default AnvilNet channel name followed
   * by a delimiter and the provided target nodeId
   */
  protected fun getChannel(target: Int): ByteArray {
    if (target == 0) {
      return channel
    }
    val bytes = (CHANNEL_NAME + CommonAnvilNetService.MESSAGE_DELIMITER).toByteArray(StandardCharsets.UTF_8)
    val bytesLength = bytes.size
    val data = ByteArray(bytesLength + 8)
    System.arraycopy(bytes, 0, data, 0, bytesLength)
    data.write(bytesLength, target)
    return data
  }

  protected fun logSent(header: NetworkHeader, data: ByteArray) {
    logger.info(
      "[Send] {} to {}: {}",
      header.path.forwardingType,
      header.path.targetPretty,
      data.formatHex()
    )
  }

  protected fun logReceive(header: NetworkHeader, data: ByteArray) {
    logger.info(
      "[Receive] {} from {}: {}",
      header.path.forwardingType,
      header.path.sourcePretty,
      data.formatHex()
    )
  }

  /**
   * Accepts the provided data and runs [listener]
   *
   * TODO: FIX THIS JAVADOC
   * Returns 0 if this node is the message target or if the message was a broadcast.
   * Returns non-0 if this node is not the final recipient of the message.
   *
   *
   *
   * A non-0 result indicates that this message should be forwarded to the final recipient
   * **if the final recipient will not receive the message another way.**
   * Typically, in a PluginMessaging-only network, this means it has to be resent. However, in a
   * configuration similar to Redis, it does not.
   *
   *
   * @param inputStream The data to accept and parse
   * @return 0 if this is node is a final recipient of the message, otherwise non-0
   */
  protected fun accept(inputStream: ByteArrayInputStream): NetworkHeader? {
    val input = ByteStreams.newDataInput(inputStream)
    val header = try {
      NetworkHeader(input, nodeId, connectionType)
    } catch (e: Exception) {
      logger.error("An error occurred parsing incoming packet from nodeId $nodeId, discarding", e)
      return null
    }
    // subsequent reads will start after the header
    inputStream.mark(0)
    val packet = packetTranslator.parse(header, input)
    header.path.setForwardingType(packet?.getContainer(PlayerData::class)?.userUUID)
    logger.info("[Receive] $header")
    // reset the inputStream to right after the header
    inputStream.reset()
    if (packet != null) {
      // In some cases, the forwarding type depends on the packet
      // Here, we are checking whether the packet has UUID information
      packet.updateNetwork(broadcastNetwork, pluginMessageNetwork)
      if (header.path.forwardingType.isForMe) {
        listener?.invoke(packet)
      }
    }
    return header
  }

  /**
   *
   * Combines the provided target, type and message into a byte array ready to be sent.
   * Uses [LightByteArrayOutputStream] because it does not create an unnecessary copy of the byte array
   * @param outputStream A [LightByteArrayOutputStream] containing the message to send
   * @return A ready-to-send byte array constructed from the provided data
   */
  protected fun getData(header: NetworkHeader, outputStream: LightByteArrayOutputStream): ByteArray {
    val payloadLength = outputStream.size()
    val data = ByteArray(header.length + payloadLength)
    header.write(data, 0)
    System.arraycopy(outputStream.toByteArray(), 0, data, header.length, payloadLength)
    return data
  }

  protected fun getData(header: NetworkHeader, inputStream: ByteArrayInputStream): ByteArray {
    val payloadLength = inputStream.available()
    val data = ByteArray(header.length + payloadLength)
    header.write(data, 0)
    val actual = inputStream.read(data, 0, payloadLength)
    check(actual == payloadLength) { "payload length mismatch. Expected $payloadLength != actual $actual" }
    return data
  }
}
