Add-Type -AssemblyName System.Drawing

$base = "E:\games\src\main\resources\assets\ascend"
$itemTexDir = Join-Path $base "textures\item"
$blockTexDir = Join-Path $base "textures\block"
$itemModelDir = Join-Path $base "models\item"
$blockModelDir = Join-Path $base "models\block"
$blockStateDir = Join-Path $base "blockstates"

foreach ($dir in @($itemTexDir, $blockTexDir, $itemModelDir, $blockModelDir, $blockStateDir)) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}

function Save-Png([System.Drawing.Bitmap]$bmp, [string]$path) {
    if (Test-Path $path) { Remove-Item $path -Force }
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

function Write-ItemModel([string]$name, [string]$parent = "minecraft:item/generated") {
    $json = @"
{
  "parent": "$parent",
  "textures": {
    "layer0": "ascend:item/$name"
  }
}
"@
    Set-Content -Path (Join-Path $itemModelDir "$name.json") -Value $json -Encoding UTF8
}

function Write-BlockModelAndState([string]$name) {
    $stateJson = @"
{
  "variants": {
    "": {
      "model": "ascend:block/$name"
    }
  }
}
"@
    Set-Content -Path (Join-Path $blockStateDir "$name.json") -Value $stateJson -Encoding UTF8

    $blockModelJson = @"
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "ascend:block/$name"
  }
}
"@
    Set-Content -Path (Join-Path $blockModelDir "$name.json") -Value $blockModelJson -Encoding UTF8

    $itemModelJson = @"
{
  "parent": "ascend:block/$name"
}
"@
    Set-Content -Path (Join-Path $itemModelDir "$name.json") -Value $itemModelJson -Encoding UTF8
}

# Colors
$cM0 = [System.Drawing.Color]::FromArgb(255, 112, 197, 232)
$cM1 = [System.Drawing.Color]::FromArgb(255, 200, 245, 255)
$cM2 = [System.Drawing.Color]::FromArgb(255, 43, 88, 118)
$cM3 = [System.Drawing.Color]::FromArgb(255, 20, 45, 65)

$cD0 = [System.Drawing.Color]::FromArgb(255, 215, 50, 40)
$cD1 = [System.Drawing.Color]::FromArgb(255, 255, 170, 50)
$cD2 = [System.Drawing.Color]::FromArgb(255, 140, 20, 20)
$cD3 = [System.Drawing.Color]::FromArgb(255, 70, 10, 10)

$cV0 = [System.Drawing.Color]::FromArgb(255, 90, 45, 120)
$cV1 = [System.Drawing.Color]::FromArgb(255, 175, 122, 197)
$cV2 = [System.Drawing.Color]::FromArgb(255, 35, 20, 50)
$cV3 = [System.Drawing.Color]::FromArgb(255, 15, 10, 22)

$cWoodHandle = [System.Drawing.Color]::FromArgb(255, 102, 68, 34)
$cDarkHandle = [System.Drawing.Color]::FromArgb(255, 60, 40, 20)

function Make-Ingot($col0, $col1, $col2, $col3, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($y=5; $y -le 10; $y++) {
        for ($x=3; $x -le 12; $x++) {
            $b.SetPixel($x, $y, $col0)
        }
    }
    for ($x=4; $x -le 11; $x++) { $b.SetPixel($x, 5, $col1) }
    for ($x=3; $x -le 12; $x++) { $b.SetPixel($x, 10, $col2) }
    for ($y=5; $y -le 10; $y++) {
        $b.SetPixel(2, $y, $col3)
        $b.SetPixel(13, $y, $col3)
    }
    for ($x=2; $x -le 13; $x++) {
        $b.SetPixel($x, 4, $col3)
        $b.SetPixel($x, 11, $col3)
    }
    Save-Png $b $outPath
}

