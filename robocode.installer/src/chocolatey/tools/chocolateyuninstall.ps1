$ErrorActionPreference = 'Stop'; # stop on all errors
#Uninstall-ChocolateyZipPackage $packageName # Only necessary if you did not unpack to package directory - see https://chocolatey.org/docs/helpers-uninstall-chocolatey-zip-package
#Uninstall-ChocolateyEnvironmentVariable # 0.9.10+ - https://chocolatey.org/docs/helpers-uninstall-chocolatey-environment-variable 
#Uninstall-BinFile # Only needed if you used Install-BinFile - see https://chocolatey.org/docs/helpers-uninstall-bin-file
