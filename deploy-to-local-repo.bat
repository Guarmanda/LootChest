@echo off

set actual_path=%~dp0

set local_repo_path=%actual_path%requirements/local-repo
echo %local_repo_path%

if "%1" == "" goto error
if "%2" == "" goto error
if "%3" == "" goto error
if "%4" == "" goto error

mvn deploy:deploy-file -Durl=file:///%local_repo_path% -DrepositoryId=local-repo -Dfile=%1 -DgroupId=%2 -DartifactId=%3 -Dversion=%4 -Dpackaging=jar;

goto end

:error
echo "Usage: deploy-to-local-repo.bat <jar-file> <group-id> <artifact-id> <version>"
:end