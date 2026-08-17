package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class CrashOut extends Command {

    public CrashOut() {
        super("crashout", "No more ragebait.");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(_ -> {
            Minecraft.getInstance().stop();
            return SINGLE_SUCCESS;
        });
    }
}

class BigRatString {
    //you found the Easter egg lol in the source code, congrats
    String d =  "⠀⠀⠀⠀⠀⠀⠀⠀          ⢀⣴⠖⣤⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀           ";
    String d1 = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡎⡇⠀⢘⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀            ";
    String d3 = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡂⠘⠀⠚⠘⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀            ";
    String d5 = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠰⠄⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀            ";
    String d4 = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⢸⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀            ";
    String d6 = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢡⠀⠀⠀⡀⢣⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀            ";
    String d8 = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡀⢀⡀⠀⠀⠀⠀⠈⢇⠀⠀⠉⠀⡟⠓⢦⠀⠀⠀⠀⠀⠀⠀⠀           ";
    String d7 = "⠀⠀⠀⠀⣀⡤⡀⠀⠀⢠⢋⠔⠀⠈⢦⠀⠀⠀⠀⠘⡄⠀⠀⠀⢱⠀⠀⢳⣤⣀⠀⠀⠀⠀⠀          ";
    String d9 = "⠀⠀⠀⠸⠁⠘⡌⢦⠀⡇⡎⠀⠀⠀⢨⠀⢰⠒⠉⠉⠱⢄⠀⠀⢠⠳⠀⢤⣹⡀⠑⢤⣀⠀⠀         ";
    String e1  = "⠀⠀⠀⠸⡀⢀⡧⢖⡉⠁⠁⠀⠀⣀⣼⠖⠓⠢⠀⠀⠲⠆⠀⠀⠀⢣⠀⠀⠀⢇⢠⠀⠙⢷⠀          ";
    String e2  = "⠀⠀⠀⢠⠟⠁⠀⠈⠁⠀⠀⠀⠀⠀⢷⠀⠀⠹⠀⠀⠀⡆⠀⠀⠀⢸⠀⠀⠀⠸⡇⠀⠀⠈⡇            ";
    String e3  = "⢀⡠⠚⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢏⢇⠀⠀⠀⠀⠀⠸⠀⠀⠀⠸⠀⠀⠀⠀⡇⠀⠀⢠⡀             ";
    String e4  = "⠉⠚⠒⠤⢀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢹⠀⠀⠀⠀⠀⠀⢇⠀⠀⠀⠃⠀⠀⠀⠁⠀⠀⡜⠀             ";
    String e5  = "⠀⠀⠀⠀⠀⠀⠉⢢⠀⠀⠀⠀⠀⠀⠀⠀⢇⠀⠀⠀⠸⡄⠘⠆⠀⠀⠀⠀⠀⠀⠀⠀⢠⠃⠀              ";
    String e6  = "⠀⠀⠀⠀⠀⠀⠀⢀⠀⡀⠀⠀⠀⠀⠀⠀⠘⣆⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠎⠀⠀               ";
    String e7  = "⠀⠀⠀⠀⠀⠀⠀⢸⠀⠹⡄⢸⠀⠀⠀⠀⠀⠘⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡠⠋⠀⠀⠀               ";
    String e8  = "⠀⠀⠀⠀⠀⠀⠀⠀⢇⠀⠙⣼⡆⠀⠀⠀⠀⠀⠘⢦⣀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⠀⠀⠀⠀               ";
    String e9  = "⠀⠀⠀⠀⠀⠀⠀⠀⠈⠓⢼⡏⠧⣄⠠⠄⠀⠀⠀⠀⠈⢯⠒⠒⠂⠤⠐⠒⠁⠀⠀⠀⠀⠀⠀             ";
    String f  = "⠀⠀⠀⠀⠀⢀⢀⣀⡠⠶⣀⠑⠟⠉⠒⣤⡀⠀⠀⠀⠀⠘⣄⠀⠀⠀⠀⢀⢀⠀⠀⠀⠀⠀⠀             ";
    String e  = "⠀⠀⠀⠀⠀⠈⠛⠒⠒⠒⠛⠛⠃⠀⠀⠀⠉⠒⢤⣀⣠⣞⠻⢭⣗⡖⠤⢬⣉⣙⣻⣷⡆⠀⠀           ";
    String y  = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠯⣉⠀⠙⠢⣀⠈⠉⠐⠓⠚⠂⠉⠁⠀⠀              ";
    String h  = "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠑⢦⣦⣶⠷⠒⠀⠀⠀⠀⠀⠀⠀⠀                ";
}
