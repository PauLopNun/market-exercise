# script comandos del usuario

$env:PATH="C:\maven\bin;$env:PATH"
cd C:\Users\paulo\Desktop\market-exercise

Write-Host "==== COMPILANDO EL PROYECTO ====" -ForegroundColor Green
mvn clean compile -q

Write-Host ""
Write-Host "==== EJECUTANDO LA APLICACION ====" -ForegroundColor Green
Write-Host ""

$testCommands = @"
LOGIN Alice
HELP
BUY 1 2
BUY 2 3
BUY 3 1
LOGS
CHECKOUT
EXIT
"@

$testCommands | Out-File -FilePath "test_input.txt" -Encoding ASCII

Get-Content "test_input.txt" | java -cp target/classes com.sts.Main

Remove-Item "test_input.txt"

Write-Host ""
Write-Host "==== PRUEBA COMPLETADA ====" -ForegroundColor Green

