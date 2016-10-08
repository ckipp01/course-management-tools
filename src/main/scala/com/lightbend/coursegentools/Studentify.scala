package com.lightbend.coursegentools

/**
  * Copyright © 2014, 2015, 2016 Lightbend, Inc. All rights reserved. [http://www.lightbend.com]
  */

object Studentify {

  def main(args: Array[String]): Unit = {

    import Helpers._
    import java.io.File

    val cmdOptions = StudentifyCmdLineOptParse.parse(args)
    if (cmdOptions.isEmpty) System.exit(-1)
    val StudentifyCmdOptions(masterRepo, targetFolder, multiJVM, firstOpt, lastOpt, selectedFirstOpt) = cmdOptions.get

    val projectName = masterRepo.getName
    val tmpDir = cleanMasterViaGit(masterRepo, projectName)
    val cleanMasterRepo = new File(tmpDir, projectName)
    val exercises: Seq[String] = getExerciseNames(cleanMasterRepo)
    val selectedExercises: Seq[String] = getSelectedExercises(exercises, firstOpt, lastOpt)
    val targetCourseFolder = new File(targetFolder, projectName)
    val initialExercise = getInitialExercise(selectedFirstOpt, selectedExercises)
    val sbtStudentCommandsTemplateFolder = new File("sbtStudentCommands")
    stageFirstExercise(initialExercise, cleanMasterRepo, targetCourseFolder)
    copyMaster(cleanMasterRepo, targetCourseFolder)
    val solutionPaths = hideExerciseSolutions(targetCourseFolder, selectedExercises)
    createBookmarkFile(initialExercise, targetCourseFolder)
    createSbtRcFile(targetCourseFolder)
    createBuildFile(targetCourseFolder, multiJVM)
    addSbtStudentCommands(sbtStudentCommandsTemplateFolder, targetCourseFolder)
    cleanUp(List(".git", ".gitignore", ".sbtopts", "man.sbt", "navigation.sbt", "shell-prompt.sbt"), targetCourseFolder)
    sbt.IO.delete(tmpDir)

  }

}
