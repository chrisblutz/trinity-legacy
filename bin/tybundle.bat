@echo off
if NOT exist "%~dp0trinity.jar" (
    echo ERROR: Trinity interpreter not found.
) else (
    java -cp "%~dp0trinity.jar" com.github.chrisblutz.trinity.libraries.compiling.LibraryCompiler
)
