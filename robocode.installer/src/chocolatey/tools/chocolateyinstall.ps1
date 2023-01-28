$ErrorActionPreference = 'Stop';
$toolsDir = "$(Split-Path -parent $MyInvocation.MyCommand.Definition)"

$params = Get-PackageParameters
if (!$params['InstallDir']) { $params['InstallDir'] = "$env:SystemDrive\Robocode" }

Start-ChocolateyProcessAsAdmin -Statements "java -jar $(Split-Path -Parent $MyInvocation.MyCommand.Definition)\robocode-$env:ChocolateyPackageVersion-setup.jar $($params['InstallDir']) silent"
