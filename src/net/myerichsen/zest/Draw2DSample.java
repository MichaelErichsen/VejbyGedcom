package net.myerichsen.zest;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class Draw2DSample extends ApplicationWindow {

	public static void main(String[] args) {
		final Draw2DSample window = new Draw2DSample(null);
		window.setBlockOnOpen(true);
		window.open();
	}

	/**
	 * @param parentShell
	 */
	public Draw2DSample(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.window.ApplicationWindow#addToolBar(int)
	 */
	@Override
	protected void addToolBar(int style) {
		super.addToolBar(style);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.
	 * Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new FillLayout());

		final Canvas canvas = new Canvas(composite, SWT.NO_REDRAW_RESIZE);

		canvas.addPaintListener(e -> e.gc.drawLine(20, 30, 40, 50));

//		LightweightSystem lws = new LightweightSystem(canvas);
//		Button button = new Button("Button", new Image(getShell().getDisplay(),
//				"C:\\Users\\michael\\git\\VejbyGedcom\\src\\net\\myerichsen\\zest\\Draw2DSample.PNG"));
//		lws.setContents(button);

		return composite;
	}
}
