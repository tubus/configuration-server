package com.github.tubus.ui.view.wiki;

import com.github.tubus.ui.view.main.MainView;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinService;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.vaadin.maxime.StringUtil;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route(value = "wiki", layout = MainView.class)
@CssImport(value = "./styles/views/wiki/wiki-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class WikiView extends Div implements AfterNavigationObserver, HasDynamicTitle, LocaleChangeObserver {

    private final Grid<String> grid = new Grid<>();
    Parser parser = Parser.builder().build();
    HtmlRenderer renderer = HtmlRenderer.builder().build();

    public WikiView() {
        setId("wiki-view");
        addClassName("wiki-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(this::createDiv);
        add(grid);
        grid.setItems(loadData());
    }

    List<String> loadData() {
        List<String> result = new ArrayList<>();
        URL docs = VaadinService.getCurrent().getResource("docs/" + getLocale().getLanguage());
        try {
            List<Path> mdFiles = Files.walk(new File(docs.getFile()).toPath())
                    .filter(file -> file.toAbsolutePath().toString().endsWith(".md"))
                    .collect(Collectors.toList());
            mdFiles.forEach(file -> {
                try {
                    byte[] encoded = Files.readAllBytes(file);
                    result.add(new String(encoded, StandardCharsets.UTF_8));
                } catch (Exception ignored) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String parseMarkdown(String value) {
        Node text = parser.parse(value);
        return renderer.render(text);
    }

    private Div createDiv(String value) {
        Div previewView = new Div();
        String html = String.format("<div>%s</div>",
                parseMarkdown(StringUtil.getNullSafeString(value)));
        Html item = new Html(html);
        previewView.add(item);
        return previewView;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        UI.getCurrent().getPage().setTitle(getPageTitle());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        UI.getCurrent().getPage().setTitle(getPageTitle());
        grid.setItems(loadData());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("main.logo.title.wiki");
    }
}
