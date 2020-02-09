package majoitus_varaus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.widgets.DateTime;

public class Majoitusvaraus {
	
	private int majoitusvarausId;
	private int majoitusId;
	private int asiakasId;
	private String aloituspvm;
	private String lopetuspvm;
	
	
	public Majoitusvaraus() {
	}
	
	
	
    public int getMajoitusvarausId() {
		return majoitusvarausId;
	}



	public void setMajoitusvarausId(int majoitusvarausId) {
		this.majoitusvarausId = majoitusvarausId;
	}



	public int getMajoitusId() {
		return majoitusId;
	}



	public void setMajoitusId(int majoitusId) {
		this.majoitusId = majoitusId;
	}



	public int getAsiakasId() {
		return asiakasId;
	}



	public void setAsiakasId(int asiakasId) {
		this.asiakasId = asiakasId;
	}


	public String getAloituspvm() {
		return aloituspvm;
	}



	public void setAloituspvm(String aloituspvm) {
		this.aloituspvm = aloituspvm;
	}



	public String getLopetuspvm() {
		return lopetuspvm;
	}



	public void setLopetuspvm(String lopetuspvm) {
		this.lopetuspvm = lopetuspvm;
	}



	@Override
    public String toString(){
        return ("Id: "+majoitusvarausId);
    }
	
	
	public static Majoitusvaraus haeMajoitusvaraus(int id, Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT * FROM majoitusvaraus WHERE MajoitusvarausID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, id); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) { // tulosjoukkoon odotetaan ainoastaan max. yhtä riviä
				System.out.println("");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		// käsitellään resultset - laitetaan tiedot asiakasoliolle
		Majoitusvaraus majoitusvarausOlio = new Majoitusvaraus();
		try {
			while (tulosjoukko.next () == true){ // tulosjoukossa oletettavasti useampia rivejä
				
				majoitusvarausOlio.setAsiakasId(Integer.parseInt(tulosjoukko.getString("AsiakasID")));
				majoitusvarausOlio.setMajoitusId(Integer.parseInt(tulosjoukko.getString("MajoitusID")));
				majoitusvarausOlio.setMajoitusvarausId(Integer.parseInt(tulosjoukko.getString("MajoitusvarausID")));
				majoitusvarausOlio.setLopetuspvm(tulosjoukko.getString("lopetuspvm"));
				majoitusvarausOlio.setAloituspvm(tulosjoukko.getString("aloituspvm"));
			
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}

		return majoitusvarausOlio;
	}
	
	public int lisaaMajoitusvaraus(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT MajoitusvarausID FROM majoitusvaraus WHERE MajoitusvarausID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getMajoitusvarausId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next()) {
				throw new Exception("Majoitusvaraus on jo olemassa");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		
		sql = "INSERT INTO majoitusvaraus (MajoitusvarausID, MajoitusID, AsiakasID, aloituspvm, lopetuspvm) VALUES (?, ?, ?, ?, ?)";
		lause = null;

		try {
			lause = connection.prepareStatement(sql);

			lause.setInt(1, getMajoitusvarausId());
			lause.setInt(2, getMajoitusId());
			lause.setInt(3, getAsiakasId());
			lause.setString(4,  getAloituspvm());
			lause.setString(5,  getLopetuspvm());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Majoitusvarauksen lisääminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	public int muokkaaMajoitusvaraus(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT MajoitusvarausID FROM majoitusvaraus WHERE MajoitusvarausID = ?";
		
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getMajoitusvarausId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next() == false) {
				throw new Exception("Majoitusvarausta ei löydy.");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
	
		sql = "UPDATE majoitusvaraus SET MajoitusvarausID = ?, MajoitusID = ?, AsiakasID = ?, aloituspvm = ?, lopetuspvm = ? WHERE MajoitusvarausID = ?"; 
		lause = null;

		try {
			lause = connection.prepareStatement(sql);
			
			lause.setInt(1, getMajoitusvarausId());
			lause.setInt(2, getMajoitusId());
			lause.setInt(3, getAsiakasId());
			lause.setString(4,  getAloituspvm());
			lause.setString(5,  getLopetuspvm());
			lause.setInt(6, getMajoitusvarausId());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Majoitusvarauksen muuttaminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	

}


