@RD  /S /Q .\src\assets
@mkdir "src\assets\platform\Android-%ARM_VERSION%"
@mkdir "..\Release\%ARM_VERSION%_%SUPPORT_VERSION%"

copy "..\01-ANEGoogleAPI-swc\com.amanitadesign.GoogleExtension.swc" src\assets

xcopy /S /Y res-%SUPPORT_VERSION%\* src\assets\platform\Android-%ARM_VERSION%\
xcopy /S /Y res-downloader\* src\assets\platform\Android-%ARM_VERSION%\
xcopy /S /Y res-billing\* src\assets\platform\Android-%ARM_VERSION%\


mkdir src\assets\swc-contents
pushd src\assets\swc-contents
JAR xf ..\com.amanitadesign.GoogleExtension.swc catalog.xml library.swf
popd

copy "..\02-ANEGoogleAPI\GoogleExtension.jar" src\assets\platform\Android-%ARM_VERSION%
copy src\assets\swc-contents\library.swf src\assets\platform\Android-%ARM_VERSION%

java -jar "..\..\%AIR_SDK_VERSION%\lib\adt.jar" -package     ^
    -target ane ..\Release\%ARM_VERSION%_%SUPPORT_VERSION%\ANEAndroidGoogleAPI.ane src\extension-%ARM_VERSION%.xml ^
    -swc src\assets\com.amanitadesign.GoogleExtension.swc ^
    -platform Android-%ARM_VERSION%                       ^
    -platformoptions  src\platform-%SUPPORT_VERSION%.xml  ^
    -C src\assets\platform\Android-%ARM_VERSION% .

@RD  /S /Q .\src\assets

