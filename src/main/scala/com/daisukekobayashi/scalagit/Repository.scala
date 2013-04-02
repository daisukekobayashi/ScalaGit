package com.daisukekobayashi.scalagit

import org.eclipse.jgit.storage.file.FileRepository
import org.eclipse.jgit.lib.{FileMode, Ref, Repository}
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.revwalk.{RevWalk, RevCommit}
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.treewalk.TreeWalk
import collection.mutable.ListBuffer
import collection.JavaConverters._


class Repository(gitDir: String) {

  val gitDirectory = gitDir
  val repository: org.eclipse.jgit.lib.Repository = new FileRepository(gitDir)

  def branches(): List[Ref] = {
    val git = new Git(this.repository)

    val listBuffer = ListBuffer[Ref]()

    git.branchList().setListMode(ListMode.ALL).call().asScala foreach { r =>
      listBuffer += r
    }
    listBuffer.toList
  }

  def commits(): List[RevCommit] = {
    val listBuffer = ListBuffer[RevCommit]()
    if (this.repository == null)
      return listBuffer.toList

    val head = this.repository.resolve("HEAD")
    val revWalk = new RevWalk(this.repository)
    val root = revWalk.parseCommit(head)

    revWalk.markStart(root)

    for (it <- revWalk.iterator().asScala) {
      listBuffer += it
    }
    listBuffer.toList
  }

  def head(): RevCommit = {
    val head = this.repository.resolve("HEAD")
    val revWalk = new RevWalk(this.repository)
    revWalk.parseCommit(head)
  }

  //def init(repositoriesFolder: File, name: String, bare: Boolean): Repository = {
  //  try {
  //    val git = Git.init().setDirectory(new File(repositoriesFolder, name)).setBare(bare).call()
  //    git.getRepository()
  //  } catch {
  //    case e: GitAPIException => throw new RuntimeException(e)
  //  }
  //}

  def tags(): List[Ref] = {
    val git = new Git(this.repository)
    val listBuffer = ListBuffer[Ref]()

    git.tagList().call().asScala foreach { r =>
      listBuffer += r
    }
    listBuffer.toList
  }

  def tree(path: String): List[(String, Boolean)] = {
    val listBuffer = ListBuffer[(String, Boolean)]()
    if (this.repository == null)
      return listBuffer.toList

    val head = this.repository.resolve("HEAD")
    val revWalk = new RevWalk(this.repository)
    val root = revWalk.parseCommit(head)

    var treeWalk: TreeWalk = null

    if (path.isEmpty()) {
      treeWalk = new TreeWalk(this.repository)
      treeWalk.addTree(root.getTree())
    } else {
      treeWalk = TreeWalk.forPath(this.repository, path, root.getTree())

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