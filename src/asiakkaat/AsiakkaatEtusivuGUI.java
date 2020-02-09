package asiakkaat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.TableItem;

import ApuMetodeja.ApuMetodeja;
import majoitus_varaus.MajoitusvarausPoistoVarmistusDialog;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

import java.sql.*;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionListener;
import java.util.function.Consumer;

public class AsiakkaatEtusivuGUI {
	// Lisatty vahvistus ikkuna ja muita pienia juttuja, esim. olioita kuljetetaan luokkien välillä jotta data säilyy ikkunoiden välillä myös -Miikka
	// Muihin luokkiin lisatty myos tarkistuksia (tosi rumasti kiireella tehtyja), heitteli NullPointerExceptionia aika paljon
	
	protected Shell asiakkaatEtusivuShl;
	private Table etusivuAsiakasTbl;
	private Composite composite_1;
	private Button lisaaAsiakasBtn;
	private Button muokkaaAsiakasBtn;
	private Button poistaAsiakasBtn;
	private TableColumn asiakasID;
	private TableColumn asiakasNimi;
	private LisaaAsiakasGUI lisaaAsiakasGui;
	private MuokkaaAsiakasGUI muokkaaAsiakasGui;
	private NaytaAsiakasTiedotGUI naytaAsiakasTiedotGui;
	private Label listaMainLbl;
	private Button naytaTiedotBtn;
	private Connection conn;
	private ResultSet asiakkaat;
	private TableItem asiakas;
	private Button btnNewButton;
	private AsiakkaatEtusivuGUI asiakkaatEtusivuGui;
	private int valittuAsiakasId;
	private AsiakasPoistoVarmistusDialog asiakasPoistoVarmistusDialog;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AsiakkaatEtusivuGUI window = new AsiakkaatEtusivuGUI();
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
		haeTiedot();
		Display display = ApuMetodeja.centerWindow(asiakkaatEtusivuShl);
		
