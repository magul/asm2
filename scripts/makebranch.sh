#!/bin/sh

if [ -z "$1" ]; then
    echo "Usage: makebranch.sh <branchname>"
    exit 1
fi

svn copy https://sheltermanager.svn.sourceforge.net/svnroot/sheltermanager/trunk https://sheltermanager.svn.sourceforge.net/svnroot/sheltermanager/branches/$1
