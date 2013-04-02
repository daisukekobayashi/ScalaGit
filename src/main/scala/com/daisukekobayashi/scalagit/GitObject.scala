package com.daisukekobayashi.scalagit


trait GitObject {
  val repository: org.eclipse.jgit.lib.Repository
  val sha: org.eclipse.jgit.lib.ObjectId
}