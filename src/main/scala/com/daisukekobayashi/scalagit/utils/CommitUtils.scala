package com.daisukekobayashi.scalagit.utils

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.{RevTree, RevWalk, RevCommit}
import collection.mutable.ListBuffer


object CommitUtils {

  def tree(commit: RevCommit): RevTree = {
    commit.getTree()
  }
}
