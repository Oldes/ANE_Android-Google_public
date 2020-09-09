:: Path to AIR SDK
@call ../setup.bat

@IF EXIST com.amanitadesign.GoogleExtension.swc DEL com.amanitadesign.GoogleExtension.swc

@echo.
"%AIR_SDK%"/bin/acompc -namespace http://amanita-design.net/extensions src/manifest.xml ^
    -swf-version 29     ^
    -source-path src	^
    -include-classes	^
    com.amanitadesign.GoogleExtension	^
    -output=com.amanitadesign.GoogleExtension.swc