		btnNewButton = new Button(asiakkaatEtusivuShl, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				asiakkaatEtusivuShl.close();
			}
		});
		btnNewButton.setText("Takaisin");
		new Label(asiakkaatEtusivuShl, SWT.NONE);

		asiakkaatEtusivuShl.open();
		asiakkaatEtusivuShl.layout();
		while (!asiakkaatEtusivuShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		asiakkaatEtusivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		asiakkaatEtusivuShl.setSize(644, 566);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			asiakkaatEtusivuShl.setSize((int)(screenSize.getWidth() / 2.7), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			asiakkaatEtusivuShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 2.4));
		}
		else if (screenSize.getWidth() == 1600) {
			asiakkaatEtusivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		asiakkaatEtusivuShl.setText("Asiakkaiden hallinta");
		asiakkaatEtusivuShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(asiakkaatEtusivuShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		listaMainLbl = new Label(composite, SWT.NONE);
		listaMainLbl.setText("Alla on lista luoduista asiakkaista");
		
		// Taulukko
		etusivuAsiakasTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_etusivuAsiakasTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_etusivuAsiakasTbl.heightHint = 310;
		etusivuAsiakasTbl.setLayoutData(gd_etusivuAsiakasTbl);
		etusivuAsiakasTbl.setHeaderVisible(true);
		etusivuAsiakasTbl.setLinesVisible(true);
		
		// AsiakasID kentta taulukossa
		asiakasID = new TableColumn(etusivuAsiakasTbl, SWT.NONE);
		asiakasID.setWidth(137);
		asiakasID.setText("Asiakas ID");
		
		// Asiakas nimi kentta taulukossa
		asiakasNimi = new TableColumn(etusivuAsiakasTbl, SWT.NONE);
		asiakasNimi.setWidth(128);
		asiakasNimi.setText("Nimi");
		
		composite_1 = new Composite(asiakkaatEtusivuShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		
		// Lisaa asiakas nappi
		lisaaAsiakasBtn = new Button(composite_1, SWT.NONE);
		GridData gd_lisaaAsiakasBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lisaaAsiakasBtn.widthHint = 110;
		lisaaAsiakasBtn.setLayoutData(gd_lisaaAsiakasBtn);
		lisaaAsiakasBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				asiakkaatEtusivuShl.close();
				lisaaAsiakasGui = new LisaaAsiakasGUI();
				lisaaAsiakasGui.setAsiakkaatEtusivuGui(asiakkaatEtusivuGui);
				lisaaAsiakasGui.open();
	
			}
		});
		lisaaAsiakasBtn.setText("Lisää uusi asiakas");
		
		
		// Muokkaa asiakas nappi
		muokkaaAsiakasBtn = new Button(composite_1, SWT.NONE);
		GridData gd_muokkaaAsiakasBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_muokkaaAsiakasBtn.widthHint = 110;
		muokkaaAsiakasBtn.setLayoutData(gd_muokkaaAsiakasBtn);
		muokkaaAsiakasBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem valittuAsiakasTbl[] = etusivuAsiakasTbl.getSelection();
				muokkaaAsiakasGui = new MuokkaaAsiakasGUI();
				muokkaaAsiakasGui.setAsiakkaatEtusivuGui(asiakkaatEtusivuGui);

				if (valittuAsiakasTbl.length < 1) {
					System.out.println("Valitse asiakas");
				}
				else {
					String asiakas = valittuAsiakasTbl[0].getText();
					asiakkaatEtusivuShl.close();
					muokkaaAsiakasGui.open(asiakas);

				}
			}
		});
		muokkaaAsiakasBtn.setText("Muokkaa asiakasta");
		
		
		// Nayta tiedot nappi
		naytaTiedotBtn = new Button(composite_1, SWT.NONE);
		naytaTiedotBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem valittuAsiakasTbl[] = etusivuAsiakasTbl.getSelection();
				naytaAsiakasTiedotGui = new NaytaAsiakasTiedotGUI();
				naytaAsiakasTiedotGui.setAsiakkaatEtusivuGui(asiakkaatEtusivuGui);

				if (valittuAsiakasTbl.length < 1) {
					System.out.println("Valitse asiakas");
				}
				else {
					String asiakas = valittuAsiakasTbl[0].getText();
					asiakkaatEtusivuShl.close();
					naytaAsiakasTiedotGui.open(asiakas);
				}
			}
		});
		GridData gd_naytaTiedotBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_naytaTiedotBtn.widthHint = 110;
		naytaTiedotBtn.setLayoutData(gd_naytaTiedotBtn);
		naytaTiedotBtn.setText("Näytä tiedot");
		new Label(composite_1, SWT.NONE);
		
		
		// Poista asiakas nappi
		poistaAsiakasBtn = new Button(composite_1, SWT.NONE);
		GridData gd_poistaAsiakasBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_poistaAsiakasBtn.widthHint = 110;
		poistaAsiakasBtn.setLayoutData(gd_poistaAsiakasBtn);
		poistaAsiakasBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem valittuAsiakasTbl[] = etusivuAsiakasTbl.getSelection();

				if (valittuAsiakasTbl.length < 1) {
					System.out.println("Valitse asiakas");
				}
				else {
					setValittuAsiakasId(Integer.parseInt(valittuAsiakasTbl[0].getText()));
					asiakasPoistoVarmistusDialog = new AsiakasPoistoVarmistusDialog();
					asiakasPoistoVarmistusDialog.setAsiakkaatEtusivuGui(getAsiakkaatEtusivuGui());
					try {
						asiakasPoistoVarmistusDialog.open();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
//					poistaAsiakas(Integer.parseInt(valittuAsiakasTbl[0].getText()));
//					etusivuAsiakasTbl.removeAll();
//					haeTiedot();
					
				}
			}
		});
		poistaAsiakasBtn.setText("Poista asiakas");

	}
	/*
	 * Haetaan luettelo asiakkaista tietokannasta
	 */
	public void haeTiedot() {
		asiakkaat = null;
		String sql = "SELECT AsiakasID, Sukunimi, Etunimi FROM asiakas";
		// avataan tietokanta
		try {
			yhdista();
			asiakkaat = conn.prepareStatement(sql).executeQuery();
			while (asiakkaat.next() == true) {
				asiakas = new TableItem(etusivuAsiakasTbl, SWT.NONE);
				asiakas.setText(new String[] {Integer.toString(asiakkaat.getInt(1)), asiakkaat.getString(2) + " " + asiakkaat.getString(3)});
			}
			System.out.println("Haettiin asiakkaat tietokannasta");
		 } catch (SQLException se) {
            // SQL virheet
			se.printStackTrace();
			//JOptionPane.showMessageDialog(null, "Tapahtui virhe tietokantaa avattaessa.", "Tietokantavirhe", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // JDBC virheet
        	e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Tapahtui JDBCvirhe tietokantaa avattaessa.", "Tietokantavirhe", JOptionPane.ERROR_MESSAGE);
		}
	}
	/*
	 * Poistetaan asiakas tietokannasta
	 */
	public void poistaAsiakas(int id) {
		// Muokattu (poisto ei onnistunut vierasavainten takia tietyissa tilanteissa) - Miikka
		String sql = "UPDATE laskujen_hallinta_ja_seuranta SET AsiakasID = NULL WHERE AsiakasID = ?";
		PreparedStatement lause = null;
		try {
			yhdista();
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause.setInt( 1, id);
			// suorita sql-lause
			int lkm = lause.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Asiakkaan poistaminen ei onnistu");
			}
		} catch (SQLException se) {
			// SQL virheet
			se.printStackTrace();
			System.out.println("SQL-virhe");
		} catch (Exception e) {

			// JDBC virheet
        	System.out.println("JDBC-virhe");
		}
		
		sql = "UPDATE majoitusvaraus SET AsiakasID = NULL WHERE AsiakasID = ?";
		PreparedStatement lause1 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause1 = conn.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause1.setInt( 1, id);
			// suorita sql-lause
			int lkm = lause1.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Asiakkaan poistaminen ei onnistu");
			}
		} catch (SQLException se) {
			// SQL virheet
			se.printStackTrace();
			System.out.println("SQL-virhe");
		} catch (Exception e) {

			// JDBC virheet
        	System.out.println("JDBC-virhe");
		}
		
		sql = "UPDATE palvelun_varaus SET AsiakasID = NULL WHERE AsiakasID = ?";
		PreparedStatement lause2 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause2 = conn.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause2.setInt( 1, id);
			// suorita sql-lause
			int lkm = lause2.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Asiakkaan poistaminen ei onnistu");
			}
		} catch (SQLException se) {
			// SQL virheet
			se.printStackTrace();
			System.out.println("SQL-virhe");
		} catch (Exception e) {

			// JDBC virheet
        	System.out.println("JDBC-virhe");
		}
		
		sql = "DELETE FROM asiakas WHERE AsiakasID = ?";
		PreparedStatement lause3 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause3 = conn.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause3.setInt( 1, id);
			// suorita sql-lause
			int lkm = lause3.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Asiakkaan poistaminen ei onnistu");
			}
		} catch (SQLException se) {
			// SQL virheet
			se.printStackTrace();
			System.out.println("SQL-virhe");
		} catch (Exception e) {

			// JDBC virheet
        	System.out.println("JDBC-virhe");
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

	public Table getEtusivuAsiakasTbl() {
		return etusivuAsiakasTbl;
	}

	public void setEtusivuAsiakasTbl(Table etusivuAsiakasTbl) {
		this.etusivuAsiakasTbl = etusivuAsiakasTbl;
	}

	public int getValittuAsiakasId() {
		return valittuAsiakasId;
	}

	public void setValittuAsiakasId(int valittuAsiakasId) {
		this.valittuAsiakasId = valittuAsiakasId;
	}

}
