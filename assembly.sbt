import AssemblyKeys._ // put this at the top of the file

assemblySettings

jarName   in assembly := name.value + "-" + version.value + ".jar"

mainClass in assembly := Some("org.fs.subrip.SubRipEditorMain")

outputPath in assembly <<= (jarName in assembly) map (jn => file(".") / jn)