function Make-Crystal($col0, $col1, $col2, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    $rows = @(
        @(7,8, 2,3),
        @(6,9, 4,5),
        @(5,10, 6,9),
        @(6,9, 10,11),
        @(7,8, 12,13)
    )
    foreach ($r in $rows) {
        for ($y=$r[2]; $y -le $r[3]; $y++) {
            for ($x=$r[0]; $x -le $r[1]; $x++) {
                $b.SetPixel($x, $y, $col0)
            }
        }
    }
    $b.SetPixel(7, 3, $col1); $b.SetPixel(8, 3, $col1)
    $b.SetPixel(6, 6, $col1); $b.SetPixel(7, 6, $col1)
    $b.SetPixel(7, 13, $col2); $b.SetPixel(8, 13, $col2)
    Save-Png $b $outPath
}

function Make-Sword($col0, $col1, $col2, $col3, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($i=0; $i -lt 8; $i++) {
        $x = 4 + $i
        $y = 11 - $i
        $b.SetPixel($x, $y, $col0)
        $b.SetPixel($x+1, $y, $col1)
        $b.SetPixel($x, $y+1, $col2)
    }
    $b.SetPixel(12, 3, $col1)
    $b.SetPixel(4, 11, $col3)
    $b.SetPixel(5, 11, $col2)
    $b.SetPixel(3, 12, $cWoodHandle)
    $b.SetPixel(2, 13, $cDarkHandle)
    $b.SetPixel(1, 14, $col3)
    Save-Png $b $outPath
}

function Make-Pickaxe($col0, $col1, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($i=0; $i -lt 10; $i++) {
        $b.SetPixel(2 + $i, 13 - $i, $cWoodHandle)
    }
    $head = @( @(9,2), @(10,2), @(11,2), @(12,3), @(13,4), @(13,5), @(8,3), @(7,4), @(6,5), @(5,6), @(4,7) )
    foreach ($pt in $head) {
        $b.SetPixel($pt[0], $pt[1], $col0)
        if ($pt[1] -le 3) { $b.SetPixel($pt[0], $pt[1], $col1) }
    }
    Save-Png $b $outPath
}

function Make-Axe($col0, $col1, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($i=0; $i -lt 10; $i++) {
        $b.SetPixel(2 + $i, 13 - $i, $cWoodHandle)
    }
    for ($x=8; $x -le 12; $x++) {
        for ($y=3; $y -le 7; $y++) {
            $b.SetPixel($x, $y, $col0)
        }
    }
    $b.SetPixel(12, 3, $col1); $b.SetPixel(11, 3, $col1)
    Save-Png $b $outPath
}

function Make-Shovel($col0, $col1, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($i=0; $i -lt 9; $i++) {
        $b.SetPixel(2 + $i, 13 - $i, $cWoodHandle)
    }
    $blade = @( @(10,3), @(11,3), @(12,3), @(9,4), @(10,4), @(11,4), @(12,4), @(13,4), @(10,5), @(11,5), @(12,5) )
    foreach ($p in $blade) { $b.SetPixel($p[0], $p[1], $col0) }
    $b.SetPixel(11, 3, $col1); $b.SetPixel(12, 3, $col1)
    Save-Png $b $outPath
}

function Make-Hoe($col0, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($i=0; $i -lt 10; $i++) {
        $b.SetPixel(2 + $i, 13 - $i, $cWoodHandle)
    }
    $blade = @( @(9,3), @(10,3), @(11,3), @(12,3), @(12,4), @(12,5) )
    foreach ($p in $blade) { $b.SetPixel($p[0], $p[1], $col0) }
    Save-Png $b $outPath
}

function Make-Helmet($col0, $col1, $col2, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($y=3; $y -le 11; $y++) {
        for ($x=3; $x -le 12; $x++) {
            if ($y -ge 8 -and ($x -ge 6 -and $x -le 9)) { continue }
            $b.SetPixel($x, $y, $col0)
        }
    }
    for ($x=4; $x -le 11; $x++) { $b.SetPixel($x, 3, $col1) }
    for ($y=5; $y -le 11; $y++) {
        $b.SetPixel(3, $y, $col2)
        $b.SetPixel(12, $y, $col2)
    }
    Save-Png $b $outPath
}

