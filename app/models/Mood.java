package models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum Mood {

	HAPPY,
	NORMAL,
	SAD,
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
                            put("sad", Mood.SAD);
                            put("happy", Mood.HAPPY);
                        }
                    });

	
}
