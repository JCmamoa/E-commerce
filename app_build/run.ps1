# Script de Execução Automatizada - E-Commerce Enterprise
param (
    [int]$Porta = 8080
)

$ErrorActionPreference = "Stop"
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "       🛒 INICIANDO E-COMMERCE ENTERPRISE (JAVA + WEB)   " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Localizar javac e java
$javac = $null
$java = $null

$localJdk = Get-ChildItem -Path "C:\Users\Casa\.jdk" -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
if ($localJdk) {
    $javac = $localJdk.FullName
    $java = Join-Path $localJdk.Directory.FullName "java.exe"
} elseif (Get-Command javac -ErrorAction SilentlyContinue) {
    $javac = (Get-Command javac).Source
    $java = (Get-Command java).Source
}

if (-not $javac -or -not (Test-Path $javac)) {
    Write-Host "❌ Erro: Compilador Java (javac) não encontrado." -ForegroundColor Red
    Write-Host "Certifique-se de que o JDK 21 está instalado ou presente em C:\Users\Casa\.jdk" -ForegroundColor Yellow
    exit 1
}

Write-Host "☕ JDK Detectado: $javac" -ForegroundColor DarkGray

# 2. Criar diretório bin
$binDir = Join-Path $PSScriptRoot "bin"
if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir -Force | Out-Null
}

# 3. Compilar arquivos Java
Write-Host "🔨 Compilando classes Java com codificação UTF-8..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path (Join-Path $PSScriptRoot "src/main/java") -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -d $binDir $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Falha na compilação!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Compilação concluída com sucesso!" -ForegroundColor Green

# 4. Executar Aplicação
Write-Host "🚀 Iniciando aplicação na porta $Porta..." -ForegroundColor Cyan
Write-Host "🌐 Interface Web ficará disponível em: http://localhost:$Porta" -ForegroundColor Magenta
Write-Host ""
Set-Location $PSScriptRoot
& $java -cp $binDir com.ecommerce.Main $Porta