function Make-Chestplate($col0, $col1, $col2, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($y=2; $y -le 13; $y++) {
        for ($x=2; $x -le 13; $x++) {
            if ($y -le 4 -and ($x -ge 6 -and $x -le 9)) { continue }
            if ($y -ge 6 -and ($x -eq 2 -or $x -eq 13)) { continue }
            $b.SetPixel($x, $y, $col0)
        }
    }
    for ($x=3; $x -le 5; $x++) { $b.SetPixel($x, 2, $col1) }
    for ($x=10; $x -le 12; $x++) { $b.SetPixel($x, 2, $col1) }
    for ($x=4; $x -le 11; $x++) { $b.SetPixel($x, 13, $col2) }
    Save-Png $b $outPath
}

function Make-Leggings($col0, $col1, $col2, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($y=2; $y -le 13; $y++) {
        for ($x=3; $x -le 12; $x++) {
            if ($y -ge 6 -and ($x -ge 7 -and $x -le 8)) { continue }
            $b.SetPixel($x, $y, $col0)
        }
    }
    for ($x=3; $x -le 12; $x++) { $b.SetPixel($x, 2, $col1) }
    for ($y=6; $y -le 13; $y++) {
        $b.SetPixel(3, $y, $col2)
        $b.SetPixel(12, $y, $col2)
    }
    Save-Png $b $outPath
}

function Make-Boots($col0, $col1, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($y=7; $y -le 13; $y++) {
        for ($x=3; $x -le 6; $x++) { $b.SetPixel($x, $y, $col0) }
        for ($x=9; $x -le 12; $x++) { $b.SetPixel($x, $y, $col0) }
    }
    $b.SetPixel(2, 13, $col0)
    $b.SetPixel(13, 13, $col0)
    for ($x=3; $x -le 6; $x++) { $b.SetPixel($x, 7, $col1) }
    for ($x=9; $x -le 12; $x++) { $b.SetPixel($x, 7, $col1) }
    Save-Png $b $outPath
}

function Make-Staff($orbCol, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    for ($i=0; $i -lt 11; $i++) {
        $b.SetPixel(2 + $i, 13 - $i, $cWoodHandle)
    }
    for ($x=11; $x -le 14; $x++) {
        for ($y=1; $y -le 4; $y++) {
            $b.SetPixel($x, $y, $orbCol)
        }
    }
    $b.SetPixel(12, 2, [System.Drawing.Color]::White)
    Save-Png $b $outPath
}

function Make-Block($baseCol, $fleckCol, [string]$outPath) {
    $b = New-Object System.Drawing.Bitmap 16, 16
    $rnd = New-Object System.Random 42
    for ($y=0; $y -lt 16; $y++) {
        for ($x=0; $x -lt 16; $x++) {
            $v = $rnd.Next(-12, 13)
            $r = [Math]::Max(0, [Math]::Min(255, $baseCol.R + $v))
            $g = [Math]::Max(0, [Math]::Min(255, $baseCol.G + $v))
            $bl = [Math]::Max(0, [Math]::Min(255, $baseCol.B + $v))
            $b.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $r, $g, $bl))
        }
    }
    if ($fleckCol -ne $null) {
        $pts = @( @(3,4), @(4,4), @(3,5), @(8,8), @(9,8), @(9,9), @(12,3), @(13,3), @(5,11), @(6,12), @(11,12) )
        foreach ($p in $pts) {
            $b.SetPixel($p[0], $p[1], $fleckCol)
        }
    }
    Save-Png $b $outPath
}

# --- 1. Materials ---
Make-Ingot $cM0 $cM1 $cM2 $cM3 (Join-Path $itemTexDir "mythril_ingot.png")
Write-ItemModel "mythril_ingot"

