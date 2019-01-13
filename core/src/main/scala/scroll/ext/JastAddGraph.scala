package src.main.scala.scroll.ext

import java.util

import scala.reflect.ClassTag
import de.tud.deussen.jastadd.gen._
import scroll.internal.errors.SCROLLErrors
import scroll.internal.errors.SCROLLErrors.{IllegalRoleInvocationDispatch, RoleNotFound, SCROLLError}

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

	def setDispatchQuery(playerObject: AnyRef, excludeClasses: Seq[Any], excludePlayers: Seq[Object], includeClasses: Seq[Any], includePlayers: Seq[Object]): Unit = {
		//println("setting DispatchQuery: " + excludeClasses + ", " + excludePlayers)
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			println("player not found: " + playerObject)
			return
		}
		val dispatchQuery = new FilterDispatchQuery()
		val excludes = new Filter()
		excludeClasses.foreach(x => {
			val wrapper = new ObjectWrapper()
			wrapper.setObject(x)
			excludes.addClasses(wrapper.clone())
		})
		excludePlayers.foreach(x => {
			val wrapper = new ObjectWrapper()
			wrapper.setObject(x)
			excludes.addPlayers(wrapper.clone())
		})
		dispatchQuery.setExcludes(excludes)
		val includes = new Filter()
		includeClasses.foreach(x => {
			val wrapper = new ObjectWrapper()
			wrapper.setObject(x)
			includes.addClasses(wrapper.clone())
		})
		includePlayers.foreach(x => {
			val wrapper = new ObjectWrapper()
			wrapper.setObject(x)
			includes.addPlayers(wrapper.clone())
		})
		dispatchQuery.setIncludes(includes)
		player.setDispatchQuery(dispatchQuery)
	}

	def dispatchObjectForApply[E](playerObject: Object, name: String, args: Array[Any]): Either[SCROLLError, (AnyRef, java.lang.reflect.Method)] = {
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			return Left(RoleNotFound(playerObject.toString, name, args))
		}
		try {
			val ret = player.dispatchObjectForApply(this.graph, name, args.asInstanceOf[Array[Object]])
			Right(ret)
		} catch {
			case r: Throwable => {
				println("apply got throwable: " + r.getMessage + r.getCause + r.getStackTrace)
				Left(IllegalRoleInvocationDispatch(playerObject.toString, name, args))
			}
		}
	}

	def dispatchObjectForSelect(playerObject: Object, name: String): Either[SCROLLError, AnyRef]= {
		val player: Player = this.graph.findPlayerByObject(playerObject)
		if(player == null) {
			return Left(RoleNotFound(playerObject.toString, name, null))
		}
		try {
			val ret = player.dispatchObjectForSelect(name)
			Right(ret)
		} catch {
			case _: Throwable => Left(SCROLLErrors.IllegalRoleInvocationDispatch(playerObject.toString, name, null))
		}
	}

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)

		var sourcePlayer: Player = this.graph.findPlayerByObject(source)

		if(sourcePlayer == null) {
			val newNatural = new Natural()
			newNatural.setObject(source)
			newNatural.setDispatchQuery(new DispatchQuery())
			this.graph.addNatural(newNatural)
			sourcePlayer = newNatural
		}

		val targetPlayer = new Role()
		targetPlayer.setObject(target)
		targetPlayer.setDispatchQuery(new DispatchQuery())

		val oldTargetPlayer = this.graph.findPlayerByObject(target)
		if(oldTargetPlayer != null) {
			if(sourcePlayer != null && this.graph.getPredecessor(oldTargetPlayer) == sourcePlayer) {
				println("already exists!")
				return false
			}
			targetPlayer.setRoleList(oldTargetPlayer.getRoleList.clone())
			this.deletePlayer(target)
		}

		sourcePlayer.addRole(targetPlayer)
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