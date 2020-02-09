package palvelu_varaus;

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
import org.eclipse.swt.widgets.DateTime;
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

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;;

public class ValitsePalveluGUI_muokkaa {

	protected Shell valitsePalveluShl;
	private Table etusivuPalveluTbl;
	private TableColumn palveluId;
	private TableColumn nimi;

	private Label valitsePalveluMainLbl;
	private TableColumn hinta;
	private TableColumn toimipisteId;
	private MuokkaaPalveluvarausGUI muokkaaPalveluvarausGui;
	//private int valittuPalveluID;
	private Connection conn;
	private PalveluvarausEtusivuGUI majoitusvarausEtusivuGui;
	private int palveluvarausIdTalteen;
	private int palveluIdTalteen;
	private int asiakasIdTalteen;
	private int aloituspvmPaivaTalteen;
	private int aloituspvmKuukausiTalteen;
	private int aloituspvmVuosiTalteen;
	private int lopetuspvmPaivaTalteen;
	private int lopetuspvmKuukausiTalteen;
	private int lopetuspvmVuosiTalteen;
	private Composite composite_1;
	private Button peruutaBtn;
	private Button hyvaksyBtn;

	
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
	public void populatePalveluTbl(Table majoitusTbl) throws SQLException {
		
		String sql = "SELECT PalveluID, Palvelun_nimi, Palvelun_hinta, ToimipisteID FROM palvelu";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			//lause.setInt( 1, 12001); // asetetaan where ehtoon (?) arvo
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
	            item = new TableItem(majoitusTbl, SWT.NONE);
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
		Display display = ApuMetodeja.centerWindow(valitsePalveluShl);

		valitsePalveluShl.open();
		valitsePalveluShl.layout();
		while (!valitsePalveluShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * Create contents of the window.
	 * @throws SQLException 
	 * @wbp.parser.entryPoint
	 */

	protected void createContents() throws SQLException {
		valitsePalveluShl = new Shell(SWT.CLOSE | SWT.MIN);
		valitsePalveluShl.setSize(651, 487);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			valitsePalveluShl.setSize((int)(screenSize.getWidth() / 2.7), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			valitsePalveluShl.setSize((int)(screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			valitsePalveluShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		valitsePalveluShl.setText("Valitse majoitus");
		valitsePalveluShl.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(valitsePalveluShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
		gd_composite.heightHint = 419;
		composite.setLayoutData(gd_composite);
		
		valitsePalveluMainLbl = new Label(composite, SWT.NONE);
		valitsePalveluMainLbl.setText("Alla on lista majoituksista");
		
		// Taulukko
		etusivuPalveluTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.TOGGLE);
		GridData gd_etusivuPalveluTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_etusivuPalveluTbl.heightHint = 367;
		etusivuPalveluTbl.setLayoutData(gd_etusivuPalveluTbl);
		etusivuPalveluTbl.setHeaderVisible(true);
		etusivuPalveluTbl.setLinesVisible(true);
		
		// PalveluID kentta taulukossa
		palveluId = new TableColumn(etusivuPalveluTbl, SWT.NONE);
		palveluId.setWidth(90);
		palveluId.setText("Palvelu ID");
		
		// Palvelu nimi kentta taulukossa
		nimi = new TableColumn(etusivuPalveluTbl, SWT.NONE);
		nimi.setWidth(129);
		nimi.setText("Nimi");
		
		hinta = new TableColumn(etusivuPalveluTbl, SWT.NONE);
		hinta.setWidth(100);
		hinta.setText("Hinta");
		
		toimipisteId = new TableColumn(etusivuPalveluTbl, SWT.NONE);
		toimipisteId.setWidth(100);
		toimipisteId.setText("Toimipiste ID");
		
		populatePalveluTbl(etusivuPalveluTbl);
		
		composite_1 = new Composite(valitsePalveluShl, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(2, false));
		
		peruutaBtn = new Button(composite_1, SWT.NONE);
		peruutaBtn.setBounds(0, 0, 75, 25);
		peruutaBtn.setText("Peruuta");
		
		hyvaksyBtn = new Button(composite_1, SWT.NONE);
		hyvaksyBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		hyvaksyBtn.setText("Hyväksy");
		
		peruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				valitsePalveluShl.close();
				majoitusvarausEtusivuGui = new PalveluvarausEtusivuGUI();
				try {
					majoitusvarausEtusivuGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
				
			}
		});
		
		hyvaksyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Haetaan valittu TableItem halutusta taulusta
				TableItem valittuAsiakasTbl[] = etusivuPalveluTbl.getSelection();
				
				muokkaaPalveluvarausGui = getMuokkaaPalveluvarausGui();
				muokkaaPalveluvarausGui.setAsiakasIdTalteen(asiakasIdTalteen);
				muokkaaPalveluvarausGui.setPalveluvarausIdTalteen(palveluvarausIdTalteen);
				muokkaaPalveluvarausGui.setPalveluIdTalteen(palveluIdTalteen);
				muokkaaPalveluvarausGui.setAloituspvmKuukausiTalteen(aloituspvmKuukausiTalteen);
				muokkaaPalveluvarausGui.setAloituspvmPaivaTalteen(aloituspvmPaivaTalteen);
				muokkaaPalveluvarausGui.setAloituspvmVuosiTalteen(aloituspvmVuosiTalteen);
				muokkaaPalveluvarausGui.setLopetuspvmKuukausiTalteen(lopetuspvmKuukausiTalteen);
				muokkaaPalveluvarausGui.setLopetuspvmPaivaTalteen(lopetuspvmPaivaTalteen);
				muokkaaPalveluvarausGui.setLopetuspvmVuosiTalteen(lopetuspvmVuosiTalteen);
				// NullPointerExceptionin valttamiseksi kun taulusta ei valittu mitaan
				if (valittuAsiakasTbl.length < 1) {
					System.out.println("Valitse yksi");
				}
				else {
					// Tallennetaan valittu AsiakasID:n arvo LuoLaskuGUI-luokan setterilla myohempaa kayttoa varten
					muokkaaPalveluvarausGui.setValittuPalveluId(Integer.parseInt(valittuAsiakasTbl[0].getText(0)));
					valitsePalveluShl.close();

					try {
						muokkaaPalveluvarausGui.open();
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
	
		
	}


	public MuokkaaPalveluvarausGUI getMuokkaaPalveluvarausGui() {
		return muokkaaPalveluvarausGui;
	}


	public void setMuokkaaPalveluvarausGui(MuokkaaPalveluvarausGUI muokkaaPalveluvarausGui) {
		this.muokkaaPalveluvarausGui = muokkaaPalveluvarausGui;
	}
	public int getPalveluvarausIdTalteen() {
		return palveluvarausIdTalteen;
	}
	public void setPalveluvarausIdTalteen(int palveluvarausIdTalteen) {
		this.palveluvarausIdTalteen = palveluvarausIdTalteen;
	}
	public int getPalveluIdTalteen() {
		return palveluIdTalteen;
	}
	public void setPalveluIdTalteen(int palveluIdTalteen) {
		this.palveluIdTalteen = palveluIdTalteen;
	}
	public int getAsiakasIdTalteen() {
		return asiakasIdTalteen;
	}
	public void setAsiakasIdTalteen(int asiakasIdTalteen) {
		this.asiakasIdTalteen = asiakasIdTalteen;
	}
	public int getAloituspvmPaivaTalteen() {
		return aloituspvmPaivaTalteen;
	}
	public void setAloituspvmPaivaTalteen(int aloituspvmPaivaTalteen) {
		this.aloituspvmPaivaTalteen = aloituspvmPaivaTalteen;
	}
	public int getAloituspvmKuukausiTalteen() {
		return aloituspvmKuukausiTalteen;
	}
	public void setAloituspvmKuukausiTalteen(int aloituspvmKuukausiTalteen) {
		this.aloituspvmKuukausiTalteen = aloituspvmKuukausiTalteen;
	}
	public int getAloituspvmVuosiTalteen() {
		return aloituspvmVuosiTalteen;
	}
	public void setAloituspvmVuosiTalteen(int aloituspvmVuosiTalteen) {
		this.aloituspvmVuosiTalteen = aloituspvmVuosiTalteen;
	}
	public int getLopetuspvmPaivaTalteen() {
		return lopetuspvmPaivaTalteen;
	}
	public void setLopetuspvmPaivaTalteen(int lopetuspvmPaivaTalteen) {
		this.lopetuspvmPaivaTalteen = lopetuspvmPaivaTalteen;
	}
	public int getLopetuspvmKuukausiTalteen() {
		return lopetuspvmKuukausiTalteen;
	}
	public void setLopetuspvmKuukausiTalteen(int lopetuspvmKuukausiTalteen) {
		this.lopetuspvmKuukausiTalteen = lopetuspvmKuukausiTalteen;
	}
	public int getLopetuspvmVuosiTalteen() {
		return lopetuspvmVuosiTalteen;
	}
	public void setLopetuspvmVuosiTalteen(int lopetuspvmVuosiTalteen) {
		this.lopetuspvmVuosiTalteen = lopetuspvmVuosiTalteen;
	}


}
