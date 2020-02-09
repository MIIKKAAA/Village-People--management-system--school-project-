package ApuMetodeja;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class ApuMetodeja {
	/**
	 * Metodi nayton koon laskemiseen ja ikkunan asettamiseen keskelle
	 * @param shl Shell - ikkuna
	 * @return display
	 */

	public static Display centerWindow(Shell shl) {
		Display display = Display.getDefault();
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shl.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    shl.setLocation(x, y);
	    
	    return display;
	}
	
	
	
	
	public int [] getIndexes(String date) throws SQLException {
		int eka = date.indexOf("-");
		int toka = date.indexOf("-", eka+1);
		int[] indeksit = new int[2];
		indeksit[0] = eka;
		indeksit[1] = toka;
		return indeksit;
	}
	
	public String getYearFromStringDate(ResultSet set, String sarake) throws SQLException {
		String date = set.getString(sarake);
		int [] idxTbl  = getIndexes(date);
		String vuosi = date.substring(0, idxTbl[0]);
		
		return vuosi;
	}
	
	public String getMonthFromStringDate(ResultSet set, String sarake) throws SQLException {
		String date = set.getString(sarake);
		int [] idxTbl  = getIndexes(date);
		String kuukausi = date.substring(idxTbl[0]+1, idxTbl[1]);
		
		return kuukausi;
	}
	
	public String getDayFromStringDate(ResultSet set, String sarake) throws SQLException {
		String date = set.getString(sarake);
		int [] idxTbl  = getIndexes(date);
		String paiva = date.substring(idxTbl[1]+1, date.length());
		
		return paiva;
	}
	
	public String dateTimeToSqlDate(DateTime date) {
		
		int vuosi = date.getYear();
		int kuukausi = date.getMonth()+1;
		int paiva = date.getDay();
		
		String sqlDateTime = vuosi+"-"+kuukausi+"-"+paiva;
		
		return sqlDateTime;
	}
	
	public DateTime createDateTime(DateTime date) {
		int paiva = date.getDay();
		int kuukausi = date.getMonth()+1;
		int vuosi = date.getYear();
		DateTime pvm = null;
		pvm.setDay(paiva);
		pvm.setMonth(kuukausi);
		pvm.setYear(vuosi);
		
		return pvm;
	}
}
