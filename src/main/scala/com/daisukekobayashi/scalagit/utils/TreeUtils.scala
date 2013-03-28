package com.daisukekobayashi.scalagit.utils

import org.eclipse.jgit.revwalk.RevTree
import collection.mutable.ListBuffer
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.treewalk.TreeWalk

object TreeUtils {

  def contents(repository: Repository, tree: RevTree): List[String] = {
    val listBuffer = ListBuffer[String]()

    val treeWalk = new TreeWalk(repository)
    treeWalk.addTree(tree)

    while (treeWalk.next()) {
      println(treeWalk.getFileMode(0))
      listBuffer += treeWalk.getNameString()
    }
    listBuffer.toList
  }

  def contents(repository: Repository, tree: RevTree, path: String): List[String] = {
    val listBuffer = ListBuffer[String]()

    val treeWalk = TreeWalk.forPath(repository, path, tree)
    if (treeWalk.isSubtree())
      treeWalk.enterSubtree()

    while (treeWalk.next()) {
      listBuffer += treeWalk.getNameString()
    }
    listBuffer.toList
  }
}