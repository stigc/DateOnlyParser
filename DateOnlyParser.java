import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
* Strict date only parser (String to Date conversion)
* 
* Use 'd', 'm' or 'y' to pattern match date parts
*  'd': day of month
*  'm': month
*  'y': year 
*  
* - 1 char will match a variable length date part 
* - 2 or more chars will match a fixed length date part
* - If date part is missing in the pattern the date part will get default 1
* - Delimiters can be everything but 'd', 'm' or 'y'
* - Calendar is always GregorianCalendar
* - Uppercase 'D', 'M' or 'Y' also matches date parts
* 
* Parseable samples 
*  	"d-m-y" 	  	"1-11-1974"
*  	"d.m.y" 	  	"01.11.1974"
*  	"d m y"			"1 11 1974"
*  	"dd/mm-yyyy"	"01/11-1974"
*	"yyyymmdd"		"19741101"
*	"y"				"1974"
* 
* Unparsable samples (throws ParseException)
*  	"d-m-y" 	  	"1-11/1974"
*  	"dd-mm-yyyy" 	"1-11-1974"
*  	"d-m-y"			"19-11-1974 "
*  	"d-m-y"			" 19-11-1974"
*  	"y"				"1-11-1974" 
*  
* @version 1.0
*/
public class DateOnlyParser
{
	private final int patternLength;
	private final char[] patternChars;
	private final GregorianCalendar cal =  new GregorianCalendar();
	private final int maxYear = cal.getMaximum(Calendar.YEAR);
	
	public DateOnlyParser(String pattern) throws NullPointerException, IllegalArgumentException
	{
		if (pattern == null) 
			throw new NullPointerException(pattern);
	
		boolean styleDetected = false;
		
		patternChars = pattern.toCharArray();
		patternLength = patternChars.length;
		
		for (int i = 0; i<patternLength; i++)
		{
		    char c = Character.toLowerCase(patternChars[i]);
		    if (c == 'y' || c == 'm' || c == 'd')
		    {
		    	patternChars[i] = c;
		    	styleDetected = true;
		    }
		}
		
		if (!styleDetected)
			throw new IllegalArgumentException("Format should contain d, m or y");
		
		cal.clear(); //removes time
	}
	
	private final boolean isDigit(char c)
	{
		return c >= '0' && c <= '9';
	}
	
	public Date parse(String val) throws NullPointerException, ParseException
	{
		if (val == null) 
			throw new NullPointerException(val);
		
		final char[] vchars = val.toCharArray();
		final int vlength = vchars.length; 
		
		int vindx = 0, year = 1, month = 1, day = 1;

		for (int pidx = 0; pidx<patternLength; pidx++)
		{
	    	if (vindx == vlength)
	    		throwParseException("Unexpected end of String", val, vindx);	    			
	    	
		    char c = patternChars[pidx];
		    if (c == 'y' || c == 'm' || c == 'd')
		    {
		    	int tokenLength = 1;
		    	
		    	//detect repeating matching patterns e.g yyyy
		    	while (pidx<patternLength-1 && patternChars[pidx+1] == c) 
		    	{
		    		pidx++;
		    		tokenLength++;
		    	}
	    		
		    	//read first digit
	    		if (!isDigit(vchars[vindx]))
	    			throwParseException("Expected digit", val, vindx);
	    		int n = vchars[vindx] - '0';
	    		vindx++;
	    		
	    		//read fixed digits
	    		if (tokenLength > 1)
	    		{
		    		while (tokenLength > 1)
		    		{
			    		if (vindx == vlength || !isDigit(vchars[vindx]))
			    			throwParseException("Expected digit", val, vindx);	    			
		    			n *= 10;
		    			n += vchars[vindx] - '0';
		    			if (n < 0)
		    				throwParseException("Number overflow", val, vindx);	    			
		    			tokenLength--;
		    			vindx++;		    			
		    		}
	    		}
	    		
	    		//read variable digits	    		
	    		else
	    		{
	    			while(vindx < vlength && isDigit(vchars[vindx]))
	    			{
		    			n *= 10;
		    			n += vchars[vindx] - '0';	
		    			if (n < 0)
		    				throwParseException("Number overflow", val, vindx);	    			
		    			vindx++;
	    			}
	    		}

	    		if (c == 'y') 
	    			year = n;
		    	else if (c == 'm') 
		    		month = n;
		    	else if (c == 'd') 
		    		day = n;
		    }
		    else 
		    {
		    	if (vchars[vindx] == c)
		    		vindx++;
		    	else 
    				throwParseException("Expected '" + c + "'", val, vindx);	    			
		    }	
		}
		
		if (vindx < vlength)
			throwParseException("Unexpected trailing string '" + val.substring(vindx) + "'", val, vindx);	    	

		if (day == 0 || day > 31)
			throwParseException("Illegal day " + day, val);
		
		if (month == 0 || month > 12)
			throwParseException("Illegal month " + month, val);
		
		if (year == 0 || year > maxYear)
			throwParseException("Illegal year " + year, val);

		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		
		if (cal.get(Calendar.DAY_OF_MONTH) != day)
			throwParseException("Illegal day " + day, val);
		
		return cal.getTime();
	}
	
	private void throwParseException(String msg, String val) throws ParseException
	{
		throwParseException(msg, val, -1);
	}
	
	private void throwParseException(String msg, String val, int vindx) throws ParseException
	{
		StringBuilder sb = new StringBuilder("Unparseable date \"" + val + "\""); 
		sb.append(" using pattern \"" + new String(patternChars) + "\""); 
		sb.append(". " + msg);
		if (vindx > -1)
			sb.append(" at index " + vindx);
		throw new ParseException(sb.toString(), vindx);
	}
}
