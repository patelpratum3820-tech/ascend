Add-Type -AssemblyName System.Drawing

$base = "E:\games\src\main\resources\assets\ascend"
$itemTexDir = Join-Path $base "textures\item"
$blockTexDir = Join-Path $base "textures\block"
$armorTexDir = Join-Path $base "textures\models\armor"
$skillTexDir = Join-Path $base "textures\skill"

foreach ($dir in @($itemTexDir, $blockTexDir, $armorTexDir, $skillTexDir)) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}

function Save-Png([System.Drawing.Bitmap]$bmp, [string]$path) {
    if (Test-Path $path) { Remove-Item $path -Force }
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

# --- 1. GENERATE 64x32 ARMOR LAYER TEXTURES (WORN ON BODY) ---
function Create-ArmorLayer([System.Drawing.Color]$mainCol, [System.Drawing.Color]$shadeCol, [System.Drawing.Color]$lightCol, [bool]$isLayer2, [string]$outPath) {
    $bmp = New-Object System.Drawing.Bitmap 64, 32
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.Clear([System.Drawing.Color]::Transparent)

    $brushMain = New-Object System.Drawing.SolidBrush $mainCol
    $brushShade = New-Object System.Drawing.SolidBrush $shadeCol
    $brushLight = New-Object System.Drawing.SolidBrush $lightCol

    if (-not $isLayer2) {
        # LAYER 1: Helmet (0,0 to 32,16), Body (16,16 to 40,32), Arms (40,16 to 56,32), Boots (0,16 to 16,32)
        # Helmet
        $g.FillRectangle($brushMain, 0, 0, 32, 16)
        $g.FillRectangle($brushLight, 4, 0, 8, 4)
        $g.FillRectangle($brushShade, 0, 8, 32, 8)

        # Body Chestplate
        $g.FillRectangle($brushMain, 16, 16, 24, 16)
        $g.FillRectangle($brushLight, 20, 16, 8, 6)
        $g.FillRectangle($brushShade, 16, 26, 24, 6)

        # Shoulders / Arms
        $g.FillRectangle($brushMain, 40, 16, 16, 16)
        $g.FillRectangle($brushLight, 42, 16, 4, 6)
        $g.FillRectangle($brushShade, 40, 26, 16, 6)

        # Boots
        $g.FillRectangle($brushMain, 0, 16, 16, 16)
        $g.FillRectangle($brushLight, 0, 16, 8, 4)
        $g.FillRectangle($brushShade, 0, 26, 16, 6)
    } else {
        # LAYER 2: Leggings
        # Waist / Belt
        $g.FillRectangle($brushMain, 16, 16, 24, 16)
        $g.FillRectangle($brushLight, 16, 16, 24, 4)
        $g.FillRectangle($brushShade, 16, 24, 24, 8)

        # Legs
        $g.FillRectangle($brushMain, 0, 16, 16, 16)
        $g.FillRectangle($brushLight, 2, 16, 4, 10)
        $g.FillRectangle($brushShade, 0, 26, 16, 6)
    }

    $brushMain.Dispose()
    $brushShade.Dispose()
    $brushLight.Dispose()
    $g.Dispose()

    Save-Png $bmp $outPath
}

# Mythril (Sky Blue / Silver)
Create-ArmorLayer ([System.Drawing.Color]::FromArgb(255, 90, 175, 220)) ([System.Drawing.Color]::FromArgb(255, 40, 95, 140)) ([System.Drawing.Color]::FromArgb(255, 190, 235, 255)) $false (Join-Path $armorTexDir "mythril_layer_1.png")
Create-ArmorLayer ([System.Drawing.Color]::FromArgb(255, 90, 175, 220)) ([System.Drawing.Color]::FromArgb(255, 40, 95, 140)) ([System.Drawing.Color]::FromArgb(255, 190, 235, 255)) $true (Join-Path $armorTexDir "mythril_layer_2.png")

# Dragonite (Crimson / Fiery Gold Trim)
Create-ArmorLayer ([System.Drawing.Color]::FromArgb(255, 185, 35, 30)) ([System.Drawing.Color]::FromArgb(255, 105, 15, 15)) ([System.Drawing.Color]::FromArgb(255, 245, 160, 40)) $false (Join-Path $armorTexDir "dragonite_layer_1.png")
Create-ArmorLayer ([System.Drawing.Color]::FromArgb(255, 185, 35, 30)) ([System.Drawing.Color]::FromArgb(255, 105, 15, 15)) ([System.Drawing.Color]::FromArgb(255, 245, 160, 40)) $true (Join-Path $armorTexDir "dragonite_layer_2.png")

# Voidsteel (Obsidian Purple / Lavender Highlight)
Create-ArmorLayer ([System.Drawing.Color]::FromArgb(255, 60, 30, 85)) ([System.Drawing.Color]::FromArgb(255, 25, 12, 40)) ([System.Drawing.Color]::FromArgb(255, 165, 105, 210)) $false (Join-Path $armorTexDir "voidsteel_layer_1.png")
Create-ArmorLayer ([System.Drawing.Color]::FromArgb(255, 60, 30, 85)) ([System.Drawing.Color]::FromArgb(255, 25, 12, 40)) ([System.Drawing.Color]::FromArgb(255, 165, 105, 210)) $true (Join-Path $armorTexDir "voidsteel_layer_2.png")

# --- 2. GENERATE HIGH QUALITY SHADED 16x16 ITEMS ---
function Make-ProItem([string]$type, $colMain, $colLight, $colDark, [string]$outPath) {
    $bmp = New-Object System.Drawing.Bitmap 16, 16

    switch ($type) {
        "helmet" {
            for ($x=3; $x -le 12; $x++) {
                for ($y=2; $y -le 11; $y++) {
                    if ($y -ge 7 -and ($x -ge 6 -and $x -le 9)) { continue } # Visor slit
                    $bmp.SetPixel($x, $y, $colMain)
                }
            }
            # Top highlight
            for ($x=4; $x -le 11; $x++) { $bmp.SetPixel($x, 2, $colLight) }
            # Brow highlight
            for ($x=5; $x -le 10; $x++) { $bmp.SetPixel($x, 6, $colLight) }
            # Shadow sides & bottom
            for ($y=3; $y -le 11; $y++) {
                $bmp.SetPixel(3, $y, $colDark)
                $bmp.SetPixel(12, $y, $colDark)
            }
            for ($x=3; $x -le 12; $x++) { $bmp.SetPixel($x, 11, $colDark) }
        }
        "chestplate" {
            for ($x=2; $x -le 13; $x++) {
                for ($y=2; $y -le 13; $y++) {
                    if ($y -le 4 -and ($x -ge 6 -and $x -le 9)) { continue } # neck
                    if ($y -ge 6 -and ($x -eq 2 -or $x -eq 13)) { continue } # arms cutout
                    $bmp.SetPixel($x, $y, $colMain)
                }
            }
            # Shoulder pauldrons
            $bmp.SetPixel(2, 2, $colLight); $bmp.SetPixel(3, 2, $colLight)
            $bmp.SetPixel(12, 2, $colLight); $bmp.SetPixel(13, 2, $colLight)
            # Center chest emblem
            for ($x=6; $x -le 9; $x++) { $bmp.SetPixel($x, 6, $colLight) }
            # Abdominal shading
            for ($x=4; $x -le 11; $x++) {
                $bmp.SetPixel($x, 9, $colDark)
                $bmp.SetPixel($x, 13, $colDark)
            }
        }
        "leggings" {
            for ($x=3; $x -le 12; $x++) {
                for ($y=2; $y -le 13; $y++) {
                    if ($y -ge 6 -and ($x -ge 7 -and $x -le 8)) { continue } # leg split
                    $bmp.SetPixel($x, $y, $colMain)
                }
            }
            # Belt highlight
            for ($x=3; $x -le 12; $x++) { $bmp.SetPixel($x, 2, $colLight) }
            # Knee guard highlights
            $bmp.SetPixel(4, 8, $colLight); $bmp.SetPixel(5, 8, $colLight)
            $bmp.SetPixel(10, 8, $colLight); $bmp.SetPixel(11, 8, $colLight)
            # Outer shadows
            for ($y=3; $y -le 13; $y++) {
                $bmp.SetPixel(3, $y, $colDark)
                $bmp.SetPixel(12, $y, $colDark)
            }
        }
        "boots" {
            for ($y=7; $y -le 13; $y++) {
                for ($x=2; $x -le 6; $x++) { $bmp.SetPixel($x, $y, $colMain) }
                for ($x=9; $x -le 13; $x++) { $bmp.SetPixel($x, $y, $colMain) }
            }
            # Cuffs
            for ($x=3; $x -le 6; $x++) { $bmp.SetPixel($x, 7, $colLight) }
            for ($x=9; $x -le 12; $x++) { $bmp.SetPixel($x, 7, $colLight) }
            # Soles
            for ($x=2; $x -le 6; $x++) { $bmp.SetPixel($x, 13, $colDark) }
            for ($x=9; $x -le 13; $x++) { $bmp.SetPixel($x, 13, $colDark) }
        }
        "sword" {
            for ($i=0; $i -lt 8; $i++) {
                $x = 4 + $i
                $y = 11 - $i
                $bmp.SetPixel($x, $y, $colMain)
                $bmp.SetPixel($x+1, $y, $colLight)
                $bmp.SetPixel($x, $y+1, $colDark)
            }
            $bmp.SetPixel(12, 3, $colLight)
            # Guard
            $bmp.SetPixel(3, 11, $colLight); $bmp.SetPixel(4, 11, $colDark); $bmp.SetPixel(5, 11, $colLight); $bmp.SetPixel(5, 12, $colDark)
            # Hilt & Pommel
            $bmp.SetPixel(3, 12, [System.Drawing.Color]::FromArgb(255, 95, 60, 30))
            $bmp.SetPixel(2, 13, [System.Drawing.Color]::FromArgb(255, 70, 40, 20))
            $bmp.SetPixel(1, 14, $colLight)
        }
    }

    Save-Png $bmp $outPath
}

# Regenerate all armor & weapons with realistic Minecraft shading
$tiers = @(
    @("mythril", [System.Drawing.Color]::FromArgb(255, 100, 190, 235), [System.Drawing.Color]::FromArgb(255, 215, 250, 255), [System.Drawing.Color]::FromArgb(255, 35, 80, 115)),
    @("dragonite", [System.Drawing.Color]::FromArgb(255, 205, 45, 35), [System.Drawing.Color]::FromArgb(255, 255, 185, 60), [System.Drawing.Color]::FromArgb(255, 100, 15, 15)),
    @("voidsteel", [System.Drawing.Color]::FromArgb(255, 75, 40, 105), [System.Drawing.Color]::FromArgb(255, 185, 130, 225), [System.Drawing.Color]::FromArgb(255, 30, 15, 45))
)

foreach ($t in $tiers) {
    $n = $t[0]; $cm = $t[1]; $cl = $t[2]; $cd = $t[3]
    Make-ProItem "helmet" $cm $cl $cd (Join-Path $itemTexDir "$($n)_helmet.png")
    Make-ProItem "chestplate" $cm $cl $cd (Join-Path $itemTexDir "$($n)_chestplate.png")
    Make-ProItem "leggings" $cm $cl $cd (Join-Path $itemTexDir "$($n)_leggings.png")
    Make-ProItem "boots" $cm $cl $cd (Join-Path $itemTexDir "$($n)_boots.png")
    Make-ProItem "sword" $cm $cl $cd (Join-Path $itemTexDir "$($n)_sword.png")
}

# --- 3. GENERATE ACCESSORIES (BRACELET, RING, TALISMAN, AMULET) ---
function Make-Bracelet([string]$outPath) {
    $bmp = New-Object System.Drawing.Bitmap 16, 16
    for ($x=4; $x -le 11; $x++) {
        $bmp.SetPixel($x, 3, [System.Drawing.Color]::Gold)
        $bmp.SetPixel($x, 12, [System.Drawing.Color]::Gold)
    }
    for ($y=4; $y -le 11; $y++) {
        $bmp.SetPixel(3, $y, [System.Drawing.Color]::Gold)
        $bmp.SetPixel(12, $y, [System.Drawing.Color]::Gold)
    }
    # Cyan Mana Crystals embedded around
    $bmp.SetPixel(7, 3, [System.Drawing.Color]::Cyan); $bmp.SetPixel(8, 3, [System.Drawing.Color]::Cyan)
    $bmp.SetPixel(3, 7, [System.Drawing.Color]::Cyan); $bmp.SetPixel(3, 8, [System.Drawing.Color]::Cyan)
    $bmp.SetPixel(12, 7, [System.Drawing.Color]::Cyan); $bmp.SetPixel(12, 8, [System.Drawing.Color]::Cyan)
    $bmp.SetPixel(7, 12, [System.Drawing.Color]::Cyan); $bmp.SetPixel(8, 12, [System.Drawing.Color]::Cyan)
    Save-Png $bmp $outPath
}

function Make-Ring([string]$outPath) {
    $bmp = New-Object System.Drawing.Bitmap 16, 16
    for ($x=5; $x -le 10; $x++) {
        $bmp.SetPixel($x, 5, [System.Drawing.Color]::FromArgb(255, 215, 50, 40))
        $bmp.SetPixel($x, 11, [System.Drawing.Color]::FromArgb(255, 215, 50, 40))
    }
    for ($y=6; $y -le 10; $y++) {
        $bmp.SetPixel(4, $y, [System.Drawing.Color]::FromArgb(255, 215, 50, 40))
        $bmp.SetPixel(11, $y, [System.Drawing.Color]::FromArgb(255, 215, 50, 40))
    }
    # Ruby fire gem on top
    for ($x=6; $x -le 9; $x++) {
        for ($y=3; $y -le 5; $y++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::OrangeRed)
        }
    }
    $bmp.SetPixel(7, 4, [System.Drawing.Color]::White)
    Save-Png $bmp $outPath
}

