package application.model;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class HyperlinkCell
		implements Callback<TableColumn<MessageTableViewCell, Hyperlink>, TableCell<MessageTableViewCell, Hyperlink>> {

	@Override
	public TableCell<MessageTableViewCell, Hyperlink> call(TableColumn<MessageTableViewCell, Hyperlink> arg) {
		TableCell<MessageTableViewCell, Hyperlink> cell = new TableCell<MessageTableViewCell, Hyperlink>() {
			@Override
			protected void updateItem(Hyperlink item, boolean empty) {
				setGraphic(item);
			}
		};
		return cell;
	}
}