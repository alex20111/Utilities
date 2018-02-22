package home.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Prefs {

	private Preferences pref;
	
	public Prefs(){}
	/**
	 * 
	 * @param name - The name of the preference
	 * @param prefDef - The definition of a preference. If it is a User defined preference or a System (Shared) preference.
	 * @throws InvalidPreferencesFormatException 
	 */
	public Prefs(String name, PrefDef prefDef) throws IllegalArgumentException
	{
		create(name, prefDef);
	}
	/**
	 * Create a new preference
	 * @param name - The name of the preference
	 * @param prefDef - The definition of a preference. If it is a User defined preference or a System (Shared) preference.
	 * @throws IllegalArgumentException
	 */
	private void create(String name, PrefDef prefDef) throws IllegalArgumentException{
		
		if (name != null && name.length() > 0){
			if (prefDef == PrefDef.SYSTEM_PREF){			
				pref  = Preferences.systemRoot().node(name);
			}
			else if (prefDef == PrefDef.USER_PREF){
				pref  = Preferences.userRoot().node(name);
			}
			else{
				throw new IllegalArgumentException("Invalid preference definition (prefDef), can be system or user");
			}
		}
		else{
			throw new IllegalArgumentException("Invalid name, please provide a name.");
		}
	}
	/**
	 * Add a new preference key/value.
	 * @param prefKey - The name of the key for the preference
	 * @param prefValue - The value for the preference as an Object
	 * @param type	- The type of object that the prefValue is .
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public Prefs savePref(String prefKey, Object prefValue, ObjType type) throws IOException, IllegalArgumentException{		
		
		if (pref != null){
			if (prefKey != null){
				if (prefValue != null){
					if (type != null)
					{
						switch(type){
						case BOOLEAN: pref.putBoolean(prefKey, (Boolean)prefValue); break;
						case STRING: pref.put(prefKey, (String)prefValue); break;			
						case DOUBLE: pref.putDouble(prefKey, (Double)prefValue); break;
						case FLOAT: pref.putFloat(prefKey, ((Float)prefValue) ); break;
						case INT: pref.putInt(prefKey, (Integer)prefValue); break;
						case LONG: pref.putLong(prefKey, (Long)prefValue); break;
						case OBJECT: pref.putByteArray(prefKey, seriObj(prefValue)); break;
						case BYTE_ARRAY: throw new IllegalArgumentException("Cannot use type " + type + " please use addPrefForByteArray");
						default : throw new IllegalArgumentException("invalid ObjType value " + type);
						}
					}else{
						throw new IllegalArgumentException("Object type is null");
					}
				}else{
					throw new IllegalArgumentException("Object prefValue is null");
				}
			}else{
				throw new IllegalArgumentException("Invalid prefKey, please enter a prefKey");
			}
		}
		return this;
	}
	/**
	 * Add a new preference key/value for byte array .
	 * @param prefKey - The name of the key for the preference
	 * @param bytesArray - The byte array to put into the preference.
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public Prefs savePrefForByteArray(String prefKey, byte[] bytesArray) throws IOException, IllegalArgumentException{

		if (pref != null){
			if (prefKey != null){
				if (bytesArray != null && bytesArray.length > 0){
					pref.putByteArray(prefKey, compressBytes(bytesArray));
				}
				else{
					throw new IllegalArgumentException("Byte array is null or empty, please enter information");
				}
			}
			else{
				throw new IllegalArgumentException("Invalid prefKey, please enter a prefKey");
			}
		}
		return this;
	}
	
	public Prefs removePref(String prefKey,Object prefValue){
		
		pref.remove(prefKey);
		
		return this;
	}
	
	/*
	 * Preferences getters
	 */
	public int getIntPref(String prefKey)
	{
		return pref.getInt(prefKey, 0);
	}
	public long getLongPref(String prefKey)
	{
		return pref.getLong(prefKey, 0);
	}
	/**
	 * Return an object that was serialized into the value. Cannot be bigger than the max value.
	 * @param prefKey
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Object getSerializeObjectPref(String prefKey) throws ClassNotFoundException, IOException
	{
		return deseriObj(pref.getByteArray(prefKey, null));
	}
	public byte[] getByteArrayPref(String prefKey) throws IOException
	{
		byte[] deComp = deCompressBytes(pref.getByteArray(prefKey, null));
		return deComp;
	}
	public String getStringPref(String prefKey)
	{
		return pref.get(prefKey, null);
	}
	public boolean getBooleanPref(String prefKey)
	{
		return pref.getBoolean(prefKey, false);
	}
	public double getDoublePref(String prefKey)
	{
		return pref.getDouble(prefKey, 0.0);
	}
	public float getFloatPref(String prefKey)
	{
		return pref.getFloat(prefKey, 0.0f);
	}	
	public Preferences getPreference()
	{
		return this.pref;
	}
	
	// end
	
	/**
	 * get the preference keys and value in a map. If preference is null or no keys defined, getPrefMap returns an empty object.
	 * @return
	 * @throws BackingStoreException
	 */
	public Map<String, Object> getPrefMap() throws BackingStoreException	{
		Map<String, Object> mp = new HashMap<String, Object>();
		
		if (pref != null && pref.keys().length > 0){
			for(String str: pref.keys()){
				mp.put(str, pref.get(str, null));
			}
		}		
		return mp;
	}		
	/**
	 * Serialize an object and compress it
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	private  byte[] seriObj(Object obj) throws IOException	{

		// serialize the object
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
				ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut)
			)
		{
			objectOut.writeObject(obj);
			objectOut.close();
			return baos.toByteArray();
		}	
	}
	/**
	 * de-Serialize an object and de-compress it
	 * @param obj
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Object deseriObj(byte[] obj) throws IOException, ClassNotFoundException 	{
		Object ob = null;

		if (obj != null && obj.length > 0)
		{
			// de-serialize the object
			try(	ByteArrayInputStream bais = new ByteArrayInputStream(obj);
					GZIPInputStream gzipIn = new GZIPInputStream(bais);
					ObjectInputStream objectIn = new ObjectInputStream(gzipIn))
					{
				ob =  objectIn.readObject();
				objectIn.close();
					}
		}
		return ob;
	}	
	private static byte[] compressBytes(byte[] bytes) throws IOException
	{
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream gzipOut = new GZIPOutputStream(baos))
				{
			gzipOut.write(bytes);
			gzipOut.close();
			return baos.toByteArray();
				}

	}
	private static byte[] deCompressBytes(byte[] bytes) throws IOException
	{
		if (bytes != null && bytes.length > 0)
		{
			try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
					GZIPInputStream gzipIn = new GZIPInputStream(baos))
					{

				byte[] buffer = new byte[1024];
				int len;
				while ((len = gzipIn.read(buffer)) != -1) {
					bos.write(buffer, 0, len);				
				}
				gzipIn.close();
				return bos.toByteArray();
					}
		}
		else
		{
			return null;
		}
	}
	/**
	 * Enum declaring the system preference definition. If it's a user defined preference
	 * or a system defined preference.
	 */
	 public enum PrefDef{
		 SYSTEM_PREF, USER_PREF;
	}
	/**
	 * The type of object is being added into the preference.
	 */
	public enum ObjType {
		STRING, BOOLEAN, BYTE_ARRAY, DOUBLE, FLOAT, INT, LONG, OBJECT;
	}
}