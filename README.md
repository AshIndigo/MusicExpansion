# Music Expansion

## About

Expands Minecraft's musical playing capabilities

## Custom Record instructions

1. Run the game once to generate a few files needed. You will need config/musicexpansion/records.json
2. Open the newly created file in a text editor
    - Set allrecords to true if you wish to allow the record maker to make all music discs.
3. In the records array, put a new string per record you wish to be added i.e ["record1", "record2"]
4. Put the PNG texture file in the same folder with the same file name as the added record i.e record1.png
5. OGG files must be MONO not STEREO for them to play in line with Vanilla, FFMPEG works well for this.
6. Setup an en_us.json file to give the disc's descriptions. Key format is as follows "item.musicexpansionexternal.record1.desc": "Record 1 Desc"
7. Launch and try out your discs in game