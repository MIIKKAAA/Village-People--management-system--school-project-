package majoitus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.widgets.DateTime;

public class Majoitus {
	
	private int majoitusId;
	private String nimi;
	private int hinta;
	private int toimipisteId;
	
	
	public Majoitus() {
	}
	
	

	public int getMajoitusId() {
		return majoitusId;
	}



	public void setMajoitusId(int majoitusId) {
		this.majoitusId = majoitusId;
	}



	public String getNimi() {
		return nimi;
	}



	public void setNimi(String nimi) {
		this.nimi = nimi;
	}



	public int getHinta() {
		return hinta;
	}



	public void setHinta(int hinta) {
		this.hinta = hinta;
	}



	public int getToimipisteId() {
		return toimipisteId;
	}



	public void setToimipisteId(int toimipisteId) {
		this.toimipisteId = toimipisteId;
	}



	@Override
    public String toString(){
        return ("Id: "+majoitusId);
    }
	
	
	public static Majoitus haeMajoitus(int id, Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT * FROM majoitus WHERE MajoitusID = ?";
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
		Majoitus majoitusOlio = new Majoitus();
		try {
			while (tulosjoukko.next () == true){ // tulosjoukossa oletettavasti useampia rivejä
		
				majoitusOlio.setHinta(Integer.parseInt(tulosjoukko.getString("Hinta")));
				majoitusOlio.setMajoitusId(Integer.parseInt(tulosjoukko.getString("MajoitusID")));
				majoitusOlio.setNimi(tulosjoukko.getString("Nimi"));
				majoitusOlio.setToimipisteId(Integer.parseInt(tulosjoukko.getString("ToimipisteID")));
			
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}

		return majoitusOlio;
	}
	
	public int lisaaMajoitus(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT MajoitusID FROM majoitus WHERE MajoitusID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getMajoitusId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next()) {
				throw new Exception("Majoitus on jo olemassa");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		
		sql = "INSERT INTO majoitus (MajoitusID, Nimi, Hinta, ToimipisteID) VALUES (?, ?, ?, ?)";
		lause = null;

		try {
			lause = connection.prepareStatement(sql);

			lause.setInt(1, getMajoitusId());
			lause.setString(2, getNimi());
			lause.setInt(3, getHinta());
			lause.setInt(4, getToimipisteId());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Majoituksen lisääminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	public int muokkaaMajoitus(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT MajoitusID FROM majoitus WHERE MajoitusID = ?";
		
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getMajoitusId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next() == false) {
				throw new Exception("Majoitusta ei löydy.");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
	
		sql = "UPDATE majoitus SET MajoitusID = ?, Nimi = ?, Hinta = ?, ToimipisteID = ? WHERE MajoitusID = ?"; 
		lause = null;

		try {
			lause = connection.prepareStatement(sql);
			
			lause.setInt(1, getMajoitusId());
			lause.setString(2, getNimi());
			lause.setInt(3, getHinta());
			lause.setInt(4, getToimipisteId());
			lause.setInt(5, getMajoitusId());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Majoituksen muuttaminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	

}


