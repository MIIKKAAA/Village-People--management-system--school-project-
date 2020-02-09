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

public class MuokkaaPalveluGUI {

	protected Shell muokkaaPalveluShl;
	private Text majoitusIdTxt;
	private Text toimipisteIdTxt;
	private Text majoitusHintaTxt;
	private Text nimiTxt;
	private Label muokkaaPalveluLbl;
	private Label palveluIdLbl;
	private Label toimipisteIdLbl;
	private Label palveluHintaLbl;
	private Label palveluNimiLbl;
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
			MuokkaaPalveluGUI window = new MuokkaaPalveluGUI();
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
		Display display = ApuMetodeja.centerWindow(muokkaaPalveluShl);

		muokkaaPalveluShl.open();
		muokkaaPalveluShl.layout();
		haeTiedot();
		while (!muokkaaPalveluShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		muokkaaPalveluShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			muokkaaPalveluShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			muokkaaPalveluShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 3.65));
		}
		else if (screenSize.getWidth() == 1600) {
			muokkaaPalveluShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				muokkaaPalveluShl.setText("Muokkaa palvelua");
		muokkaaPalveluShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(muokkaaPalveluShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		muokkaaPalveluLbl = new Label(composite, SWT.NONE);
		muokkaaPalveluLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		muokkaaPalveluLbl.setAlignment(SWT.CENTER);
		muokkaaPalveluLbl.setText("Muokkaa alla palvelun tietoja");
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
		
		nimiTxt = new Text(composite, SWT.BORDER);
		nimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO PALVELUTYYPPI KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		nimiTxt.setToolTipText("Palvelun nimi");
		
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
				palvelutEtusivuGui.open();
				muokkaaPalveluShl.close();
			}
		});
		
		Button palveluMuokkaaBtn = new Button(composite, SWT.NONE);
		palveluMuokkaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		palveluMuokkaaBtn.setText("Hyväksy");
		palveluMuokkaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				muutaTiedot();
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
	 * Muutetaan palvelun tiedot tietokannassa
	 */
	public void muutaTiedot() {
		palveluOlio.setNimi(nimiTxt.getText());
		palveluOlio.setHinta(Integer.parseInt(majoitusHintaTxt.getText()));
		palveluOlio.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
		try {
			palveluOlio.muutaPalvelu(conn);
			System.out.println("Palvelun " + PalveluID + " tiedot muutettu");
			palvelutEtusivuGui = new PalvelutEtusivuGUI();
			palvelutEtusivuGui.open();
			muokkaaPalveluShl.close();
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
