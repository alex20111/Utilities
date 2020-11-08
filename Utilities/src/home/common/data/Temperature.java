package home.common.data;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;




public class Temperature {

//	private static final Logger logger = LogManager.getLogger(Temperature.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat raw = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private NumberFormat formatter = new DecimalFormat("#0.#");

	private  String tempSun = "-99";
	private  String tmpSunUpdDt = "21:45am";
	private  String tempShade = "-99";
	private  String tmpShadeUpdDt = "10:45am";
	private  String tempPool = "-99";
	private  String tmpPoolUpdDt = "11:45am";
	
	
	private Map<TempRecName, String> tempMap = new HashMap<>();
	private Map<TempRecName, String> tempDateMap = new HashMap<>();
	
	//TODO temperature others? in a map? or re-define to have a list of object?  or keep this as legacy and create a new list of object for the new added temperature.

	public Temperature() {}

	public String getTempSun() {
		return tempSun;
	}
	public void setTempSun(String tempSun) {
		
		this.tempSun = tempSun;
	}
	public String getTempShade() {
		return tempShade;
	}
	public void setTempShade(String tempShade) {
		this.tempShade = tempShade;
	}
	public String getTempPool() {
		return tempPool;
	}
	public void setTempPool(String tempPool) {
		this.tempPool = tempPool;
	}

	public String getTmpSunUpdDt() {
		return tmpSunUpdDt;
	}
	public void setTmpSunUpdDt(String tmpSunUpdDt) {
		this.tmpSunUpdDt = tmpSunUpdDt;
	}
	public String getTmpShadeUpdDt() {
		return tmpShadeUpdDt;
	}
	public void setTmpShadeUpdDt(String tmpShadeUpdDt) {
		this.tmpShadeUpdDt = tmpShadeUpdDt;
	}
	public String getTmpPoolUpdDt() {
		return tmpPoolUpdDt;
	}
	public void setTmpPoolUpdDt(String tmpPoolUpdDt) {
		this.tmpPoolUpdDt = tmpPoolUpdDt;
	}
	public Map<TempRecName, String> getTempMap() {
		return tempMap;
	}
	public void setTempMap(Map<TempRecName, String> tempMap) {
		this.tempMap = tempMap;
	}
	public Map<TempRecName, String> getTempDateMap() {
		return tempDateMap;
	}
	public void setTempDateMap(Map<TempRecName, String> tempDateMap) {
		this.tempDateMap = tempDateMap;
	}
	public SimpleDateFormat sdf() {
		return sdf;
	}
	public NumberFormat tempFormat() {
		return formatter;
	}
	public SimpleDateFormat raw() {
		return raw;
	}
	/**
	 * Return temperature raw data , unformattted..
	 * @param rec
	 * @return
	 */
	public String getTempRawByRecName(TempRecName rec) {
		if (rec != null) {
			if (rec == TempRecName.AA) {
				return tempShade;
			}else if  (rec == TempRecName.BB){
				return tempSun;
			}else if  (rec == TempRecName.pool) {
				return tempPool;
			}else {
				 tempMap.get(rec);
			}
		}
		return null;
	}
	public String getTempRawDateByRecName(TempRecName rec) {
		if (rec != null) {
			if (rec == TempRecName.AA) {
				return tmpShadeUpdDt;
			}else if  (rec == TempRecName.BB){
				return tmpSunUpdDt;
			}else if  (rec == TempRecName.pool) {
				return tmpPoolUpdDt;
			}else {
				return tempDateMap.get(rec);
			}
		}
		return null;	
		
	}
	public String getTempFormattedByRecName(TempRecName rec) {
		if (rec != null) {
			if (rec == TempRecName.AA) {
				return tempShade;
			}else if  (rec == TempRecName.BB){
				return tempSun;
			}else if  (rec == TempRecName.pool) {
				return tempPool;
			}else {
				return (tempMap .get(rec) != null ? formatter.format(Double.valueOf(tempMap.get(rec))) : "-90" );
				
			}
		}
		return null;
	}
	public String getTempFormattedDateByRecName(TempRecName rec) {
		if (rec != null) {
			if (rec == TempRecName.AA) {
				return tmpShadeUpdDt;
			}else if  (rec == TempRecName.BB){
				return tmpSunUpdDt;
			}else if  (rec == TempRecName.pool) {
				return tmpPoolUpdDt;
			}else {
				return tempDateMap.get(rec);
			}
		}
		return null;	
	}	
	
	@Override
	public String toString() {
		return "Temperature [tempSun=" + tempSun + ", tmpSunUpdDt=" + tmpSunUpdDt + ", tempShade=" + tempShade
				+ ", tmpShadeUpdDt=" + tmpShadeUpdDt + ", tempPool=" + tempPool + ", tmpPoolUpdDt=" + tmpPoolUpdDt
				+ ", tempMap=" + tempMap + ", tempDateMap=" + tempDateMap + "]";
	}

	//defenitions of the temperature record name
	public enum TempRecName {

		AA("AA"), BB("BB"), pool("Pool"), GARAGE("Garage");
		
		private String recorderName;
		
		private TempRecName(String recName){
			recorderName = recName;
		}
		
		public String getRecName(){
			return this.recorderName;
		}
		
		//AA, BB and POOL are grandfather..  get the new sensor name and exclude AA, BB, Pool
		public static List<TempRecName> getNewSensors(){
			return Arrays.stream(TempRecName.values()).filter(r -> r != TempRecName.AA && r != TempRecName.BB && r != TempRecName.pool).collect(Collectors.toList());
		}
	}
}

