package sEProj.generate;

import java.io.PrintStream;

import sEProj.Component;
import sEProj.Date;
import sEProj.RRule;
import sEProj.TemporalComponent;
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
		for(Component event : cal.getComponent()) {
			doSwitch(event);
		}
		printer.println("END:VCALENDAR");
		return true;
	}
	
	@Override
	public Boolean caseComponent(Component component) {
		if(component instanceof TemporalComponent) {
			return caseTemporalComponent((TemporalComponent) component);
		}
		return false;
	}
	
	@Override
	public Boolean caseTemporalComponent(TemporalComponent component) {
		if(component instanceof VEvent) {
			return caseVEvent((VEvent) component);
		}
		return false;
	}
	
	@Override
	public Boolean caseVEvent(VEvent event) {
		printer.println("BEGIN:VEVENT");
		
		if(event.getDtstart()!= null) {
//			printer.print("DTSTART;"); 
//			printer.print("VALUE=DATE:");
			printer.print("DTSTART:"); 
			doSwitch(event.getDtstart());
		}
		
		if(event.getDtend()!= null) {
//			printer.print("DTEND;");
//			printer.print("VALUE=DATE:");
			printer.print("DTEND:");
			doSwitch(event.getDtend());
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
		if(event.getTransp()!= null && !event.getTransp().isEmpty()) {
			printer.println("TRANSP:"+event.getTransp());
		}
		if(event.getSummary()!= null && !event.getSummary().isEmpty()) {
		printer.println("SUMMARY:"+event.getSummary());
		}
		if(event.getDescription()!= null && !event.getDescription().isEmpty()) {
			printer.println("DESCRIPTION:"+event.getDescription());
		}
		
		//WARNING ne pas mettre en stirng???
		if(event.getLocation()!= null && !event.getLocation().isEmpty()) {
			printer.println("LOCATION:"+event.getLocation());
		}
		
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
		String dateToString = ""; //TODO mettre une methode qui tranforme en toString
		String year = Integer.toString(date.getYear());
		String month = Integer.toString(date.getMonth());
		String day = Integer.toString(date.getDay());
		
		int hourInt = date.getHour();
		if(hourInt > 2) {//TODO RECULER JOUR ECT... SI > 2
			hourInt -= 2;//PARIS AJOUTE 2H
		}	
		String hour = Integer.toString(hourInt);
		
		String min = Integer.toString(date.getMinute());
		
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
		
		if(!hour.equals("0") || !min.equals("0")) {//Si heure ou min renseignés
			dateToString+="T";
			
			while(hour.length()<2) {
				hour = "0"+hour;
			}
			
			while(min.length()<2) {
				min = "0"+min;
			}
			
			dateToString += hour+min+"00Z";
		}
		
		printer.print(dateToString);
		printer.println();
		return true;
	}

}
