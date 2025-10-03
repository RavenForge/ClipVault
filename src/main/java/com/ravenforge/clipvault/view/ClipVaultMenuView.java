package com.ravenforge.clipvault.view;

import com.ravenforge.clipvault.component.NewTabDialogComponent;
import com.ravenforge.clipvault.component.PasswordDialogComponent;
import com.ravenforge.clipvault.model.Tab;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClipVaultMenuView extends MenuBar {
    public ClipVaultMenuView(List<Tab> tabs, Consumer<Tab> openTabConsumer, BiConsumer<Tab, String> openSecretTabConsumer, BiConsumer<String, String> newTabFunction) {
        this.getMenus().addAll(
                tabsMenu(tabs, openTabConsumer, newTabFunction),
                secretTabsMenu(tabs, openSecretTabConsumer)
        );
    }

    private Menu tabsMenu(List<Tab> tabList, Consumer<Tab> openTabConsumer, BiConsumer<String, String> newTabFunction) {
        var menu = new Menu("_Tabs");
        menu.setMnemonicParsing(true);

        var newTab = createItem("_New Tab", new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newTab.setMnemonicParsing(true);
        newTab.setOnAction(_ -> {
            NewTabDialogComponent.createNewTabDialog(newTabFunction);
        });

        List<MenuItem> tabs = tabList.stream().filter(tab -> tab.getPassword() == null).map(tab -> {
            var menuItem = new MenuItem(tab.getName());
            menuItem.setOnAction(_ -> openTabConsumer.accept(tab));
            return menuItem;
        }).toList();

        menu.getItems().addAll(newTab, new SeparatorMenuItem());
        menu.getItems().addAll(tabs);


        return menu;
    }

    private Menu secretTabsMenu(List<Tab> secretTabList, BiConsumer<Tab, String> openSecretTabConsumer) {
        var menu = new Menu("_Secret Tabs");
        menu.setMnemonicParsing(true);

        List<MenuItem> tabs = secretTabList.stream().filter(tab -> tab.getPassword() != null).map(tab -> {
            var menuItem = new MenuItem(tab.getName());
            menuItem.setOnAction(_ -> {
                PasswordDialogComponent.createPasswordDialogComponent(openSecretTabConsumer, tab);
                return;
            });
            return menuItem;
        }).toList();

        menu.getItems().addAll(tabs);

        return menu;
    }

    private MenuItem createItem(String text, KeyCombination keyCombination) {
        var item = new MenuItem(text);

        if (keyCombination != null)
            item.setAccelerator(keyCombination);

        return item;
    }
}
