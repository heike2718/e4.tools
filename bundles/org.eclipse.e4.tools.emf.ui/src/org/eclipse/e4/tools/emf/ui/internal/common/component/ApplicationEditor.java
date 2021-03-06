/*******************************************************************************
 * Copyright (c) 2010 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.tools.emf.ui.internal.common.component;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.tools.emf.ui.common.component.AbstractComponentEditor;
import org.eclipse.e4.tools.emf.ui.internal.Messages;
import org.eclipse.e4.tools.emf.ui.internal.common.ModelEditor;
import org.eclipse.e4.tools.emf.ui.internal.common.VirtualEntry;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MBindingContext;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl;
import org.eclipse.e4.ui.model.application.descriptor.basic.impl.BasicPackageImpl;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ApplicationEditor extends AbstractComponentEditor {

	private Composite composite;
	private Image image;
	private Image tbrImage;
	private EMFDataBindingContext context;
	private Button createRemoveRootContext;

	private IListProperty HANDLER_CONTAINER__HANDLERS = EMFProperties.list(CommandsPackageImpl.Literals.HANDLER_CONTAINER__HANDLERS);
	private IListProperty BINDING_CONTAINER__BINDINGS = EMFProperties.list(CommandsPackageImpl.Literals.BINDING_TABLE_CONTAINER__BINDING_TABLES);
	private IListProperty APPLICATION__COMMANDS = EMFProperties.list(ApplicationPackageImpl.Literals.APPLICATION__COMMANDS);
	private IListProperty PART_DESCRIPTOR_CONTAINER__DESCRIPTORS = EMFProperties.list(BasicPackageImpl.Literals.PART_DESCRIPTOR_CONTAINER__DESCRIPTORS);
	private IListProperty ELEMENT_CONTAINER__CHILDREN = EMFProperties.list(UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN);
	private IListProperty APPLICATION__ADDONS = EMFProperties.list(ApplicationPackageImpl.Literals.APPLICATION__ADDONS);
	private IListProperty MENU_CONTRIBUTIONS = EMFProperties.list(MenuPackageImpl.Literals.MENU_CONTRIBUTIONS__MENU_CONTRIBUTIONS);
	private IListProperty TOOLBAR_CONTRIBUTIONS = EMFProperties.list(MenuPackageImpl.Literals.TOOL_BAR_CONTRIBUTIONS__TOOL_BAR_CONTRIBUTIONS);
	private IListProperty TRIM_CONTRIBUTIONS = EMFProperties.list(MenuPackageImpl.Literals.TRIM_CONTRIBUTIONS__TRIM_CONTRIBUTIONS);
	private IListProperty APPLICATION__CATEGORIES = EMFProperties.list(ApplicationPackageImpl.Literals.APPLICATION__CATEGORIES);

	private IValueProperty BINDING_TABLE_CONTAINER__ROOT_CONTEXT = EMFProperties.value(CommandsPackageImpl.Literals.BINDING_TABLE_CONTAINER__ROOT_CONTEXT);

	public ApplicationEditor(EditingDomain editingDomain, ModelEditor editor) {
		super(editingDomain, editor);
	}

	@Override
	public Image getImage(Object element, Display display) {
		if (element instanceof MUIElement) {
			if (((MUIElement) element).isToBeRendered()) {
				if (image == null) {
					try {
						image = loadSharedImage(display, new URL("platform:/plugin/org.eclipse.e4.tools.emf.ui/icons/full/modelelements/Application.png")); //$NON-NLS-1$
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return image;
			} else {
				if (tbrImage == null) {
					try {
						tbrImage = loadSharedImage(display, new URL("platform:/plugin/org.eclipse.e4.tools.emf.ui/icons/full/modelelements/tbr/Application.png")); //$NON-NLS-1$
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return tbrImage;
			}
		}
		return null;
	}

	@Override
	public String getLabel(Object element) {
		return Messages.ApplicationEditor_Label;
	}

	@Override
	public String getDescription(Object element) {
		return Messages.ApplicationEditor_Description;
	}

	@Override
	public Composite getEditor(Composite parent, Object object) {
		if (composite == null) {
			context = new EMFDataBindingContext();
			composite = createForm(parent, context);
		}
		getMaster().setValue(object);

		if (createRemoveRootContext != null) {
			createRemoveRootContext.setSelection(((MApplication) object).getRootContext() != null);
		}

		return composite;
	}

	protected Composite createForm(Composite parent, EMFDataBindingContext context) {
		parent = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		gl.horizontalSpacing = 10;
		parent.setLayout(gl);

		IWidgetValueProperty textProp = WidgetProperties.text(SWT.Modify);

		if (getEditor().isShowXMIId() || getEditor().isLiveModel()) {
			ControlFactory.createXMIId(parent, this);
		}

		{
			Label l = new Label(parent, SWT.NONE);
			l.setText(Messages.ModelTooling_Common_Id);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

			Text t = new Text(parent, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			t.setLayoutData(gd);
			context.bindValue(textProp.observeDelayed(200, t), EMFEditProperties.value(getEditingDomain(), ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__ELEMENT_ID).observeDetail(getMaster()));
		}

		{
			Label l = new Label(parent, SWT.NONE);
			l.setText(Messages.ApplicationEditor_RootContext);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

			createRemoveRootContext = new Button(parent, SWT.CHECK);
			createRemoveRootContext.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MApplication app = (MApplication) getMaster().getValue();
					if (app.getRootContext() == null) {
						addContext();
					} else {
						removeContext();
					}
				}
			});
			createRemoveRootContext.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		}

		ControlFactory.createCheckBox(parent, Messages.ModelTooling_UIElement_ToBeRendered, getMaster(), context, WidgetProperties.selection(), EMFEditProperties.value(getEditingDomain(), UiPackageImpl.Literals.UI_ELEMENT__TO_BE_RENDERED));
		ControlFactory.createCheckBox(parent, Messages.ModelTooling_UIElement_Visible, getMaster(), context, WidgetProperties.selection(), EMFEditProperties.value(getEditingDomain(), UiPackageImpl.Literals.UI_ELEMENT__VISIBLE));

		ControlFactory.createStringListWidget(parent, this, Messages.ModelTooling_Context_Variables, UiPackageImpl.Literals.CONTEXT__VARIABLES, VERTICAL_LIST_WIDGET_INDENT);
		ControlFactory.createStringListWidget(parent, this, Messages.ApplicationEditor_BindingContexts, CommandsPackageImpl.Literals.BINDINGS__BINDING_CONTEXTS, VERTICAL_LIST_WIDGET_INDENT);

		return parent;
	}

	void removeContext() {
		Command cmd = SetCommand.create(getEditingDomain(), getMaster().getValue(), CommandsPackageImpl.Literals.BINDING_TABLE_CONTAINER__ROOT_CONTEXT, null);
		if (cmd.canExecute()) {
			getEditingDomain().getCommandStack().execute(cmd);
		}
	}

	void addContext() {
		MBindingContext context = MCommandsFactory.INSTANCE.createBindingContext();
		Command cmd = SetCommand.create(getEditingDomain(), getMaster().getValue(), CommandsPackageImpl.Literals.BINDING_TABLE_CONTAINER__ROOT_CONTEXT, context);
		if (cmd.canExecute()) {
			getEditingDomain().getCommandStack().execute(cmd);
		}
	}

	@Override
	public IObservableList getChildList(final Object element) {
		final WritableList list = new WritableList();
		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_ADDONS, APPLICATION__ADDONS, element, Messages.ApplicationEditor_Addons) {

			@Override
			protected boolean accepted(Object o) {
				return true;
			}

		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_HANDLER, HANDLER_CONTAINER__HANDLERS, element, Messages.ApplicationEditor_Handlers) {

			@Override
			protected boolean accepted(Object o) {
				return true;
			}

		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_PART_DESCRIPTORS, PART_DESCRIPTOR_CONTAINER__DESCRIPTORS, element, Messages.ApplicationEditor_PartDescriptors) {

			@Override
			protected boolean accepted(Object o) {
				return true;
			}

		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_BINDING_TABLE, BINDING_CONTAINER__BINDINGS, element, Messages.ApplicationEditor_BindingTables) {

			@Override
			protected boolean accepted(Object o) {
				return true;
			}

		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_COMMAND, APPLICATION__COMMANDS, element, Messages.ApplicationEditor_Commands) {

			@Override
			protected boolean accepted(Object o) {
				return true;
			}

		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_CATEGORIES, APPLICATION__CATEGORIES, element, Messages.ApplicationEditor_Categories) {

			@Override
			protected boolean accepted(Object o) {
				return true;
			}

		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_WINDOWS, ELEMENT_CONTAINER__CHILDREN, element, Messages.ApplicationEditor_Windows) {

			@Override
			protected boolean accepted(Object o) {
				return true;
			}

		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_MENU_CONTRIBUTIONS, MENU_CONTRIBUTIONS, element, Messages.ApplicationEditor_MenuContributions) {
			@Override
			protected boolean accepted(Object o) {
				return true;
			}
		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_TOOLBAR_CONTRIBUTIONS, TOOLBAR_CONTRIBUTIONS, element, Messages.ApplicationEditor_ToolBarContributions) {
			@Override
			protected boolean accepted(Object o) {
				return true;
			}
		});

		list.add(new VirtualEntry<Object>(ModelEditor.VIRTUAL_TRIM_CONTRIBUTIONS, TRIM_CONTRIBUTIONS, element, Messages.ApplicationEditor_TrimContributions) {
			@Override
			protected boolean accepted(Object o) {
				return true;
			}
		});

		MApplication application = (MApplication) element;
		if (application.getRootContext() != null) {
			list.add(0, application.getRootContext());
		}

		BINDING_TABLE_CONTAINER__ROOT_CONTEXT.observe(element).addValueChangeListener(new IValueChangeListener() {

			public void handleValueChange(ValueChangeEvent event) {
				if (event.diff.getOldValue() != null) {
					list.remove(event.diff.getOldValue());
					if (getMaster().getValue() == element) {
						createRemoveRootContext.setSelection(false);
					}
				}

				if (event.diff.getNewValue() != null) {
					list.add(0, event.diff.getNewValue());
					if (getMaster().getValue() == element) {
						createRemoveRootContext.setSelection(true);
					}
				}
			}
		});

		return list;
	}

	@Override
	public String getDetailLabel(Object element) {
		return null;
	}

	@Override
	public FeaturePath[] getLabelProperties() {
		return new FeaturePath[] { FeaturePath.fromList(UiPackageImpl.Literals.UI_ELEMENT__TO_BE_RENDERED) };
	}
}