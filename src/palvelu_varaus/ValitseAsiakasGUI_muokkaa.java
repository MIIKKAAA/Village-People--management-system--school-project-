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

public class ValitseAsiakasGUI_muokkaa {

	protected Shell valitseAsiakasShl;
	private Table etusivuAsiakasTbl;
	private TableColumn asiakasID;
	private TableColumn etunimi;

	private Label valitseAsiakasMainLbl;
	private TableColumn sukunimi;
	private TableColumn osoite;
	private MuokkaaPalveluvarausGUI muokkaaPalveluvarausGui;
	//private int valittuAsiakasID;
	private Connection conn;
	private PalveluvarausEtusivuGUI palveluvarausEtusivuGui;
	private Composite composite_1;
	private int palveluvarausIdTalteen;
	private int palveluIdTalteen;
	private int asiakasIdTalteen;
	private int aloituspvmPaivaTalteen;
	private int aloituspvmKuukausiTalteen;
	private int aloituspvmVuosiTalteen;
	private int lopetuspvmPaivaTalteen;
	private int lopetuspvmKuukausiTalteen;
	private int lopetuspvmVuosiTalteen;


	
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
	public void populateAsiakasTbl(Table asiakasTbl) throws SQLException {
		
		String sql = "SELECT AsiakasID, Etunimi, Sukunimi, Postiosoite FROM asiakas";
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
	            item = new TableItem(asiakasTbl, SWT.NONE);
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
		Display display = ApuMetodeja.centerWindow(valitseAsiakasShl);
		valitseAsiakasShl.open();
		valitseAsiakasShl.layout();
		while (!valitseAsiakasShl.isDisposed()) {
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
		valitseAsiakasShl = new Shell(SWT.CLOSE | SWT.MIN);
		valitseAsiakasShl.setSize(651, 487);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			valitseAsiakasShl.setSize((int)(screenSize.getWidth() / 2.7), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			valitseAsiakasShl.setSize((int)(screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			valitseAsiakasShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		valitseAsiakasShl.setText("Valitse asiakas");
		valitseAsiakasShl.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(valitseAsiakasShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
		gd_composite.heightHint = 419;
		composite.setLayoutData(gd_composite);
		
		valitseAsiakasMainLbl = new Label(composite, SWT.NONE);
		valitseAsiakasMainLbl.setText("Alla on lista asiakkaista");
		
		// Taulukko
		etusivuAsiakasTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.TOGGLE);
		GridData gd_etusivuAsiakasTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_etusivuAsiakasTbl.heightHint = 367;
		etusivuAsiakasTbl.setLayoutData(gd_etusivuAsiakasTbl);
		etusivuAsiakasTbl.setHeaderVisible(true);
		etusivuAsiakasTbl.setLinesVisible(true);
		
		// AsiakasID kentta taulukossa
		asiakasID = new TableColumn(etusivuAsiakasTbl, SWT.NONE);
		asiakasID.setWidth(90);
		asiakasID.setText("Asiakas ID");
		
		// Asiakas nimi kentta taulukossa
		etunimi = new TableColumn(etusivuAsiakasTbl, SWT.NONE);
		etunimi.setWidth(129);
		etunimi.setText("Etunimi");
		
		sukunimi = new TableColumn(etusivuAsiakasTbl, SWT.NONE);
		sukunimi.setWidth(100);
		sukunimi.setText("Sukunimi");
		
		osoite = new TableColumn(etusivuAsiakasTbl, SWT.NONE);
		osoite.setWidth(100);
		osoite.setText("Osoite");
		
		
		composite_1 = new Composite(valitseAsiakasShl, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(3, false));
		
		Button peruutaBtn = new Button(composite_1, SWT.NONE);
		peruutaBtn.setSize(75, 25);
		peruutaBtn.setText("New Button");
		
		Button hyvaksyBtn = new Button(composite_1, SWT.NONE);
		hyvaksyBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		hyvaksyBtn.setText("Hyväksy");
		new Label(composite_1, SWT.NONE);
		
		peruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				valitseAsiakasShl.close();
				muokkaaPalveluvarausGui = getMuokkaaPalveluvarausGui();
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
		});
		
		peruutaBtn.setText("Peruuta");
		
		hyvaksyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Haetaan valittu TableItem halutusta taulusta
				TableItem valittuAsiakasTbl[] = etusivuAsiakasTbl.getSelection();
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
					muokkaaPalveluvarausGui.setValittuAsiakasId(Integer.parseInt(valittuAsiakasTbl[0].getText(0)));
					valitseAsiakasShl.close();

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
		
		populateAsiakasTbl(etusivuAsiakasTbl);

		
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
