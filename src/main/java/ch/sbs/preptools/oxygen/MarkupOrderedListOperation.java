package ch.sbs.preptools.oxygen;

public class MarkupOrderedListOperation extends MarkupListOperation {
	
	public String getDescription() {
		return "Markup current elements as an ordered list";
	}
	
	protected void doOperation() throws Exception {
		markupList("ol");
	}
}
