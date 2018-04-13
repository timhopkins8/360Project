@echo off
REM mm2.bat - Batch file for assembling under Visual Studio .NET
REM Does not assume we are in a VS command line window

REM copy filename to PNAME for processing
SET PNAME=%1
REM remove .ASM if they provided it
SET PNAME=%PNAME:.asm=%

"C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\bin\ml.exe" -Zi -Zd -c -Fl -Sg -coff %PNAME%.asm
if errorlevel 1 goto terminate

REM add the /MAP option for a map file in the link command.

REM Use the following link command for Visual.Studio.Net:
"C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\bin\LINK.exe" %PNAME%.obj /SUBSYSTEM:CONSOLE /INCREMENTAL:NO /DEBUG

if errorLevel 1 goto terminate

dir %PNAME%.*

:terminate
REM pause
