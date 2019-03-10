package scroll.internal.graph

import src.main.scala.scroll.internal.graph.{CachedJastAddRoleGraph, JastAddRoleGraph}

object ScalaRoleGraphBuilder {
  private[this] var _cached: Boolean = true
  private[this] var _jastAdd: Boolean = true
  private[this] var _checkForCycles: Boolean = false


  def cached(cached: Boolean): ScalaRoleGraphBuilder.type = {
    _cached = cached
    this
  }

  def jastAdd(jastAdd: Boolean): ScalaRoleGraphBuilder.type = {
    _jastAdd = jastAdd
    this
  }

  def checkForCycles(checkForCycles: Boolean): ScalaRoleGraphBuilder.type = {
    _checkForCycles = checkForCycles
    this
  }

  def build: RoleGraph = (_cached, _jastAdd) match {
    case (true, false) => new CachedScalaRoleGraph(checkForCycles = _checkForCycles)
    case (false, false) => new ScalaRoleGraph(checkForCycles = _checkForCycles)
    case (true, true) => new CachedJastAddRoleGraph(checkForCycles = _checkForCycles)
    case (false, true) => new JastAddRoleGraph(checkForCycles = _checkForCycles)
  }
}
