package ch.sbs.preptools.oxygen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;

import static ch.sbs.preptools.oxygen.utils.editor.getSelectionStart;
import static ch.sbs.preptools.oxygen.utils.xml.getElementAtOffset;
import static ch.sbs.preptools.oxygen.utils.xml.getFirstChildElement;
import static ch.sbs.preptools.oxygen.utils.xml.isDtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.splitAtOffset;
import static ch.sbs.preptools.oxygen.utils.xml.unwrap;
import ch.sbs.preptools.oxygen.utils.xpath;

public class OutdentOperation extends NoArgAuthorOperation {
	
	public String getDescription() {
		return "Decrease indentation";
	}
	
	private static final Pattern HEADING_PATTERN = Pattern.compile("^h([1-6])$");
	
	protected void doOperation() throws Exception {
		AuthorElement element = getElementAtOffset(getSelectionStart(editor), controller);
		Matcher matcher = HEADING_PATTERN.matcher(element.getLocalName());
		if (!isDtbElement(element) || !matcher.matches())
			throw new AuthorOperationException("Operation only permitted on heading elements.");
		outdentHeading(element);
	}
	
	private void outdentHeading(AuthorElement heading) throws Exception {
		Matcher matcher = HEADING_PATTERN.matcher(heading.getLocalName());
		matcher.matches();
		int lvl = Integer.parseInt(matcher.group(1));
		if (lvl < 2)
			throw new AuthorOperationException("Operation not permitted on h1.");
		AuthorElement level = (AuthorElement)heading.getParent();
		assert(isDtbElement("level" + lvl, level));
		controller.renameElement(heading, "h" + (lvl - 1));
		if (xpath.isTrue("preceding-sibling::node()[not(normalize-space(string(.))='')]", heading, controller))
			level = splitAtOffset(level, heading.getStartOffset(), controller);
		AuthorElement nextSubLevel = xpath.getElement("following-sibling::level" + (lvl + 1), heading, controller);
		if (nextSubLevel != null)
			splitAtOffset(level, nextSubLevel.getStartOffset(), controller);
		if (xpath.isTrue("preceding-sibling::node()[not(normalize-space(string(.))='')]", level, controller)) {
			AuthorElement superLevel = (AuthorElement)level.getParent();
			superLevel = splitAtOffset(superLevel, level.getStartOffset(), controller);
			level = getFirstChildElement(superLevel, controller); }
		unwrap(level, controller);
		editor.setCaretPosition(editor.getCaretOffset() + 1);
	}
}
