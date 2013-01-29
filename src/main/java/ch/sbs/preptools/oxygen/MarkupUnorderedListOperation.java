package ch.sbs.preptools.oxygen;

public class MarkupUnorderedListOperation extends MarkupListOperation {
	
	public String getDescription() {
		return "Markup current elements as an unordered list";
	}
	
	protected void doOperation() throws Exception {
		markupList("ul");
	}
}
