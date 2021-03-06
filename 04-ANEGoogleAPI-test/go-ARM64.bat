:: Path to AIR SDK
@SET AIR_SDK=..\..\AIRSDK-Windows-Harman
@SET SUPPORT_VERSION=services-18.0.1_androidx-1.1.0
@SET LIBS=libs

@echo Building SWF file

COPY /Y ..\Release\ARM64_%SUPPORT_VERSION%\ANEAndroidGoogleAPI.ane %LIBS%\ANEAndroidGoogleAPI.ane

java -Dsun.io.useCanonCaches=false -Xms32m -Xmx512m -Dflexlib="%AIR_SDK%\frameworks" -jar "%AIR_SDK%\lib\mxmlc-cli.jar" ^
	-load-config="%AIR_SDK%/frameworks/air-config.xml" -load-config+=TestAndroidGooglePlayANEConfig.xml +configname=air ^
	-optimize=true -o TestAndroidGooglePlayANE.swf

	
@set BUILD_NAME=Build\TestAndroidGooglePlayANE-ARM64.apk

@IF EXIST Build GOTO BUILD_DIR_EXISTS
	@MKDIR Build
	@GOTO BUILD_DIR_READY
:BUILD_DIR_EXISTS

@IF NOT EXIST %BUILD_NAME% GOTO BUILD_DIR_READY
	echo Deleting old build...
	DEL /F /Q "%BUILD_NAME%"
:BUILD_DIR_READY

@echo Packing new build...



@set AIR_NOANDROIDFLAIR=true

java -jar %AIR_SDK%\lib\adt.jar -package -target apk-captive-runtime -arch armv8 ^
	-storetype pkcs12 -keystore TestAndroidGooglePlayANE.p12 -storepass fd ^
	%BUILD_NAME%  application-v28.xml TestAndroidGooglePlayANE.swf icons/* ^
	-extdir %LIBS%

@echo off

DEL TestAndroidGooglePlayANE.swf
DEL %LIBS%\ANEAndroidGoogleAPI.ane



IF NOT EXIST  %BUILD_NAME% GOTO NOBUILD
	echo Build ready!
	GOTO INSTALL
:NOBUILD
	echo Build failed!
	@CALL ..\beep.bat
	@PAUSE
	GOTO END
:INSTALL


%AIR_SDK%\lib\android\bin\adb install -r %BUILD_NAME%

java -jar %AIR_SDK%\lib\adt.jar -launchApp -platform android -appid com.amanitadesign.TestAmanitaAndroidANE

@CALL ..\beep.bat

:END


