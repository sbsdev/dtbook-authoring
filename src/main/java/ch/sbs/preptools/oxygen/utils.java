package ch.sbs.preptools.oxygen;

import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

public abstract class utils {
	
	public static abstract class xml {
		
		public static final String XMLNS_DTB = "http://www.daisy.org/z3986/2005/dtbook/";
		
		public static String element(String name, String namespace) {
			return "<" + name + " xmlns='" + namespace + "'></" + name + ">";
		}
		
		public static String withAttribute(String element, String attr, String value) {
			return element.replaceFirst(">", " " + attr + "='" + value + "'>");
		}
		
		public static String withContent(String element, String content) {
			return element.replaceFirst("><", ">" + content + "<");
		}
		
		public static String dtbElement(String name) {
			return element(name, XMLNS_DTB);
		}
		
		public static boolean isDtbElement(AuthorElement element) {
			return XMLNS_DTB.equals(element.getNamespace());
		}
		
		public static boolean isDtbElement(String name, AuthorElement element) {
			return isDtbElement(element) && name.equals(element.getName());
		}
		
		public static boolean matchesDtbElement(Pattern regex, AuthorElement element) {
			return isDtbElement(element) && regex.matcher(element.getName()).matches();
		}
		
		public static boolean isWhitespaceOnlyNode(AuthorNode node) throws BadLocationException {
			return node.getTextContent().replaceAll("\\s", "").equals("");
		}
		
		public static AuthorElement getElementAtOffset(int offset, AuthorDocumentController controller)
				throws BadLocationException {
			AuthorNode node = controller.getNodeAtOffset(offset);
			while (node != null) {
				if (node instanceof AuthorElement)
					return (AuthorElement)node;
				node = node.getParent(); }
			return null;
		}
		
		public static AuthorElement getFirstChildElement(AuthorElement element, AuthorDocumentController controller) {
			for (AuthorNode node : element.getContentNodes())
				if (node instanceof AuthorElement)
					return (AuthorElement)node;
			return null;
		}
		
		public static AuthorElement splitAtOffset(AuthorElement element, int offset, AuthorDocumentController controller)
				throws AuthorOperationException, BadLocationException {
			String fragment = controller.serializeFragmentToXML(controller.createDocumentFragment(
					offset, element.getEndOffset() - 1));
			controller.delete(offset, element.getEndOffset() - 1);
			controller.insertXMLFragment(
					withContent(element(element.getLocalName(), element.getNamespace()), fragment),
					element.getEndOffset() + 1);
			return getElementAtOffset(element.getEndOffset() + 2, controller);
		}
		
		public static void unwrap(AuthorElement element, AuthorDocumentController controller)
				throws BadLocationException {
			controller.insertFragment(
					element.getEndOffset() + 1,
					controller.createDocumentFragment(element.getStartOffset() + 1, element.getEndOffset() - 1));
			controller.deleteNode(element);
		}
	}
	
	public static abstract class xpath {
		
		public static AuthorNode[] getNodeset(String xpath, AuthorNode context, AuthorDocumentController controller)
				throws AuthorOperationException {
			return controller.findNodesByXPath(xpath, context, false, true, true, false);
		}
		
		public static AuthorNode getNode(String xpath, AuthorNode context, AuthorDocumentController controller)
				throws AuthorOperationException {
			AuthorNode[] nodeset = getNodeset(xpath, context, controller);
			if (nodeset.length == 0)
				return null;
			return nodeset[0];
		}
		
		public static AuthorElement getElement(String xpath, AuthorNode context, AuthorDocumentController controller)
				throws AuthorOperationException {
			AuthorNode node = getNode(xpath, context, controller);
			if (node == null)
				return null;
			return (AuthorElement)node;
		}
		
		public static boolean isTrue(String xpath, AuthorNode context, AuthorDocumentController controller)
				throws AuthorOperationException {
			Object[] result = controller.evaluateXPath(xpath, context, false, true, true, false);
			if (result.length == 0)
				return false;
			if (result[0] instanceof Boolean)
				return (Boolean)result[0];
			return true;
		}
	}
	
	public static abstract class editor {
		
		public static int getSelectionStart(AuthorEditorAccess editor) {
			int start = editor.getSelectionStart();
			int end = editor.getSelectionEnd();
			return (start < end) ? start : end;
		}
		
		public static int getSelectionEnd(AuthorEditorAccess editor) {
			int start = editor.getSelectionStart();
			int end = editor.getSelectionEnd();
			return (start < end) ? end : start;
		}
	}
	
	public static abstract class gui {
		
		public static boolean showOkCancelMessage(String title, String message, AuthorWorkspaceAccess access) {
			return access.showConfirmDialog(
					title, 
					message, 
					new String[] {"OK", "Cancel"}, 
					new int[] {0, 1}) == 0;
		}
		
		public static int showYesNoCancelMessage(String title, String message, int defaultButton, AuthorWorkspaceAccess access) {
			int index = 0;
			if (defaultButton == 0) index = 1;
			if (defaultButton == -1) index = 2;
			return access.showConfirmDialog(
					title, 
					message, 
					new String[] {"Yes", "No", "Cancel"}, 
					new int[] {1, 0, -1}, 
					index);
		}
		
		public static void showMessage(String title, String message, AuthorWorkspaceAccess access) {
			access.showConfirmDialog(
					title,
					message,
					new String[] {"OK"},
					new int[] {0});
		}
	}
}
