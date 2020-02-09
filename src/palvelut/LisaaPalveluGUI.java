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

public class LisaaPalveluGUI {

	protected Shell lisaaPalveluShl;
	private Text palveluIdTxt;
	private Text toimipisteIdTxt;
	private Text majoitusHintaTxt;
	private Text palveluNimiTxt;
	private Label lisaaPalveluLbl;
	private Label palveluIdLbl;
	private Label toimipisteIdLbl;
	private Label palveluHintaLbl;
	private Label palveluNimiLbl;
	private Label label;
	private Connection conn;
	private Palvelu palveluOlio = new Palvelu(); //asiakasolio
	private PalvelutEtusivuGUI palvelutEtusivuGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LisaaPalveluGUI window = new LisaaPalveluGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		yhdista();
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(lisaaPalveluShl);

		lisaaPalveluShl.open();
		lisaaPalveluShl.layout();
		while (!lisaaPalveluShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		lisaaPalveluShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			lisaaPalveluShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			lisaaPalveluShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 3.65));
		}
		else if (screenSize.getWidth() == 1600) {
			lisaaPalveluShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				lisaaPalveluShl.setText("Lisää palvelu");
		lisaaPalveluShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(lisaaPalveluShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		lisaaPalveluLbl = new Label(composite, SWT.NONE);
		lisaaPalveluLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		lisaaPalveluLbl.setAlignment(SWT.CENTER);
		lisaaPalveluLbl.setText("Syötä alle palvelun tiedot");
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
		
		
		// Ensimmaisen rivin label ja tekstikentta (palveluid ja toimipiste id)
		palveluIdLbl = new Label(composite, SWT.NONE);
		palveluIdLbl.setText("Palvelu ID");
		
		toimipisteIdLbl = new Label(composite, SWT.NONE);
		toimipisteIdLbl.setText("Toimipiste ID");
		
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		palveluIdTxt = new Text(composite, SWT.BORDER);
		palveluIdTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

	            Text text = (Text)e.getSource();

	            final String oldS = text.getText();
	            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

	            boolean isFloat = true;
	            try
	            {
	                Float.parseFloat(newS);
	            }
	            catch(NumberFormatException ex)
	            {
	                isFloat = false;
	            }

	            System.out.println(newS);

	            if(!isFloat)
	                e.doit = false;
	        }
		});
		palveluIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		palveluIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA

			}
		});
		palveluIdTxt.setToolTipText("Palvelu ID");
		
		toimipisteIdTxt = new Text(composite, SWT.BORDER);
		toimipisteIdTxt.addVerifyListener(new VerifyListener() {
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
		toimipisteIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toimipisteIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO TOIMIPISTENIMI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		toimipisteIdTxt.setToolTipText("Toimipisteen ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Kolmannen rivin label ja tekstikentta (palvelunimi ja hinta)
		palveluNimiLbl = new Label(composite, SWT.NONE);
		palveluNimiLbl.setToolTipText("");
		palveluNimiLbl.setText("Palvelun nimi");
		
		palveluHintaLbl = new Label(composite, SWT.NONE);
		palveluHintaLbl.setText("Hinta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		palveluNimiTxt = new Text(composite, SWT.BORDER);
		palveluNimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		palveluNimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO PALVELUTYYPPI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		palveluNimiTxt.setToolTipText("Palvelun nimi");
		
		majoitusHintaTxt = new Text(composite, SWT.BORDER);
		majoitusHintaTxt.addVerifyListener(new VerifyListener() {
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
		
		
		// Napit
		Button peruutaBtn = new Button(composite, SWT.NONE);
		peruutaBtn.setText("Peruuta");
		peruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				palvelutEtusivuGui = new PalvelutEtusivuGUI();
				lisaaPalveluShl.close();
				palvelutEtusivuGui.open();

			}
		});
		
		Button palveluLisaaBtn = new Button(composite, SWT.NONE);
		palveluLisaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		palveluLisaaBtn.setText("Hyväksy");
		palveluLisaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lisaaTiedot();
			}
		});
		new Label(composite, SWT.NONE);


	}
	/*
	 * Lis�t��n palvelun tiedot tietokannassa
	 */
	public void lisaaTiedot() {
		// Lisatty if-else haara silta varalta jos syotetaan tyhjaa tietoa -Miikka
		if (palveluIdTxt.getText () == "") {
			System.out.println("Et syöttäny Palvelu ID:tä, yritä uudelleen!");
			lisaaPalveluShl.close();
		}
		else {
			palveluOlio.setPalveluId(Integer.parseInt(palveluIdTxt.getText()));
		}
		if (palveluNimiTxt.getText() =="") {
			palveluOlio.setNimi("");
		}
		else {
			palveluOlio.setNimi(palveluNimiTxt.getText());
		}
		if (majoitusHintaTxt.getText() == "") {
			palveluOlio.setHinta(0);
		}
		else {
			palveluOlio.setHinta(Integer.parseInt(majoitusHintaTxt.getText()));
		}
		if(toimipisteIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Toimipiste ID:tä, yritä uudelleen!");
			lisaaPalveluShl.close();
		}
		else {
			palveluOlio.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
		}
		try {
			palveluOlio.lisaaPalvelu(conn);
			System.out.println("Palvelu " + palveluIdTxt.getText() + " lis�ttiin tietokantaan");
			palvelutEtusivuGui = new PalvelutEtusivuGUI();
			palvelutEtusivuGui.open();
			lisaaPalveluShl.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public PalvelutEtusivuGUI getPalvelutEtusivuGui() {
		return palvelutEtusivuGui;
	}

	public void setPalvelutEtusivuGui(PalvelutEtusivuGUI palvelutEtusivuGui) {
		this.palvelutEtusivuGui = palvelutEtusivuGui;
	}
	
}
