package application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import application.model.HyperlinkCell;
import application.model.Message;
import application.model.MessageTableViewCell;
import application.model.User;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	private Service service = new Service();
	private String token = "";
	private List<Message> messageArray = null;
	
	private TableView<MessageTableViewCell> messageTable ;
	private TableColumn<MessageTableViewCell, String> updateTimeColumn;
	private TableColumn<MessageTableViewCell, Hyperlink> messageColumn;
	private ObservableList<MessageTableViewCell> messageList;

	@Override
	public void start(Stage primaryStage) {

		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(25, 25, 25, 25));
		final int width = 565;
		final int height = 330;

		// 控件布局
		Text scenetitle = new Text("Welcome");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		root.add(scenetitle, 0, 0, 2, 1);

		Label userName = new Label("用户名:");
		root.add(userName, 0, 1);

		TextField userNameTextField = new TextField();
		root.add(userNameTextField, 1, 1);

		Label pw = new Label("密   码:");
		root.add(pw, 0, 2);

		PasswordField passWordField = new PasswordField();
		root.add(passWordField, 1, 2);

		Button btn = new Button();
		btn.setText("登录");

		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		root.add(hbBtn, 1, 4);

		final Text actiontarget = new Text();
		root.add(actiontarget, 1, 6);

		// login
		btn.setOnAction((ActionEvent event) -> {
			JSONObject loginRs = service.login(new User(userNameTextField.getText(), passWordField.getText()));
			JSONObject messageRs = new JSONObject();

			if (0 == (loginRs.getIntValue("resultCode"))) { // login success
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("登录成功");
				token = loginRs.getJSONObject("result").getString("token");

				// get messages from server
				Map<String, Object> messageParam = new HashMap<String, Object>();
				messageParam.put("token", token);
				messageRs = service.getMessage(messageParam);

				if (0 == messageRs.getIntValue("resultCode")) {
					messageArray = JSON.parseArray(messageRs.getString("result"), Message.class);
					final Label label = new Label("Hello! " + userNameTextField.getText());
			        label.setFont(new Font("Arial", 20));
			 
			        messageTable = new TableView<MessageTableViewCell>();
			        messageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			        messageTable.setFixedCellSize(25);
			        messageTable.prefHeightProperty().bind(messageTable.fixedCellSizeProperty().multiply(Bindings.size(messageTable.getItems()).add(10.01)));
			        messageTable.minHeightProperty().bind(messageTable.prefHeightProperty());
			        messageTable.maxHeightProperty().bind(messageTable.prefHeightProperty());
			        
			        updateTimeColumn = new TableColumn<>("更新时间");
			        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
			        updateTimeColumn.setMinWidth(200);
			        
			        messageColumn = new TableColumn<>("消息");
			        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
			        messageColumn.setMinWidth(350);
			        messageColumn.setCellFactory(new HyperlinkCell());
			 
			        messageTable.getColumns().add(updateTimeColumn);
			        messageTable.getColumns().add(messageColumn);
			 
			        messageList = FXCollections.observableArrayList();
			        for(Message msg : messageArray) {
			        	messageList.add(new MessageTableViewCell(msg));
			        }
			        
			        
			        messageTable.setItems(messageList);
					
					VBox box = new VBox();
					box.getChildren().addAll(label, messageTable);

					final Scene scene = new Scene(box, width, height);
					scene.setFill(null);

					final Stage stage = new Stage();
					stage.initStyle(StageStyle.TRANSPARENT);
					stage.setScene(scene);
					Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
					stage.setX(primaryScreenBounds.getWidth() - width);
					stage.setY(primaryScreenBounds.getHeight() - height);
					stage.show();
					primaryStage.hide();
				}

//				if (messageArray.size() > 0) {
//					messageLinks = new Hyperlink[messageArray.size()];
//					// 显示信息
//					for (int i = 0; i < messageArray.size(); i++) {
//						Message msg = messageArray.get(i);
//						Hyperlink link = new Hyperlink();
//						link.setText(msg.getTitle());
//						link.setOnAction((ActionEvent e) -> {
//							try {
//								// open in system's browser
//								Desktop.getDesktop().browse(new URI(msg.getUrl()));
//							} catch (Exception e1) {
//								e1.printStackTrace();
//							}
//						});
//						messageLinks[i] = link;
//					}
//				}
			}else{ //login failed
				
			}

			

	        

			// Task t = new Task() {
			// @Override
			// protected Object call() throws Exception {
			// Thread.sleep(1000);
			// Platform.runLater(stage::close);
			// return "";
			// }
			// };
			// new Thread(t).start();
		});

		// root.getChildren().add(btn);

		Scene scene = new Scene(root, 300, 250);

		primaryStage.setTitle("登录");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
