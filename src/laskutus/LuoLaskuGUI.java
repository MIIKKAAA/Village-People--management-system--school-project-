package laskutus;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ApuMetodeja.ApuMetodeja;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.VerifyEvent;

public class LuoLaskuGUI {

	protected Shell lisaaLaskuShl;
	private LaskutusEtusivuGUI laskutusEtusivuGui;
	private Composite composite_1;
	private Table laskutettavaMajoitusvarausTbl;
	private Composite composite_2;
	private Label laskutettavatPalveluvarauksetLbl;
	private Table laskutettavatPalveluvarauksetTbl;
	private TableColumn palveluvarausId;
	private Label laskuID;
	private Text laskuIdTxt;
	private Composite composite_4;
	private Label tilinumeroLbl;
	private Text tilinumeroTxt;
	private Label saajanNimiLbl;
	private Text saajanNimiTxt;
	private Label viitenumeroLbl;
	private Text viitenumeroTxt;
	private Label maksuMaaraLbl;
	private Text maksuMaaraTxt;
	private Label laskunTyyppiLbl;
	private Combo laskuTyyppiCombo;
	private Label laskuLuontiPvmLbl;
	private Label laskunErapvmLbl;
	private DateTime laskunLuontiPvmDateTime;
	private DateTime laskunEraPvmDateTime;
	private Button luoLaskuBtn;
	private Button laskuPeruutaBtn;
	private Label asiakasIdLbl;
	private Text asiakasIdTxt;
	private ValitseLaskutettavaAsiakasGUI valitseLaskutettavaAsiakasGui;
	private int valittuAsiakasID;
	private int valittuPalveluvarausID;
	private int valittuMajoitusvarausID;
	Connection conn;
	Lasku lasku = new Lasku();
	private TableColumn majoitusId;
	private TableColumn palveluId;
	private int hintaPalveluId;
	private int hintaMajoitusId;
	private ValittuVarausVaroitusDialog valittuVarausVaroitusDialog;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LuoLaskuGUI window = new LuoLaskuGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @throws SQLException 
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
		Display display = ApuMetodeja.centerWindow(lisaaLaskuShl);
		populateMajoitusvarausTbl(laskutettavaMajoitusvarausTbl);
		populatePalveluvarausTbl(laskutettavatPalveluvarauksetTbl);

