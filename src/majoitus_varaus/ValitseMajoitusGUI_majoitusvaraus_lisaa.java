package majoitus_varaus;

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

public class ValitseMajoitusGUI_majoitusvaraus_lisaa {

	protected Shell valitseMajoitusShl;
	private Table etusivuMajoitusTbl;
	private TableColumn majoitusId;
	private TableColumn nimi;

	private Label valitseMajoitusMainLbl;
	private TableColumn hinta;
	private TableColumn toimipisteId;
	private LisaaMajoitusvarausGUI lisaaMajoitusvarausGui;
	//private int valittuMajoitusID;
	private Connection conn;
	private MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui;
	private int majoitusvarausIdTalteen;
	private int majoitusIdTalteen;
	private int asiakasIdTalteen;
	private DateTime aloituspvmTalteen;
	private DateTime lopetuspvmTalteen;
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
	public void populateMajoitusTbl(Table majoitusTbl) throws SQLException {
		
		String sql = "SELECT MajoitusID, Nimi, Hinta, ToimipisteID FROM majoitus";
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
		Display display = ApuMetodeja.centerWindow(valitseMajoitusShl);

		valitseMajoitusShl.open();
		valitseMajoitusShl.layout();
		while (!valitseMajoitusShl.isDisposed()) {
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
		valitseMajoitusShl = new Shell(SWT.CLOSE | SWT.MIN);
		valitseMajoitusShl.setSize(651, 487);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			valitseMajoitusShl.setSize((int)(screenSize.getWidth() / 2.7), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			valitseMajoitusShl.setSize((int)(screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			valitseMajoitusShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		valitseMajoitusShl.setText("Valitse majoitus");
		valitseMajoitusShl.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(valitseMajoitusShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
		gd_composite.heightHint = 419;
		composite.setLayoutData(gd_composite);
		
		valitseMajoitusMainLbl = new Label(composite, SWT.NONE);
		valitseMajoitusMainLbl.setText("Alla on lista majoituksista");
		
		// Taulukko
		etusivuMajoitusTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.TOGGLE);
		GridData gd_etusivuMajoitusTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_etusivuMajoitusTbl.heightHint = 367;
		etusivuMajoitusTbl.setLayoutData(gd_etusivuMajoitusTbl);
		etusivuMajoitusTbl.setHeaderVisible(true);
		etusivuMajoitusTbl.setLinesVisible(true);
		
		// MajoitusID kentta taulukossa
		majoitusId = new TableColumn(etusivuMajoitusTbl, SWT.NONE);
		majoitusId.setWidth(90);
		majoitusId.setText("Majoitus ID");
		
		// Majoitus nimi kentta taulukossa
		nimi = new TableColumn(etusivuMajoitusTbl, SWT.NONE);
		nimi.setWidth(129);
		nimi.setText("Nimi");
		
		hinta = new TableColumn(etusivuMajoitusTbl, SWT.NONE);
		hinta.setWidth(100);
		hinta.setText("Hinta");
		
		toimipisteId = new TableColumn(etusivuMajoitusTbl, SWT.NONE);
		toimipisteId.setWidth(100);
		toimipisteId.setText("Toimipiste ID");
		
		populateMajoitusTbl(etusivuMajoitusTbl);
		
		composite_1 = new Composite(valitseMajoitusShl, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(2, false));
		
		peruutaBtn = new Button(composite_1, SWT.NONE);
		peruutaBtn.setBounds(0, 0, 75, 25);
		peruutaBtn.setText("Peruuta");
		
		hyvaksyBtn = new Button(composite_1, SWT.NONE);
		hyvaksyBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		hyvaksyBtn.setText("Hyväksy");
		
		peruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				valitseMajoitusShl.close();
				majoitusvarausEtusivuGui = new MajoitusvarausEtusivuGUI();
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
				TableItem valittuAsiakasTbl[] = etusivuMajoitusTbl.getSelection();
				
				lisaaMajoitusvarausGui = getLisaaMajoitusvarausGui();
				lisaaMajoitusvarausGui.setAsiakasIdTalteen(asiakasIdTalteen);
				lisaaMajoitusvarausGui.setMajoitusvarausIdTalteen(majoitusvarausIdTalteen);
				System.out.println("majvarId: "+majoitusvarausIdTalteen);
				lisaaMajoitusvarausGui.setMajoitusIdTalteen(majoitusIdTalteen);
				// NullPointerExceptionin valttamiseksi kun taulusta ei valittu mitaan
				if (valittuAsiakasTbl.length < 1) {
					System.out.println("Valitse yksi");
				}
				else {
					// Tallennetaan valittu AsiakasID:n arvo LuoLaskuGUI-luokan setterilla myohempaa kayttoa varten
					lisaaMajoitusvarausGui.setValittuMajoitusId(Integer.parseInt(valittuAsiakasTbl[0].getText(0)));
					valitseMajoitusShl.close();

					try {
						lisaaMajoitusvarausGui.open();
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


	public LisaaMajoitusvarausGUI getLisaaMajoitusvarausGui() {
		return lisaaMajoitusvarausGui;
	}


	public void setLisaaMajoitusvarausGui(LisaaMajoitusvarausGUI lisaaMajoitusvarausGui) {
		this.lisaaMajoitusvarausGui = lisaaMajoitusvarausGui;
	}
	public int getMajoitusvarausIdTalteen() {
		return majoitusvarausIdTalteen;
	}
	public void setMajoitusvarausIdTalteen(int majoitusvarausIdTalteen) {
		this.majoitusvarausIdTalteen = majoitusvarausIdTalteen;
	}
	public int getMajoitusIdTalteen() {
		return majoitusIdTalteen;
	}
	public void setMajoitusIdTalteen(int majoitusIdTalteen) {
		this.majoitusIdTalteen = majoitusIdTalteen;
	}
	public int getAsiakasIdTalteen() {
		return asiakasIdTalteen;
	}
	public void setAsiakasIdTalteen(int asiakasIdTalteen) {
		this.asiakasIdTalteen = asiakasIdTalteen;
	}
	public DateTime getAloituspvmTalteen() {
		return aloituspvmTalteen;
	}
	public void setAloituspvmTalteen(DateTime aloituspvmTalteen) {
		this.aloituspvmTalteen = aloituspvmTalteen;
	}
	public DateTime getLopetuspvmTalteen() {
		return lopetuspvmTalteen;
	}
	public void setLopetuspvmTalteen(DateTime lopetuspvmTalteen) {
		this.lopetuspvmTalteen = lopetuspvmTalteen;
	}

}
