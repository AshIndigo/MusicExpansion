package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.item.ItemCustomRecord;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MusicExpansionResourcePack extends AbstractFileResourcePack {

    private final File fileDir;

    public MusicExpansionResourcePack() {
        super(new File(MusicExpansion.MODID_EXTERNAL));
        fileDir = new File(MinecraftClient.getInstance().runDirectory, "config/" + MusicExpansion.MODID);
    }

    @Override
    protected InputStream openFile(String name) throws FileNotFoundException {
        String[] names = name.split("/");
        if (!fileDir.exists()) {
           boolean success = fileDir.mkdirs();
           if (!success) {
               Logger.getLogger(MusicExpansion.MODID).log(Level.WARNING, "Unable to create necessary directory(s)! Please check file permissions");
            }
        }
        if (name.equals("pack.mcmeta")) {
            return new ByteArrayInputStream(("{\n \"pack\": {\n   \"description\": \"MusicExpansion's internal pack\",\n   \"pack_format\": 4\n}\n}").getBytes(StandardCharsets.UTF_8));
        } else if (name.equals("pack.png")) {
            return new ByteArrayInputStream("".getBytes());
        } else if (name.equals("assets/" + MusicExpansion.MODID_EXTERNAL + "/sounds.json")) {
            return getSoundsJson();
        } else if (names[1].equals(MusicExpansion.MODID_EXTERNAL) && (names[2].equals("textures") || names[2].equals("lang") || name.endsWith(".ogg"))) {
            if (!new File(fileDir, "/" + names[names.length - 1]).exists()) {
                Logger.getLogger(MusicExpansion.MODID).log(Level.WARNING, "File not found! Missing File: " + new File(fileDir, "/" + names[names.length - 1]).toString());
            }
            return new FileInputStream(new File(fileDir, "/" + names[names.length - 1]));
        } else if (names[2].equals("models")) {
            Identifier id = new Identifier(names[1], FilenameUtils.removeExtension(names[names.length - 1]));
            return getItemJson(id);
        }
        return new ByteArrayInputStream("".getBytes());
    }

    public static ByteArrayInputStream getSoundsJson() {
        JsonObject file = new JsonObject();
        for (ItemCustomRecord sound : MusicExpansion.records) {
            JsonObject soundInfo = new JsonObject();
            JsonObject trackInfo = new JsonObject();
            trackInfo.addProperty("name", sound.getEvent().getId().getNamespace() + ":music/" + sound.getEvent().getId().getPath());
            trackInfo.addProperty("stream", true);
            JsonArray array = new JsonArray();
            array.add(trackInfo);
            soundInfo.add("sounds", array);
            file.add(sound.getEvent().getId().getPath(), soundInfo);
        }
        return new ByteArrayInputStream(file.toString().getBytes());
    }

    public static ByteArrayInputStream getItemJson(Identifier identifier) {
        JsonObject file = new JsonObject();
        file.addProperty("forge_marker", 1);
        file.addProperty("parent", "item/generated");
        JsonObject texture = new JsonObject();
        texture.addProperty("layer0", identifier.getNamespace() + ":items/" + identifier.getPath());
        file.add("textures", texture);
        return new ByteArrayInputStream(file.toString().getBytes());
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        return Collections.emptyList();
    }

    @Override
    public boolean contains(ResourceType var1, Identifier var2) {
        if (var1 == ResourceType.SERVER_DATA) {
            return false;
        }
        if (var2.getPath().equals("pack.mcmeta")) {
            return true;
        }
        return var2.getNamespace().equals(MusicExpansion.MODID_EXTERNAL) && !var2.getPath().endsWith(".mcmeta");
    }

    @Override
    protected boolean containsFile(String var1) {
        return false; // NO-OP
    }


    @Override
    public Set<String> getNamespaces(ResourceType var1) {
        HashSet<String> set = new HashSet<>();
        set.add(MusicExpansion.MODID_EXTERNAL);
        return set;
    }

    @Override
    public void close() {

    }
}