		lisaaLaskuShl.open();
		lisaaLaskuShl.layout();
		while (!lisaaLaskuShl.isDisposed()) {
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
	/**
	 * Create contents of the window.
	 */
	
	public void populateMajoitusvarausTbl(Table majoitusvarausTbl) throws SQLException {
		String sql = "SELECT MajoitusvarausID, MajoitusID FROM majoitusvaraus WHERE AsiakasID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			lause.setInt(1, getValittuAsiakasID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) { // tulosjoukkoon odotetaan ainoastaan max. yhtä riviä
				System.out.println("...");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}

        java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
		try {
			TableItem item;
			
			while (tulosjoukko.next ()){
	            item = new TableItem(majoitusvarausTbl, SWT.NONE);
	            for (int i = 1; i <= columnsNumber; i++) {
	                item.setText(i - 1, tulosjoukko.getString(i));
			}
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	public void populatePalveluvarausTbl(Table palveluvarausTbl) throws SQLException {
			
		String sql = "SELECT PalveluvarausID, PalveluID FROM palvelun_varaus WHERE AsiakasID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;

		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuAsiakasID()); // asetetaan where ehtoon (?) arvo
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
		

        java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
		try {
			TableItem item;
			while (tulosjoukko.next ()){
	            item = new TableItem(palveluvarausTbl, SWT.NONE);
	            for (int i = 1; i <= columnsNumber; i++) {
	                item.setText(i - 1, tulosjoukko.getString(i));
			}
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public int laskePalveluHinta() throws SQLException {
		String sql = "SELECT Palvelun_hinta FROM palvelu WHERE PalveluID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
	
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getHintaPalveluId()); // asetetaan where ehtoon (?) arvo
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
		
	
	    java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
	    int columnsNumber = rsmd.getColumnCount();
	    int palveluHinta = 0;
		try {
			while (tulosjoukko.next ()){
	            for (int i = 1; i <= columnsNumber; i++) {
	               palveluHinta = Integer.parseInt(tulosjoukko.getString(i));
	       			System.out.println(palveluHinta);	
			}
		}
	
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
		return palveluHinta;
	}
	
	public int laskeMajoitusHinta() throws SQLException {
		String sql = "SELECT Hinta FROM majoitus WHERE MajoitusID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
	
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getHintaMajoitusId()); // asetetaan where ehtoon (?) arvo
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
		
	
	    java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
	    int columnsNumber = rsmd.getColumnCount();
	    int majoitusHinta = 0;
		try {
			while (tulosjoukko.next ()){
	            for (int i = 1; i <= columnsNumber; i++) {
	               	majoitusHinta = Integer.parseInt(tulosjoukko.getString(i));
	       			System.out.println(majoitusHinta);
			}
		}
	
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
		return majoitusHinta;
	}
	
	public int laskeYhteishinta() throws SQLException {
		int yhteishinta = laskePalveluHinta()+laskeMajoitusHinta();
		return yhteishinta;
	}
	
	public void lisaaLaskuTietokantaan() throws SQLException, Exception {
		
		boolean lasku_lisatty = true;
		lasku = null;
		
		try {
			lasku = Lasku.haeLasku(Integer.parseInt(laskuIdTxt.getText()), conn);
		}catch (SQLException e) {
			lasku_lisatty = false;
			e.printStackTrace();
		} catch (Exception e) {
			lasku_lisatty = false;
			e.printStackTrace();
		}

		lasku.setLasku_id(Integer.parseInt(laskuIdTxt.getText()));
		lasku.setAsiakas_id(Integer.parseInt(asiakasIdTxt.getText()));
		lasku.setErapvm(dateTimeToSqlDate(laskunEraPvmDateTime));
		lasku.setLaskunluontipvm(dateTimeToSqlDate(laskunLuontiPvmDateTime));
		
		TableItem valitutPalveluvarauksetTbl[] = laskutettavatPalveluvarauksetTbl.getSelection();
		if (valitutPalveluvarauksetTbl.length < 1) {
			lasku.setPalveluvaraus_id(0);
		}
		else {
			lasku.setPalveluvaraus_id(Integer.parseInt(valitutPalveluvarauksetTbl[0].getText(0)));	
		}
		
		TableItem valittuMajoitusvarausTbl[] = laskutettavaMajoitusvarausTbl.getSelection();
		if (valittuMajoitusvarausTbl.length <1) {
			lasku.setMajoitusvaraus_id(0);
		}
		else {
			lasku.setMajoitusvaraus_id(Integer.parseInt(valittuMajoitusvarausTbl[0].getText(0)));
		}
	
		lasku.setLaskun_tyyppi(laskuTyyppiCombo.getText());
		lasku.setTilinumero(Integer.parseInt(tilinumeroTxt.getText()));
		lasku.setSaajan_nimi(saajanNimiTxt.getText());
		lasku.setViitenumero(Integer.parseInt(viitenumeroTxt.getText()));

		lasku.setMaksun_maara(Integer.parseInt(maksuMaaraTxt.getText())); //TODO
		lasku.lisaaLasku(conn);
	}
	
	public String dateTimeToSqlDate(DateTime date) {
		
		int vuosi = date.getYear();
		int kuukausi = date.getMonth()+1;
		int paiva = date.getDay();
		
		String sqlDateTime = vuosi+"-"+kuukausi+"-"+paiva;
		
		return sqlDateTime;
	}

	protected void createContents() {
		lisaaLaskuShl = new Shell(SWT.MIN);
		lisaaLaskuShl.setSize(1005, 652);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			lisaaLaskuShl.setSize((int)(screenSize.getWidth() / 1.25), (int) (screenSize.getHeight() / 1.2));
		}
		else if (screenSize.getWidth() > 1600){
			lisaaLaskuShl.setSize((int)(screenSize.getWidth() / 2.08), (int) (screenSize.getHeight() / 2));
		}
		else if (screenSize.getWidth() == 1600) {
			lisaaLaskuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		lisaaLaskuShl.setText("Lisää lasku");
		GridLayout gl_lisaaLaskuShl = new GridLayout(3, false);
		lisaaLaskuShl.setLayout(gl_lisaaLaskuShl);
		
		composite_1 = new Composite(lisaaLaskuShl, SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_composite_1.widthHint = 288;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setLayout(new GridLayout(1, false));
		
		Label laskutettavaMajoitusvarausLbl = new Label(composite_1, SWT.NONE);
		laskutettavaMajoitusvarausLbl.setText("Valitse laskutettavat majoitusvaraukset");

		
		// Laskutettavat majoitukset taulu
		laskutettavaMajoitusvarausTbl = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.TOGGLE);
		laskutettavaMajoitusvarausTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				TableItem item =  laskutettavaMajoitusvarausTbl.getItem(new Point(e.x, e.y));

			    if (item == null) {
			    	laskutettavaMajoitusvarausTbl.deselectAll();
			    	setHintaMajoitusId(0);
			    }
				TableItem valittuMajoitusTbl[] = laskutettavaMajoitusvarausTbl.getSelection();
				if (valittuMajoitusTbl.length > 0) {
					setHintaMajoitusId(Integer.parseInt(valittuMajoitusTbl[0].getText(1)));
				}
				try {
					maksuMaaraTxt.setText(Integer.toString(laskeYhteishinta()));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});


		GridData gd_laskutettavaMajoitusvarausTbl = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_laskutettavaMajoitusvarausTbl.heightHint = 313;
		gd_laskutettavaMajoitusvarausTbl.widthHint = 245;
		laskutettavaMajoitusvarausTbl.setLayoutData(gd_laskutettavaMajoitusvarausTbl);
		laskutettavaMajoitusvarausTbl.setSize(529, 433);
		laskutettavaMajoitusvarausTbl.setHeaderVisible(true);
		laskutettavaMajoitusvarausTbl.setLinesVisible(true);
		
		
		
		// Taulun kentat
		TableColumn majoitusvarausId = new TableColumn(laskutettavaMajoitusvarausTbl, SWT.NONE);
		majoitusvarausId.setWidth(110);
		majoitusvarausId.setText("Majoitusvaraus ID");
		
		majoitusId = new TableColumn(laskutettavaMajoitusvarausTbl, SWT.NONE);
		majoitusId.setWidth(100);
		majoitusId.setText("Majoitus ID");
		
		composite_2 = new Composite(lisaaLaskuShl, SWT.H_SCROLL | SWT.V_SCROLL);
		composite_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		composite_2.setLayout(new GridLayout(1, false));
		
		laskutettavatPalveluvarauksetLbl = new Label(composite_2, SWT.NONE);
		laskutettavatPalveluvarauksetLbl.setText("Valitse laskutettavat palveluvaraukset");
		
		
		// Laskutettavat palvelut taulu
		laskutettavatPalveluvarauksetTbl = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		
		laskutettavatPalveluvarauksetTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

			    TableItem item =  laskutettavatPalveluvarauksetTbl.getItem(new Point(e.x, e.y));

			    if (item == null) {
			    	laskutettavatPalveluvarauksetTbl.deselectAll();
			    	setHintaPalveluId(0);
			    }
				TableItem valittuPalveluTbl[] = laskutettavatPalveluvarauksetTbl.getSelection();
				if (valittuPalveluTbl.length > 0) {
					setHintaPalveluId(Integer.parseInt(valittuPalveluTbl[0].getText(1)));
				}
				try {
					maksuMaaraTxt.setText(Integer.toString(laskePalveluHinta()+laskeMajoitusHinta()));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridData gd_laskutettavatPalveluvarauksetTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_laskutettavatPalveluvarauksetTbl.heightHint = 313;
		laskutettavatPalveluvarauksetTbl.setLayoutData(gd_laskutettavatPalveluvarauksetTbl);
		laskutettavatPalveluvarauksetTbl.setHeaderVisible(true);
		laskutettavatPalveluvarauksetTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		palveluvarausId = new TableColumn(laskutettavatPalveluvarauksetTbl, SWT.NONE);
		palveluvarausId.setWidth(100);
		palveluvarausId.setText("Palveluvaraus ID");
		
		palveluId = new TableColumn(laskutettavatPalveluvarauksetTbl, SWT.NONE);
		palveluId.setWidth(100);
		palveluId.setText("Palvelu ID");
		
		composite_4 = new Composite(lisaaLaskuShl, SWT.NONE);
		GridData gd_composite_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_4.widthHint = 330;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setLayout(new GridLayout(1, false));
		
		
		// LaskuID label ja tekstikentta
		laskuID = new Label(composite_4, SWT.NONE);
		laskuID.setSize(51, 15);
		laskuID.setText("Laskun ID");
		
		laskuIdTxt = new Text(composite_4, SWT.BORDER);
		laskuIdTxt.addVerifyListener(new VerifyListener() {
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
		laskuIdTxt.setTextLimit(11);
		laskuIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO LASKU ID KUUNTELIJA
			}
		});
		laskuIdTxt.setToolTipText("Laskun ID");
		GridData gd_laskuIdTxt = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_laskuIdTxt.widthHint = 182;
		laskuIdTxt.setLayoutData(gd_laskuIdTxt);
		laskuIdTxt.setSize(76, 21);
		
		asiakasIdLbl = new Label(composite_4, SWT.NONE);
		asiakasIdLbl.setText("Asiakkaan ID");
		
		asiakasIdTxt = new Text(composite_4, SWT.BORDER);
		asiakasIdTxt.setEditable(false);
		asiakasIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKAS ID KUUNTELIJA
			}
		});
		asiakasIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		asiakasIdTxt.setText(Integer.toString(getValittuAsiakasID()));
		
		
		// Tilinumero label ja tekstikentta
		tilinumeroLbl = new Label(composite_4, SWT.NONE);
		tilinumeroLbl.setText("Tilinumero");
		
		tilinumeroTxt = new Text(composite_4, SWT.BORDER);
		tilinumeroTxt.addVerifyListener(new VerifyListener() {
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
		tilinumeroTxt.setTextLimit(11);
		tilinumeroTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO TILINUMERO KUUNTELIJA
			}
		});
		tilinumeroTxt.setToolTipText("Tilinumero");
		tilinumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Saajan nimi label ja tekstikentta
		saajanNimiLbl = new Label(composite_4, SWT.NONE);
		saajanNimiLbl.setToolTipText("");
		
		saajanNimiLbl.setText("Saajan nimi");
		
		saajanNimiTxt = new Text(composite_4, SWT.BORDER);
		saajanNimiTxt.setTextLimit(50);
		saajanNimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO SAAJA NIMI KUUNTELIJA
			}
		});
		saajanNimiTxt.setToolTipText("Maksun saajan nimi");
		saajanNimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Viitenumero label ja tekstikentta
		viitenumeroLbl = new Label(composite_4, SWT.NONE);
		viitenumeroLbl.setText("Viitenumero");
		
		viitenumeroTxt = new Text(composite_4, SWT.BORDER);
		viitenumeroTxt.addVerifyListener(new VerifyListener() {
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
		viitenumeroTxt.setTextLimit(11);
		viitenumeroTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO VIITENUMERO KUUNTELIJA
			}
		});
		viitenumeroTxt.setToolTipText("Viitenumero");
		viitenumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Maksun maara label ja tekstikentta
		maksuMaaraLbl = new Label(composite_4, SWT.NONE);
		maksuMaaraLbl.setText("Maksun määrä");
		
		maksuMaaraTxt = new Text(composite_4, SWT.BORDER);
		maksuMaaraTxt.addVerifyListener(new VerifyListener() {
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
		maksuMaaraTxt.setTextLimit(11);
		maksuMaaraTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAKSUMAARA KUUNTELIJA
			}
		});
		maksuMaaraTxt.setToolTipText("Maksun määrä");
		maksuMaaraTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Laskun luontipvm lbl ja aikavalinta
		laskuLuontiPvmLbl = new Label(composite_4, SWT.NONE);
		laskuLuontiPvmLbl.setText("Laskun luontipäivämäärä");
		
		laskunLuontiPvmDateTime = new DateTime(composite_4, SWT.BORDER);
		laskunLuontiPvmDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO LASKUN LUONTIPVM KUUNTELIJA
	
			}
		});
		
		
		// Laskun erapvm label ja aikavalinta
		laskunErapvmLbl = new Label(composite_4, SWT.NONE);
		laskunErapvmLbl.setText("Laskun eräpäivämäärä");
		
		laskunEraPvmDateTime = new DateTime(composite_4, SWT.BORDER);
		laskunEraPvmDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO LASKUN ERAPVM KUUNTELIJA
			}
		});
		
		
		// Laskutyyppi label ja pudotusvalikko
		laskunTyyppiLbl = new Label(composite_4, SWT.NONE);
		laskunTyyppiLbl.setText("Valitse laskun tyyppi");
		
		laskuTyyppiCombo = new Combo(composite_4, SWT.NONE);
		laskuTyyppiCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO LASKU TYYPPI KUUNTELIJA

			}
		});
		laskuTyyppiCombo.setItems(new String[] {"Paperilasku", "Sähköpostilasku"});
		
		// Gridin asettelua
		new Label(lisaaLaskuShl, SWT.NONE);
		new Label(lisaaLaskuShl, SWT.NONE);
		new Label(lisaaLaskuShl, SWT.NONE);
		
		
		// Alanapit
		laskuPeruutaBtn = new Button(lisaaLaskuShl, SWT.NONE);
		laskuPeruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO PERUUTA NAPPI KUUNTELIJA
				lisaaLaskuShl.close();
				valitseLaskutettavaAsiakasGui = new ValitseLaskutettavaAsiakasGUI();
				try {
					valitseLaskutettavaAsiakasGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
			}
		});
		laskuPeruutaBtn.setText("Peruuta");
		new Label(lisaaLaskuShl, SWT.NONE);
		
		luoLaskuBtn = new Button(lisaaLaskuShl, SWT.NONE);
		luoLaskuBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// LUO LASKU KUUNTELIJA NAPPI
				TableItem valitutPalveluvarauksetTbl[] = laskutettavatPalveluvarauksetTbl.getSelection();	
				TableItem valittuMajoitusvarausTbl[] = laskutettavaMajoitusvarausTbl.getSelection();
				if (valitutPalveluvarauksetTbl.length <1 && valittuMajoitusvarausTbl.length <1) {
					valittuVarausVaroitusDialog = new ValittuVarausVaroitusDialog();
					valittuVarausVaroitusDialog.open();
				}
				else {
					try {
						lisaaLaskuTietokantaan();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					lisaaLaskuShl.close();
					laskutusEtusivuGui = new LaskutusEtusivuGUI();
					try {
						laskutusEtusivuGui.open();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		luoLaskuBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		luoLaskuBtn.setText("Hyväksy");

	}

	public int getValittuPalveluvarausID() {
		return valittuPalveluvarausID;
	}

	public void setValittuPalveluvarausID(int valittuPalveluvarausID) {
		this.valittuPalveluvarausID = valittuPalveluvarausID;
	}

	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}

	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
	}
		
	public int getValittuAsiakasID() {
		return valittuAsiakasID;
	}
	
	public void setValittuAsiakasID(int valittuAsiakasID) {
		this.valittuAsiakasID = valittuAsiakasID;
	}
	
	public int getHintaPalveluId() {
		return hintaPalveluId;
	}
	
	public void setHintaPalveluId(int hintaPalveluId) {
		this.hintaPalveluId = hintaPalveluId;
	}
	
	public int getHintaMajoitusId() {
		return hintaMajoitusId;
	}
	
	public void setHintaMajoitusId(int hintaMajoitusId) {
		this.hintaMajoitusId = hintaMajoitusId;
	}
	
	
}
