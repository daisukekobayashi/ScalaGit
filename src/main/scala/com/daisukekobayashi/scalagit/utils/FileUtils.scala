package com.daisukekobayashi.scalagit.utils

import java.io.File
import scala.collection.JavaConverters._
import collection.mutable.ListBuffer
import org.eclipse.jgit.lib.RepositoryCache.FileKey
import org.eclipse.jgit.util.FS

object FileUtils {

  def repositories(repositoriesFolder: File, onlyBare: Boolean): List[String] = {
    val listBuffer = ListBuffer[String]()
    if (repositoriesFolder == null || !repositoriesFolder.exists())
      return listBuffer.toList

    repositoriesFolder.list() foreach { file =>
      val gitDir = FileKey.resolve(new File(repositoriesFolder, file), FS.DETECTED)
      if (gitDir != null) {
        if (onlyBare && gitDir.getName().equals(".git")) {

        } else {
          listBuffer += file
        }
      }
    }
    listBuffer.toList
  }
}