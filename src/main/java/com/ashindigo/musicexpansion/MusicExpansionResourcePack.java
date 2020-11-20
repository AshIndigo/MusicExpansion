package com.ashindigo.musicexpansion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

// TODO Could probably shave off custom item model stuff now...
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
                MusicExpansion.logger.log(Level.WARN, "Unable to create necessary directory(s)! Please check file permissions");
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
                MusicExpansion.logger.log(Level.WARN, "File not found! Missing File: " + new File(fileDir, "/" + names[names.length - 1]).toString());
            }
            return new FileInputStream(new File(fileDir, "/" + names[names.length - 1]));
        } else if (names[2].equals("models")) {
            Identifier id = new Identifier(names[1], FilenameUtils.removeExtension(names[names.length - 1]));
            if (id.getPath().contains("custom_disc")) {
                if (id.getPath().matches("custom_disc_([0-9]|[1-9][0-9]|[1-9][0-9][0-9])$")) {
                    return getCustomDiscJson(Integer.parseInt(id.getPath().split("_")[2]));
                } else {
                    return getItemPredicateJson(id);
                }
            } else {
                return getItemJson(id);
            }
        }
        return new ByteArrayInputStream("".getBytes());
    }

    private InputStream getCustomDiscJson(int i) {
        Identifier identifier = new Identifier(MusicExpansion.MODID_EXTERNAL, "item/" + MusicExpansion.tracks.get(i).getPath());
        JsonObject file = new JsonObject();
        file.addProperty("parent", "item/generated");
        JsonObject texture = new JsonObject();
        texture.addProperty("layer0", identifier.getNamespace() + ":" + identifier.getPath());
        file.add("textures", texture);
        return new ByteArrayInputStream(file.toString().getBytes());
    }

    public static ByteArrayInputStream getSoundsJson() {
        JsonObject file = new JsonObject();
        for (Identifier sound : MusicExpansion.tracks) {
            JsonObject soundInfo = new JsonObject();
            JsonObject trackInfo = new JsonObject();
            trackInfo.addProperty("name", sound.getNamespace() + ":music/" + sound.getPath());
            trackInfo.addProperty("stream", true);
            JsonArray array = new JsonArray();
            array.add(trackInfo);
            soundInfo.add("sounds", array);
            file.add(sound.getPath(), soundInfo);
        }
        return new ByteArrayInputStream(file.toString().getBytes());
    }

    public static ByteArrayInputStream getItemPredicateJson(Identifier identifier) {
        JsonObject file = new JsonObject();
        file.addProperty("parent", "item/generated");
        JsonObject texture = new JsonObject();
        texture.addProperty("layer0", identifier.getNamespace() + ":item/" + identifier.getPath());
        file.add("textures", texture);
        JsonArray overrides = new JsonArray();
        ArrayList<Identifier> tracks = MusicExpansion.tracks;
        for (int i = 0, tracksSize = tracks.size(); i < tracksSize; i++) {
            JsonObject root = new JsonObject();
            JsonObject value = new JsonObject();
            value.addProperty(MusicExpansion.MODID + ":" + "custom_disc", i * 1F);
            root.add("predicate", value);
            root.addProperty("model", identifier.getNamespace() + ":item/" + identifier.getPath() + "_" + i);
            overrides.add(root);
        }
        file.add("overrides", overrides);
        return new ByteArrayInputStream(file.toString().getBytes());
    }

    public static ByteArrayInputStream getItemJson(Identifier identifier) {
        JsonObject file = new JsonObject();
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
        if (var2.getNamespace().equals(MusicExpansion.MODID_EXTERNAL)) {
            return !var2.getPath().endsWith(".mcmeta");
        } else return var2.getPath().contains("custom_disc") && var2.getPath().endsWith(".json");
    }

    @Override
    protected boolean containsFile(String var1) {
        return false; // NO-OP
    }


    @Override
    public Set<String> getNamespaces(ResourceType var1) {
        HashSet<String> set = new HashSet<>();
        set.add(MusicExpansion.MODID_EXTERNAL);
        set.add(MusicExpansion.MODID);
        return set;
    }

    @Override
    public void close() {

    }
}
