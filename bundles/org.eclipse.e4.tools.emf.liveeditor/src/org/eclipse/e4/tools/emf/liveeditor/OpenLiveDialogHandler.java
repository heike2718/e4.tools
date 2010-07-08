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
package org.eclipse.e4.tools.emf.liveeditor;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tools.emf.ui.common.IModelResource;
import org.eclipse.e4.tools.emf.ui.internal.wbm.ApplicationModelEditor;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class OpenLiveDialogHandler {
	private Shell shell;

	public OpenLiveDialogHandler() {
		System.err.println("Created Live Dialog Handler");
	}

	@Execute
	public void run(@Named(IServiceConstants.ACTIVE_SHELL) Shell s,
			IEclipseContext c, MApplication application, IStylingEngine engine) {
		if (this.shell == null || this.shell.isDisposed()) {
			try {
				this.shell = new Shell(s.getDisplay(),SWT.ON_TOP|SWT.SHELL_TRIM);
				//FIXME Style
				this.shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE)); 
				FillLayout layout = new FillLayout();
				layout.marginHeight=10;
				layout.marginWidth=10;
				this.shell.setLayout(layout);
				final IEclipseContext childContext = c
						.createChild("EditorContext");
				MemoryModelResource resource = new MemoryModelResource(application);
				childContext.set(IModelResource.class, resource);
				childContext.set(Composite.class.getName(), shell);
				childContext.set(IModelResource.class, resource);
				
				ContextInjectionFactory.make(ApplicationModelEditor.class, childContext);
				
//				new ApplicationModelEditor(shell, childContext, resource, null);
				shell.open();
				shell.addDisposeListener(new DisposeListener() {

					public void widgetDisposed(DisposeEvent e) {
						childContext.dispose();
						shell = null;
					}
				});				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
