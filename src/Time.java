import java.time.LocalTime;

@javax.jdo.annotations.PersistenceCapable

public class Time
{
	// Represents a time in hour:minute. For example, 8:52 is 8:52 AM and 15:4 is 3:04 PM.

	int hour; // 0 -- 23
	int minute; // 0 -- 59

	public int differenceFrom(Time earlierTime)

	/* Returns the time difference in minutes between "earlierTime" and the target time object,
	   with "earlierTime" always regarded as the earlier time. Note that the time interval may include midnight.
           For example, suppose the target object represents 1:30. Then:

           if earlierTime = 23:30, the function will return 120 (minutes);
           if earlierTime = 0:30, the function will return 60 (minutes);
	   if earlierTime = 9:30, the function will return 960 (minutes), etc. */

	{
		int laterHour = earlierTime.hour;
		int laterMinute = earlierTime.minute;
		laterHour = hour - laterHour;
		laterMinute = minute - laterMinute;
		if (laterHour < 0) {
			laterHour = laterHour + 24;
		}
		if (laterMinute < 0) {
			laterMinute = laterMinute + 60;
			laterHour = laterHour - 1;
		}
		laterHour = laterHour*60;
		return laterMinute + laterHour;
	
	}

	public boolean isInInterval(int h1, int m1, int h2, int m2)

	/* Checks if the target time object is in the time interval from h1:m1 to h2:m2, inclusive,
	   with h1:m1 always regarded as earlier than h2:m2. Note that the interval may include midnight. */
	{
		//System.out.println(hour + " " + minute);
		//System.out.println(h1 + " " + m1 + " " + h2 + " " + m2);

		if ( (h1 < hour) && (h2 > hour)) return true;
		if (( h1 > hour) && ( h2 < hour)) return false;
		if (( h1 == hour) && (m1 == minute)) return true;
		if (( h1 == hour) && (m1 > minute)) return false;
		if (( h1 == hour ) && (m1 < minute)) return true;
		if ((h1 > hour)) return false;
			
		if (h1 > hour) return false;
		if ( (h1 == hour) && (m1 > minute)) return false;
		
		int count;
		if (h2 < h1) {
			h2 = h2 + 24;
			count = h2 - h1;
			
			if ((h1 + count) > hour) {
				return true;
			}	
		}
		else {
			count = h2 - h1;
			if (( h1 + count) > hour) {
				return false;
			}
		}
		if (( h2 < hour)) return false;
		return true; 
	}
}