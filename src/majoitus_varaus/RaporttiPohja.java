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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ApuMetodeja.ApuMetodeja;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class RaporttiPohja {

	protected Shell majoitusvarausRaporttiPohjaShl;
	private Table raporttiTbl;
	private int valittuToimipisteID;
	private String valittuAloituspvm;
	private String valittuLopetuspvm;
	private Connection conn;
	private MajoitusRaportointiGUI majoitusRaportointiGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RaporttiPohja window = new RaporttiPohja();
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
		// Avautuessa keskell
		createContents();
		Display display = ApuMetodeja.centerWindow(majoitusvarausRaporttiPohjaShl);
		populateTable();
		System.out.println(getValittuToimipisteID());
		System.out.println(getValittuAloituspvm());
		System.out.println(getValittuLopetuspvm());
		majoitusvarausRaporttiPohjaShl.open();
		majoitusvarausRaporttiPohjaShl.layout();
		while (!majoitusvarausRaporttiPohjaShl.isDisposed()) {
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
	public void populateTable() throws SQLException {
		// SQL-kysely ja tulosjoukon valmistelu
		String sql = "SELECT toimipisteet.Nimi, majoitusvaraus.AsiakasID, majoitusvaraus.aloituspvm, majoitus.hinta FROM majoitusvaraus "+
						"INNER JOIN majoitus ON majoitusvaraus.MajoitusID = majoitus.MajoitusID INNER JOIN toimipisteet ON "+
						"majoitus.ToimipisteID = toimipisteet.ToimipisteID WHERE toimipisteet.ToimipisteID = ? AND aloituspvm BETWEEN ? AND ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;

		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			lause.setInt(1, getValittuToimipisteID());
			lause.setString(2,  getValittuAloituspvm());
			lause.setString(3, getValittuLopetuspvm());
			System.out.println(lause.toString());
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
	            item = new TableItem(raporttiTbl, SWT.NONE);
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
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		majoitusvarausRaporttiPohjaShl = new Shell();
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			majoitusvarausRaporttiPohjaShl.setSize((int)(screenSize.getWidth() / 2.55), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			majoitusvarausRaporttiPohjaShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 2.55));
		}
		else if (screenSize.getWidth() == 1600) {
			majoitusvarausRaporttiPohjaShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		majoitusvarausRaporttiPohjaShl.setSize(670, 384);
		majoitusvarausRaporttiPohjaShl.setText("Majoitusvaraus raportti");
		majoitusvarausRaporttiPohjaShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(majoitusvarausRaporttiPohjaShl, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		raporttiTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		raporttiTbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		raporttiTbl.setHeaderVisible(true);
		raporttiTbl.setLinesVisible(true);
		
		TableColumn toimipisteColumn = new TableColumn(raporttiTbl, SWT.NONE);
		toimipisteColumn.setWidth(142);
		toimipisteColumn.setText("Toimipiste");
		
		TableColumn asiakasColumn = new TableColumn(raporttiTbl, SWT.NONE);
		asiakasColumn.setWidth(151);
		asiakasColumn.setText("Asiakas");
		
		TableColumn pvmColumn = new TableColumn(raporttiTbl, SWT.NONE);
		pvmColumn.setWidth(135);
		pvmColumn.setText("Päivämäärä");
		
		TableColumn hintaColumn = new TableColumn(raporttiTbl, SWT.NONE);
		hintaColumn.setWidth(100);
		hintaColumn.setText("Hinta");
		
		Button okBtn = new Button(composite, SWT.NONE);
		okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				majoitusvarausRaporttiPohjaShl.close();
				
			}
		});
		okBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		okBtn.setText("OK");

	}

	public int getValittuToimipisteID() {
		return valittuToimipisteID;
	}

	public void setValittuToimipisteID(int valittuToimipisteID) {
		this.valittuToimipisteID = valittuToimipisteID;
	}

	public String getValittuAloituspvm() {
		return valittuAloituspvm;
	}

	public void setValittuAloituspvm(String valittuAloituspvm) {
		this.valittuAloituspvm = valittuAloituspvm;
	}

	public String getValittuLopetuspvm() {
		return valittuLopetuspvm;
	}

	public void setValittuLopetuspvm(String valittuLopetuspvm) {
		this.valittuLopetuspvm = valittuLopetuspvm;
	}

	
	
	
}
