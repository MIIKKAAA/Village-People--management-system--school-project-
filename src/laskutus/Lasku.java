package laskutus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.widgets.DateTime;

public class Lasku {
	
	private int lasku_id;
	private String laskun_tyyppi;
	private int palveluvaraus_id;
	private int asiakas_id;
	private int majoitusvaraus_id;
	private int tilinumero;
	private String saajan_nimi;
	private int viitenumero;
	private int maksun_maara;
	private String laskunluontipvm;
	private String erapvm;
	
	public Lasku() {
	}
	
	public void setLasku_id(int lasku_id) {
		this.lasku_id = lasku_id;
	}

	public void setLaskun_tyyppi(String laskun_tyyppi) {
		this.laskun_tyyppi = laskun_tyyppi;
	}

	public void setPalveluvaraus_id(int palveluvaraus_id) {
		this.palveluvaraus_id = palveluvaraus_id;
	}

	public void setAsiakas_id(int asiakas_id) {
		this.asiakas_id = asiakas_id;
	}

	public void setMajoitusvaraus_id(int majoitusvaraus_id) {
		this.majoitusvaraus_id = majoitusvaraus_id;
	}

	public void setTilinumero(int tilinumero) {
		this.tilinumero = tilinumero;
	}


	public void setSaajan_nimi(String saajan_nimi) {
		this.saajan_nimi = saajan_nimi;
	}

	public void setViitenumero(int viitenumero) {
		this.viitenumero = viitenumero;
	}

	public void setMaksun_maara(int maksun_maara) {
		this.maksun_maara = maksun_maara;
	}

	public void setLaskunluontipvm(String laskunLuontiPvmDate) {
		this.laskunluontipvm = laskunLuontiPvmDate;
	}

	public void setErapvm(String erapvm) {
		this.erapvm = erapvm;
	}

	public int getLasku_id() {
		return lasku_id;
	}

	public String getLaskun_tyyppi() {
		return laskun_tyyppi;
	}

	public int getPalveluvaraus_id() {
		return palveluvaraus_id;
	}

	public int getAsiakas_id() {
		return asiakas_id;
	}

	public int getMajoitusvaraus_id() {
		return majoitusvaraus_id;
	}

	public int getTilinumero() {
		return tilinumero;
	}


	public String getSaajan_nimi() {
		return saajan_nimi;
	}

	public int getViitenumero() {
		return viitenumero;
	}

	public int getMaksun_maara() {
		return maksun_maara;
	}

	public String getLaskunluontipvm() {
		return laskunluontipvm;
	}

	public String getErapvm() {
		return erapvm;
	}
	
    @Override
    public String toString(){
        return (lasku_id + " " +asiakas_id + " " + erapvm);
    }
	
	
	public static Lasku haeLasku(int id, Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT * FROM laskujen_hallinta_ja_seuranta WHERE LaskuID = ?";
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
		Lasku laskuOlio = new Lasku();
		try {
			while (tulosjoukko.next () == true){ // tulosjoukossa oletettavasti useampia rivejä
				laskuOlio.setAsiakas_id(tulosjoukko.getInt("AsiakasID"));
				laskuOlio.setLasku_id(tulosjoukko.getInt("LaskuID"));
				laskuOlio.setPalveluvaraus_id(tulosjoukko.getInt("PalveluvarausID"));
				laskuOlio.setMajoitusvaraus_id(tulosjoukko.getInt("MajoitusvarausID"));
				laskuOlio.setLaskun_tyyppi(tulosjoukko.getString("Laskun_tyyppi"));
				laskuOlio.setTilinumero(tulosjoukko.getInt("Tilinumero"));
				laskuOlio.setSaajan_nimi(tulosjoukko.getString("Saajan_nimi"));
				laskuOlio.setViitenumero(tulosjoukko.getInt("Viitenumero"));
				laskuOlio.setMaksun_maara(tulosjoukko.getInt("Maksun_määrä"));
				laskuOlio.setLaskunluontipvm(tulosjoukko.getString("Laskunluontipvm"));
				laskuOlio.setErapvm(tulosjoukko.getString("Eräpvm"));
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}

		return laskuOlio;
	}
	
	public int lisaaLasku(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT LaskuID FROM laskujen_hallinta_ja_seuranta WHERE LaskuID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getLasku_id()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next()) {
				throw new Exception("Lasku on jo olemassa");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		
		sql = "INSERT INTO laskujen_hallinta_ja_seuranta (LaskuID, PalveluvarausID, AsiakasID, MajoitusvarausID, Laskun_tyyppi, "+
			"Tilinumero, Saajan_nimi, Viitenumero, Maksun_määrä, Laskunluontipvm, Eräpvm)"+
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		lause = null;

		try {
			lause = connection.prepareStatement(sql);

			lause.setInt(1, getLasku_id());
			if (getPalveluvaraus_id() == 0){
				lause.setNull(2, java.sql.Types.INTEGER);
			}
			else {
				lause.setInt(2, getPalveluvaraus_id());
			}
			lause.setInt(3, getAsiakas_id());
			if (getMajoitusvaraus_id() == 0) {
				lause.setNull(4, java.sql.Types.INTEGER);
			}
			else {
				lause.setInt(4, getMajoitusvaraus_id());
			}
			lause.setString(5, getLaskun_tyyppi());
			lause.setInt(6, getTilinumero());
			lause.setString(7, getSaajan_nimi());
			lause.setInt(8, getViitenumero());
			lause.setInt(9, getMaksun_maara());
			lause.setString(10, getLaskunluontipvm());
			lause.setString(11, getErapvm());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Laskun lisääminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	public int muokkaaLasku(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT LaskuID FROM laskujen_hallinta_ja_seuranta WHERE LaskuID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getLasku_id()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next() == false) {
				throw new Exception("Laskua ei löydy.");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}

		sql = "UPDATE laskujen_hallinta_ja_seuranta SET LaskuID = ?, PalveluvarausID = ?, AsiakasID = ?, MajoitusvarausID = ?, "
				+ "Laskun_tyyppi = ?, Tilinumero = ?, Saajan_nimi = ?, Viitenumero = ?, Maksun_määrä= ?,"+""
				+ "Laskunluontipvm = ?, Eräpvm = ? WHERE LaskuID = ?";
		lause = null;

		try {
			lause = connection.prepareStatement(sql);

			lause.setInt(1, getLasku_id());
			if (getPalveluvaraus_id() == 0){
				lause.setNull(2, java.sql.Types.INTEGER);
			}
			else {
				lause.setInt(2, getPalveluvaraus_id());
			}
			lause.setInt(3, getAsiakas_id());
			if (getMajoitusvaraus_id() == 0) {
				lause.setNull(4, java.sql.Types.INTEGER);
			}
			else {
				lause.setInt(4, getMajoitusvaraus_id());
			}
			lause.setString(5, getLaskun_tyyppi());
			lause.setInt(6, getTilinumero());
			lause.setString(7, getSaajan_nimi());
			lause.setInt(8, getViitenumero());
			lause.setInt(9, getMaksun_maara());
			lause.setString(10, getLaskunluontipvm());
			lause.setString(11, getErapvm());
			lause.setInt(12, getLasku_id());
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Laskun muuttaminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	

}


