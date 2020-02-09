package laskutus;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import ApuMetodeja.ApuMetodeja;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ValittuVarausVaroitusDialog {

	protected Shell valittuVarausVaroitusShl;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ValittuVarausVaroitusDialog window = new ValittuVarausVaroitusDialog();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(valittuVarausVaroitusShl);
		valittuVarausVaroitusShl.open();
		valittuVarausVaroitusShl.layout();
		while (!valittuVarausVaroitusShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		valittuVarausVaroitusShl = new Shell();

		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			valittuVarausVaroitusShl.setSize((int)(screenSize.getWidth() / 1.25), (int) (screenSize.getHeight() / 1.2));
		}
		else if (screenSize.getWidth() > 1600){
			valittuVarausVaroitusShl.setSize((int)(screenSize.getWidth() / 8.3), (int) (screenSize.getHeight() / 5.5));
		}
		else if (screenSize.getWidth() == 1600) {
			valittuVarausVaroitusShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		valittuVarausVaroitusShl.setText("Varoitus");
		valittuVarausVaroitusShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(valittuVarausVaroitusShl, SWT.NONE);
		composite.setLayout(new GridLayout(5, false));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Label huomioLbl = new Label(composite, SWT.NONE);
		huomioLbl.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		huomioLbl.setText("Varoitus!");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Label lblValitseAinakinYksi = new Label(composite, SWT.NONE);
		lblValitseAinakinYksi.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblValitseAinakinYksi.setText("Valitse ainakin yksi varaus");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Label lblJommastaKummastaListasta = new Label(composite, SWT.NONE);
		lblJommastaKummastaListasta.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		lblJommastaKummastaListasta.setText("jommasta kummasta listasta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				valittuVarausVaroitusShl.close();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("OK");

	}

}