$cC0 = [System.Drawing.Color]::FromArgb(255, 0, 220, 255)
$cC1 = [System.Drawing.Color]::FromArgb(255, 200, 255, 255)
$cC2 = [System.Drawing.Color]::FromArgb(255, 0, 140, 200)
Make-Crystal $cC0 $cC1 $cC2 (Join-Path $itemTexDir "mana_crystal.png")
Write-ItemModel "mana_crystal"

Make-Ingot $cD0 $cD1 $cD2 $cD3 (Join-Path $itemTexDir "dragonite_ingot.png")
Write-ItemModel "dragonite_ingot"

Make-Ingot $cV0 $cV1 $cV2 $cV3 (Join-Path $itemTexDir "voidsteel_ingot.png")
Write-ItemModel "voidsteel_ingot"

Make-Crystal $cV0 $cV1 $cV2 (Join-Path $itemTexDir "shadow_essence.png")
Write-ItemModel "shadow_essence"

Make-Crystal $cD0 $cD1 $cD2 (Join-Path $itemTexDir "dragon_scale.png")
Write-ItemModel "dragon_scale"

Make-Ingot ([System.Drawing.Color]::SandyBrown) ([System.Drawing.Color]::LightGoldenrodYellow) ([System.Drawing.Color]::SaddleBrown) ([System.Drawing.Color]::Black) (Join-Path $itemTexDir "dungeon_map.png")
Write-ItemModel "dungeon_map"

Make-Crystal $cC0 $cC1 $cC2 (Join-Path $itemTexDir "reset_stone.png")
Write-ItemModel "reset_stone"

# --- 2. Gear Sets ---
# Mythril
Make-Sword $cM0 $cM1 $cM2 $cM3 (Join-Path $itemTexDir "mythril_sword.png")
Write-ItemModel "mythril_sword" "minecraft:item/handheld"
Make-Pickaxe $cM0 $cM1 (Join-Path $itemTexDir "mythril_pickaxe.png")
Write-ItemModel "mythril_pickaxe" "minecraft:item/handheld"
Make-Axe $cM0 $cM1 (Join-Path $itemTexDir "mythril_axe.png")
Write-ItemModel "mythril_axe" "minecraft:item/handheld"
Make-Shovel $cM0 $cM1 (Join-Path $itemTexDir "mythril_shovel.png")
Write-ItemModel "mythril_shovel" "minecraft:item/handheld"
Make-Hoe $cM0 (Join-Path $itemTexDir "mythril_hoe.png")
Write-ItemModel "mythril_hoe" "minecraft:item/handheld"
Make-Helmet $cM0 $cM1 $cM2 (Join-Path $itemTexDir "mythril_helmet.png")
Write-ItemModel "mythril_helmet"
Make-Chestplate $cM0 $cM1 $cM2 (Join-Path $itemTexDir "mythril_chestplate.png")
Write-ItemModel "mythril_chestplate"
Make-Leggings $cM0 $cM1 $cM2 (Join-Path $itemTexDir "mythril_leggings.png")
Write-ItemModel "mythril_leggings"
Make-Boots $cM0 $cM1 (Join-Path $itemTexDir "mythril_boots.png")
Write-ItemModel "mythril_boots"

# Dragonite
Make-Sword $cD0 $cD1 $cD2 $cD3 (Join-Path $itemTexDir "dragonite_sword.png")
Write-ItemModel "dragonite_sword" "minecraft:item/handheld"
Make-Pickaxe $cD0 $cD1 (Join-Path $itemTexDir "dragonite_pickaxe.png")
Write-ItemModel "dragonite_pickaxe" "minecraft:item/handheld"
Make-Axe $cD0 $cD1 (Join-Path $itemTexDir "dragonite_axe.png")
Write-ItemModel "dragonite_axe" "minecraft:item/handheld"
Make-Shovel $cD0 $cD1 (Join-Path $itemTexDir "dragonite_shovel.png")
Write-ItemModel "dragonite_shovel" "minecraft:item/handheld"
Make-Hoe $cD0 (Join-Path $itemTexDir "dragonite_hoe.png")
Write-ItemModel "dragonite_hoe" "minecraft:item/handheld"
Make-Helmet $cD0 $cD1 $cD2 (Join-Path $itemTexDir "dragonite_helmet.png")
Write-ItemModel "dragonite_helmet"
Make-Chestplate $cD0 $cD1 $cD2 (Join-Path $itemTexDir "dragonite_chestplate.png")
Write-ItemModel "dragonite_chestplate"
Make-Leggings $cD0 $cD1 $cD2 (Join-Path $itemTexDir "dragonite_leggings.png")
Write-ItemModel "dragonite_leggings"
Make-Boots $cD0 $cD1 (Join-Path $itemTexDir "dragonite_boots.png")
Write-ItemModel "dragonite_boots"

