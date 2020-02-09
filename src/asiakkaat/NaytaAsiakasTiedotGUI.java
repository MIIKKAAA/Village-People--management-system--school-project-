package asiakkaat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Text;

import ApuMetodeja.ApuMetodeja;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;

import java.sql.*;

public class NaytaAsiakasTiedotGUI {

	protected Shell naytaTiedotShl;
	private Text asiakasIdTxt;
	private Text sahkopostiTxt;
	private Text etunimiTxt;
	private Text sukunimiTxt;
	private Text osoiteTxt;
	private Label asiakkaanTiedotLbl;
	private Label asiakasIdLbl;
	private Label sahkopostiLbl;
	private Label etunimiLbl;
	private Label sukunimiLbl;
	private Label osoiteLbl;
	private Label postinumeroLbl;
	private Label syntymaaikaLbl;
	private Text postinumeroTxt;
	private Label postitoimipaikkaLbl;
	private Text postitoimipaikkaTxt;
	private Label puhelinnumeroLbl;
	private Text puhelinnumeroTxt;
	private Label lblNewLabel;
	static String asiakasID;
	private Text syntymaaikaTxt;
	private Connection conn;
	private Asiakas asiakasOlio = new Asiakas(); //asiakasolio
	private AsiakkaatEtusivuGUI asiakkaatEtusivuGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NaytaAsiakasTiedotGUI window = new NaytaAsiakasTiedotGUI();
			asiakasID = args[0];
			window.open(asiakasID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open(String asiakas) {
		asiakasID = asiakas;
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(naytaTiedotShl);
;
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
		naytaTiedotShl.setSize(450, 413);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			naytaTiedotShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.75));
		}
		else if (screenSize.getWidth() > 1600){
			naytaTiedotShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			naytaTiedotShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		naytaTiedotShl.setText("Asiakkaan tiedot");
		naytaTiedotShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(naytaTiedotShl, SWT.NONE);
		composite.setToolTipText("Etunimi");
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		asiakkaanTiedotLbl = new Label(composite, SWT.NONE);
		asiakkaanTiedotLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		asiakkaanTiedotLbl.setAlignment(SWT.CENTER);
		asiakkaanTiedotLbl.setText("Alla näet asiakkaan tiedot");
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
		
		lblNewLabel = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		// Ekan rivin labelit (asiakasId ja sahkoposti) 
		
		asiakasIdLbl = new Label(composite, SWT.NONE);
		asiakasIdLbl.setText("Asiakas ID");	
		sahkopostiLbl = new Label(composite, SWT.NONE);
		sahkopostiLbl.setText("Sähköposti");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Ekan rivin tekstikentat (asiakasId ja sahkoposti)
		asiakasIdTxt = new Text(composite, SWT.BORDER);
		asiakasIdTxt.setText(asiakasID);
		asiakasIdTxt.setEditable(false);
		asiakasIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		asiakasIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKAS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		asiakasIdTxt.setToolTipText("");
		
		sahkopostiTxt = new Text(composite, SWT.BORDER);
		sahkopostiTxt.setEditable(false);
		sahkopostiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		sahkopostiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO SAHKOPOSTI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		sahkopostiTxt.setToolTipText("");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Tokan rivin labelit (etunimi ja sukunimi) 
		etunimiLbl = new Label(composite, SWT.NONE);
		etunimiLbl.setText("Etunimi");
		
		sukunimiLbl = new Label(composite, SWT.NONE);
		sukunimiLbl.setText("Sukunimi");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		// Tokan rivin labelit (etunimi ja sukunimi)
		etunimiTxt = new Text(composite, SWT.BORDER);
		etunimiTxt.setEditable(false);
		etunimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		etunimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ETUNIMI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		etunimiTxt.setToolTipText("");
		
		sukunimiTxt = new Text(composite, SWT.BORDER);
		sukunimiTxt.setEditable(false);
		sukunimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		sukunimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO SUKUNIMI TOIMINNALLISUUS TARVITTAESSA
			}
		});
		sukunimiTxt.setToolTipText("");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Kolmannen rivin labelit (osoite ja postitoimipaikka)
		osoiteLbl = new Label(composite, SWT.NONE);
		osoiteLbl.setToolTipText("");
		osoiteLbl.setText("Osoite");
		
		postitoimipaikkaLbl = new Label(composite, SWT.NONE);
		postitoimipaikkaLbl.setText("Postitoimipaikka");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		// Kolmannen rivin tekstikentat (osoite ja postitoimipaikka)
		osoiteTxt = new Text(composite, SWT.BORDER);
		osoiteTxt.setEditable(false);
		osoiteTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		osoiteTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO OSOITE KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		osoiteTxt.setToolTipText("");
		
		postitoimipaikkaTxt = new Text(composite, SWT.BORDER);
		postitoimipaikkaTxt.setEditable(false);
		postitoimipaikkaTxt.setToolTipText("");
		postitoimipaikkaTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Neljannen rivin labelit (postinumero ja syntymaaika)
		postinumeroLbl = new Label(composite, SWT.NONE);
		postinumeroLbl.setText("Postinumero");
		
		syntymaaikaLbl = new Label(composite, SWT.NONE);
		syntymaaikaLbl.setText("Syntymäaika");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		// Neljannen rivin tekstit (postinumero ja syntymaaika)
		postinumeroTxt = new Text(composite, SWT.BORDER);
		postinumeroTxt.setEditable(false);
		postinumeroTxt.setToolTipText("");
		postinumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		syntymaaikaTxt = new Text(composite, SWT.BORDER);
		syntymaaikaTxt.setEditable(false);
		syntymaaikaTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Viidennen rivin labeli (puhelinnumero)
		puhelinnumeroLbl = new Label(composite, SWT.NONE);
		puhelinnumeroLbl.setText("Puhelinnumero");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		// Viidennen rivin tekstikentta (puhelinnumero)
		puhelinnumeroTxt = new Text(composite, SWT.BORDER);
		puhelinnumeroTxt.setEditable(false);
		puhelinnumeroTxt.setToolTipText("");
		puhelinnumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Button asiakasTiedotOkBtn = new Button(composite, SWT.NONE);
		asiakasTiedotOkBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		asiakasTiedotOkBtn.setText("OK");
		asiakasTiedotOkBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				naytaTiedotShl.close();
				asiakkaatEtusivuGui = getAsiakkaatEtusivuGui();
				asiakkaatEtusivuGui.open();
			}
		});
		new Label(composite, SWT.NONE);


	}
	/*
	 * Haetaan asiakkaan tiedot tietokannasta
	 */
	public void haeTiedot() {
		asiakasOlio = null;
		// avataan tietokanta
		try {
			yhdista ();
			asiakasOlio = Asiakas.haeAsiakas(conn, Integer.parseInt(asiakasID));
		 } catch (SQLException se) {
            // SQL virheet
			se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
        	e.printStackTrace();
		}
		etunimiTxt.setText(asiakasOlio.getEtunimi());
		osoiteTxt.setText(asiakasOlio.getLahiosoite());
		postinumeroTxt.setText(String.valueOf(asiakasOlio.getPostinro()));
		postitoimipaikkaTxt.setText(asiakasOlio.getPostitoimipaikka());
		puhelinnumeroTxt.setText(String.valueOf(asiakasOlio.getPuhelinnro()));
		sahkopostiTxt.setText(asiakasOlio.getEmail());
		sukunimiTxt.setText(asiakasOlio.getSukunimi());
		syntymaaikaTxt.setText(asiakasOlio.getSyntymaaika());
		System.out.println("Asiakkaan " + asiakasID + " tiedot haettiin");
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

	public AsiakkaatEtusivuGUI getAsiakkaatEtusivuGui() {
		return asiakkaatEtusivuGui;
	}

	public void setAsiakkaatEtusivuGui(AsiakkaatEtusivuGUI asiakkaatEtusivuGui) {
		this.asiakkaatEtusivuGui = asiakkaatEtusivuGui;
	}
	
}
