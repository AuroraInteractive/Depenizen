package com.denizenscript.depenizen.bukkit.commands.bungee;

import com.denizenscript.depenizen.bukkit.bungee.BungeeBridge;
import com.denizenscript.depenizen.bukkit.bungee.packets.out.ExecuteCommandPacketOut;
import com.denizenscript.depenizen.bukkit.bungee.packets.out.KeepAlivePacketOut;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;

public class BungeeExecuteCommand extends AbstractCommand {

    // <--[command]
    // @Name BungeeExecute
    // @Syntax bungeeexecute [<command>]
    // @Group Depenizen
    // @Plugin Depenizen, BungeeCord
    // @Required 1
    // @Short Runs a command on the Bungee proxy server.
    //
    // @Description
    // This command runs a command on the Bungee proxy server. Works similarly to "execute as_server".
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to run the 'alert' bungee command.
    // - bungeeexecute "alert Network restart in 5 minutes..."
    //
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        for (aH.Argument arg : aH.interpretArguments(scriptEntry.aHArgs)) {
            if (!scriptEntry.hasObject("command")) {
                scriptEntry.addObject("command", arg.asElement());
            }
            else {
                arg.reportUnhandled();
            }
        }
        if (!scriptEntry.hasObject("command")) {
            throw new InvalidArgumentsException("Must define a COMMAND to be run.");
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        Element command = scriptEntry.getElement("command");
        if (scriptEntry.dbCallShouldDebug()) {
            dB.report(scriptEntry, getName(), command.debug());
        }
        if (!BungeeBridge.instance.connected) {
            dB.echoError("Cannot BungeeExecute: bungee is not connected!");
            return;
        }
        ExecuteCommandPacketOut packet = new ExecuteCommandPacketOut(command.asString());
        BungeeBridge.instance.sendPacket(packet);
        BungeeBridge.instance.sendPacket(new KeepAlivePacketOut());
    }
}