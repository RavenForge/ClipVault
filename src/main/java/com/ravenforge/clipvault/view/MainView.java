package com.ravenforge.clipvault.view;

import atlantafx.base.theme.Styles;
import com.ravenforge.clipvault.component.NotificationComponent;
import com.ravenforge.clipvault.crypto.CryptoUtil;
import com.ravenforge.clipvault.model.Tab;
import com.ravenforge.clipvault.model.query.QSnippet;
import com.ravenforge.clipvault.model.query.QTab;
import jakarta.persistence.PersistenceException;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.sqlite.SQLiteException;

import java.util.List;

public class MainView extends BorderPane {
    private final ObservableList<javafx.scene.control.Tab> javaFXTabs;
    private final Pagination pagination;
    private ObservableList<Node> stackPaneChildren;

    public MainView() {
        TabPane tabPane = new TabPane();
        javaFXTabs = tabPane.getTabs();

        pagination = new Pagination();
        pagination.setPageCount(1);
        pagination.setMaxPageIndicatorCount(5);

        var stackPane = new StackPane();
        stackPaneChildren = stackPane.getChildren();

        tabPane.getTabs().addListener((ListChangeListener<? super javafx.scene.control.Tab>) e -> {
            if (e.getList().isEmpty()) {
                stackPaneChildren = stackPane.getChildren();
                setCenter(stackPane);
                NotificationComponent.SCROLL_MARGIN = 0d;
                return;
            }

            setCenter(tabPane);
        });

        List<Tab> tabs = new QTab().select().findList();
        tabs.stream().filter(tab -> tab.getPassword() == null).forEach(tab -> javaFXTabs.add(createTab(tab, null)));

        setTop(new ClipVaultMenuView(tabs, this::openTab, this::openTab, this::newTab));
        setCenter(tabs.isEmpty() ? stackPane: tabPane);
        setBottom(pagination);
    }

    private javafx.scene.control.Tab createTab(Tab tab, String password) {
        javafx.scene.control.Tab currentTab = new javafx.scene.control.Tab(tab.getName());
        currentTab.setGraphic(new FontIcon(null == password ? Feather.HARD_DRIVE : Feather.LOCK));

        StackPane stackPane = new StackPane();
        var stackPaneChildren = stackPane.getChildren();

        SnippetView snippetView = new SnippetView(tab, password, pagination, stackPaneChildren, this::deleteTab);
        stackPaneChildren.add(snippetView);

        ScrollPane scrollPane = new ScrollPane(stackPane);
        scrollPane.setFitToWidth(true);

        scrollPane.vvalueProperty().addListener((_, _, newValue) -> {
            double contentHeight = stackPane.getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();

            NotificationComponent.SCROLL_MARGIN = newValue.doubleValue() * (contentHeight - viewportHeight);
        });

        currentTab.setContent(scrollPane);

        currentTab.setOnSelectionChanged(_ -> {
            if (currentTab.isSelected()) {
                this.stackPaneChildren = stackPaneChildren;
                snippetView.refreshPagination();

                double contentHeight = stackPane.getBoundsInLocal().getHeight();
                double viewportHeight = scrollPane.getViewportBounds().getHeight();
                NotificationComponent.SCROLL_MARGIN = scrollPane.getVvalue() * (contentHeight - viewportHeight);
            }
        });

        pagination.currentPageIndexProperty().addListener((_, _, newValue) -> {
            if (currentTab.isSelected()) {
                snippetView.nextPage(newValue);
            }
        });

        return currentTab;
    }

    private void newTab(String tabName, String password) {
        Tab tab = new Tab();
        tab.setName(tabName);
        if (tab.getName().isBlank()) {
            NotificationComponent.createNotification(stackPaneChildren, Feather.ALERT_TRIANGLE, "Cannot Create Tab With Empty Name", Styles.DANGER);
            return;
        }

        if (password != null && !password.isBlank())
            tab.setPassword(CryptoUtil.hash(password));

        try {
            tab.save();
            refreshMenu();
            if (null != password && !password.isBlank()) {
                openTab(tab, password);
                return;
            }
            openTab(tab);
            NotificationComponent.createNotification(stackPaneChildren, Feather.POCKET, "Tab Created", Styles.SUCCESS);
        } catch (PersistenceException e) {
            var cause = (SQLiteException) e.getCause();
            String message;
            if (19 == cause.getErrorCode()) {
                message = "Tab Already Exists";
            } else {
                message = "Unknown Error";
            }

            NotificationComponent.createNotification(stackPaneChildren, Feather.ALERT_TRIANGLE, message, Styles.DANGER);
        }
    }

    private void openTab(Tab tab) {
        if (javaFXTabs.stream().noneMatch(tabFX -> tabFX.getText().equals(tab.getName()))) {
            javaFXTabs.add(createTab(tab, null));
        }
    }

    private void openTab(Tab tab, String password) {
        if (!CryptoUtil.verifyHash(password, tab.getPassword())) {
            NotificationComponent.createNotification(stackPaneChildren, Feather.ALERT_TRIANGLE, "Invalid Password", Styles.DANGER);
            return;
        }

        if (javaFXTabs.stream().noneMatch(tabFX -> tabFX.getText().equals(tab.getName()))) {
            javaFXTabs.add(createTab(tab, password));
        }
    }

    private void refreshMenu() {
        List<Tab> tabs = new QTab().select().findList();
        setTop(new ClipVaultMenuView(tabs, this::openTab, this::openTab, this::newTab));
    }

    private void deleteTab(String tabName) {
        var tab = new QTab().select().name.eq(tabName).setMaxRows(1).findOne();
        assert tab != null;

        javaFXTabs.stream()
                .filter(tabFX -> tabFX.getText().equals(tab.getName()))
                .findFirst()
                .ifPresent(javaFXTabs::remove);

        new QSnippet().tab.id.eq(tab.getId()).delete();
        tab.delete();
        refreshMenu();
    }
}
