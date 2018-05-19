import java.util.*;
import javax.jdo.*;

@javax.jdo.annotations.PersistenceCapable

public class Flight
{
	String airlineCompanyName;
	String flightNum; // { airlineCompanyName, flightNum } is a key
	Airport origin; 
	Airport destination;
	Time departTime;
	Time arriveTime;


	public String toString()
	{
		return airlineCompanyName+" "+flightNum+" "+
		       origin.name+" "+departTime.hour+":"+departTime.minute+" "+
		       destination.name+" "+arriveTime.hour+":"+arriveTime.minute ;
	}

	public static Flight find(String airlineCompanyName, String flightNum, PersistenceManager pm)

	/* Returns the flight that has the two parameter values; returns null if no such flight exists.
           { airlineCompanyName, flightNum } is assumed to be a key for Flight class.
	   The function is applied to the database held by the persistence manager "pm". */

	{
		//System.out.println(airlineCompanyName + " " + flightNum);
		Query q = pm.newQuery(Flight.class);
		q.declareParameters("String name, String number");
		q.setFilter("this.airlineCompanyName == name &&"  + "this.flightNum == number");
		//q.setFilter("this.airlineCompanyName == \"name\" &&"  + "this.flightNum == \"number\"");
		Collection<Flight> list = (Collection<Flight>) q.execute(new String(airlineCompanyName), new String(flightNum));
		Flight f = Utility.extract(list);
		q.close(list);
		//System.out.println(f);
		return f;

	}

	public static Collection<Flight> getFlights(String a1, String a2, Query q)
	
	/* Given airport names a1 and a2, the function returns the collection of
	   all flights departing from a1 and arriving to a2.
	   Sort the result by (airlineCompanyName, flightNum). */

	{
		q.setClass(Flight.class);
		q.declareParameters("String a11, String a22");
		q.setFilter("this.origin.name == a11 &&" + "this.destination.name == a22");
		q.setOrdering("this.airlineCompanyName, this.flightNum");
		return (Collection<Flight>) q.execute(a1, a2);

	}

	public static Collection<Flight> getFlightsForCities(String c1, String c2, Query q)

	/* Given city names c1 and c2, the function returns the collection of all flights 
	   departing from an airport close to c1 and arriving to an airport close to c2. 
	   Sort the result by (airlineCompanyName, flightNum). */

	{
		q.setClass(Flight.class);
        q.declareParameters("String c11, String c22");
        q.declareVariables("City city, city2");
        q.setFilter("(this.origin.closeTo.contains(city) && city.name == c11) &&" + "(this.destination.closeTo.contains(city2)) && " + "city2.name == c22");
        q.setOrdering("this.airlineCompanyName, this.flightNum");
        return (Collection<Flight>) q.execute(c1,c2);
	}

	public static Collection<Flight> getFlightsDepartTime(
		  String a1, String a2, int h1, int m1, int h2, int m2, Query q)

	/* Given airport names a1 and a2 and times h1:m1 and h2:m2,
	   the function returns the collection of all flights departing from a1 and arriving to a2
	   satisfying the condition that the departure time is h1:m1 at earliest and h2:m2 at latest.
	   Note that the time interval from h1:m1 to h2:m2 may include midnight.
	   Sort the result by (airlineCompanyName, flightNum). */

	{	
		q.setClass(Flight.class);
        q.declareParameters("String a11, String a22, int h11, int m11, int h22, int m22");
        //q.declareVariables("City city, city2");
        q.setFilter("(this.origin.name == a11) &&" + "(this.destination.name == a22) && " + "(this.departTime.isInInterval(h11,m11,h22,m22))");
        //q.setOrdering("this.airlineCompanyName, this.flightNum");
        Object[] object = new Object[] {a1,a2, new Integer(h1), new Integer(m1), new Integer(h2), new Integer(m2)};
        return (Collection<Flight>) q.executeWithArray(object);
	}

	public static Collection<Object[]> getFlightsConnection(
		  String a1, String a2, int h1, int m1, int h2, int m2,
		  int connectionAtLeast, int connectionAtMost, Query q)

	/* Given airport names a1 and a2, times h1:m1 and h2:m2, and connection time lower and upper bounds in minutes,
	   connectionAtLeast and connectionAtMost, the function returns the pairs of all flights f and f1 satisfying
	   the following conditions:

	   1. f departs from a1 and arrives to a connecting airport "ca" different from a2; and
	   2. The departure time of f is h1:m1 at earliest and h2:m2 at latest; and
	   3. There is a second flight f1 from "ca" to a2; and
	   4. The connecting time, i.e. the time interval in minutes between 
	      the arrival time of f and the departure time of f1, is at least connectionAtLeast
	      and at most connectionAtMost. 

	   Note again that the relevant time intervals may include midnight.
	   Sort the result by (f.airlineCompanyName, f.flightNum, f1.airlineCompanyName, f1.flightNum). */

	{
		
		q.setClass(Flight.class);
        q.declareParameters("String a11, String a22, int h11, int m11, int h22, int m22, int connectionAtLeast1, int connectionAtMost1");
        q.declareVariables("Flight f; Flight f1; Airport ca");;
        q.setFilter("f.origin.name == a11 && f.destination == ca &&" + "f1.origin == ca && f1.destination.name == a22 &&" + "f.departTime.isInInterval(h11,m11,h22,m22) &&" 
        + "(f1.departTime.differenceFrom(f.arriveTime)) <= connectionAtMost1 && " + "f1.departTime.differenceFrom(f.arriveTime) >= connectionAtLeast1");
        q.setResult("distinct f, f1");
        q.setOrdering("f, f1");
        Object[] object = new Object[]{a1,a2,new Integer(h1),new Integer(m1), new Integer(h2), new Integer(m2), new Integer(connectionAtLeast), new Integer(connectionAtMost)};
       
        return (Collection<Object[]>) q.executeWithArray(object);     
	}

	public static Collection<Object[]> groupByCompany(Query q)

	/* Group the flights by their airline company names.
	   Then return the set of 2-tuples <airlineCompanyName: String, num: int> where:

	   num = the total number of flights operated by airlineCompanyName

	   Sort the result by airlineCompanyName. */

	{
		q.setClass(Flight.class);
        q.setGrouping("airlineCompanyName");
        q.setResult("airlineCompanyName, count(airlineCompanyName)");
        q.setOrdering("airlineCompanyName");

        return (Collection<Object[]>) q.execute();
	}
}
