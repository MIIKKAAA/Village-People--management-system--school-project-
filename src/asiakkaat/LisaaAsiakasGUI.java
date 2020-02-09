package asiakkaat;

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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.VerifyEvent;

public class LisaaAsiakasGUI {

	protected Shell lisaaAsiakasShl;
	private Text asiakasIdTxt;
	private Text sahkopostiTxt;
	private Text etunimiTxt;
	private Text sukunimiTxt;
	private Text osoiteTxt;
	private Label lisaaAsiakasLbl;
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
	private DateTime syntymaaikaDate;
	private Label puhelinnumeroLbl;
	private Text puhelinnumeroTxt;
	private Label lblNewLabel;
	String asiakasID;
	private Connection conn;
	private Asiakas asiakasOlio = new Asiakas(); //asiakasolio
	private AsiakkaatEtusivuGUI asiakkaatEtusivuGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LisaaAsiakasGUI window = new LisaaAsiakasGUI();
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
		Display display = ApuMetodeja.centerWindow(lisaaAsiakasShl);
		lisaaAsiakasShl.open();
		lisaaAsiakasShl.layout();
		while (!lisaaAsiakasShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		lisaaAsiakasShl = new Shell(SWT.CLOSE | SWT.MIN);
		lisaaAsiakasShl.setSize(450, 437);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			lisaaAsiakasShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.75));
		}
		else if (screenSize.getWidth() > 1600){
			lisaaAsiakasShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			lisaaAsiakasShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		lisaaAsiakasShl.setText("Lisää asiakas");
		lisaaAsiakasShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(lisaaAsiakasShl, SWT.NONE);
		composite.setToolTipText("Etunimi");
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		lisaaAsiakasLbl = new Label(composite, SWT.NONE);
		lisaaAsiakasLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		lisaaAsiakasLbl.setAlignment(SWT.CENTER);
		lisaaAsiakasLbl.setText("Syötä alle asiakkaan tiedot");
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
		asiakasIdTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

				 String string = e.text;
			      char[] chars = new char[string.length()];
			      string.getChars(0, chars.length, chars, 0);
			      for (int i = 0; i < chars.length; i++) {
			         if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
			         }
			      }
	        }
		});
		asiakasIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		asiakasIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKAS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		asiakasIdTxt.setToolTipText("Asiakas ID");
		
		sahkopostiTxt = new Text(composite, SWT.BORDER);
		sahkopostiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		sahkopostiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO SAHKOPOSTI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		sahkopostiTxt.setToolTipText("Sähköposti");
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
		etunimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		etunimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ETUNIMI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		etunimiTxt.setToolTipText("Etunimi");
		
		sukunimiTxt = new Text(composite, SWT.BORDER);
		sukunimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		sukunimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO SUKUNIMI TOIMINNALLISUUS TARVITTAESSA
			}
		});
		sukunimiTxt.setToolTipText("Sukunimi");
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
		osoiteTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		osoiteTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO OSOITE KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		osoiteTxt.setToolTipText("Osoite");
		
		postitoimipaikkaTxt = new Text(composite, SWT.BORDER);
		postitoimipaikkaTxt.setToolTipText("Postitoimipaikka");
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
		postinumeroTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

				 String string = e.text;
			      char[] chars = new char[string.length()];
			      string.getChars(0, chars.length, chars, 0);
			      for (int i = 0; i < chars.length; i++) {
			         if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
			         }
			      }
	        }
		});
		postinumeroTxt.setToolTipText("Postinumero");
		postinumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		syntymaaikaDate = new DateTime(composite, SWT.BORDER);
		syntymaaikaDate.setToolTipText("");
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
		puhelinnumeroTxt.addVerifyListener(new VerifyListener() {
			 public void verifyText(VerifyEvent e) {

				 String string = e.text;
			      char[] chars = new char[string.length()];
			      string.getChars(0, chars.length, chars, 0);
			      for (int i = 0; i < chars.length; i++) {
			         if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
			         }
			      }
	        }
		});
		puhelinnumeroTxt.setToolTipText("Puhelinnumero");
		puhelinnumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Napit
		Button peruutaBtn = new Button(composite, SWT.NONE);
		peruutaBtn.setText("Peruuta");
		peruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO PERUUTA NAPILLE KUUNTELIJA
				lisaaAsiakasShl.close();
				asiakkaatEtusivuGui = getAsiakkaatEtusivuGui();
				asiakkaatEtusivuGui.open();
			}
		});
		
		Button asiakasLisaaBtn = new Button(composite, SWT.NONE);
		asiakasLisaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		asiakasLisaaBtn.setText("Hyväksy");
		asiakasLisaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lisaaTiedot();
			}
		});
		new Label(composite, SWT.NONE);


	}
	/*
	 * Lis�t��n asiakkaan tiedot tietokantaan
	 */
	public void lisaaTiedot() {
		// Lisatty if-elset ettei heita erroria tyhjista kentista -Miikka
		if (asiakasIdTxt.getText() == "") {
			System.out.println("ASIAKASID ON LAITETTAVA");
		}
		else {
			asiakasOlio.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		}
		if (etunimiTxt.getText() == "") {
			asiakasOlio.setEtunimi("");
		}
		else {
			asiakasOlio.setEtunimi(etunimiTxt.getText());
		}
		if (osoiteTxt.getText() == "") {
			asiakasOlio.setLahiosoite("");
		}
		else {
			asiakasOlio.setLahiosoite(osoiteTxt.getText());
		}
		if (postinumeroTxt.getText() == "") {
			asiakasOlio.setPostinro(0);

		}
		else {
			asiakasOlio.setPostinro(Integer.parseInt(postinumeroTxt.getText()));
		}
		if (postitoimipaikkaTxt.getText() == "") {
			asiakasOlio.setPostitoimipaikka("");
		}
		else {
			asiakasOlio.setPostitoimipaikka(postitoimipaikkaTxt.getText());
		}
		if (puhelinnumeroTxt.getText() == "") {
			asiakasOlio.setPuhelinnro(0);
		}
		else {
			asiakasOlio.setPuhelinnro(Integer.parseInt(puhelinnumeroTxt.getText()));
		}
		if (sahkopostiTxt.getText() == "") {
			asiakasOlio.setEmail("");
		}
		else {
			asiakasOlio.setEmail(sahkopostiTxt.getText());
		}
		if (sukunimiTxt.getText() == "") {
			asiakasOlio.setSukunimi("");
		}
		else {
			asiakasOlio.setSukunimi(sukunimiTxt.getText());
		}
		String syntymaAika = Integer.toString(syntymaaikaDate.getYear()) + "-" + Integer.toString(syntymaaikaDate.getMonth()+1) + "-" + Integer.toString(syntymaaikaDate.getDay());
		asiakasOlio.setSyntymaaika(syntymaAika);
		try {
			yhdista();
			asiakasOlio.lisaaAsiakas(conn);
			System.out.println("Asiakas " + asiakasIdTxt.getText() + " lis�ttiin tietokantaan");
			asiakkaatEtusivuGui = getAsiakkaatEtusivuGui();
			lisaaAsiakasShl.close();
			asiakkaatEtusivuGui.open();


		} catch (SQLException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
