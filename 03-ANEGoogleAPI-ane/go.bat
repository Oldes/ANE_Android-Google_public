@SET SUPPORT_VERSION=services-18.0.1_androidx-1.1.0
@SET AIR_SDK_VERSION=AIRSDK-Windows-Harman

::#################################
:: ARMv7 version with support:
@SET ARM_VERSION=ARM
CALL go-common.bat

::#################################
:: ARMv8 version with support:
@SET ARM_VERSION=ARM64
CALL go-common.bat
