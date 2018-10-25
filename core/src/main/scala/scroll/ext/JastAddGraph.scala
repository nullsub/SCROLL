package src.main.scala.scroll.ext

import java.util

//import de.tud.deussen.jastadd.gen.{AddExp, MulExp, Number, Root, Var}

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

	def putEdge(source: N, target: N): Boolean = {
		println("putEdge: todo implement")
		true
	}

	def removeEdge(player: N, role: N): Boolean = {
		println("removeEdge: todo implement")
		true
	}

	def removeNode(player: N): Boolean = {
		println("removeNode: todo implement")
		true
	}

	def hasCycle: Boolean = {
		println("hasCycle: todo implement")
		false
	}

	def edges(): util.Set[Edge[N]] = {
		println("edges(): todo implement")
		new util.HashSet()
	}

	def nodes(): util.Set[N] = {
		println("nodes(): todo implement")
		new util.HashSet[N]
	}

	def successors(player: N): util.Set[N] = {
		println("successors: todo implement")
		new util.HashSet[N]
	}

	def predecessors(player: N): util.Set[N] = {
		println("predecessors: todo implement")
		new util.HashSet[N]
	}
}