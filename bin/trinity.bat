@echo off
if NOT exist "%~dp0trinity.jar" (
    echo ERROR: Trinity interpreter not found.
) else (
    java -jar "%~dp0trinity.jar" %*
)
