#!/usr/bin/env bash

cd deployments
echo "Unzipping War..."
unzip aircel_git_src-1.0.war > /dev/null
mv WEB-INF/classes/* .
mkdir libs
mv amt.class status.class libs/
mv WEB-INF/lib/* libs/

echo "Fetching Chromedriver ..."
curl --output chromedriver.zip https://chromedriver.storage.googleapis.com/$(curl https://chromedriver.storage.googleapis.com/LATEST_RELEASE)/chromedriver_win32.zip
unzip chromedriver.zip && rm chromedriver.zip
mv chromedriver.exe libs/chromedriver

rm -rf WEB-INF META-INF aircel_git_src-1.0.war

echo "Generating MD5 "
md5sum libs/* > md5.md

echo "Generating JAR "
jar cfm Start.jar manifest.txt MyTerminal*.class
rm MyTerminal*.class manifest.txt

cd ..
git clone https://github.com/arpitjindal97/aircel_git_bin.git

rm -rf aircel_git_bin/*
cp -rf deployments* aircel_git_bin/

echo "Pushing to aircel_git_bin"
cd aircel_git_bin
git add .
git commit -m "automated build from arpitjindal97/aircel_git_src.git"
git push origin master
cd ..

rm -rf deployments
echo "Done"
exit 0
