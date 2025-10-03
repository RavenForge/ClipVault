package com.ravenforge.clipvault.view;

import atlantafx.base.theme.Styles;
import com.ravenforge.clipvault.component.ConfirmationDialogComponent;
import com.ravenforge.clipvault.component.FieldComponent;
import com.ravenforge.clipvault.component.IconButtonBarComponent;
import com.ravenforge.clipvault.component.IconButtonComponent;
import com.ravenforge.clipvault.component.NotificationComponent;
import com.ravenforge.clipvault.crypto.CryptoUtil;
import com.ravenforge.clipvault.model.Snippet;
import com.ravenforge.clipvault.model.Tab;
import com.ravenforge.clipvault.model.query.QSnippet;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.function.Consumer;

public class SnippetView extends VBox {
    private int currentPage = 0;
    private final Tab tab;
    private final String password;
    private final boolean encrypted;
    private final Pagination pagination;
    private final ObservableList<Node> stackPaneChildren;
    private final Consumer<String> deleteTab;
    private final QSnippet snippetQueryBuilder = new QSnippet();

    public SnippetView(Tab tab, String password, Pagination pagination, ObservableList<Node> stackPaneChildren, Consumer<String> deleteTab) {
        this.tab = tab;
        this.password = password;
        this.encrypted = null != password;
        this.pagination = pagination;
        this.stackPaneChildren = stackPaneChildren;
        this.deleteTab = deleteTab;
        setSpacing(10);
        setPadding(new Insets(15));
    }

    public void refreshPagination() {
        int pageCount = (int) Math.ceil((double) snippetQueryBuilder.select().tab.id.eq(tab.getId()).findCount() / 5);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(currentPage);

        var parent = getChildren();
        parent.clear();
        if (currentPage == 0) {
            deleteTab(parent);
            creationSnippetForm(parent);
        }

        search(currentPage).forEach(snippet -> loadSnippet(parent, snippet));
    }

    public void nextPage(Number page) {
        currentPage = (int) page;
        refreshPagination();
    }

    private void deleteTab(ObservableList<Node> parent) {
        Button deleteTab = new Button("Delete Tab", new FontIcon(Feather.ALERT_TRIANGLE));
        deleteTab.getStyleClass().add(Styles.DANGER);

        deleteTab.setOnAction(_ -> {
            var title = "Delete tab";
            var message = "Do you want to delete the current tab?";
            var shouldDelete = ConfirmationDialogComponent.createConfirmationDialog(getScene().getWindow(), title, message);

            if (shouldDelete)
                this.deleteTab.accept(tab.getName());
        });

        parent.add(deleteTab);
    }

    private void creationSnippetForm(ObservableList<Node> parent) {
        Label description = new Label("Description:");
        TextArea descField = FieldComponent.createEditableField();

        Label valueLabel = new Label("Snippet:");
        TextArea snippetArea = FieldComponent.createEditableField();

        Button saveButton = IconButtonComponent.createIconButton(Feather.SAVE, Styles.SUCCESS);
        saveButton.setOnAction(_ -> {

            if (descField.getText().isBlank() || snippetArea.getText().isBlank()) {
                NotificationComponent.createNotification(stackPaneChildren, Feather.ALERT_TRIANGLE , "Description/Snippet can't be empty", Styles.DANGER);
                return;
            }

            Snippet snippet = new Snippet();
            snippet.setTab(tab);
            String name = descField.getText();
            snippet.setName(encrypted ? CryptoUtil.encrypt(name, password) : name);
            String value = snippetArea.getText();
            snippet.setValue(encrypted ? CryptoUtil.encrypt(value, password) : value);
            snippet.save();

            NotificationComponent.createNotification(stackPaneChildren, Feather.POCKET,"Snippet Saved", Styles.SUCCESS);
            descField.setText("");
            snippetArea.setText("");

            refreshPagination();
        });

        parent.addAll(description, descField, valueLabel, snippetArea, IconButtonBarComponent.createIconButtonBar(saveButton));
    }

    private void loadSnippet(ObservableList<Node> parent, Snippet snippet) {
        String name = encrypted ? CryptoUtil.decrypt(snippet.getName(), password) :snippet.getName();
        String value = encrypted ? CryptoUtil.decrypt(snippet.getValue(), password) : snippet.getValue();

        Label description = new Label("Description:");
        TextArea descField = FieldComponent.createField(name);

        Label valueLabel = new Label("Snippet:");
        TextArea snippetArea = FieldComponent.createField(value);

        Button editButton = IconButtonComponent.createIconButton(Feather.EDIT, Styles.ACCENT);

        Button deleteButton = IconButtonComponent.createIconButton(Feather.TRASH, Styles.DANGER);

        Button copyButton = IconButtonComponent.createIconButton(Feather.COPY, null);


        editButton.setOnAction(_ -> {
            FontIcon icon = (FontIcon) editButton.getGraphic();
            if(icon.getIconCode() == Feather.EDIT) {
                editButton.setGraphic(new FontIcon(Feather.POCKET));
                descField.setEditable(true);
                snippetArea.setEditable(true);
                FieldComponent.addFieldListener(descField);
                FieldComponent.addFieldListener(snippetArea);
            }

            if (icon.getIconCode() == Feather.POCKET) {
                var editedName = descField.getText();
                var editedValue = snippetArea.getText();
                if (!editedName.isBlank() && !editedValue.isBlank()) {
                    snippet.setName(encrypted ? CryptoUtil.encrypt(editedName, password) : editedName);
                    snippet.setValue(encrypted ? CryptoUtil.encrypt(editedValue, password) : editedValue);
                    snippet.save();
                    NotificationComponent.createNotification(stackPaneChildren, Feather.POCKET, "Snippet Edited", Styles.SUCCESS);
                    FieldComponent.removeFieldListener(descField);
                    FieldComponent.removeFieldListener(snippetArea);
                    descField.setEditable(false);
                    snippetArea.setEditable(false);
                    editButton.setGraphic(new FontIcon(Feather.EDIT));
                } else  {
                    NotificationComponent.createNotification(stackPaneChildren, Feather.ALERT_TRIANGLE, "Description/Snippet can't be empty", Styles.DANGER);
                }
            }
        });

        deleteButton.setOnAction(_ -> {
            var title = "Delete Snippet";
            var message = "Do you want to delete the snippet of description: " + name + " ?";
            var shouldDelete = ConfirmationDialogComponent.createConfirmationDialog(getScene().getWindow(), title, message);

            if (shouldDelete) {
                snippet.delete();
                refreshPagination();
                NotificationComponent.createNotification(stackPaneChildren, Feather.POCKET,"Snippet Deleted", Styles.SUCCESS);
            }
        });
        copyButton.setOnAction(_ -> copyToClipboard(value));

        var iconButtonBar = IconButtonBarComponent.createIconButtonBar(editButton, deleteButton, copyButton);

        parent.addAll(description, descField, valueLabel, snippetArea, iconButtonBar);
    }

    private void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);

        NotificationComponent.createNotification(stackPaneChildren, Feather.POCKET, "Copied", Styles.SUCCESS);
    }

    private List<Snippet> search(int page) {
        return snippetQueryBuilder.select().tab.id.eq(tab.getId()).setFirstRow(page * 5).setMaxRows(5).orderBy().createdAt.desc().findList();
    }
}
