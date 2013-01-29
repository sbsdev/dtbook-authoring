package ch.sbs.preptools.oxygen;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;

public abstract class NoArgAuthorOperation implements AuthorOperation {
	
	protected AuthorDocumentController controller;
	protected AuthorWorkspaceAccess workspace;
	protected AuthorEditorAccess editor;
	
	protected abstract void doOperation() throws Exception; 
	
	public void doOperation(AuthorAccess access, ArgumentsMap arguments)
			throws IllegalArgumentException, AuthorOperationException {
		
		try {
			controller = access.getDocumentController();
			workspace = access.getWorkspaceAccess();
			editor = access.getEditorAccess();
			doOperation(); }
		catch (AuthorOperationException e) {
			throw e; }
		catch (Exception e) {
			throw new AuthorOperationException(e.getMessage()); }
	}
	
	public ArgumentDescriptor[] getArguments() { return new ArgumentDescriptor[0]; }
	
}