function Make-Talisman([string]$outPath) {
    $bmp = New-Object System.Drawing.Bitmap 16, 16
    for ($x=4; $x -le 11; $x++) {
        for ($y=5; $y -le 13; $y++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 45, 20, 65))
        }
    }
    for ($i=6; $i -le 9; $i++) {
        $bmp.SetPixel($i, 7, [System.Drawing.Color]::FromArgb(255, 185, 120, 235))
        $bmp.SetPixel($i, 11, [System.Drawing.Color]::FromArgb(255, 185, 120, 235))
        $bmp.SetPixel(7, $i, [System.Drawing.Color]::FromArgb(255, 185, 120, 235))
    }
    # Cord
    $bmp.SetPixel(7, 3, [System.Drawing.Color]::DarkGray); $bmp.SetPixel(8, 3, [System.Drawing.Color]::DarkGray)
    $bmp.SetPixel(6, 4, [System.Drawing.Color]::DarkGray); $bmp.SetPixel(9, 4, [System.Drawing.Color]::DarkGray)
    Save-Png $bmp $outPath
}

Make-Bracelet (Join-Path $itemTexDir "mana_bracelet.png")
Make-Ring (Join-Path $itemTexDir "stamina_ring.png")
Make-Talisman (Join-Path $itemTexDir "shadow_talisman.png")

