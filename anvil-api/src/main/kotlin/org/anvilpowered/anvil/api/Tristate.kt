package org.anvilpowered.anvil.api


/**
 * Represents three possible states.
 *
 * [TRUE] A positive setting
 * [FALSE] A negative setting
 * [UNDEFINED] A non-existent setting
 **/
enum class Tristate(state: Boolean?) {

  /**
   * Represents a positive state
   */
  TRUE(true),

  /**
   * Represents a negative state
   */
  FALSE(false),

  /**
   * Represents a non-existent state
   */
  UNDEFINED(null);

  val isTrue = state == true
  val isFalse = state == false
  val isUndefined = state == null
}
