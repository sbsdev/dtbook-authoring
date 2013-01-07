package nota.oxygen.common.dtbook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

import nota.oxygen.common.BaseAuthorOperation;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.ecss.extensions.api.node.AuthorElement;

public class FindURLsOperation extends BaseAuthorOperation {
	
	private static final String URL_SEARCH_REGEX =
		"(?i)(" +
		"(?:https?://)?(?:(?:[A-Z0-9](?:[A-Z0-9-/]{0,61}[A-Z0-9])?\\.)+[A-Z]{2,6}" + 
		"|https?://localhost" + 
		"|https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::\\d+)?/?(?:[/?][^\\s<]+)?)";
	
	private static final Pattern URL_PATTERN = Pattern.compile(URL_SEARCH_REGEX);
	
	public String getDescription() {
		return "Find and markup URLs as dtb:a";
	}
	
	protected void doOperation() throws AuthorOperationException {
		AuthorDocumentController controller = getAuthorAccess().getDocumentController();
		TextContentIterator iterator = controller.getTextContentIterator(0, controller.getAuthorDocumentNode().getEndOffset());
		while (iterator.hasNext()) {
			TextContext context = iterator.next();
			Matcher matcher = URL_PATTERN.matcher(context.getText().toString());
			while (matcher.find()) {
				String url = matcher.group();
				int start = context.getTextStartOffset() + matcher.start();
				int end = context.getTextStartOffset() + matcher.end();
				try {
					AuthorElement element = getElementAtOffset(start);
					if (isDtbElement("a", element) && url.equals(element.getTextContent()))
						continue; }
				catch (BadLocationException e) {}
				getAuthorAccess().getEditorAccess().select(start, end);
				if (showYesNoCancelMessage("Possible URL found", "Markup '" + url + "' as a dtb:a ?", 1) == 1)
					controller.surroundInFragment(withAttribute(dtbElement("a"), "href", url), start, end - 1); }}
	}
	
	private static final String XMLNS_DTB = "http://www.daisy.org/z3986/2005/dtbook/";
	
	private static String dtbElement(String name) {
		return "<" + name + " xmlns='" + XMLNS_DTB + "'></" + name + ">";
	}
	
	private static String withAttribute(String element, String attr, String value) {
		return element.replaceFirst(">", " " + attr + "='" + value + "'>");
	}
	
	private static boolean isDtbElement(String name, AuthorElement element) {
		return name.equals(element.getName()) && XMLNS_DTB.equals(element.getNamespace());
	}
	
	public ArgumentDescriptor[] getArguments() {
		return new ArgumentDescriptor[0];
	}
	
	protected void parseArguments(ArgumentsMap args) throws IllegalArgumentException {}
}
