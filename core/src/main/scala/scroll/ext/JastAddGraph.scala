package src.main.scala.scroll.ext

import java.util

import de.tud.deussen.jastadd.gen.{Edge, Graph, Node}

class JastAddGraph[N] { // extends MutableGraph[N] {

	var graph = new Graph()

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " + target.toString)
		if(!this.nodes.contains(source)) {
			val node = new Node()
			node.setValue(source)
			this.graph.addNode(node)
		}
		if(!this.nodes.contains(target)) {
			val node = new Node()
			node.setValue(target)
			this.graph.addNode(node)
		}

		val edge = new Edge()
		edge.setSource(source)
		edge.setTarget(target)
		val it = this.graph.getEdgeList.iterator
		while(it.hasNext) {
			val edge = it.next()
			if(edge.getSource == source && edge.getTarget == target)
				return false
		}
		this.graph.addEdge(edge)

		true
	}

	def removeEdge(source: Object, target: Object): Boolean = {
		println("removeEdge:" + source.toString + " " + target.toString)
		val it = this.graph.getEdgeList.iterator
		while(it.hasNext) {
			val edge = it.next()
			if(edge.getSource == source && edge.getTarget == target) {
				it.remove()
				return true
			}
		}
		false
	}

	def removeNode(node: Object): Boolean = {
		println("removeNode: " + node.toString)
		var graphChanged = false
		val nodeIt = this.graph.getNodeList.iterator
		while(nodeIt.hasNext) {
			val node = nodeIt.next()
			if(node.getValue == node) {
				nodeIt.remove()
				graphChanged = true
			}
		}
		val edgeIt = this.graph.getEdgeList.iterator
		while(edgeIt.hasNext) {
			val edge = edgeIt.next()
			if(edge.getSource == node || edge.getTarget == node) {
				edgeIt.remove()
				graphChanged = true
			}

		}
		graphChanged
	}


	def hasCycle: Boolean = {
		println("hasCycle(): checking cycle...")

		val it = this.graph.getNodeList.iterator
		while(it.hasNext) {
			val node = it.next()
			val successors = this.successors(node)
			if(successors.size() > 0) {
				successors.retainAll(this.predecessors(node))
				if(successors.size() > 0) {
					return true
				}
			}
		}
		false
	}

	def edges(): util.Set[Edge] = {
		val someSet = new util.HashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			someSet.add(edge)
			//println("edges(): " + edge.getSource.toString + " " + edge.getTarget.toString)
		})
		someSet
	}

	def nodes(): util.Set[Object] = {
		val nodes = new util.HashSet[Object]()
		this.graph.getNodeList.forEach(node => {
			nodes.add(node.getValue)
			//println("nodes(): " + node.getValue.toString)
		})
		nodes
	}

	def successors(node: Object, root: Object = None): util.Set[Object] = {
		println("successors: " + node.toString)
		val rootVal = if(root == None) node else root
		val values = new util.HashSet[Object]()
		edges().forEach(edge => {
			if(edge.getSource == node && edge.getTarget != rootVal) {
				values.add(edge.getTarget)
				values.addAll(successors(edge.getTarget, rootVal))
			}
		})
		values
	}

	def predecessors(node: Object, root: Object = None): util.Set[Object] = {
		println("predecessors: " + node.toString)
		val rootVal = if(root == None) node else root
		val values = new util.HashSet[Object]()
		edges().forEach(edge => {
			if(edge.getTarget == node && edge.getSource != rootVal) {
				values.add(edge.getSource)
				values.addAll(predecessors(edge.getSource, rootVal))
			}
		})
		values
	}

}