package models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum Mood {

	HAPPY,
	NORMAL,
	SAID,
	ANGRY;

	public static Mood fromString(String status) {
		return STRINGTOENUMMAP.get(status);
	}
	
	public static final Map<String,Mood> STRINGTOENUMMAP = 
			Collections.unmodifiableMap(
                    new HashMap<String, Mood>() {
                        {
                            put("angry", Mood.ANGRY);
                            put("normal", Mood.NORMAL);
                            put("said", Mood.SAID);
                            put("happy", Mood.HAPPY);
                        }
                    });

	
}
