package majoitus_varaus;

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

public class NaytaMajoitusvarausTiedotGUI {

	protected Shell naytaTiedotShl;
	private Text majoitusvarausIdTxt;
	private Text majoitusIdTxt;
	private Text asiakasIdTxt;
	private Label naytaTiedotLbl;
	private Label majoitusvarausIdLbl;
	private Label majoitusIdLbl;
	private Label asiakasID;
	private Label aloituspvmLbl;
	private Label lopetuspvmLbl;
	private Text aloituspvmTxt;
	private Text lopetuspvmTxt;
	private Label label;
	private Connection conn;
	private int valittuMajoitusvarausID;
	private MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NaytaMajoitusvarausTiedotGUI window = new NaytaMajoitusvarausTiedotGUI();
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
		Display display = ApuMetodeja.centerWindow(naytaTiedotShl);
		luoMajoitusvarausTiedot();
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
	
	private void luoMajoitusvarausTiedot() throws SQLException {
		String sql = "SELECT MajoitusvarausID, MajoitusID, AsiakasID, aloituspvm, lopetuspvm "+
					"FROM majoitusvaraus WHERE MajoitusvarausID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuMajoitusvarausID()); // asetetaan where ehtoon (?) arvo
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
	
			majoitusvarausIdTxt.setText(Integer.toString(tulosjoukko.getInt("MajoitusvarausID")));
			asiakasIdTxt.setText(Integer.toString(tulosjoukko.getInt("AsiakasID")));
			majoitusIdTxt.setText(Integer.toString(tulosjoukko.getInt("MajoitusID")));
			aloituspvmTxt.setText(tulosjoukko.getString("aloituspvm"));
			lopetuspvmTxt.setText(tulosjoukko.getString("lopetuspvm"));
		}
	}
	
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		naytaTiedotShl = new Shell(SWT.CLOSE | SWT.MIN);
		naytaTiedotShl.setSize(460, 344);
		
		
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
		
				naytaTiedotShl.setText("Varauksen tiedot");
		naytaTiedotShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(naytaTiedotShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		naytaTiedotLbl = new Label(composite, SWT.NONE);
		naytaTiedotLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		naytaTiedotLbl.setAlignment(SWT.CENTER);
		naytaTiedotLbl.setText("Alla näet varauksen tiedot");
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
		
		
		// Ekan rivin label ja tekstikentta (majoitus ja toimipiste)
		majoitusvarausIdLbl = new Label(composite, SWT.NONE);
		majoitusvarausIdLbl.setText("Majoitusvaraus ID");
		
				majoitusIdLbl = new Label(composite, SWT.NONE);
				majoitusIdLbl.setText("Majoitus ID");
		
		label = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		majoitusvarausIdTxt = new Text(composite, SWT.BORDER);
		majoitusvarausIdTxt.setEditable(false);
		majoitusvarausIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusvarausIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUSVARAUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		majoitusvarausIdTxt.setToolTipText("Majoitusvaraus ID");
		
		majoitusIdTxt = new Text(composite, SWT.BORDER);
		majoitusIdTxt.setEditable(false);
		majoitusIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		majoitusIdTxt.setToolTipText("Majoitus ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Toisen rivin label ja tekstikentta (asiakas id ja toimipiste id)
		asiakasID = new Label(composite, SWT.NONE);
		asiakasID.setText("Asiakas ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		asiakasIdTxt = new Text(composite, SWT.BORDER);
		asiakasIdTxt.setEditable(false);
		asiakasIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		asiakasIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKASID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		asiakasIdTxt.setToolTipText("Asiakas ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Neljannen rivin label ja tekstikentta (aloituspvm ja lopetuspvm)
		aloituspvmLbl = new Label(composite, SWT.NONE);
		aloituspvmLbl.setText("Aloituspäivämäärä");
		
		lopetuspvmLbl = new Label(composite, SWT.NONE);
		lopetuspvmLbl.setText("Lopetuspäivämäärä");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		aloituspvmTxt = new Text(composite, SWT.BORDER);
		aloituspvmTxt.setEditable(false);
		aloituspvmTxt.setToolTipText("Aloituspäivämäärä");
		aloituspvmTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lopetuspvmTxt = new Text(composite, SWT.BORDER);
		lopetuspvmTxt.setEditable(false);
		lopetuspvmTxt.setToolTipText("Lopetuspäivämäärä");
		lopetuspvmTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
		
		Button majoitusvarausOkBtn = new Button(composite, SWT.NONE);
		majoitusvarausOkBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		majoitusvarausOkBtn.setText("OK");
		majoitusvarausOkBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				naytaTiedotShl.close();
				majoitusvarausEtusivuGui = getMajoitusvarausEtusivuGui();
				try {
					majoitusvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(composite, SWT.NONE);
	}

	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}

	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
	}

	public MajoitusvarausEtusivuGUI getMajoitusvarausEtusivuGui() {
		return majoitusvarausEtusivuGui;
	}

	public void setMajoitusvarausEtusivuGui(MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui) {
		this.majoitusvarausEtusivuGui = majoitusvarausEtusivuGui;
	}


	
	
}
