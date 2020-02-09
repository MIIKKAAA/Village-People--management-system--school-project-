package palvelut;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Text;

import ApuMetodeja.ApuMetodeja;
import asiakkaat.Asiakas;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

public class NaytaPalveluTiedotGUI {

	protected Shell naytaTiedotShl;
	private Text majoitusIdTxt;
	private Text toimipisteIdTxt;
	private Text nimiTxt;
	private Text majoitusHintaTxt;
	private Label naytaPalveluLbl;
	private Label palveluIdLbl;
	private Label toimipisteIdLbl;
	private Label asiakasID;
	private Label palveluHintaLbl;
	private Label label;
	static String PalveluID;
	private Connection conn;
	private Palvelu palveluOlio = new Palvelu(); //asiakasolio
	private PalvelutEtusivuGUI palvelutEtusivuGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NaytaPalveluTiedotGUI window = new NaytaPalveluTiedotGUI();
			PalveluID = args[0];
			window.open(PalveluID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open(String palvelu) {
		PalveluID = palvelu;
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(naytaTiedotShl);

		naytaTiedotShl.open();
		naytaTiedotShl.layout();
		haeTiedot();
		while (!naytaTiedotShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		naytaTiedotShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			naytaTiedotShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.9));
		}
		else if (screenSize.getWidth() > 1600){
			naytaTiedotShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 3.65));
		}
		else if (screenSize.getWidth() == 1600) {
			naytaTiedotShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				naytaTiedotShl.setText("Palvelun tiedot");
		naytaTiedotShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(naytaTiedotShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		naytaPalveluLbl = new Label(composite, SWT.NONE);
		naytaPalveluLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		naytaPalveluLbl.setAlignment(SWT.CENTER);
		naytaPalveluLbl.setText("Alla näet palvelun tiedot");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		label = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Ensimmaisen rivin label ja tekstikentta (palveluid ja toimipiste id
		palveluIdLbl = new Label(composite, SWT.NONE);
		palveluIdLbl.setText("Palvelu ID");
		
		toimipisteIdLbl = new Label(composite, SWT.NONE);
		toimipisteIdLbl.setText("Toimipiste ID");
		
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		majoitusIdTxt = new Text(composite, SWT.BORDER);
		majoitusIdTxt.setText(PalveluID);
		majoitusIdTxt.setEditable(false);
		majoitusIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
				System.out.println("TOIMII");
			}
		});
		majoitusIdTxt.setToolTipText("Palvelu ID");
		
		toimipisteIdTxt = new Text(composite, SWT.BORDER);
		toimipisteIdTxt.setEditable(false);
		toimipisteIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toimipisteIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO TOIMIPISTENIMI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		toimipisteIdTxt.setToolTipText("Toimipisteen ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Toisen rivin label ja tekstikentta (asiakasid ja hinta)
		asiakasID = new Label(composite, SWT.NONE);
		asiakasID.setText("Palvelun nimi");
		
		palveluHintaLbl = new Label(composite, SWT.NONE);
		palveluHintaLbl.setText("Hinta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		nimiTxt = new Text(composite, SWT.BORDER);
		nimiTxt.setEditable(false);
		nimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKASID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		nimiTxt.setToolTipText("");
		
		majoitusHintaTxt = new Text(composite, SWT.BORDER);
		majoitusHintaTxt.setEditable(false);
		majoitusHintaTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusHintaTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUSHINTA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		majoitusHintaTxt.setToolTipText("Palvelun hinta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Button palveluOkBtn = new Button(composite, SWT.NONE);
		palveluOkBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		palveluOkBtn.setText("OK");
		palveluOkBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				palvelutEtusivuGui = getPalvelutEtusivuGui();
				palvelutEtusivuGui.open();
				naytaTiedotShl.close();

			}
		});
		new Label(composite, SWT.NONE);


	}
	/*
	 * Haetaan palvelun tiedot tietokannasta
	 */
	public void haeTiedot() {
		palveluOlio = null;
		// avataan tietokanta
		try {
			yhdista ();
			palveluOlio = Palvelu.haePalvelu(conn, Integer.parseInt(PalveluID));
		 } catch (SQLException se) {
            // SQL virheet
			se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
        	e.printStackTrace();
		}
		nimiTxt.setText(palveluOlio.getNimi());
		toimipisteIdTxt.setText(String.valueOf(palveluOlio.getToimipisteId()));
		majoitusHintaTxt.setText(String.valueOf(palveluOlio.getHinta()));
		System.out.println("Palvelun " + PalveluID + " tiedot haettiin");
	}
	/*
	Avataan tietokantayhteys
	*/
	public void yhdista() throws SQLException, Exception {
		conn = null;
		String url = "jdbc:mariadb://localhost:3306/vp"; // palvelin = localhost, :portti annettu asennettaessa, tietokannan nimi
		try {
			// ota yhteys kantaan, kayttaja = root, salasana = mariadb
			conn=DriverManager.getConnection(url,"root","root");
		}
		catch (SQLException e) { // tietokantaan ei saada yhteytt�
			conn = null;
			throw e;
		}
		catch (Exception e ) { // JDBC ajuria ei l�ydy
			throw e;
		}
		
	}
	/*
	Suljetaan tietokantayhteys
	*/
	public void sulje_kanta() throws SQLException, Exception {
		// suljetaan		
		try {
			// sulje yhteys kantaan
			conn.close ();
		}
		catch (SQLException e) { // tietokantavirhe
			throw e;
		}
		catch (Exception e ) { // muu virhe tapahtui
			throw e;
		}
		
	}

	public PalvelutEtusivuGUI getPalvelutEtusivuGui() {
		return palvelutEtusivuGui;
	}

	public void setPalvelutEtusivuGui(PalvelutEtusivuGUI palvelutEtusivuGui) {
		this.palvelutEtusivuGui = palvelutEtusivuGui;
	}
	
}
