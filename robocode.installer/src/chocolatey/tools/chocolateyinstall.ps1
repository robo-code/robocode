$ErrorActionPreference = 'Stop';
$toolsDir   = "$(Split-Path -parent $MyInvocation.MyCommand.Definition)"
# Get-ChocolateyUnzip -FileFullPath  "$(Split-Path -Parent $MyInvocation.MyCommand.Definition)\\robocode-*-setup.jar" -Destination "C:\robocode"
Start-ChocolateyProcessAsAdmin -Statements "java -jar $(Split-Path -Parent $MyInvocation.MyCommand.Definition)\robocode-$env:ChocolateyPackageVersion-setup.jar C:\robocode\ silent"
