package laskutus;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

import Aloitussivu.AloitussivuGUI;
import ApuMetodeja.ApuMetodeja;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class LaskutusEtusivuGUI {

	protected Shell laskutusEtusivuShl;
	private Table etusivuLaskutusTbl;
	private Composite composite_1;
	private Button luoLaskuBtn;
	private Button muokkaaLaskuBtn;
	private Button poistaLaskuBtn;
	private TableColumn laskuId;
	private TableColumn asiakasId;
	private TableColumn majoitusvarausId;
	private MuokkaaLaskuGUI muokkaaLaskuGui;
	private Label laskuListaLbl;
	private TableColumn palveluvarausId;
	private Button naytaTiedotBtn;
	private ValitseLaskutettavaAsiakasGUI valitseLaskutettavaAsiakasGui;
	private int valittuLaskuID;
	private int valittuMajoitusvarausID;
	private int valittuPalveluvarausID;
	private int valittuAsiakasID;
	private Connection conn;
	private LaskuPoistoVarmistusDialog laskuPoistoVarmistusDialog;
	private Button paivitaTauluBtn;
	private Button takaisinBtn;
	private AloitussivuGUI aloitusSivuGui;
	private LaskutusEtusivuGUI laskutusEtusivuGui;
	private LaskuPohjaEmail laskuPohjaEmail;
	private LaskuPohjaPaperi laskuPohjaPaperi;
	private String laskunTyyppi;
	private TableColumn luontipvm;
	private TableColumn tyyppi;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LaskutusEtusivuGUI window = new LaskutusEtusivuGUI();
			window.setLaskutusEtusivuGui(window);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}
	
	/**
	 * Open the window.
	 * @throws Exception 
	 */
	public void open() throws SQLException {
		try {
			yhdista();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(laskutusEtusivuShl);
		populateEtusivuTbl(etusivuLaskutusTbl);
		
		laskutusEtusivuShl.open();
		laskutusEtusivuShl.layout();
		while (!laskutusEtusivuShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	/*
	Avataan tietokantayhteys
	*/
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

	/**
	 * Create contents of the window.
	 */
	public void populateEtusivuTbl(Table etusivuLaskutusTbl) throws SQLException {
		
		// SQL-kysely ja tulosjoukon valmistelu
		String sql = "SELECT LaskuID, AsiakasID, PalveluvarausID, MajoitusvarausID, Laskunluontipvm, Laskun_tyyppi FROM laskujen_hallinta_ja_seuranta";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("Tyhjä??");
			}
		} catch (SQLException se) {
            // SQL virheet, TODO
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet, TODO
            e.printStackTrace();
		}
		
		// sarakkeiden maaran haku alempaa taulun tayttamista varten iteroinnilla
        java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
        // iteroidaan tulosjoukkoa lapi niin kauan kun riittaa, lisataan uudet TableItem:t ja niiden sisalto
		try {
			TableItem item;
			while (tulosjoukko.next ()){
	            item = new TableItem(etusivuLaskutusTbl, SWT.NONE);
	            for (int i = 1; i <= columnsNumber; i++) {
	            	if (tulosjoukko.getString(i) == null) {
		                item.setText(i - 1, "");
	            	}
	            	else {
	            		item.setText(i - 1, tulosjoukko.getString(i));

	            	}
			}
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void haeLaskuTyyppi() throws SQLException {
		// SQL-kysely ja tulosjoukon valmistelu
		String sql = "SELECT Laskun_tyyppi FROM laskujen_hallinta_ja_seuranta WHERE LaskuID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			lause.setInt(1, getValittuLaskuID());
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("Tyhjä??");
			}
		} catch (SQLException se) {
            // SQL virheet, TODO
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet, TODO
            e.printStackTrace();
		}
		
		// sarakkeiden maaran haku alempaa taulun tayttamista varten iteroinnilla
        java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
        // iteroidaan tulosjoukkoa lapi niin kauan kun riittaa, lisataan uudet TableItem:t ja niiden sisalto
		try {
			while (tulosjoukko.next ()){
	            setLaskunTyyppi(tulosjoukko.getString("Laskun_tyyppi"));
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	protected void createContents() {
		laskutusEtusivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		laskutusEtusivuShl.setSize(824, 450);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			laskutusEtusivuShl.setSize((int)(screenSize.getWidth() / 2.1), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			laskutusEtusivuShl.setSize((int)(screenSize.getWidth() / 2.5), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			laskutusEtusivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		laskutusEtusivuShl.setText("Laskutus");
		laskutusEtusivuShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(laskutusEtusivuShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		
		laskuListaLbl = new Label(composite, SWT.NONE);
		laskuListaLbl.setText("Alla on lista luoduista laskuista");
		
		// Laskutus taulukko
		etusivuLaskutusTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.TOGGLE);

		etusivuLaskutusTbl.setLinesVisible(true);
		etusivuLaskutusTbl.setHeaderVisible(true);
		GridData gd_etusivuLaskutusTbl = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_etusivuLaskutusTbl.heightHint = 311;
		etusivuLaskutusTbl.setLayoutData(gd_etusivuLaskutusTbl);
		
		// Taulun kentat
		laskuId = new TableColumn(etusivuLaskutusTbl, SWT.NONE);
		laskuId.setWidth(68);
		laskuId.setText("Lasku ID");
		
		asiakasId = new TableColumn(etusivuLaskutusTbl, SWT.NONE);
		asiakasId.setWidth(75);
		asiakasId.setText("Asiakas ID");
		
		palveluvarausId = new TableColumn(etusivuLaskutusTbl, SWT.NONE);
		palveluvarausId.setWidth(99);
		palveluvarausId.setText("Palveluvaraus ID");
		
		majoitusvarausId = new TableColumn(etusivuLaskutusTbl, SWT.NONE);
		majoitusvarausId.setWidth(114);
		majoitusvarausId.setText("Majoitusvaraus ID");
		
		luontipvm = new TableColumn(etusivuLaskutusTbl, SWT.NONE);
		luontipvm.setWidth(114);
		luontipvm.setText("Luontipäivämäärä");
		
		tyyppi = new TableColumn(etusivuLaskutusTbl, SWT.NONE);
		tyyppi.setWidth(100);
		tyyppi.setText("Tyyppi");
		
		
		takaisinBtn = new Button(composite, SWT.NONE);
		takaisinBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				laskutusEtusivuShl.close();
			}
		});
		takaisinBtn.setText("Takaisin");
		
		composite_1 = new Composite(laskutusEtusivuShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		
		
		
		// Luo uusi lasku nappi
		luoLaskuBtn = new Button(composite_1, SWT.NONE);
		GridData gd_luoLaskuBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_luoLaskuBtn.widthHint = 100;
		luoLaskuBtn.setLayoutData(gd_luoLaskuBtn);
		luoLaskuBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO LISAA LASKU NAPPI KUUNTELIJA
				valitseLaskutettavaAsiakasGui = new ValitseLaskutettavaAsiakasGUI();
				try {
					laskutusEtusivuShl.close();
					valitseLaskutettavaAsiakasGui.open();
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		luoLaskuBtn.setText("Luo uusi lasku");
		
		
		// Muokkaa laskua nappi
		muokkaaLaskuBtn = new Button(composite_1, SWT.NONE);
		GridData gd_muokkaaLaskuBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_muokkaaLaskuBtn.widthHint = 100;
		muokkaaLaskuBtn.setLayoutData(gd_muokkaaLaskuBtn);
		muokkaaLaskuBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				muokkaaLaskuGui = new MuokkaaLaskuGUI();
				TableItem valittuLaskuTbl[] = etusivuLaskutusTbl.getSelection();
				if (valittuLaskuTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					muokkaaLaskuGui.setValittuLaskuID(Integer.parseInt(valittuLaskuTbl[0].getText(0)));
					muokkaaLaskuGui.setValittuAsiakasID(Integer.parseInt(valittuLaskuTbl[0].getText(1)));
					try {
						laskutusEtusivuShl.close();
						muokkaaLaskuGui.open();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
	
		});
		muokkaaLaskuBtn.setText("Muokkaa laskua");
		
		naytaTiedotBtn = new Button(composite_1, SWT.NONE);
		naytaTiedotBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				laskuPohjaEmail = new LaskuPohjaEmail();
				TableItem valittuLaskuTbl[] = etusivuLaskutusTbl.getSelection();
				setValittuLaskuID(Integer.parseInt(valittuLaskuTbl[0].getText(0)));
				try {
					haeLaskuTyyppi();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				if (valittuLaskuTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					laskuPohjaEmail.setValittuLaskuID(Integer.parseInt(valittuLaskuTbl[0].getText(0)));
					laskuPohjaEmail.setValittuAsiakasID(Integer.parseInt(valittuLaskuTbl[0].getText(1)));
					laskuPohjaEmail.setValittuMajoitusvarausID(Integer.parseInt(valittuLaskuTbl[0].getText(3)));
					laskuPohjaEmail.setValittuPalveluvarausID(Integer.parseInt(valittuLaskuTbl[0].getText(2)));
					
					try {
						laskuPohjaEmail.open();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		GridData gd_naytaTiedotBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_naytaTiedotBtn.widthHint = 100;
		naytaTiedotBtn.setLayoutData(gd_naytaTiedotBtn);
		naytaTiedotBtn.setText("Luo liite/tulosta");
		new Label(composite_1, SWT.NONE);
		
		
		// Poista lasku nappi
		poistaLaskuBtn = new Button(composite_1, SWT.NONE);
		GridData gd_poistaLaskuBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_poistaLaskuBtn.widthHint = 100;
		poistaLaskuBtn.setLayoutData(gd_poistaLaskuBtn);
		poistaLaskuBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				TableItem valittuLaskuTbl[] = etusivuLaskutusTbl.getSelection();
				if (valittuLaskuTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					setValittuLaskuID(Integer.parseInt(valittuLaskuTbl[0].getText(0)));
					laskuPoistoVarmistusDialog = new LaskuPoistoVarmistusDialog();
					laskuPoistoVarmistusDialog.setLaskutusEtusivuGui(getLaskutusEtusivuGui());
					try {
						laskuPoistoVarmistusDialog.open();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
			}
		});
		poistaLaskuBtn.setText("Poista lasku");
		new Label(composite_1, SWT.NONE);
		

	}

	public int getValittuLaskuID() {
		return valittuLaskuID;
	}

	public void setValittuLaskuID(int valittuLaskuID) {
		this.valittuLaskuID = valittuLaskuID;
	}
	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}
	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
	}
	public int getValittuPalveluvarausID() {
		return valittuPalveluvarausID;
	}

	public void setValittuPalveluvarausID(int valittuPalveluvarausID) {
		this.valittuPalveluvarausID = valittuPalveluvarausID;
	}
	public int getValittuAsiakasID() {
		return valittuAsiakasID;
	}
	public void setValittuAsiakasID(int valittuAsiakasID) {
		this.valittuAsiakasID = valittuAsiakasID;
	}
	public LaskutusEtusivuGUI getLaskutusEtusivuGui() {
		return laskutusEtusivuGui;
	}
	public void setLaskutusEtusivuGui(LaskutusEtusivuGUI laskutusEtusivuGui) {
		this.laskutusEtusivuGui = laskutusEtusivuGui;
	}
	public Shell getLaskutusEtusivuShl() {
		return laskutusEtusivuShl;
	}
	public void setLaskutusEtusivuShl(Shell laskutusEtusivuShl) {
		this.laskutusEtusivuShl = laskutusEtusivuShl;
	}
	public Table getEtusivuLaskutusTbl() {
		return etusivuLaskutusTbl;
	}
	public void setEtusivuLaskutusTbl(Table etusivuLaskutusTbl) {
		this.etusivuLaskutusTbl = etusivuLaskutusTbl;
	}

	public String getLaskunTyyppi() {
		return laskunTyyppi;
	}

	public void setLaskunTyyppi(String laskunTyyppi) {
		this.laskunTyyppi = laskunTyyppi;
	}

}
