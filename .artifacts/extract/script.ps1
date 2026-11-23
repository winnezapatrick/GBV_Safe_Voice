$src = "C:\Users\bsc_inf_22_23\Desktop\SAVE VOICE TEST CASES.docx"
$temp = "C:\Users\bsc_inf_22_23\AndroidStudioProjects\GBVAPP\.artifacts\extract\temp.docx"
$extractPath = "C:\Users\bsc_inf_22_23\AndroidStudioProjects\GBVAPP\.artifacts\extract\files"

if (Test-Path $extractPath) { Remove-Item $extractPath -Recurse -Force }
$fs = New-Object System.IO.FileStream($src, [System.IO.FileMode]::Open, [System.IO.FileAccess]::Read, [System.IO.FileShare]::ReadWrite)
$out = New-Object System.IO.FileStream($temp, [System.IO.FileMode]::Create, [System.IO.FileAccess]::Write)
$fs.CopyTo($out)
$out.Close()
$fs.Close()

Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory($temp, $extractPath)
$docXml = [xml](Get-Content "$extractPath\word\document.xml" -Encoding UTF8)
$nodes = $docXml.DocumentElement.SelectNodes("//*[local-name()='t']")
foreach ($n in $nodes) {
    Write-Output $n.InnerText
}
