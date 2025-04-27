@echo off
setlocal enabledelayedexpansion

rem On récupère le chemin du projet passé en argument
set BASEDIR=%1

rem On construit le chemin absolu du repo local
set "LOCAL_REPO_PATH=file://%BASEDIR:/=\%/requirements/local-repo"

rem Utilisation de powershell pour remplacer dans lootchest\pom.xml
powershell -Command "(Get-Content %BASEDIR%\lootchest\pom.xml) -replace 'LOCAL_PATH', '%LOCAL_REPO_PATH%' | Set-Content %BASEDIR%\lootchest\pom.xml"