package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.item.ItemCustomRecord;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RecordJsonParser {

    private static boolean allRecords = false;


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Deprecated
    public static ArrayList<ItemCustomRecord> parseOld() throws IOException {
        ArrayList<ItemCustomRecord> records = new ArrayList<>();
        File recordsJson = new File(FabricLoader.getInstance().getConfigDir() + File.separator + MusicExpansion.MODID + File.separator + "records.json");
        if (!recordsJson.exists()) {
            recordsJson.getParentFile().mkdirs();
            recordsJson.createNewFile();
            FileWriter fw = new FileWriter(recordsJson);
            fw.write("{\n \t\"allrecords\": false, \n\t\"records\": []\n}");
            fw.close();
        }
        JsonReader reader = new JsonReader(new FileReader(recordsJson));
        JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
        setAllRecords(root.get("allrecords").getAsBoolean());
        JsonArray element = root.getAsJsonArray("records");
        if (element != null) {
            for (JsonElement object : element) {
                records.add(new ItemCustomRecord(new Identifier(MusicExpansion.MODID_EXTERNAL, object.getAsString()), new SoundEvent(new Identifier(MusicExpansion.MODID_EXTERNAL, object.getAsString()))));
            }
        }
        return records;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static ArrayList<Identifier> parse() throws IOException {
        ArrayList<Identifier> tracks = new ArrayList<>();
        File recordsJson = new File(FabricLoader.getInstance().getConfigDir() + File.separator + MusicExpansion.MODID + File.separator + "records.json");
        if (!recordsJson.exists()) {
            recordsJson.getParentFile().mkdirs();
            recordsJson.createNewFile();
            FileWriter fw = new FileWriter(recordsJson);
            fw.write("{\n \t\"allrecords\": false, \n\t\"records\": []\n}");
            fw.close();
        }
        JsonReader reader = new JsonReader(new FileReader(recordsJson));
        JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
        setAllRecords(root.get("allrecords").getAsBoolean());
        JsonArray element = root.getAsJsonArray("records");
        if (element != null) {
            for (JsonElement object : element) {
                tracks.add(new Identifier(MusicExpansion.MODID_EXTERNAL, object.getAsString()));
            }
        }
        return tracks;
    }

    public static boolean isAllRecords() {
        return allRecords;
    }

    public static void setAllRecords(boolean allRecords) {
        RecordJsonParser.allRecords = allRecords;
    }
}
