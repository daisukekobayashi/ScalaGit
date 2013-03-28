package com.daisukekobayashi.scalagit.utils

import java.io.File
import scala.collection.JavaConverters._
import org.eclipse.jgit.lib.{FileMode, Ref, Repository}
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import collection.mutable.ListBuffer
import org.eclipse.jgit.revwalk.{RevWalk, RevCommit}
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.treewalk.TreeWalk


object RepositoryUtils {

  def branches(repository: Repository): List[Ref] = {
    val git = new Git(repository)

    val listBuffer = ListBuffer[Ref]()

    git.branchList().setListMode(ListMode.ALL).call().asScala foreach { r =>
      listBuffer += r
    }
    listBuffer.toList
  }

  def commits(repository: Repository): List[RevCommit] = {
    val listBuffer = ListBuffer[RevCommit]()
    if (repository == null)
      return listBuffer.toList

    val head = repository.resolve("HEAD")
    val revWalk = new RevWalk(repository)
    val root = revWalk.parseCommit(head)

    revWalk.markStart(root)

    for (it <- revWalk.iterator().asScala) {
      listBuffer += it
    }
    listBuffer.toList
  }

  def head(repository: Repository): RevCommit = {
    val head = repository.resolve("HEAD")
    val revWalk = new RevWalk(repository)
    revWalk.parseCommit(head)
  }

  def init(repositoriesFolder: File, name: String, bare: Boolean): Repository = {
    try {
      val git = Git.init().setDirectory(new File(repositoriesFolder, name)).setBare(bare).call()
      git.getRepository()
    } catch {
      case e: GitAPIException => throw new RuntimeException(e)
    }
  }

  def tags(repository: Repository): List[Ref] = {
    val git = new Git(repository)
    val listBuffer = ListBuffer[Ref]()

    git.tagList().call().asScala foreach { r =>
      listBuffer += r
    }
    listBuffer.toList
  }

  def tree(repository: Repository, path: String): List[(String, Boolean)] = {
    val listBuffer = ListBuffer[(String, Boolean)]()
    if (repository == null)
      return listBuffer.toList

    val head = repository.resolve("HEAD")
    val revWalk = new RevWalk(repository)
    val root = revWalk.parseCommit(head)

    var treeWalk: TreeWalk = null

    if (path.isEmpty()) {
      treeWalk = new TreeWalk(repository)
      treeWalk.addTree(root.getTree())
    } else {
      treeWalk = TreeWalk.forPath(repository, path, root.getTree())

      if (treeWalk.isSubtree())
        treeWalk.enterSubtree()
      else
        listBuffer += treeWalk.getNameString() ->
          ((treeWalk.getRawMode(0) & FileMode.TYPE_MASK) == FileMode.TYPE_FILE)
    }

    try {
      while (treeWalk.next()) {
        listBuffer += treeWalk.getNameString() ->
          ((treeWalk.getRawMode(0) & FileMode.TYPE_MASK) == FileMode.TYPE_FILE)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
    listBuffer.toList
  }
}