# Voidsteel
Make-Sword $cV0 $cV1 $cV2 $cV3 (Join-Path $itemTexDir "voidsteel_sword.png")
Write-ItemModel "voidsteel_sword" "minecraft:item/handheld"
Make-Pickaxe $cV0 $cV1 (Join-Path $itemTexDir "voidsteel_pickaxe.png")
Write-ItemModel "voidsteel_pickaxe" "minecraft:item/handheld"
Make-Axe $cV0 $cV1 (Join-Path $itemTexDir "voidsteel_axe.png")
Write-ItemModel "voidsteel_axe" "minecraft:item/handheld"
Make-Shovel $cV0 $cV1 (Join-Path $itemTexDir "voidsteel_shovel.png")
Write-ItemModel "voidsteel_shovel" "minecraft:item/handheld"
Make-Hoe $cV0 (Join-Path $itemTexDir "voidsteel_hoe.png")
Write-ItemModel "voidsteel_hoe" "minecraft:item/handheld"
Make-Helmet $cV0 $cV1 $cV2 (Join-Path $itemTexDir "voidsteel_helmet.png")
Write-ItemModel "voidsteel_helmet"
Make-Chestplate $cV0 $cV1 $cV2 (Join-Path $itemTexDir "voidsteel_chestplate.png")
Write-ItemModel "voidsteel_chestplate"
Make-Leggings $cV0 $cV1 $cV2 (Join-Path $itemTexDir "voidsteel_leggings.png")
Write-ItemModel "voidsteel_leggings"
Make-Boots $cV0 $cV1 (Join-Path $itemTexDir "voidsteel_boots.png")
Write-ItemModel "voidsteel_boots"

# --- 3. Specials ---
Make-Sword $cM0 $cM1 $cM2 $cM3 (Join-Path $itemTexDir "mythril_katana.png")
Write-ItemModel "mythril_katana" "minecraft:item/handheld"

Make-Sword $cD0 $cD1 $cD2 $cD3 (Join-Path $itemTexDir "dragonite_greatsword.png")
Write-ItemModel "dragonite_greatsword" "minecraft:item/handheld"

Make-Sword $cV0 $cV1 $cV2 $cV3 (Join-Path $itemTexDir "voidsteel_dagger.png")
Write-ItemModel "voidsteel_dagger" "minecraft:item/handheld"

Make-Sword ([System.Drawing.Color]::SandyBrown) ([System.Drawing.Color]::White) ([System.Drawing.Color]::Brown) ([System.Drawing.Color]::Black) (Join-Path $itemTexDir "longbow.png")
Write-ItemModel "longbow" "minecraft:item/handheld"

# --- 4. Staffs ---
Make-Staff ([System.Drawing.Color]::Cyan) (Join-Path $itemTexDir "apprentice_staff.png")
Write-ItemModel "apprentice_staff" "minecraft:item/handheld"

Make-Staff ([System.Drawing.Color]::OrangeRed) (Join-Path $itemTexDir "flame_staff.png")
Write-ItemModel "flame_staff" "minecraft:item/handheld"

Make-Staff ([System.Drawing.Color]::Yellow) (Join-Path $itemTexDir "storm_staff.png")
Write-ItemModel "storm_staff" "minecraft:item/handheld"

