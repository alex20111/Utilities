package home.misc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class CVSUtils {

	public static String objectToCVS(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		StringBuilder sb = new StringBuilder();
		
		for (Method method : obj.getClass().getDeclaredMethods()) {
		    if (Modifier.isPublic(method.getModifiers())
		        && method.getParameterTypes().length == 0
		        && method.getReturnType() != void.class
		    ) {
		        Object value = method.invoke(obj);
		        if (value != null) {
		        	sb.append(value + ",");
		        }else{
		        	sb.append(" " + ",");
		        }		      
		    }
		}
		return sb.toString();
	}
	
	public static String objectListToCVS(List<Object> objs, String seperateWith) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		StringBuilder sb = new StringBuilder();
		
		for(Object obj : objs){
			for (Method method : obj.getClass().getDeclaredMethods()) {
				if (Modifier.isPublic(method.getModifiers())
						&& method.getParameterTypes().length == 0
						&& method.getReturnType() != void.class
						) {
					Object value = method.invoke(obj);
					if (value != null) {
						sb.append(value + seperateWith);
					}else{
						sb.append(" " + seperateWith);
					}		      
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}	
}
