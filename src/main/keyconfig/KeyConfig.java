package main.keyconfig;

import java.awt.event.KeyEvent;

import main.notificationsystem.Notifications;

public class KeyConfig {
	private static boolean touchMode;
	private static int left = KeyEvent.VK_LEFT;
	private static int right = KeyEvent.VK_RIGHT;
	private static int up = KeyEvent.VK_UP;
	private static int down = KeyEvent.VK_DOWN;
	private static int anchor = KeyEvent.VK_SPACE;
	private static int inv = KeyEvent.VK_I;
	private static int reset = KeyEvent.VK_R;
	private static int reset_confirm = KeyEvent.VK_Y;
	private static int reset_cancel = KeyEvent.VK_N;
	private static int save = KeyEvent.VK_S;
	
	public static boolean getTouchMode() { return touchMode; }
	public static void setTouchMode(boolean newTouchMode) {
		touchMode = newTouchMode;
		Notifications.Notify("Touch mode " + (touchMode ? "enabled!" : "disabled!"), Notifications.Seconds(3));
	}
	
	public static int getKey(KeyEnum key) {
		if (key == KeyEnum.UP) return up;
		if (key == KeyEnum.DOWN) return down;
		if (key == KeyEnum.LEFT) return left;
		if (key == KeyEnum.RIGHT) return right;
		if (key == KeyEnum.ANCHOR) return anchor;
		if (key == KeyEnum.INVENTORY) return inv;
		if (key == KeyEnum.RESET) return reset;
		if (key == KeyEnum.RESET_CONFIRM) return reset_confirm;
		if (key == KeyEnum.RESET_CANCEL) return reset_cancel;
		if (key == KeyEnum.SAVE) return save;
		return -1;
	}
}