# Write models for accessories
function Write-ModelGen([string]$name) {
    $j = @"
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "ascend:item/$name"
  }
}
"@
    Set-Content -Path (Join-Path $base "models\item\$name.json") -Value $j -Encoding UTF8
}
Write-ModelGen "mana_bracelet"
Write-ModelGen "stamina_ring"
Write-ModelGen "shadow_talisman"

# --- 4. GENERATE 16x16 SKILL LOGOS FOR HUD ---
function Make-SkillIcon([string]$name, [System.Drawing.Color]$col, [string]$symbol) {
    $bmp = New-Object System.Drawing.Bitmap 16, 16
    for ($y=2; $y -le 13; $y++) {
        for ($x=2; $x -le 13; $x++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(220, 10, 15, 25))
        }
    }
    for ($x=2; $x -le 13; $x++) {
        $bmp.SetPixel($x, 2, $col)
        $bmp.SetPixel($x, 13, $col)
        $bmp.SetPixel(2, $x, $col)
        $bmp.SetPixel(13, $x, $col)
    }

    # Custom glyphs
    switch ($symbol) {
        "arrow" {
            for ($i=4; $i -le 11; $i++) { $bmp.SetPixel($i, 8, $col) }
            $bmp.SetPixel(10, 7, $col); $bmp.SetPixel(9, 6, $col)
            $bmp.SetPixel(10, 9, $col); $bmp.SetPixel(9, 10, $col)
        }
        "fire" {
            for ($y=5; $y -le 11; $y++) {
                for ($x=6; $x -le 9; $x++) { $bmp.SetPixel($x, $y, $col) }
            }
            $bmp.SetPixel(7, 3, [System.Drawing.Color]::Yellow)
            $bmp.SetPixel(8, 4, [System.Drawing.Color]::Yellow)
        }
        "cross" {
            for ($i=4; $i -le 11; $i++) {
                $bmp.SetPixel($i, 7, $col); $bmp.SetPixel($i, 8, $col)
                $bmp.SetPixel(7, $i, $col); $bmp.SetPixel(8, $i, $col)
            }
        }
        "bolt" {
            $bmp.SetPixel(9, 4, $col); $bmp.SetPixel(8, 5, $col); $bmp.SetPixel(7, 6, $col)
            for ($x=6; $x -le 10; $x++) { $bmp.SetPixel($x, 7, $col) }
            $bmp.SetPixel(8, 8, $col); $bmp.SetPixel(7, 9, $col); $bmp.SetPixel(6, 10, $col)
        }
        "shield" {
            for ($x=5; $x -le 10; $x++) {
                for ($y=5; $y -le 10; $y++) { $bmp.SetPixel($x, $y, $col) }
            }
            $bmp.SetPixel(7, 11, $col); $bmp.SetPixel(8, 11, $col)
        }
        "skull" {
            for ($x=5; $x -le 10; $x++) {
                for ($y=4; $y -le 9; $y++) { $bmp.SetPixel($x, $y, $col) }
            }
            $bmp.SetPixel(6, 6, [System.Drawing.Color]::Black); $bmp.SetPixel(9, 6, [System.Drawing.Color]::Black)
            $bmp.SetPixel(6, 11, $col); $bmp.SetPixel(7, 11, $col); $bmp.SetPixel(8, 11, $col); $bmp.SetPixel(9, 11, $col)
        }
        "star" {
            for ($i=4; $i -le 11; $i++) {
                $bmp.SetPixel($i, $i, $col)
                $bmp.SetPixel(15-$i, $i, $col)
                $bmp.SetPixel(7, $i, $col)
                $bmp.SetPixel($i, 7, $col)
            }
        }
    }
    Save-Png $bmp (Join-Path $skillTexDir "$name.png")
}

