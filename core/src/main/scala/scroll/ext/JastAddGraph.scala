package src.main.scala.scroll.ext

import java.util

import scala.reflect.ClassTag
import de.tud.deussen.jastadd.gen._
import scroll.internal.errors.SCROLLErrors
import scroll.internal.errors.SCROLLErrors.{IllegalRoleInvocationDispatch, InvocationError, RoleNotFound, SCROLLError}

class JastAddGraph[N] { // extends MutableGraph[N] {
	var graph: Tree = new Tree()

	def printTree(): Unit = {
		println("Tree: ")
		this.graph.getNaturalList.forEach(r => {
			println("  natural: " + r.getObject.toString)
			this.printNode(r,1)
		})
	}

	def printNode(node: Player, level: Int): Unit = {

		node.getRoleList.forEach(r => {
			println("    " * level + "node: " + r.getObject.toString)
			this.printNode(r, level +1)
		})
	}

	def doDispatch[E](playerObject: Object, name: String, args: Any*): Either[SCROLLError, E]= {
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			return Left(RoleNotFound(playerObject.toString, name, args))
		}

		try {
			Right(player.dispatch(name, args).asInstanceOf[E])
		} catch {
			case _: Throwable => Left(IllegalRoleInvocationDispatch(playerObject.toString, name, args))
		}
	}


	def dispatchSelect[E](playerObject: Object, name: String): Either[SCROLLError, E]= {
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			return Left(RoleNotFound(playerObject.toString, name, null))
		}

		try {
			Right(player.dispatchSelect(name).asInstanceOf[E])
		} catch {
			case _: Throwable => Left(SCROLLErrors.IllegalRoleInvocationDispatch(playerObject.toString, name, null))
		}
	}

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)

		val targetPlayer = new Role()
		targetPlayer.setObject(target)

		var sourcePlayer: Player = this.graph.findPlayerByObject(source)
		val oldTargetPlayer = this.graph.findPlayerByObject(target)

		if(oldTargetPlayer != null) {
			if(sourcePlayer != null && this.graph.getPredecessor(oldTargetPlayer) == sourcePlayer) {
				println("already exists!")
				return false
			}
			targetPlayer.setRoleList(oldTargetPlayer.getRoleList.clone())
			this.deletePlayer(target)
		}

		if(sourcePlayer == null) {
			val newNatural = new Natural()
			newNatural.setObject(source)
			this.graph.addNatural(newNatural)
			sourcePlayer = newNatural
		}
		sourcePlayer.addRole(targetPlayer)

		this.printTree()
		true
	}

	def removeRole(playerObject: Object, roleObject: Object): Unit = {
		println("removeRole: player " + playerObject.toString + ", role " + roleObject.toString)

		val player: Player = this.graph.findPlayerByObject(playerObject)
		val playerRole: Player = player.findPlayerByObject(roleObject)
		if(player == null || playerRole == null) {
			throw new Exception("playerPlayer == null || playerRole == null")
		}

		val  oldPlayer: Natural = new Natural
		oldPlayer.setObject(playerRole.getObject)
		oldPlayer.setRoleList(playerRole.getRoleList.clone())
		this.graph.addNatural(oldPlayer)

		player.removeRole(playerRole)
	}

	def deletePlayer[P <: AnyRef : ClassTag](playerObject: P): Unit =
	{
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			return
		}

		val pred = this.graph.getPredecessor(player)
		if(pred == null) {
			this.graph.removeNatural(player)
		} else {
			pred.removeRole(player)
		}
	}

	def removePlayer[P <: AnyRef : ClassTag](playerObject: P): Unit = {
		println("removePlayer: " + playerObject.toString)
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			return
		}

		player.getRoleList.forEach(r => {
			val  oldPlayer: Natural = new Natural
			oldPlayer.setObject(r.getObject)
			oldPlayer.setRoleList(r.getRoleList.clone())
			this.graph.addNatural(oldPlayer)
		})

		this.deletePlayer(playerObject)
	}

	def containsPlayer(playerObject: AnyRef): Boolean = {
		val player = this.graph.findPlayerByObject(playerObject)
		if(player != null)
			return true
		false
	}

	def allPlayers(): Seq[AnyRef] = {
		val ret = new util.LinkedHashSet[Object]
		this.graph.getNaturalList.forEach(n => {
			ret.add(n.getObject)
		})
		var lastLevel = ret
		do {
			val newLevel = new util.LinkedHashSet[Object]
			lastLevel.forEach(e => {
				newLevel.addAll(this.successors(e))
			})
			ret.addAll(newLevel)
			lastLevel = newLevel
		} while(!lastLevel.isEmpty)
		scala.collection.JavaConverters.asScalaIteratorConverter(ret.iterator()).asScala.toSeq
	}

	def successors(playerObject: AnyRef): util.Set[Object] = {
		val ret = new util.LinkedHashSet[Object]
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player != null) {
			player.getRoleList.forEach(f => {
				ret.add(f.getObject)
			})
		}
		ret
	}

	def predecessors(playerObject: AnyRef): Seq[AnyRef] = {
		var player: Player = this.graph.findPlayerByObject(playerObject)

		val ret: util.Set[Object] = new util.LinkedHashSet[Object]
		while(player != null) {
			player = this.graph.getPredecessor(player)
			if(player != null) {
				ret.add(player.getObject)
			}
		}
		scala.collection.JavaConverters.asScalaIteratorConverter(ret.iterator()).asScala.toSeq
	}
}