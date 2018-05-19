package com.storycraft.storage;

import com.storycraft.StoryPlugin;

import java.io.*;

public class PluginDataStorage extends Storage<byte[]> {

    private StoryPlugin plugin;

    public PluginDataStorage(StoryPlugin plugin){
        this.plugin = plugin;
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public File getDataFolder(){
        return getPlugin().getDataFolder();
    }

    protected File getFile(String name){
        return new File(getDataFolder(), name);
    }

    @Override
    public boolean saveSync(byte[] data, String name) throws IOException {
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(getFile(name)));

        writer.write(data);
        writer.close();

        return true;
    }

    @Override
    public byte[] getSync(String name) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        File file = getFile(name);
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));

        byte[] readBuffer = new byte[2048];
        while (input.read(readBuffer, 0, readBuffer.length) != -1) {
            output.write(readBuffer);
        }

        input.close();
        output.close();

        return output.toByteArray();
    }
}