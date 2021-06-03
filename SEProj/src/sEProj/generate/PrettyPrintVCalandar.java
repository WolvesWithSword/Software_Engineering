package sEProj.generate;

import java.io.PrintStream;

import sEProj.Date;
import sEProj.RRule;
import sEProj.VCalendar;
import sEProj.VEvent;
import sEProj.util.SEProjSwitch;

public class PrettyPrintVCalandar extends SEProjSwitch<Boolean>{
	PrintStream printer;
	
	public PrettyPrintVCalandar(PrintStream prinet){
		this.printer = prinet;
	}
	
	@Override
	public Boolean caseVCalendar(VCalendar cal) {
		printer.println("BEGIN:VCALENDAR");
		printer.println("PRODID:"+cal.getProdid());
		printer.println("VERSION:"+cal.getVersion());
		printer.println("CALSCALE:"+cal.getCalscale());
		printer.println("METHOD:"+cal.getMethod());
		for(VEvent event : cal.getEvents()) {
			doSwitch(event);
		}
		printer.println("END:VCALENDAR");
		return true;
	}
	
	@Override
	public Boolean caseVEvent(VEvent event) {
		printer.println("BEGIN:VEVENT");
		
		if(event.getDtstart()!= null) {
			printer.print("DTSTART;"); 
			doSwitch(event.getDtstart());
		}
		
		if(event.getDtend()!= null) {
			printer.print("DTEND;"); 
			doSwitch(event.getDtstart());
		}
		
		if(event.getDtstamp()!= null) {
			//probleme de representation
			printer.print("DTSTAMP:"); 
			doSwitch(event.getDtstamp());
		}
		
		if(event.getCreated()!= null) {
			//WARNING pb comme dtStamp
			printer.print("CREATED:"); 
			doSwitch(event.getCreated());
		}
		
		if(event.getLast_modified()!= null) {
			//WARNING pb comme dtStamp
			printer.print("LAST-MODIFIED:"); 
			doSwitch(event.getLast_modified());
		}
		
		//peut etre generer le UID avant
		printer.println("UID:"+event.getUID()); 
		printer.println("TRANSP:"+event.getTransp());
		printer.println("SUMMARY:"+event.getSummary());
		printer.println("DESCRIPTION:"+event.getDescription());
		
		//WARNING ne pas mettre en stirng???
		printer.println("LOCATION;"+event.getLocation());
		
		if(event.getRrule()!=null) {
			doSwitch(event.getRrule());
		}
		
		printer.println("END:VEVENT");
		return true;
	}
	
	@Override
	public Boolean caseRRule(RRule rRule) {
		if(rRule == null) return false;
		printer.print("RRULE:");
		if(rRule.getFreq()!=null) {
			printer.print("FREQ="+rRule.getFreq().getLiteral());
		}
		if(rRule.getCount()!=0) {
			printer.print("COUNT="+rRule.getCount());
		}
		if(rRule.getInterval()!=0) {
			printer.print("INTERVAL="+rRule.getInterval());
		}
		//ajout de UTIL???
		return true;
	}
	
	@Override
	public Boolean caseDate(Date date) {
		printer.print("VALUE=DATE:");
		String dateToString = ""; //TODO mettre une methode qui tranforme en toString
		String year = Integer.toString(date.getYear());
		String month = Integer.toString(date.getMonth());
		String day = Integer.toString(date.getDay());
		while(year.length()<4) {
			year = "0"+year;
		}
		while(month.length()<2) {
			month = "0"+month;
		}
		while(day.length()<2) {
			day = "0"+day;
		}
		dateToString = year+month+day;
		printer.print(dateToString);
		printer.println();
		return true;
	}

}
