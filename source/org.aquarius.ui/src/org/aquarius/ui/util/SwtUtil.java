/**
 *
 */
package org.aquarius.ui.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aquarius.log.LogUtil;
import org.aquarius.ui.Messages;
import org.aquarius.util.AssertUtil;
import org.aquarius.util.DesktopUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IDisposable;
import org.slf4j.Logger;

/**
 * @author aquarius.github@gmail.com
 *
 */
public class SwtUtil {

	// private static final Pattern NumberPattern = Pattern.compile("(\\d+)");

	/**
	 * From SWT.CHROMIUM since 4.17
	 */
	public static final int CHROMIUM = 1 << 17;

	/**
	 * From SWT.EDGE since 4.19
	 */
	public static final int EDGE = 1 << 18;

	public static final Logger logger = LogUtil.getLogger(SwtUtil.class);

	private static Pattern numberPattern = Pattern.compile("^[1-9]\\d*|0$"); //$NON-NLS-1$

	/**
	 *
	 */
	private SwtUtil() {
		// No instances will be created.
	}

	/**
	 * 
	 * @param selectionProvider
	 * @return
	 */
	public static ISelection getSelection(ISelectionProvider selectionProvider) {
		if (null == selectionProvider) {
			return StructuredSelection.EMPTY;
		}

		ISelection selection = selectionProvider.getSelection();
		if (null == selection) {
			return StructuredSelection.EMPTY;
		} else {
			return selection;
		}
	}

	/**
	 * 
	 * @param selection
	 * @return
	 */
	public static ISelection wrapSelection(ISelection selection) {
		if (null == selection) {
			return StructuredSelection.EMPTY;
		}

		return selection;
	}

	/**
	 * Return whether the selection is null or empty.<BR>
	 * 
	 * @param selection
	 * @return
	 */
	public static boolean isEmpty(ISelection selection) {
		return ((null == selection) || (selection.isEmpty()));
	}

	/**
	 * Return whether the selection provider is null or has empty selection.<BR>
	 * 
	 * @param selection
	 * @return
	 */
	public static boolean isEmpty(ISelectionProvider selectionProvider) {

		if (null == selectionProvider) {
			return true;
		}

		return isEmpty(selectionProvider.getSelection());
	}

	/**
	 * If the selection is null or empty,return null.<BR>
	 * Otherwise return the first element.<BR>
	 * 
	 * @param selection
	 * @return
	 */
	public static Object findFirstElement(ISelection selection) {
		if ((null == selection) || (selection.isEmpty())) {
			return null;
		}

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		return structuredSelection.getFirstElement();
	}

	/**
	 * Return whether the widget is valid or not.<BR>
	 *
	 * @param widget
	 * @return
	 */
	public static boolean isValid(Widget widget) {
		return (null != widget) && (!widget.isDisposed());
	}

	/**
	 * Return whether the resource is valid or not.<BR>
	 *
	 * @param resource
	 * @return
	 */
	public static boolean isValid(Resource resource) {
		return (null != resource) && (!resource.isDisposed());
	}

	/**
	 * paint percent cell.<BR>
	 *
	 * @param table
	 * @param columnIndex
	 * @param foreGround
	 * @param backGround
	 */
	public static void updatePercentColumn(Table table, int columnIndex, Color foreGround, Color backGround) {
		table.addListener(SWT.PaintItem, new Listener() {

			@Override
			public void handleEvent(Event event) {

				doPaintTableCell(table, columnIndex, foreGround, backGround, event);

			}

		});
	}

