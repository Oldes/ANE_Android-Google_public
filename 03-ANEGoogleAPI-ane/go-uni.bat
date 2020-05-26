@SET SUPPORT_VERSION=services-18.0.1_androidx-1.1.0
@SET AIR_SDK_VERSION=AIRSDK-Windows-Harman

GOTO COMPI

@RD  /S /Q .\src\assets
@mkdir "src\assets\platform\Android-ARM"
@mkdir "..\Release\%SUPPORT_VERSION%"

copy "..\01-ANEGoogleAPI-swc\com.amanitadesign.GoogleExtension.swc" src\assets

xcopy /S /Y res-downloader\* src\assets\platform\Android-ARM\
xcopy /S /Y res-%SUPPORT_VERSION%\* src\assets\platform\Android-ARM\
::xcopy /S /Y res-billing\* src\assets\platform\Android-%ARM_VERSION%\

DEL src\assets\platform\Android-ARM\dependencies.txt
DEL src\assets\platform\Android-ARM\classes.txt

mkdir src\assets\swc-contents
pushd src\assets\swc-contents
JAR xf ..\com.amanitadesign.GoogleExtension.swc catalog.xml library.swf
popd

copy "..\02-ANEGoogleAPI\GoogleExtension.jar" src\assets\platform\Android-ARM
copy src\assets\swc-contents\library.swf src\assets\platform\Android-ARM

::copy "..\02-ANEGoogleAPI\GoogleExtension.jar" src\assets\platform\Android-ARM64
::copy src\assets\swc-contents\library.swf src\assets\platform\Android-ARM64

xcopy /S /Y src\assets\platform\Android-ARM\* src\assets\platform\Android-ARM64\

:COMPI
java -jar "..\..\%AIR_SDK_VERSION%\lib\adt.jar" -package     ^
    -target ane ..\Release\%SUPPORT_VERSION%\ANEAndroidGoogleAPI.ane src\extension.xml ^
    -swc src\assets\com.amanitadesign.GoogleExtension.swc ^
	-platform Android-ARM -C src\assets\platform\Android-ARM . ^
	-platformoptions  src\platform-%SUPPORT_VERSION%.xml ^
	-platform Android-ARM64 -C src\assets\platform\Android-ARM64 . ^
	-platformoptions  src\platform-%SUPPORT_VERSION%.xml ^
    
PAUSE
@RD  /S /Q .\src\assets


