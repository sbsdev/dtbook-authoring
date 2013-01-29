package ch.sbs.preptools.oxygen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;

import static ch.sbs.preptools.oxygen.utils.editor.getSelectionStart;
import static ch.sbs.preptools.oxygen.utils.xml.isDtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.getElementAtOffset;
import static ch.sbs.preptools.oxygen.utils.xml.matchesDtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.splitAtOffset;
import ch.sbs.preptools.oxygen.utils.xpath;

public class MarkupHeadingOperation extends NoArgAuthorOperation {
	
	public String getDescription() {
		return "Markup current element as a level heading";
	}
	
	private static final Pattern LEVEL_PATTERN = Pattern.compile("^level([1-6])$");
	private static final Pattern HEADING_PATTERN = Pattern.compile("^h([1-6])$");
	
	protected void doOperation() throws Exception {
		AuthorElement element = getElementAtOffset(getSelectionStart(editor), controller);
		if (matchesDtbElement(HEADING_PATTERN, element))
			throw new AuthorOperationException("Current element is already a heading.");
		if (matchesDtbElement(LEVEL_PATTERN, element))
			throw new AuthorOperationException("Current element is a level container can therefore not become a level heading.");
		AuthorElement parent = (AuthorElement)element.getParent();
		Matcher matcher = LEVEL_PATTERN.matcher(parent.getLocalName());
		if (!isDtbElement(parent) || !matcher.matches())
			throw new AuthorOperationException("The current element is not the child of a level container element.");
		controller.renameElement(element, "h" + matcher.group(1));
		if (xpath.isTrue("preceding-sibling::node()[not(normalize-space(string(.))='')]", element, controller))
			splitAtOffset(parent, element.getStartOffset(), controller);
	}
}
