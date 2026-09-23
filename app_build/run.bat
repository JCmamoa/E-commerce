@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

echo ==========================================================
echo        E-COMMERCE ENTERPRISE (JAVA + WEB REACT SPA)       
echo ==========================================================

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run.ps1" %*

pause
