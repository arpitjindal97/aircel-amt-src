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
mv chromedriver.exe libs/chromedriver_win

curl --output chromedriver.zip https://chromedriver.storage.googleapis.com/$(curl https://chromedriver.storage.googleapis.com/LATEST_RELEASE)/chromedriver_mac64.zip
unzip chromedriver.zip && rm chromedriver.zip
chmod +x chromedriver 
mv chromedriver libs/chromedriver_mac

curl --output chromedriver.zip https://chromedriver.storage.googleapis.com/$(curl https://chromedriver.storage.googleapis.com/LATEST_RELEASE)/chromedriver_linux64.zip
unzip chromedriver.zip && rm chromedriver.zip
chmod +x chromedriver 
mv chromedriver libs/chromedriver_linux

rm -rf WEB-INF META-INF aircel_git_src-1.0.war

echo "Generating MD5 "
md5sum libs/* > md5.md

echo "Generating JAR "
jar cfm Start.jar manifest.txt MyTerminal*.class
rm MyTerminal*.class manifest.txt

cd ..
git clone git@github.com:arpitjindal97/aircel_git_bin.git

rm -rf aircel_git_bin/*
cp -rf deployments/* aircel_git_bin/
rm -rf deployments

echo "Pushing to aircel_git_bin"
cd aircel_git_bin && ls -alh libs/
git config user.name "Arpit Agarwal"
git config user.email "arpitjindal1997@hotmail.com"
git add .
git commit -m "chmod +x chromedriver"
git push origin master
cd ..

echo "Done"
exit 0
