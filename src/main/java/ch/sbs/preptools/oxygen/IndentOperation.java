package ch.sbs.preptools.oxygen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;

import static ch.sbs.preptools.oxygen.utils.editor.getSelectionStart;
import static ch.sbs.preptools.oxygen.utils.xml.dtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.getElementAtOffset;
import static ch.sbs.preptools.oxygen.utils.xml.isDtbElement;

import ch.sbs.preptools.oxygen.utils.xpath;

public class IndentOperation extends NoArgAuthorOperation {
	
	public String getDescription() {
		return "Increase indentation";
	}
	
	private static final Pattern HEADING_PATTERN = Pattern.compile("^h([1-6])?$");
	
	protected void doOperation() throws Exception {
		AuthorElement element = getElementAtOffset(getSelectionStart(editor), controller);
		Matcher matcher = HEADING_PATTERN.matcher(element.getLocalName());
		if (!isDtbElement(element) || !matcher.matches())
			throw new AuthorOperationException("Operation only permitted on heading elements.");
		indentHeading(element);
	}
	
	private void indentHeading(AuthorElement heading) throws Exception {
		Matcher matcher = HEADING_PATTERN.matcher(heading.getLocalName());
		matcher.matches();
		int lvl = Integer.parseInt(matcher.group(1));
		if (lvl == 6)
			throw new AuthorOperationException("Operation not permitted on h6.");
		AuthorElement level = (AuthorElement)heading.getParent();
		assert(isDtbElement("level" + lvl, level));
		controller.renameElement(heading, "h" + (lvl + 1));
		AuthorElement nextSubLevel = xpath.getElement("following-sibling::level" + (lvl + 1), heading, controller);
		controller.surroundInFragment(
				dtbElement("level" + (lvl + 1)),
				heading.getStartOffset(),
				(nextSubLevel != null) ? nextSubLevel.getStartOffset() - 1 : level.getEndOffset() - 1);
		AuthorElement previousLevel = xpath.getElement(
				"preceding-sibling::*[not(normalize-space(string(.))='')][1][self::level" + lvl + "]", level, controller);
		if (previousLevel != null) {
			int insertAt = previousLevel.getEndOffset();
			controller.insertFragment(insertAt,
					controller.createDocumentFragment(level.getStartOffset() + 1, level.getEndOffset() - 1));
			controller.deleteNode(level);
			level = getElementAtOffset(insertAt + 1, controller); }
		editor.setCaretPosition(xpath.getElement("descendant::h" + (lvl + 1), level, controller).getStartOffset() + 1);
	}
}
