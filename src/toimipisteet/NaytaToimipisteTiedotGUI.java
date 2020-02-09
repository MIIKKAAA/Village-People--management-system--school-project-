package toimipisteet;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

public class NaytaToimipisteTiedotGUI {

	protected Shell naytaTiedotShl;
	private Text nimiTxt;
	private Text toimipisteIdTxt;
	private Text osoiteTxt;
	private Label naytaTiedotLbl;
	private Label nimiLbl;
	private Label toimipisteIdLbl;
	private Label osoiteLbl;
	private Label label;
	private Connection conn;
	private int valittuToimipisteID;
	private ToimipisteEtusivuGUI toimipisteEtusivuGui;
	private Label paikkakuntaLbl;
	private Text paikkakuntaTxt;
	private Label postinumeroLbl;
	private Text postinumeroTxt;
	private NaytaToimipisteTiedotGUI naytaToimipisteTiedotGui;


	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		yhdista();

		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(naytaTiedotShl);
		luoToimipisteTiedot();
		naytaTiedotShl.open();
		naytaTiedotShl.layout();
		while (!naytaTiedotShl.isDisposed()) {
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
		catch (SQLException e) { // tietokantaan ei saad yhteyttä
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
	
	private void luoToimipisteTiedot() throws SQLException {
		String sql = "SELECT ToimipisteID, Nimi, Osoite, Paikkakunta, Postinumero "+
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
	
	
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		naytaTiedotShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			naytaTiedotShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.9));
		}
		else if (screenSize.getWidth() > 1600){
			naytaTiedotShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3.2));
		}
		else if (screenSize.getWidth() == 1600) {
			naytaTiedotShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				naytaTiedotShl.setText("Toimipisteen tiedot");
		naytaTiedotShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(naytaTiedotShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		naytaTiedotLbl = new Label(composite, SWT.NONE);
		naytaTiedotLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		naytaTiedotLbl.setAlignment(SWT.CENTER);
		naytaTiedotLbl.setText("Alla näet toimipisteen tiedot");
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
		
		
		// Ekan rivin label ja tekstikentta (toimipiste ja toimipiste)
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
				System.out.println("TOIMII");
			}
		});
		toimipisteIdTxt.setToolTipText("");
		
		nimiTxt = new Text(composite, SWT.BORDER);
		nimiTxt.setEditable(false);
		nimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		nimiTxt.setToolTipText("");
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
		osoiteTxt.setEditable(false);
		osoiteTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		osoiteTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKASID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		osoiteTxt.setToolTipText("");
		
		paikkakuntaTxt = new Text(composite, SWT.BORDER);
		paikkakuntaTxt.setEditable(false);
		paikkakuntaTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		postinumeroLbl = new Label(composite, SWT.NONE);
		postinumeroLbl.setText("Postinumero");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		postinumeroTxt = new Text(composite, SWT.BORDER);
		postinumeroTxt.setEditable(false);
		postinumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Button toimipisteOkBtn = new Button(composite, SWT.NONE);
		toimipisteOkBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toimipisteOkBtn.setText("OK");
		toimipisteOkBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				naytaTiedotShl.close();
				toimipisteEtusivuGui = getToimipisteEtusivuGui();
				try {
					toimipisteEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(composite, SWT.NONE);
	}

	public int getValittuToimipisteID() {
		return valittuToimipisteID;
	}

	public void setValittuToimipisteID(int valittuToimipisteID) {
		this.valittuToimipisteID = valittuToimipisteID;
	}

	public ToimipisteEtusivuGUI getToimipisteEtusivuGui() {
		return toimipisteEtusivuGui;
	}

	public void setToimipisteEtusivuGui(ToimipisteEtusivuGUI toimipisteEtusivuGui) {
		this.toimipisteEtusivuGui = toimipisteEtusivuGui;
	}

	public NaytaToimipisteTiedotGUI getNaytaToimipisteTiedotGui() {
		return naytaToimipisteTiedotGui;
	}

	public void setNaytaToimipisteTiedotGui(NaytaToimipisteTiedotGUI naytaToimipisteTiedotGui) {
		this.naytaToimipisteTiedotGui = naytaToimipisteTiedotGui;
	}


	
	
}
