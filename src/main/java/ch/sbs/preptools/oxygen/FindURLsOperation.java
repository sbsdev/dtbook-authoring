package ch.sbs.preptools.oxygen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.ecss.extensions.api.node.AuthorElement;

import static ch.sbs.preptools.oxygen.utils.gui.showYesNoCancelMessage;
import static ch.sbs.preptools.oxygen.utils.xml.dtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.getElementAtOffset;
import static ch.sbs.preptools.oxygen.utils.xml.isDtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.withAttribute;

public class FindURLsOperation extends NoArgAuthorOperation {
	
	public String getDescription() {
		return "Find and markup URLs as dtb:a";
	}
	
	private static final String URL_SEARCH_REGEX =
		"(?i)(" +
		"(?:https?://)?(?:(?:[A-Z0-9](?:[A-Z0-9-/]{0,61}[A-Z0-9])?\\.)+[A-Z]{2,6}" + 
		"|https?://localhost" + 
		"|https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::\\d+)?/?(?:[/?][^\\s<]+)?)";
	
	private static final Pattern URL_PATTERN = Pattern.compile(URL_SEARCH_REGEX);
	
	protected void doOperation() throws Exception {
		TextContentIterator iterator = controller.getTextContentIterator(0, controller.getAuthorDocumentNode().getEndOffset());
		while (iterator.hasNext()) {
			TextContext context = iterator.next();
			Matcher matcher = URL_PATTERN.matcher(context.getText().toString());
			while (matcher.find()) {
				String url = matcher.group();
				int start = context.getTextStartOffset() + matcher.start();
				int end = context.getTextStartOffset() + matcher.end();
				AuthorElement element = getElementAtOffset(start, controller);
				if (isDtbElement("a", element) && url.equals(element.getTextContent()))
					continue;
				editor.select(start, end);
				if (showYesNoCancelMessage("Possible URL found", "Markup '" + url + "' as a dtb:a ?", 1, workspace) == 1)
					controller.surroundInFragment(withAttribute(dtbElement("a"), "href", url), start, end - 1); }}
	}
}
