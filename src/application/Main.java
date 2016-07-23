package application;

import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import application.model.HyperlinkCell;
import application.model.Message;
import application.model.MessageTableViewCell;
import application.model.ServerResponse;
import application.model.User;
import application.util.base.HttpUtils;
import javafx.application.Application;
import javafx.application.Platform;
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

public class Main extends Application {
	private Service service = new Service();
	private String token = "";
	private List<Message> messageArray = null;

	private TableView<MessageTableViewCell> messageTable;
	private TableColumn<MessageTableViewCell, String> updateTimeColumn;
	private TableColumn<MessageTableViewCell, Hyperlink> messageColumn;
	private ObservableList<MessageTableViewCell> messageList;
	private TrayIcon trayIcon;

	private String userName = "";
	private String passWord = "";

	final int width = 565;
	final int height = 330;

	/**
	 * 控件布局
	 * 
	 * @param loginStage
	 *            登录
	 * @param messageStage
	 *            消息
	 */
	private void draw(Stage loginStage, Stage messageStage) {
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Welcome");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		root.add(scenetitle, 0, 0, 2, 1);

		Label userNameLabel = new Label("用户名:");
		root.add(userNameLabel, 0, 1);

		TextField userNameTextField = new TextField();
		root.add(userNameTextField, 1, 1);

		Label pwLabel = new Label("密   码:");
		root.add(pwLabel, 0, 2);

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

		Scene scene = new Scene(root, 300, 250);

		loginStage.setTitle(Constants.RES_STAGE_TITLE_LOGIN);
		loginStage.setScene(scene);
		loginStage.show();

		// 登录按钮动作
		btn.setOnAction((ActionEvent event) -> {
			userName = userNameTextField.getText();
			passWord = passWordField.getText();

			ServerResponse loginRs = this.loginAction(userName, passWord);
			if (loginRs.isSuccess()) {
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText(Constants.RES_MESSAGE_LOGIN_SUCCESS);
				// 登陆成功后，隐藏登陆框
				loginStage.hide();
				// 初始化消息框
				this.drawMessageStage(messageStage);
				// 系统托盘区
				this.enableTray(messageStage);
				// 获取消息
				this.getMessage(token);
				// 更新消息列表
				messageTable.setItems(messageList);
			} else {

			}

		});
	}

	private void drawMessageStage(Stage stage) {
		final Label label = new Label("Hello! " + userName);
		label.setFont(new Font("Arial", 20));

		// 消息列表
		messageTable = new TableView<MessageTableViewCell>();
		messageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		messageTable.setFixedCellSize(25);
		messageTable.prefHeightProperty()
				.bind(messageTable.fixedCellSizeProperty().multiply(Bindings.size(messageTable.getItems()).add(10.01)));
		messageTable.minHeightProperty().bind(messageTable.prefHeightProperty());
		messageTable.maxHeightProperty().bind(messageTable.prefHeightProperty());

		VBox box = new VBox();
		box.getChildren().addAll(label, messageTable);

		final Scene scene = new Scene(box, width, height);
		scene.setFill(null);
		stage.setScene(scene);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX(primaryScreenBounds.getWidth() - width);
		stage.setY(primaryScreenBounds.getHeight() - height);
		stage.show();

	}

	// get messages from server
	private void getMessage(String token) {
		JSONObject messageRs = new JSONObject();

		Map<String, Object> messageParam = new HashMap<String, Object>();
		messageParam.put("token", token);
		messageRs = service.getMessage(messageParam);

		if (0 == messageRs.getIntValue("resultCode")) {
			messageArray = JSON.parseArray(messageRs.getString("result"), Message.class);

			updateTimeColumn = new TableColumn<>(Constants.RES_MESSAGE_COL_UPDATETIME);
			updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
			updateTimeColumn.setMinWidth(200);

			messageColumn = new TableColumn<>(Constants.RES_MESSAGE_COL_TITLE);
			messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
			messageColumn.setMinWidth(350);
			messageColumn.setCellFactory(new HyperlinkCell());

			messageTable.getColumns().add(updateTimeColumn);
			messageTable.getColumns().add(messageColumn);

			messageList = FXCollections.observableArrayList();
			for (Message msg : messageArray) {
				messageList.add(new MessageTableViewCell(msg));
			}

		}
	}

	private ServerResponse loginAction(String userName, String passWord) {
		ServerResponse rs = new ServerResponse();

		JSONObject loginRs = service.login(new User(userName, passWord));

		// login success
		if (0 == (loginRs.getIntValue("resultCode"))) {
			rs.setSuccess(true);

			token = loginRs.getJSONObject("result").getString("token");
			String urlLogin = "http://www.baidu.com";//test
					//loginRs.getJSONObject("result").getString("url");
			HttpUtils.openUrlByBrowser(urlLogin);
		}
		// login failed
		else {
			rs.setSuccess(false);
		}

		return rs;
		// Task t = new Task() {
		// @Override
		// protected Object call() throws Exception {
		// Thread.sleep(1000);
		// Platform.runLater(stage::close);
		// return "";
		// }
		// };
		// new Thread(t).start();
	}

	@Override
	public void start(Stage primaryStage) {

		final Stage loginStage = primaryStage;
		final Stage messageStage = new Stage();
		this.draw(loginStage, messageStage);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	// 启用系统托盘
	private void enableTray(final Stage stage) {
		PopupMenu popupMenu = new PopupMenu();
		java.awt.MenuItem openItem = new java.awt.MenuItem(Constants.RES_TRAYICON_SHOW);
		java.awt.MenuItem hideItem = new java.awt.MenuItem(Constants.RES_TRAYICON_HIDE);
		java.awt.MenuItem quitItem = new java.awt.MenuItem(Constants.RES_TRAYICON_EXIT);

		ActionListener acl = new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();
				Platform.setImplicitExit(false); // 多次使用显示和隐藏设置false

				if (item.getLabel().equals(Constants.RES_TRAYICON_EXIT)) {
					SystemTray.getSystemTray().remove(trayIcon);
					Platform.exit();
					return;
				}
				if (item.getLabel().equals(Constants.RES_TRAYICON_SHOW)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.show();
						}
					});
				}
				if (item.getLabel().equals(Constants.RES_TRAYICON_HIDE)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.hide();
						}
					});
				}

			}

		};

		// 双击事件方法
		MouseListener sj = new MouseListener() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				Platform.setImplicitExit(false); // 多次使用显示和隐藏设置false
				if (e.getClickCount() == 2) {
					if (stage.isShowing()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								stage.hide();
							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								stage.show();
							}
						});
					}
				}

			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}
		};

		openItem.addActionListener(acl);
		quitItem.addActionListener(acl);
		hideItem.addActionListener(acl);

		popupMenu.add(openItem);
		popupMenu.add(hideItem);
		popupMenu.add(quitItem);

		try {

			SystemTray tray = SystemTray.getSystemTray();
			// 系统托盘区显示图片 大小：8*8
			BufferedImage image = ImageIO.read(Main.class.getResourceAsStream("/trayIcon.png"));
			trayIcon = new TrayIcon(image, Constants.RES_TRAYICON_DISPLAY, popupMenu);
			trayIcon.setToolTip(Constants.RES_TRAYICON_DISPLAY);
			tray.add(trayIcon);
			trayIcon.addMouseListener(sj);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
