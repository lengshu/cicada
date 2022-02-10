/**
 *
 */
package org.aquarius.ui.key;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.aquarius.util.AssertUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Key Definition binder.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public final class KeyBinder {

	private int keyCode = 0;

	private boolean ctrl;

	private boolean shift;

	private boolean alt;

	private boolean command;

	private String method;

	private String symbol;

	private static final Map<String, Integer> keyMapping = new TreeMap<>();

	static {

		keyMapping.put("pause", SWT.PAUSE);

		keyMapping.put("esc", Integer.valueOf(SWT.ESC));
		keyMapping.put("escape", Integer.valueOf(SWT.ESC));

		keyMapping.put("space", Integer.valueOf(SWT.SPACE));

		keyMapping.put("pageup", SWT.PAGE_UP);
		keyMapping.put("page up", SWT.PAGE_UP);

		keyMapping.put("pagedown", SWT.PAGE_DOWN);
		keyMapping.put("page down", SWT.PAGE_DOWN);

		keyMapping.put("home", SWT.HOME);
		keyMapping.put("end", SWT.END);

		keyMapping.put("left", SWT.ARROW_LEFT);
		keyMapping.put("←", SWT.ARROW_LEFT);

		keyMapping.put("up", SWT.ARROW_UP);
		keyMapping.put("↑", SWT.ARROW_UP);

		keyMapping.put("right", SWT.ARROW_RIGHT);
		keyMapping.put("→", SWT.ARROW_RIGHT);

		keyMapping.put("down", SWT.ARROW_DOWN);
		keyMapping.put("↓", SWT.ARROW_DOWN);

		keyMapping.put("back", 8);
		keyMapping.put("backspace", 8);

		keyMapping.put("ins", SWT.INSERT);
		keyMapping.put("insert", SWT.INSERT);

		keyMapping.put("del", Integer.valueOf(SWT.DEL));
		keyMapping.put("delete", Integer.valueOf(SWT.DEL));

		keyMapping.put("?", SWT.HELP);
		keyMapping.put("help", SWT.HELP);

		keyMapping.put("enter", 13);
		keyMapping.put("return", 13);
	}

	public static final KeyBinder CtrlEnterKeyBinder = KeyBinder.createKeyBinder("ctrl+enter", "ctrlEnter");

	public static final KeyBinder ShiftEnterKeyBinder = KeyBinder.createKeyBinder("shift+enter", "shiftEnter");
	public static final KeyBinder ShiftDeleteKeyBinder = KeyBinder.createKeyBinder("shift+delete", "shiftDelete");

	public static final KeyBinder BackspaceKeyBinder = KeyBinder.createKeyBinder("backspace", "backspace");
	public static final KeyBinder CopyKeyBinder = KeyBinder.createKeyBinder("ctrl+c", "copy");

	public static final KeyBinder DeleteKeyBinder = KeyBinder.createKeyBinder("del", "delete");
	public static final KeyBinder InsertKeyBinder = KeyBinder.createKeyBinder("insert", "delete");
	public static final KeyBinder UpKeyBinder = KeyBinder.createKeyBinder("up", "up");
	public static final KeyBinder DownKeyBinder = KeyBinder.createKeyBinder("down", "down");
	public static final KeyBinder LeftKeyBinder = KeyBinder.createKeyBinder("left", "left");
	public static final KeyBinder RightKeyBinder = KeyBinder.createKeyBinder("right", "right");
	public static final KeyBinder HomeKeyBinder = KeyBinder.createKeyBinder("home", "home");
	public static final KeyBinder EndKeyBinder = KeyBinder.createKeyBinder("end", "end");
	public static final KeyBinder PageUpKeyBinder = KeyBinder.createKeyBinder("pageup", "pageUp");
	public static final KeyBinder PageDownKeyBinder = KeyBinder.createKeyBinder("pagedown", "pageDown");
	public static final KeyBinder HelperKeyBinder = KeyBinder.createKeyBinder("help", "help");
	public static final KeyBinder EnterKeyBinder = KeyBinder.createKeyBinder("enter", "enter");
	public static final KeyBinder EscKeyBinder = KeyBinder.createKeyBinder("esc", "escape");
	public static final KeyBinder PauseKeyBinder = KeyBinder.createKeyBinder("pause", "pause");

	public static final KeyBinder SelectAllKeyBinder = KeyBinder.createKeyBinder("ctrl+a", "selectAll");

	/**
	 *
	 */
	private KeyBinder() {
		super();
	}

	/**
	 * @return the character
	 */
	public int getKeyCode() {
		return this.keyCode;
	}

	/**
	 * @return the ctrl
	 */
	public boolean isCtrl() {
		return this.ctrl;
	}

	/**
	 * @return the shift
	 */
	public boolean isShift() {
		return this.shift;
	}

	/**
	 * @return the alt
	 */
	public boolean isAlt() {
		return this.alt;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.alt, this.keyCode, this.ctrl, this.shift, this.command, this.method);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyBinder other = (KeyBinder) obj;

		return this.alt == other.alt && this.ctrl == other.ctrl && this.shift == other.shift && this.command == other.command
				&& StringUtils.equalsIgnoreCase(this.keyCode + "", other.keyCode + "") && StringUtils.equalsIgnoreCase(this.method, other.method);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String toString() {
		return "KeyBinder [method=" + this.method + ", symbol=" + this.symbol + "]";
	}

	/**
	 *
	 * @param symbol
	 * @param method
	 * @return
	 */
	public static final KeyBinder createKeyBinder(String symbol, String method) {
		AssertUtil.assertNotNull(symbol);

		String[] keyNames = StringUtils.split(symbol, "+");
		KeyBinder keyBinder = new KeyBinder();

		for (String keyName : keyNames) {
			bind(keyName, keyBinder);
		}

		keyBinder.method = method;

		return keyBinder;
	}

	/**
	 * Judge whether the event is matched to the kind binder.<BR>
	 *
	 * @param event
	 * @return
	 */
	public boolean isMatch(KeyEvent event) {

		boolean flag = (this.ctrl) == ((event.stateMask & SWT.CTRL) != 0);
		flag = flag && ((this.shift) == ((event.stateMask & SWT.SHIFT) != 0));
		flag = flag && ((this.alt) == ((event.stateMask & SWT.ALT) != 0));
		flag = flag && ((this.command) == ((event.stateMask & SWT.COMMAND) != 0));

		if (!flag) {
			return false;
		}

		// char keyCode = (char) event.keyCode;
		// keyCode = Character.toLowerCase(keyCode);

		return this.keyCode == event.keyCode;
	}

	/**
	 *
	 * @param keyName
	 * @param keyBinder
	 */
	private static void bind(String keyName, KeyBinder keyBinder) {
		if (StringUtils.equalsIgnoreCase("ctrl", keyName)) {
			keyBinder.ctrl = true;
			return;
		}

		if (StringUtils.equalsIgnoreCase("shift", keyName)) {
			keyBinder.shift = true;
			return;
		}

		if (StringUtils.equalsIgnoreCase("alt", keyName)) {
			keyBinder.alt = true;
			return;
		}

		if (StringUtils.equalsIgnoreCase("command", keyName)) {
			keyBinder.alt = true;
			return;
		}

		Integer character = keyMapping.get(keyName.toLowerCase());
		if (null != character) {
			keyBinder.keyCode = character.intValue();
			return;
		}

		char[] chars = keyName.toCharArray();
		if (chars.length != 1) {
			throw new IllegalArgumentException("Wrong Symbol");
		}

		if (keyBinder.keyCode != 0) {
			throw new IllegalArgumentException("Multi Key");
		}

		keyBinder.keyCode = Character.toLowerCase(chars[0]);
	}

}
