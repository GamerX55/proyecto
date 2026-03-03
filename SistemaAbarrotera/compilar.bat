@echo off
echo  Compilando Sistema Abarrotera
javac -encoding UTF-8 -d bin -sourcepath src src\modelo\*.java src\servicio\*.java src\ui\*.java
if %errorlevel% == 0 (
    echo.
    echo  Compilacion exitosa!
    echo  Ejecuta: ejecutar.bat
) else (
    echo.
    echo  Error en la compilacion.
)
echo ========================================
pause
