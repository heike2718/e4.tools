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
package org.eclipse.e4.tools.compat.parts;

import org.eclipse.e4.tools.services.IDirtyProviderService;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

public abstract class DISaveableViewPart<C> extends DIViewPart<C> implements ISaveablePart2, IDirtyProviderService {
	private boolean dirtyState;
	
	public DISaveableViewPart(Class<C> clazz) {
		super(clazz);
	}
	
//	public void doSave(IProgressMonitor monitor) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void doSaveAs() {
//		// TODO Auto-generated method stub
//		
//	}

	public void setDirtyState(boolean dirtyState) {
		if( dirtyState != this.dirtyState ) {
			this.dirtyState = dirtyState;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	public boolean isDirty() {
		return dirtyState;
	}

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		
		getContext().declareModifiable(IDirtyProviderService.class);
		getContext().set(IDirtyProviderService.class, this);
	}
	
//	public boolean isSaveAsAllowed() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean isSaveOnCloseNeeded() {
//		// TODO Auto-generated method stub
//		return false;
//	}	
}