Make-Staff ([System.Drawing.Color]::Magenta) (Join-Path $itemTexDir "summoner_staff.png")
Write-ItemModel "summoner_staff" "minecraft:item/handheld"

Make-Staff ([System.Drawing.Color]::DarkOrchid) (Join-Path $itemTexDir "void_staff.png")
Write-ItemModel "void_staff" "minecraft:item/handheld"

# --- 5. Blocks ---
$cStone = [System.Drawing.Color]::FromArgb(255, 125, 125, 125)
$cDeepslate = [System.Drawing.Color]::FromArgb(255, 60, 60, 65)
$cNether = [System.Drawing.Color]::FromArgb(255, 100, 30, 30)

Make-Block $cStone $cM0 (Join-Path $blockTexDir "mythril_ore.png")
Write-BlockModelAndState "mythril_ore"

Make-Block $cDeepslate $cM0 (Join-Path $blockTexDir "deepslate_mythril_ore.png")
Write-BlockModelAndState "deepslate_mythril_ore"

Make-Block $cStone $cC0 (Join-Path $blockTexDir "mana_crystal_ore.png")
Write-BlockModelAndState "mana_crystal_ore"

Make-Block $cDeepslate $cC0 (Join-Path $blockTexDir "deepslate_mana_crystal_ore.png")
Write-BlockModelAndState "deepslate_mana_crystal_ore"

Make-Block $cNether $cD0 (Join-Path $blockTexDir "dragonite_ore.png")
Write-BlockModelAndState "dragonite_ore"

# Mystic wood
Make-Block ([System.Drawing.Color]::FromArgb(255, 45, 30, 75)) $null (Join-Path $blockTexDir "mystic_log.png")
Write-BlockModelAndState "mystic_log"

Make-Block ([System.Drawing.Color]::FromArgb(255, 70, 50, 105)) $null (Join-Path $blockTexDir "stripped_mystic_log.png")
Write-BlockModelAndState "stripped_mystic_log"

Make-Block ([System.Drawing.Color]::FromArgb(255, 80, 60, 120)) $null (Join-Path $blockTexDir "mystic_planks.png")
Write-BlockModelAndState "mystic_planks"

Make-Block ([System.Drawing.Color]::FromArgb(255, 60, 40, 130)) ([System.Drawing.Color]::Cyan) (Join-Path $blockTexDir "mystic_leaves.png")
Write-BlockModelAndState "mystic_leaves"

# Emberwood
Make-Block ([System.Drawing.Color]::FromArgb(255, 80, 20, 25)) $null (Join-Path $blockTexDir "emberwood_log.png")
Write-BlockModelAndState "emberwood_log"

Make-Block ([System.Drawing.Color]::FromArgb(255, 120, 35, 40)) $null (Join-Path $blockTexDir "stripped_emberwood_log.png")
Write-BlockModelAndState "stripped_emberwood_log"

Make-Block ([System.Drawing.Color]::FromArgb(255, 140, 45, 50)) $null (Join-Path $blockTexDir "emberwood_planks.png")
Write-BlockModelAndState "emberwood_planks"

Make-Block ([System.Drawing.Color]::FromArgb(255, 150, 30, 30)) ([System.Drawing.Color]::OrangeRed) (Join-Path $blockTexDir "emberwood_leaves.png")
Write-BlockModelAndState "emberwood_leaves"

# Dungeon & Boss Blocks
Make-Block ([System.Drawing.Color]::FromArgb(255, 25, 20, 35)) ([System.Drawing.Color]::Cyan) (Join-Path $blockTexDir "dungeon_gate_block.png")
Write-BlockModelAndState "dungeon_gate_block"

Make-Block ([System.Drawing.Color]::FromArgb(255, 30, 15, 45)) ([System.Drawing.Color]::Gold) (Join-Path $blockTexDir "boss_altar.png")
Write-BlockModelAndState "boss_altar"

Write-Host "SUCCESS: Generated all 53 items, blocks, textures, and models!"
