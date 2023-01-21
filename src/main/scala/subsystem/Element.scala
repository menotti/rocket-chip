package freechips.rocketchip.subsystem

import chisel3._
import chisel3.util._

import org.chipsalliance.cde.config.Parameters
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.interrupts._
import freechips.rocketchip.prci._
import freechips.rocketchip.tile.{LookupByHartIdImpl}
import freechips.rocketchip.subsystem._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.devices.debug.{TLDebugModule}
import freechips.rocketchip.devices.tilelink._

trait ElementParams {
  val name: String
  val clockSinkParams: ClockSinkParameters
}

abstract class InstantiableElementParams[ElementType <: LazyModule] extends ElementParams {
  def instantiate(crossing: ElementCrossingParamsLike, lookup: LookupByHartIdImpl)(implicit p: Parameters): ElementType
}

/** An interface for describing the parameteization of how Elements are connected to interconnects */
trait ElementCrossingParamsLike {
  /** The type of clock crossing that should be inserted at the element boundary. */
  def crossingType: ClockCrossingType
  /** Parameters describing the contents and behavior of the point where the element is attached as an interconnect master. */
  def master: ElementPortParamsLike
  /** Parameters describing the contents and behavior of the point where the element is attached as an interconnect slave. */
  def slave: ElementPortParamsLike
  /** The subnetwork location of the device selecting the apparent base address of MMIO devices inside the element */
  def mmioBaseAddressPrefixWhere: TLBusWrapperLocation
  /** Inject a reset management subgraph that effects the element child reset only */
  def resetCrossingType: ResetCrossingType
  /** Keep the element clock separate from the interconnect clock (e.g. even if they are synchronous to one another) */
  def forceSeparateClockReset: Boolean
}

/** An interface for describing the parameterization of how a particular element port is connected to an interconnect */
trait ElementPortParamsLike {
  /** The subnetwork location of the interconnect to which this element port should be connected. */
  def where: TLBusWrapperLocation
  /** Allows port-specific adapters to be injected into the interconnect side of the attachment point. */
  def injectNode(context: Attachable)(implicit p: Parameters): TLNode
}
