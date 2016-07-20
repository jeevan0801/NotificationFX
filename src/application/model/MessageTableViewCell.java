package application.model;

import java.awt.Desktop;
import java.net.URI;

import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;

public class MessageTableViewCell {
	private String updateTime;
	private Hyperlink message;

	public MessageTableViewCell(Message msgObj) {
		this.updateTime = msgObj.getTime();
		this.message = new Hyperlink(msgObj.getTitle());
		
		message.setOnAction((ActionEvent e) -> {
			try {
				Desktop.getDesktop().browse(new URI(msgObj.getUrl()));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Hyperlink getMessage() {
		return message;
	}

	public void setMessage(Hyperlink message) {
		this.message = message;
	}

}
