@echo off


MKDIR GoogleExtension
CD GoogleExtension
jar -xvf ..\ANEGoogleApi-studio\app\build\outputs\aar\app-release.aar
MOVE classes.jar ..\GoogleExtension.jar

CD ..
COPY /b GoogleExtension.jar +,,

RD  /S /Q GoogleExtension