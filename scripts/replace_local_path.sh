#!/bin/bash

# On récupère le chemin du projet passé en argument
BASEDIR="$1"

# On construit le chemin absolu du repo local
LOCAL_REPO_PATH="file://$BASEDIR/requirements/local-repo"

# On fait le remplacement dans lootchest/pom.xml
sed -i "s|LOCAL_PATH|$LOCAL_REPO_PATH|g" "$BASEDIR/lootchest/pom.xml"