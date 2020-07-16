package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.item.ItemCustomRecord;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RecordJsonParser {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static ArrayList<ItemCustomRecord> parse() throws IOException {
        ArrayList<ItemCustomRecord> records = new ArrayList<>();
        File recordsJson = new File(FabricLoader.getInstance().getConfigDirectory() + "/" + MusicExpansion.MODID + "/records.json");
        if (!recordsJson.exists()) {
            recordsJson.getParentFile().mkdirs();
            recordsJson.createNewFile();
        }
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(new File(FabricLoader.getInstance().getConfigDirectory() + File.separator + MusicExpansion.MODID+ File.separator + "records.json")));
        JsonArray element = gson.fromJson(reader, JsonArray.class);
        if (element != null) {
            for (JsonElement object : element) {
                records.add(new ItemCustomRecord(new Identifier(MusicExpansion.MODID_EXTERNAL, object.getAsString()), new SoundEvent(new Identifier(MusicExpansion.MODID_EXTERNAL, object.getAsString()))));
            }
        }
        return records;
    }
}
