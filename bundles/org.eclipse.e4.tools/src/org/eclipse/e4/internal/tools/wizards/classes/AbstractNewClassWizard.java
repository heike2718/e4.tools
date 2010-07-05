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
package org.eclipse.e4.internal.tools.wizards.classes;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.internal.tools.wizards.classes.AbstractNewClassPage.JavaClass;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public abstract class AbstractNewClassWizard extends Wizard implements INewWizard {
	protected IPackageFragmentRoot root;
	protected IFile file;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		root = getFragmentRoot(getInitialJavaElement(selection));
		System.err.println("Root: " + root);
	}

	protected IJavaElement getInitialJavaElement(IStructuredSelection selection) {
		IJavaElement jelem = null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;

				jelem = (IJavaElement) adaptable.getAdapter(IJavaElement.class);
				if (jelem == null || !jelem.exists()) {
					jelem = null;
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null
							&& resource.getType() != IResource.ROOT) {
						while (jelem == null
								&& resource.getType() != IResource.PROJECT) {
							resource = resource.getParent();
							jelem = (IJavaElement) resource
									.getAdapter(IJavaElement.class);
						}
						if (jelem == null) {
							jelem = JavaCore.create(resource); // java project
						}
					}
				}
			}
		}

		return jelem;
	}

	protected IPackageFragmentRoot getFragmentRoot(IJavaElement elem) {
		IPackageFragmentRoot initRoot = null;
		if (elem != null) {
			initRoot = (IPackageFragmentRoot) elem
					.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			try {
				if (initRoot == null
						|| initRoot.getKind() != IPackageFragmentRoot.K_SOURCE) {
					IJavaProject jproject = elem.getJavaProject();
					if (jproject != null) {
						initRoot = null;
						if (jproject.exists()) {
							IPackageFragmentRoot[] roots = jproject
									.getPackageFragmentRoots();
							for (int i = 0; i < roots.length; i++) {
								if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
									initRoot = roots[i];
									break;
								}
							}
						}
						if (initRoot == null) {
							initRoot = jproject.getPackageFragmentRoot(jproject
									.getResource());
						}
					}
				}
			} catch (JavaModelException e) {
				// TODO
				e.printStackTrace();
			}
		}
		return initRoot;
	}

	protected abstract String getContent();
	
	public JavaClass getDomainClass() {
		return ((AbstractNewClassPage) getPages()[0]).getClazz();
	}
	
	@Override
	public boolean performFinish() {
		JavaClass clazz = getDomainClass();
		String content = getContent();
		
		IPackageFragment fragment = clazz.getPackageFragment();
		if (fragment != null) {
			String cuName = clazz.getName() + ".java";
			IResource resource = fragment.getCompilationUnit(cuName)
					.getResource();
			file = (IFile) resource;
			try {
				if (!file.exists()) {
					file.create(new ByteArrayInputStream(content.getBytes()),
							true, null);
				} else {
					file.setContents(new ByteArrayInputStream(content.getBytes()),
							IFile.FORCE | IFile.KEEP_HISTORY, null);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;

	}
	
	public IFile getFile() {
		return file;
	}
}
