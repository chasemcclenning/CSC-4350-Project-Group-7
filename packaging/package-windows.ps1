$ErrorActionPreference = "Stop"

$projectDir = Split-Path -Parent $PSScriptRoot
Set-Location $projectDir

& .\mvnw.cmd clean package
if ($LASTEXITCODE -ne 0) {
    throw "Maven build failed."
}

$packageInput = Join-Path $projectDir "target\jpackage-input"
$installerDir = Join-Path $projectDir "target\installer"
New-Item -ItemType Directory -Force -Path $packageInput, $installerDir | Out-Null

Copy-Item "target\libris.jar" $packageInput
Copy-Item "target\dependency\*.jar" $packageInput

& jpackage `
    --type exe `
    --name Libris `
    --app-version 1.0.0 `
    --vendor "CSC 4350 Group 7" `
    --description "Library management system for librarians and members" `
    --input $packageInput `
    --main-jar libris.jar `
    --main-class Launcher `
    --dest $installerDir `
    --win-dir-chooser `
    --win-menu `
    --win-menu-group "Libris" `
    --win-shortcut `
    --java-options "-Dfile.encoding=UTF-8"

if ($LASTEXITCODE -ne 0) {
    throw "Windows installer creation failed."
}

Get-ChildItem $installerDir -Filter "*.exe" | ForEach-Object {
    Write-Output "Created $($_.FullName)"
}
