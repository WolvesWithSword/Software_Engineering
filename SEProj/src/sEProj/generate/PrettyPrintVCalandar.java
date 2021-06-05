package sEProj.generate;

import java.io.PrintStream;
import java.util.stream.Collectors;

import sEProj.Component;
import sEProj.DAY;
import sEProj.Date;
import sEProj.RRule;
import sEProj.StatusEnum;
import sEProj.TemporalComponent;
import sEProj.VCalendar;
import sEProj.VEvent;
import sEProj.VJournal;
import sEProj.VTODO;
import sEProj.VTimeZone;
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
		if(component instanceof VTimeZone) {
			return caseVTimeZone((VTimeZone) component);
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
		
		if(component.getStatus()!=StatusEnum.UNDEFINED) {
			printer.println("STATUS:"+component.getStatus()); 
		}
		
		if(component.getRrule()!=null) {
			doSwitch(component.getRrule());
		}
		
		if(component.getExdate() != null) {
			printer.println("EXDATE:"+component.getExdate().stream().map((elt)->elt.toString()).collect(Collectors.joining(",")));
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
		if(rRule.getEntryDayNumber()!=null) {
			sb.append("BYDAY=");
			for(int i = 0; i<rRule.getEntryDayNumber().size();i++) {
				if(i>0) sb.append(",");
				int dayNumber = rRule.getEntryDayNumber().get(i).getByDayNumber();
				DAY day = rRule.getEntryDayNumber().get(i).getByDay();
				if(dayNumber!=0) {
					sb.append(dayNumber);
				}
				sb.append(day.getLiteral());
			}
			sb.append(";");
		}

		printer.println(sb.substring(0, sb.length()-1));//remove le dernier ";"
		
		//ajout de UTIL???
		return true;
	}
	
	@Override
	public Boolean caseVTimeZone(VTimeZone timeZone) {
		printer.println("BEGIN:VTIMEZONE");
		if(timeZone.getTZID()!=null && !timeZone.getTZID().isEmpty()) {
			printer.println("TZID:"+timeZone.getTZID());
		} else {
			printer.println("TZID:Europe/Paris");
		}
		if(timeZone.getTZURL()!=null && !timeZone.getTZURL().isEmpty()) {
			printer.println("TZURL:"+timeZone.getTZURL());
		}
		printer.println("END:VTIMEZONE");
		return true;
	}
	
	@Override
	public Boolean caseDate(Date date) {
		printer.print(date.toString());
		printer.println();
		return true;
	}
	
	

}