Make-SkillIcon "shadow_step" ([System.Drawing.Color]::Cyan) "arrow"
Make-SkillIcon "flame_slash" ([System.Drawing.Color]::OrangeRed) "fire"
Make-SkillIcon "healing_aura" ([System.Drawing.Color]::LimeGreen) "cross"
Make-SkillIcon "summon_soldier" ([System.Drawing.Color]::MediumPurple) "skull"
Make-SkillIcon "mana_bolt" ([System.Drawing.Color]::DeepSkyBlue) "bolt"
Make-SkillIcon "arrow_rain" ([System.Drawing.Color]::Gold) "arrow"
Make-SkillIcon "iron_skin" ([System.Drawing.Color]::LightGray) "shield"
Make-SkillIcon "lightning_strike" ([System.Drawing.Color]::Yellow) "bolt"
Make-SkillIcon "berserk" ([System.Drawing.Color]::Red) "fire"
Make-SkillIcon "soul_bond" ([System.Drawing.Color]::Violet) "cross"

Make-SkillIcon "sig_triple_slash" ([System.Drawing.Color]::Gold) "star"
Make-SkillIcon "sig_summon_portal" ([System.Drawing.Color]::Purple) "skull"
Make-SkillIcon "sig_meteor" ([System.Drawing.Color]::OrangeRed) "fire"
Make-SkillIcon "sig_piercing_volley" ([System.Drawing.Color]::Gold) "arrow"
Make-SkillIcon "sig_guardian_slam" ([System.Drawing.Color]::LightBlue) "shield"

Write-Host "PRO ASSETS GENERATED SUCCESSFULLY!"

