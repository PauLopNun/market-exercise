@echo off
REM Simple Terminal Supermarket - Test Script for Windows

echo ================================
echo StS - Test Script
echo ================================
echo.

REM Set Maven path
set PATH=C:\maven\bin;%PATH%

REM Compile
echo Compiling...
call mvn clean compile -q
if errorlevel 1 (
    echo Error compiling
    pause
    exit /b 1
)

REM Run with test input
echo Running application...
echo.

(
echo LOGIN Alice
echo BUY 1 2
echo BUY 2 1
echo HELP
echo LOGS
echo CHECKOUT
echo EXIT
) | java -cp target/classes com.sts.Main

echo.
echo ================================
echo Test completed successfully
echo ================================
pause

