@echo off & npm outdated --parseable --depth=0  >up.txt & for /f "delims=^" %%i in (up.txt) do (
for /f "delims=:" %%i in ("%%~ni") do (npm install %%i@latest) 
)& del /s /q up.txt