	/**
	 * @param table
	 * @param columnIndex
	 * @param foreGround
	 * @param backGround
	 * @param event
	 */
	protected static void doPaintTableCell(Table table, int columnIndex, Color foreGround, Color backGround, Event event) {

		if (event.index == columnIndex) {

			event.detail &= ~SWT.HOT;

			TableColumn column = table.getColumn(columnIndex);
			TableItem item = (TableItem) event.item;
			Display display = table.getDisplay();
			GC gc = event.gc;

			int clientWidth = column.getWidth();

			Rectangle bounds = event.getBounds();
			bounds.width = clientWidth;

			gc.fillRectangle(bounds);

			String text = item.getText(columnIndex);

			if (null == text) {
				text = "0"; //$NON-NLS-1$
			}

			Matcher matcher = numberPattern.matcher(text);
			int percent = 0;

			if (matcher.find()) {
				percent = NumberUtils.toInt(matcher.group());
			}

			Color oldForeground = gc.getForeground();
			Color oldBackground = gc.getBackground();
			gc.setForeground(foreGround);
			gc.setBackground(backGround);

			int width = (clientWidth - 1) * percent / 100;
			gc.fillGradientRectangle(event.x, event.y, width, event.height, true);

			Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, event.height - 1);
			gc.drawRectangle(rect2);
			gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));

			Point size = event.gc.textExtent(text);
			int offset = Math.max(0, (event.height - size.y) / 2);

			int x = (event.x + ((clientWidth - size.x) / 2));
			gc.drawText(text, x, event.y + offset, true);

			gc.setForeground(oldForeground);
			gc.setBackground(oldBackground);
		}
	}

	/**
	 * @param table
	 * @param columnIndex
	 * @param foreGround
	 * @param backGround
	 * @param event
	 */
	protected static void doPaintTableCell1(Table table, int columnIndex, Color foreGround, Color backGround, Event event) {
		event.detail &= ~SWT.HOT;

		int index = event.index;

		if (index == columnIndex) {

			TableItem item = (TableItem) event.item;

			String text = item.getText(columnIndex);

			if (null == text) {
				return;
			}

			// int clientWidth = item.getBounds().width;
			int clientWidth = table.getColumn(columnIndex).getWidth();

			GC gc = event.gc;

			gc.fillRectangle(event.getBounds());

			Color oldForeground = gc.getForeground();
			Color oldBackground = gc.getBackground();
			gc.setForeground(foreGround);
			gc.setBackground(backGround);

			int progress = 0;
			Matcher matcher = numberPattern.matcher(text);

			if (matcher.find()) {
				progress = NumberUtils.toInt(matcher.group());
			}

			int drawClientWidth = 0;
			if (progress >= 0) {
				drawClientWidth = (clientWidth * progress) / 100;
			}

			gc.fillRoundRectangle(event.x, event.y, drawClientWidth, event.height, 2, 2);

			Point size = event.gc.textExtent(text);
			int offset = Math.max(0, (event.height - size.y) / 2);

			int x = (event.x + ((clientWidth - size.x) / 2));
			gc.drawText(text, x, event.y + offset, true);

			gc.setForeground(oldForeground);
			gc.setBackground(oldBackground);
		}
	}

	/**
	 * Check whether the current thread is ui thread.<BR>
	 *
	 * @param display
	 */
	public static void checkNotUThread(Display display) {
		if (isUThread()) {
			throw new UnsupportedOperationException("Dont use ui thread to do it"); //$NON-NLS-1$
		}
	}

	/**
	 * Return the current thread is ui thread or not.<BR>
	 *
	 * @return
	 */
	public static boolean isUThread() {
		return null != Display.getCurrent();
	}

	/**
	 * release resource.<BR>
	 *
	 * @param <T>
	 * @param resources
	 */
	public static <T extends IDisposable> void disposeQuietly(Collection<IDisposable> resources) {
		for (IDisposable resource : resources) {
			try {
				if (null != resource) {
					resource.dispose();
				}
			} catch (Exception e) {
				logger.error("disposeQuietly", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * release resource.<BR>
	 *
	 * @param <T>
	 * @param resources
	 */
	public static <T extends IDisposable> void disposeQuietly(IDisposable... resources) {
		for (IDisposable resource : resources) {
			try {
				if (null != resource) {
					resource.dispose();
				}
			} catch (Exception e) {
				logger.error("disposeQuietly", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * release resource.<BR>
	 *
	 * @param <T>
	 * @param widgets
	 */
	public static <T extends IDisposable> void disposeWidgetQuietly(Collection<Widget> widgets) {
		for (Widget widget : widgets) {
			try {
				if ((null != widget) && (!widget.isDisposed())) {
					widget.dispose();
				}
			} catch (Exception e) {
				logger.error("disposeQuietly", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * release resource.<BR>
	 *
	 * @param <T>
	 * @param widgets
	 */
	public static <T extends IDisposable> void disposeWidgetQuietly(Widget... widgets) {
		for (Widget widget : widgets) {
			try {
				if ((null != widget) && (!widget.isDisposed())) {
					widget.dispose();
				}
			} catch (Exception e) {
				logger.error("disposeQuietly", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 *
	 * @param <T>
	 * @param resources
	 */
	public static <T extends Resource> void disposeResourceQuietly(Collection<T> resources) {
		for (Resource resource : resources) {
			try {
				if (null != resource && (!resource.isDisposed())) {
					resource.dispose();
				}
			} catch (Exception e) {
				logger.error("disposeQuietly", e); //$NON-NLS-1$
			}
		}
	}

	public static <T extends Resource> void disposeResourceQuietly(T... resources) {
		for (Resource resource : resources) {
			try {
				if (null != resource && (!resource.isDisposed())) {
					resource.dispose();
				}
			} catch (Exception e) {
				logger.error("disposeQuietly", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public static IWorkbenchWindow findActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * 
	 * @return
	 */
	public static IWorkbenchPage findActiveWorkbenchPage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * Find a available shell to use.<BR>
	 *
	 * @return
	 */
	public static Shell findShell() {

		Shell shell = findDisplay().getActiveShell();

		if (isValid(shell)) {
			return shell;
		}

		Shell[] shells = Display.getDefault().getShells();
		if (ArrayUtils.isNotEmpty(shells)) {
			for (Shell currentShell : shells) {
				if (isValid(currentShell)) {
					return currentShell;
				}
			}
		}

		return null;

	}

	public static Display findDisplay() {
		Display display = Display.getCurrent();
		if (null != display) {
			return display;
		}

		return Display.getDefault();

	}

	/**
	 * Close the shell.<BR>
	 * 
	 * @param shell
	 */
	public static void closeShell(Shell shell) {
		if (isValid(shell)) {
			shell.close();
		}
	}

	/**
	 * Close the active editor.<BR>
	 */
	public static void closeActiveEditor(boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();

		if (null != editor) {
			page.closeEditor(editor, save);
		}
	}

	/**
	 * Close the editor.<BR>
	 */
	public static void closeEditor(IEditorPart editor, boolean save) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		if (null != editor) {
			page.closeEditor(editor, save);
		}
	}

	/**
	 * Use system error dialog to show exception.<BR>
	 *
	 * @param e
	 * @param title
	 */
	public static void showErrorDialog(Shell shell, String pluginId, String title, Throwable e) {
		Status status = new Status(IStatus.ERROR, pluginId, e.getLocalizedMessage(), e);

		ErrorDialog errorDialog = new ErrorDialog(shell, title, e.getLocalizedMessage(), status, Status.ERROR | IStatus.WARNING | IStatus.INFO);

		errorDialog.open();
	}

	/**
	 *
	 * @param parent
	 */
	public static void disposeChildren(Composite parent) {
		Control[] children = parent.getChildren();
		disposeWidgetQuietly(children);
	}

	/**
	 * Create a prompt decorate.<BR>
	 * It will show the left-bottom corner.<BR>
	 *
	 * @param control
	 * @param prompt
	 */
	public static ControlDecoration createPromptDecoration(Control control, String prompt) {

		if (StringUtils.isNotBlank(prompt)) {

			ControlDecoration decoration = SwtUtil.createPromptDecoration(control);

			decoration.setDescriptionText(prompt);

			return decoration;
		}

		return null;

	}

	/**
	 * Create a prompt decorate.<BR>
	 * It will show the left-bottom corner.<BR>
	 *
	 * @param control
	 * @param prompt
	 */
	public static ControlDecoration createPromptDecoration(Control control) {

		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
		ControlDecoration decoration = new ControlDecoration(control, SWT.LEFT | SWT.BOTTOM);
		decoration.setImage(image);

		decoration.setShowOnlyOnFocus(true);

		return decoration;

	}

	/**
	 * Create a error decorate.<BR>
	 * It will show the left-top corner.<BR>
	 *
	 * @param control
	 * @param prompt
	 */
	public static ControlDecoration createErrorDecoration(Control control) {
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
		ControlDecoration decoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
		decoration.setImage(image);

		decoration.hide();

		return decoration;
	}

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param store
	 * @param key
	 * @return
	 */
	public static boolean openConfirm(Shell parent, String title, String message, IPreferenceStore store, String key) {

		AssertUtil.assertNotNull(store);
		AssertUtil.assertNotNull(key);

		String mark = store.getString(key);
		if (MessageDialogWithToggle.ALWAYS.equals(mark)) {
			return true;
		}

		String toggleMessage = Messages.SwtUtil_ToggleMessage;

		MessageDialogWithToggle dialog = MessageDialogWithToggle.open(MessageDialog.CONFIRM, parent, title, message, toggleMessage, false, store, key,
				SWT.NONE);

		return dialog.getReturnCode() == Dialog.OK;
	}

	/**
	 * Create a link ,it support click to open the url to open it in the external
	 * browser.
	 * 
	 * @param parent
	 * @param label
	 * @return
	 */
	public static Link createLink(Composite parent, String label) {
		Link link = new Link(parent, SWT.NONE);
		link.setText(label);
		link.addListener(SWT.Selection, event -> DesktopUtil.openWebpages(event.text));

		return link;
	}

	/**
	 * Find the children with specified name.
	 *
	 * @param <T>
	 * @param parent
	 * @param recusive
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> findChildren(Composite parent, boolean recusive, Class<T> clazz) {
		List<T> controlList = new ArrayList<>();
		List<Composite> parentList = new ArrayList<>();

		parentList.add(parent);

		for (int i = 0; i < parentList.size(); i++) {
			Composite composite = parentList.get(i);

			Control[] children = composite.getChildren();

			for (Control control : children) {
				if (recusive) {
					if (control instanceof Composite) {
						parentList.add((Composite) control);
					}
				}

				if (clazz == control.getClass()) {
					controlList.add((T) control);
				}
			}
		}

		return controlList;
	}

	/**
	 * @return
	 */
	public static boolean isSupportEdge() {

		if (!SystemUtils.IS_OS_WINDOWS) {
			return false;
		}

		try {
			Field field = SWT.class.getField("CHROMIUM");

			Object value = field.get(SWT.class);
			if (value instanceof Integer) {
				Integer intValue = (Integer) value;

				return intValue == SwtUtil.CHROMIUM;
			}

		} catch (Exception e) {
			return false;
		}

		return false;
	}

	public static StyledText createStyledTextWithLine(Composite parent, int style) {
		StyledText styledText = new StyledText(parent, style);

		addLineNumbers(styledText);

		return styledText;
	}

	/**
	 * @param styledText
	 */
	public static void addLineNumbers(StyledText styledText) {

		styledText.addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent event) {
				// Set the line number
				event.bulletIndex = styledText.getLineAtOffset(event.lineOffset);

				// Set the style, 12 pixles wide for each digit
				StyleRange style = new StyleRange();

				style.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
				style.background = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

				int maxLine = styledText.getLineCount();

				style.metrics = new GlyphMetrics(0, 0, Integer.toString(maxLine + 1).length() * 12);

				// Create and set the bullet
				event.bullet = new Bullet(ST.BULLET_NUMBER, style);
			}
		});
		styledText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// For line number redrawing.
				styledText.redraw();
			}
		});
	}
}
