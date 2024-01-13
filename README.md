This is a mod to use Custom portal api[Forge] to add portal between 2 dimension accrodding to forge configs and allow Create's Rail to Pass through it.
You can config portal in ccpacrcompact-common.toml.
Custom Portal API[Forge] mod is required, Create mod is optional.

Here is the configs to add portal betweem 2 dimensions using custom portal api.

All conifgs of one custom portal  shoud in a list([...]), A list([...]) of conifgs of one custom portal should have at least 3 list([...]).

Each list sholud in format [[Dimensions' Info], [Light portal type], [portal RGB color]].

[Dimensions' Info] -> [modid:DimAID, modid:DimBID, modid:FrameBlockID, diffPara(Optional)].DimA(X, Y, Z) = diffPara * DimB(X, Y, Z), diffPara is vacant or can not be convert to Double, diffPara = 1.0.

[Light portal type] -> [Light type, modid:ID], Light type -> Fire, Item, Water, Fluid, Custom. Empty, not any or modid:ID is not exist,Light type = 'Fire'.

[portal RGB color] -> [R, G, B]. R/G/B is empty or can not be convert to Integer, R/G/B = 127.

[Light portal type], [portal RGB color]  can be Empty list([]) but can not be not any thing.

"Dimension Configs" = [[["minecraft:overworld", "minecraft:the_end", "minecraft:end_stone_bricks", "8.0"], ["Item", "minecraft:ender_eye"], ["45", "61", "101"]], 
                       [["minecraft:the_nether", "minecraft:the_end", "minecraft:end_stone", "0.015625"], ["Fire"], ["77", "88", "22"]]]
                       
Whether to use the Custom Portal API Vanilla teleport entity method.

If this set to 'true', the diffPara of Dimension config wll be invalid.(Default=false).


"Vanilla CPA teleport" = false

Whether to use the Custom Portal API Vanilla Find, link and create portal method.

If the previous config(Vanilla CPA teleport) is set to 'true', this will be invalid.

Tips:Custom Portal API will save the link of portal between 2 dimension in .nbt file.Once a Link is created, it is not deleted, even if the portal is destroyed.If this set to 'false', the program will dynamically locate the portal at the destination.(Default=false).

"Vanilla CPA Portal" = false
