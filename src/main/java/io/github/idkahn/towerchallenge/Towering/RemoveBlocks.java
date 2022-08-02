package io.github.idkahn.towerchallenge.Towering;

import io.github.idkahn.towerchallenge.Towering.TowerListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RemoveBlocks implements CommandExecutor {

    private TowerListener towerListener;

    public RemoveBlocks(TowerListener towerListener) {
        this.towerListener = towerListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        towerListener.removeBlocks();

        return true;
    }
}
