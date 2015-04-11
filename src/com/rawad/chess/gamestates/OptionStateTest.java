package com.rawad.chess.gamestates;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;

public class OptionStateTest {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	private Composite composite;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					OptionStateTest window = new OptionStateTest();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		composite = new Composite(shell, SWT.NONE);
		composite.setBounds(10, 10, 414, 242);
		m_bindingContext = initDataBindings();

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeBackgroundCompositeObserveWidget = WidgetProperties
				.background().observe(composite);
		IObservableValue backgroundShellObserveValue = PojoProperties.value(
				"background").observe(shell);
		bindingContext.bindValue(observeBackgroundCompositeObserveWidget,
				backgroundShellObserveValue, null, null);
		//
		return bindingContext;
	}
}
