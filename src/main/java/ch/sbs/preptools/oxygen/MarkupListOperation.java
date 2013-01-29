package ch.sbs.preptools.oxygen;

import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

import static ch.sbs.preptools.oxygen.utils.editor.getSelectionEnd;
import static ch.sbs.preptools.oxygen.utils.editor.getSelectionStart;
import static ch.sbs.preptools.oxygen.utils.xml.dtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.getElementAtOffset;
import static ch.sbs.preptools.oxygen.utils.xml.isDtbElement;
import static ch.sbs.preptools.oxygen.utils.xml.isWhitespaceOnlyNode;
import static ch.sbs.preptools.oxygen.utils.xml.withAttribute;

public abstract class MarkupListOperation extends NoArgAuthorOperation {
	
	protected void markupList(String type) throws Exception {
		AuthorElement firstElement = (AuthorElement)controller.getNodeAtOffset(getSelectionStart(editor) + 1);
		AuthorElement lastElement = (AuthorElement)controller.getNodeAtOffset(getSelectionEnd(editor) - 1);
		if (firstElement.getParent() != lastElement.getParent())
			throw new AuthorOperationException("Selected nodes must not siblings");
		AuthorDocumentFragment fragment = controller.createDocumentFragment(
				firstElement.getStartOffset(),
				lastElement.getEndOffset());
		for (AuthorNode node : fragment.getContentNodes()) {
			if (!(node instanceof AuthorElement))
				if (!isWhitespaceOnlyNode(node))
					throw new AuthorOperationException("Cannot apply markup to mixed content.");
			if (!isDtbElement("p", (AuthorElement)node))
				throw new AuthorOperationException("Operation can only be applied to paragraphs."); }
		controller.surroundInFragment(
				withAttribute(dtbElement("list"), "type", type),
				firstElement.getStartOffset(),
				lastElement.getEndOffset());
		AuthorElement list = getElementAtOffset(firstElement.getStartOffset(), controller);
		for (AuthorNode node : list.getContentNodes())
			if (node instanceof AuthorElement)
				controller.renameElement((AuthorElement)node, "li");
	}
}