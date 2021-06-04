package sEProj.generate;

import java.io.PrintStream;

import sEProj.Component;
import sEProj.Date;
import sEProj.RRule;
import sEProj.TemporalComponent;
import sEProj.VCalendar;
import sEProj.VEvent;
import sEProj.VJournal;
import sEProj.VTODO;
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
		printer.println("END:VCALENDAR\n");
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
		
		if(component instanceof VTODO) {
			return caseVTODO((VTODO) component);
		}
		
		if(component instanceof VJournal) {
			return caseVJournal((VJournal) component);
		}
		
		return false;
	}
	
	public void printTemporalComponent(TemporalComponent component) {
		
		//peut etre generer le UID avant
		printer.println("UID:"+component.getUID()); 
		
		if(component.getDtstamp()!= null) {
			printer.print("DTSTAMP:"); 
			doSwitch(component.getDtstamp());
		}
		
		if(component.getDtstart()!= null) {
//			printer.print("DTSTART;"); 
//			printer.print("VALUE=DATE:");
			printer.print("DTSTART:"); 
			doSwitch(component.getDtstart());
		}
		
		if(component.getSummary()!= null && !component.getSummary().isEmpty()) {
			printer.println("SUMMARY:"+component.getSummary());
		}
		
		if(component.getDescription()!= null && !component.getDescription().isEmpty()) {
			printer.println("DESCRIPTION:"+component.getDescription());
		}
		
		if(component.getCreated()!= null) {
			printer.print("CREATED:"); 
			doSwitch(component.getCreated());
		}
		
		if(component.getLast_modified()!= null) {
			printer.print("LAST-MODIFIED:"); 
			doSwitch(component.getLast_modified());
		}
		
		if(component.getRrule()!=null) {
			doSwitch(component.getRrule());
		}
		
	}
	
	@Override
	public Boolean caseVEvent(VEvent event) {
		printer.println("BEGIN:VEVENT");
		
		//Attribut communs
		printTemporalComponent(event);
		
		if(event.getDtend()!= null) {
//			printer.print("DTEND;");
//			printer.print("VALUE=DATE:");
			printer.print("DTEND:");
			doSwitch(event.getDtend());
		}
		
		if(event.getTransp()!= null && !event.getTransp().isEmpty()) {
			printer.println("TRANSP:"+event.getTransp());
		}
		
		if(event.getLocation()!= null && !event.getLocation().isEmpty()) {
			printer.println("LOCATION:"+event.getLocation());
		}
		
		printer.println("END:VEVENT");
		return true;
	}
	
	@Override
	public Boolean caseVTODO(VTODO todo) {
		printer.println("BEGIN:VTODO");
		
		//Attribut communs
		printTemporalComponent(todo);
		
		if(todo.getDue()!= null) {
//			printer.print("DTEND;");
//			printer.print("VALUE=DATE:");
			printer.print("DUE:");
			doSwitch(todo.getDue());
		}
		
		if(todo.getPriority()!= 0) {//? A voir l'interêt d'une prio de 0
			printer.println("PRIORITY:"+todo.getPriority());
		}
		
		if(todo.getLocation()!= null && !todo.getLocation().isEmpty()) {
			printer.println("LOCATION:"+todo.getLocation());
		}
		
		printer.println("END:VTODO");
		return true;
	}
	
	@Override 
	public Boolean caseVJournal(VJournal journal) {
		
		printer.println("BEGIN:VJOURNAL");
		
		printTemporalComponent(journal);
		
		printer.println("END:VJOURNAL");
		return true;
	}
	
	@Override
	public Boolean caseRRule(RRule rRule) {
		if(rRule == null) return false;
		printer.print("RRULE:");
		
		StringBuilder sb = new StringBuilder();
		
		if(rRule.getFreq()!=null) {
			sb.append("FREQ=").append(rRule.getFreq().getLiteral()).append(";");
		}
		if(rRule.getCount()!=0) {
			sb.append("COUNT=").append(rRule.getCount()).append(";");
		}
		if(rRule.getInterval()!=0) {
			sb.append("INTERVAL=").append(rRule.getInterval()).append(";");
		}

		printer.println(sb.substring(0, sb.length()-2));//remove le dernier ";"
		
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
