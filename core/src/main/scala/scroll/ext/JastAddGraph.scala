package src.main.scala.scroll.ext

import java.util

import de.tud.deussen.jastadd.gen.{Edge, Graph, Node}
import scala.collection._


import scala.reflect.ClassTag

/*
class test123 {
	def createAst: Root = {
		println("I am creating AST...")
		val root = new Root()
		val addExp = new AddExp()
		addExp.setA(new Number(0.5f))
		addExp.setB(new Number(1))

		val someVar = new Var("varName")
		val mulExp = new MulExp()
		mulExp.setA(addExp)
		mulExp.setB(someVar)

		root.setExp(mulExp)
		return root
	}
}
*/

class JastAddGraph[N] { // extends MutableGraph[N] {

	var graph = new Graph()

	def putEdge(source: Object, target: Object): Boolean = {
		println("putEdge:" + source.toString + " " +target.toString)
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
		println("removeEdge:" + source.toString + " " +target.toString)
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
		println("edges(): ")
		val someSet = new util.HashSet[Edge]()
		this.graph.getEdgeList.forEach(edge => {
			someSet.add(edge)
		})
		someSet
	}

	def nodes(): util.Set[Object] = {
		println("nodes(): ")
		val nodes = new util.HashSet[Object]()
		this.graph.getNodeList.forEach(node => {
			nodes.add(node.getValue)
		})
		nodes
	}

	//fixme for cycles
	def successors(node: Object) : util.Set[Object] = {
		println("successors: ")

		val values = new util.HashSet[Object]()

		val it = this.graph.getEdgeList.iterator
		while(it.hasNext) {
			val edge = it.next()
			if(edge.getSource == node) {
				values.add(edge.getTarget)
				values.addAll(successors(edge.getTarget))
			}
		}
		values
	}

	//fixme for cycles
	def predecessors(node: Object): util.Set[Object] = {
		println("predecessors: ")
		val values = new util.HashSet[Object]()

		val it = this.graph.getEdgeList.iterator
		while(it.hasNext) {
			val edge = it.next()
			if(edge.getTarget == node) {
				values.add(edge.getSource)
				values.addAll(predecessors(edge.getSource))
			}
		}
		values
	}
}