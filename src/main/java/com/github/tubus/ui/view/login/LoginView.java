package com.github.tubus.ui.view.login;

import ch.carnet.kasparscherrer.LanguageSelect;
import com.github.tubus.ui.data.repo.UserAccountRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.github.tubus.ui.service.security.AccountServiceImpl;
import com.github.tubus.ui.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.atomic.AtomicBoolean;
import static com.github.tubus.ui.util.FieldValuesValidator.validateName;
import static com.github.tubus.ui.util.FieldValuesValidator.validatePassword;
import static com.github.tubus.ui.util.NotificationUtils.tryWithNotification;
import static com.github.tubus.ui.util.constant.CssStyleId.*;
import static com.github.tubus.ui.util.provider.VaadinFieldsProvider.*;

@Route("login")
@CssImport("./styles/views/main/login.css")
@SpringComponent @UIScope
public class LoginView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver,
		HasDynamicTitle, LocaleChangeObserver {

	private final H1 header = new H1();
	private final LoginForm login = new LoginForm();

	private boolean superUserCreationMode = false;

	private TextField superUserName;
	private PasswordField superUserPassword;

	public LoginView(final UserAccountRepository userAccountRepository,
					 final AccountServiceImpl accountService,
					 final @Value("${logo.url}") String logoUrl,
					 final I18NProvider i18NProvider) {
		if (SecurityUtils.isUserLoggedIn()) {
			UI.getCurrent().navigate("configserver/components");
		}

		setUpView();

		LanguageSelect languageSelect = provideLanguageCombobox(i18NProvider);
		Image logoImage = new Image(logoUrl, "");
		logoImage.setId(LOGO_IMAGE);

		if (!userAccountRepository.superAdminExists()) {
			superUserCreationMode = true;
			createSuperUserForm(accountService, i18NProvider, languageSelect, logoImage);
		} else {
			setUpLoginForm();
			add(logoImage, login, languageSelect);
		}
	}

	private void setUpLoginForm() {
		login.setId(LOGIN_FORM);
		login.setAction("login");
		login.setI18n(createLoginI18n());
		login.addForgotPasswordListener(e -> Notification.show(getTranslation("notification.superuser.delegated"), 1000, Notification.Position.MIDDLE));
	}

	private void setUpView() {
		addClassName(LOGIN);
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.START);
	}

	private void createSuperUserForm(AccountServiceImpl accountService, I18NProvider i18NProvider, LanguageSelect languageSelect, Image logoImage) {
		setJustifyContentMode(JustifyContentMode.CENTER);
		Button ok = new Button("OK");
		superUserName = provideNameInputField(i18NProvider, UI.getCurrent().getLocale());
		superUserPassword = providePasswordField(i18NProvider, UI.getCurrent().getLocale());
		ok.addClickListener(event -> superUserCreateAction(accountService, superUserName, superUserPassword));
		header.setText(getTranslation("login.admin.create"));
		HorizontalLayout superUserCreationForm = superUserCreationForm(superUserName, superUserPassword);
		add(	header,
				logoImage,
				superUserCreationForm, ok, languageSelect);
	}

	private void superUserCreateAction(AccountServiceImpl accountService, TextField name, PasswordField password) {
		AtomicBoolean errorsDetected = new AtomicBoolean(false);
		validateName(name, errorsDetected);
		validatePassword(password, errorsDetected);
		if (errorsDetected.get()) {
			return;
		}
		tryWithNotification(() -> {
			accountService.createSuperUser(name.getValue(), password.getValue());
			superUserCreationMode = false;
			UI.getCurrent().getPage().reload();
		});
	}

	private HorizontalLayout superUserCreationForm(TextField name, PasswordField password) {
		HorizontalLayout formLayout = new HorizontalLayout();
		formLayout.add(name);
		formLayout.add(password);
		formLayout.setAlignItems(Alignment.BASELINE);
		formLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		return formLayout;
	}

	private LoginI18n createLoginI18n() {
		LoginI18n i18n = LoginI18n.createDefault();

		LoginI18n.Header header = new LoginI18n.Header();
		i18n.setHeader(header);
		i18n.setForm(new LoginI18n.Form());
		i18n.setErrorMessage(new LoginI18n.ErrorMessage());

		i18n.getHeader().setTitle(getTranslation("login.header.title"));
		i18n.getHeader().setDescription(getTranslation("login.header.description"));
		i18n.getForm().setUsername(getTranslation("login.form.username"));
		i18n.getForm().setTitle(getTranslation("login.form.title"));
		i18n.getForm().setSubmit(getTranslation("login.form.submit"));
		i18n.getForm().setPassword(getTranslation("login.form.password"));
		i18n.getForm().setForgotPassword(getTranslation("login.form.password.forgot"));
		i18n.getErrorMessage().setTitle(getTranslation("login.error.title"));
		i18n.getErrorMessage().setMessage(getTranslation("login.error.message"));
		return i18n;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(beforeEnterEvent.getLocation()
				.getQueryParameters()
				.getParameters()
				.containsKey("error")) {
			login.setError(true);
		}
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		if (SecurityUtils.isUserLoggedIn()) {
			UI.getCurrent().navigate("configserver/components");
		}
	}

	@Override
	public String getPageTitle() {
		return superUserCreationMode ? getTranslation("login.admin.title") : getTranslation("login.page.title");
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		if (superUserCreationMode) {
			header.setText(getTranslation("login.admin.create"));
			superUserName.setTitle(getTranslation("input.name.title"));
			superUserName.setPlaceholder(getTranslation("input.name.placeholder"));
			superUserPassword.setTitle(getTranslation("input.password.title"));
			superUserPassword.setPlaceholder(getTranslation("input.password.placeholder"));
			UI.getCurrent().getPage().setTitle(getTranslation("login.admin.title"));
		} else {
			UI.getCurrent().getPage().setTitle(getTranslation("login.page.title"));
			login.setI18n(createLoginI18n());
		}
	}
}