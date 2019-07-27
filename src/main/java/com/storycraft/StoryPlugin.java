package com.storycraft;

import com.storycraft.command.CommandManager;
import com.storycraft.core.CoreManager;
import com.storycraft.core.command.SayCommand;

import java.lang.instrument.Instrumentation;

public class StoryPlugin extends MainPlugin {

    private CoreManager coreManager;

    public StoryPlugin() {
        this.coreManager = new CoreManager(this);
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    @Override
    protected void registerCommand(CommandManager manager) {
        manager.addCommand(new SayCommand());
    }

    @Override
    protected void registerMiniPlugin(MiniPluginLoader loader) {
        loader.addMiniPlugin(coreManager);
    }

    public static void main(String[] args){
        System.out.println("이 프로그램은 단독 실행 될수 없습니다");
    }

    public static void premain(String args, Instrumentation inst) throws Exception {
        System.out.println("Story Server Preloaded");
    }
}
