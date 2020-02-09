package toimipisteet;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Composite;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Text;

import ApuMetodeja.ApuMetodeja;
import laskutus.Lasku;

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

public class MuokkaaToimipisteGUI {

	protected Shell muokkaaToimipisteShl;
	private Text nimiTxt;
	private Text toimipisteIdTxt;
	private Text osoiteTxt;
	private Label muokkaaToimipisteLbl;
	private Label toimipisteIdLbl;
	private Label nimiLbl;
	private Label osoiteLbl;
	private Label label;
	private Connection conn;
	Toimipiste toimipiste = new Toimipiste();
	private int valittuToimipisteID;
	private ToimipisteEtusivuGUI toimipisteEtusivuGui;
	private Label paikkakuntaLbl;
	private Text paikkakuntaTxt;
	private Label postinumeroLbl;
	private Text postinumeroTxt;
	private MuokkaaToimipisteGUI muokkaaToimipisteGui;
	

	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		createContents();
		yhdista();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(muokkaaToimipisteShl);
		luoToimipisteTiedot();
		lisaaTiedotToimipisteOlioon();
		muokkaaToimipisteShl.open();
		muokkaaToimipisteShl.layout();
		while (!muokkaaToimipisteShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	public void yhdista() throws SQLException, Exception {
		conn = null;
		String url = "jdbc:mariadb://localhost:3306/vp"; // palvelin = localhost, :portti annettu asennettaessa, tietokannan nimi
		try {
			// ota yhteys kantaan, kayttaja = root, salasana = root
			conn=DriverManager.getConnection(url,"root","root");
		}
		catch (SQLException e) { // tietokantaan ei saada yhteyttä
			conn = null;
			throw e;
		}
		catch (Exception e ) { // JDBC ajuria ei löydy
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
	
	
	public void lisaaTiedotToimipisteOlioon() throws SQLException, Exception {
		toimipiste.setNimi(osoiteTxt.getText());
		toimipiste.setOsoite(osoiteTxt.getText());
		toimipiste.setPaikkakunta(paikkakuntaTxt.getText());
		toimipiste.setPostinumero(Integer.parseInt(postinumeroTxt.getText()));
		toimipiste.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
	}
	
	private void luoToimipisteTiedot() throws SQLException {
		String sql = "SELECT ToimipisteID, Paikkakunta, Nimi, Osoite, Postinumero "+
					"FROM toimipisteet WHERE ToimipisteID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuToimipisteID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
        
		if (tulosjoukko.next()) {
	
			toimipisteIdTxt.setText(Integer.toString(tulosjoukko.getInt("ToimipisteID")));
			nimiTxt.setText(tulosjoukko.getString("Nimi"));
			osoiteTxt.setText(tulosjoukko.getString("Osoite"));
			paikkakuntaTxt.setText(tulosjoukko.getString("Paikkakunta"));
			postinumeroTxt.setText(Integer.toString(tulosjoukko.getInt("Postinumero")));
			
		}
	}
	
	public void muokkaaToimipiste() throws SQLException, Exception {
		boolean toimipiste_lisatty = true;
		toimipiste = null;
		
		try {
			toimipiste = Toimipiste.haeToimipiste(Integer.parseInt(toimipisteIdTxt.getText()), conn);
		}catch (SQLException e) {
			toimipiste_lisatty = false;
			e.printStackTrace();
		} catch (Exception e) {
			toimipiste_lisatty = false;
			e.printStackTrace();
		}

		
		toimipiste.setNimi(nimiTxt.getText());
		toimipiste.setOsoite(osoiteTxt.getText());
		toimipiste.setPaikkakunta(paikkakuntaTxt.getText());
		toimipiste.setPostinumero(Integer.parseInt(postinumeroTxt.getText()));
		toimipiste.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
		
		toimipiste.muokkaaToimipiste(conn);
	}


	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		muokkaaToimipisteShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			muokkaaToimipisteShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			muokkaaToimipisteShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3.3));
		}
		else if (screenSize.getWidth() == 1600) {
			muokkaaToimipisteShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		muokkaaToimipisteShl.setText("Muokkaa toimipisteta");
		muokkaaToimipisteShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(muokkaaToimipisteShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		muokkaaToimipisteLbl = new Label(composite, SWT.NONE);
		muokkaaToimipisteLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		muokkaaToimipisteLbl.setAlignment(SWT.CENTER);
		muokkaaToimipisteLbl.setText("Muokkaa alla toimipisteen tietoja");
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
		new Label(composite, SWT.NONE);
		
		
		// Ekan rivin label ja tekstikentta (toimipiste ja toimipisteid)
		toimipisteIdLbl = new Label(composite, SWT.NONE);
		toimipisteIdLbl.setText("Toimipiste ID");
		
		nimiLbl = new Label(composite, SWT.NONE);
		nimiLbl.setText("Nimi");
		
		label = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		toimipisteIdTxt = new Text(composite, SWT.BORDER);
		toimipisteIdTxt.setEditable(false);
		toimipisteIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toimipisteIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUSVARAUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			
			}
		});
		toimipisteIdTxt.setToolTipText("Toimipiste ID");
		
		nimiTxt = new Text(composite, SWT.BORDER);
		nimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		nimiTxt.setToolTipText("Nimi");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Toisen rivin label ja tekstikentta (asiakas id ja toimipiste id)
		osoiteLbl = new Label(composite, SWT.NONE);
		osoiteLbl.setText("Osoite");
		
		paikkakuntaLbl = new Label(composite, SWT.NONE);
		paikkakuntaLbl.setText("Paikkakunta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		osoiteTxt = new Text(composite, SWT.BORDER);
		osoiteTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		osoiteTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKASID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		osoiteTxt.setToolTipText("Osoite");
		
		paikkakuntaTxt = new Text(composite, SWT.BORDER);
		paikkakuntaTxt.setToolTipText("Paikkakunta");
		paikkakuntaTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		postinumeroLbl = new Label(composite, SWT.NONE);
		postinumeroLbl.setText("Postinumero");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
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
				muokkaaToimipisteShl.close();
				toimipisteEtusivuGui = new ToimipisteEtusivuGUI();
				try {
					toimipisteEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		});
		
		Button toimipisteMuokkaaBtn = new Button(composite, SWT.NONE);
		toimipisteMuokkaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toimipisteMuokkaaBtn.setText("Hyväksy");
		toimipisteMuokkaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					muokkaaToimipiste();
					muokkaaToimipisteShl.close();
					toimipisteEtusivuGui = getToimipisteEtusivuGui();
					toimipisteEtusivuGui.open();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(composite, SWT.NONE);


	}

	public ToimipisteEtusivuGUI getToimipisteEtusivuGui() {
		return toimipisteEtusivuGui;
	}

	public void setToimipisteEtusivuGui(ToimipisteEtusivuGUI toimipisteEtusivuGui) {
		this.toimipisteEtusivuGui = toimipisteEtusivuGui;
	}

	public MuokkaaToimipisteGUI getMuokkaaToimipisteGui() {
		return muokkaaToimipisteGui;
	}

	public void setMuokkaaToimipisteGui(MuokkaaToimipisteGUI muokkaaToimipisteGui) {
		this.muokkaaToimipisteGui = muokkaaToimipisteGui;
	}
	
	public int getValittuToimipisteID() {
		return valittuToimipisteID;
	}

	public void setValittuToimipisteID(int valittuToimipisteID) {
		this.valittuToimipisteID = valittuToimipisteID;
	}
	
}
