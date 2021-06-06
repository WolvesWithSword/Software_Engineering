package SEProj.design;

import java.time.LocalDateTime;
import java.util.UUID;

import org.eclipse.emf.ecore.EObject;

import sEProj.Component;
import sEProj.Date;
import sEProj.RRule;
import sEProj.SEProjFactory;
import sEProj.TemporalComponent;
import sEProj.VCalendar;
import sEProj.VEvent;

/**
 * The services class used by VSM.
 */
public class Services {
    
    /**
    * See http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.sirius.doc%2Fdoc%2Findex.html&cp=24 for documentation on how to write service methods.
    */
    public EObject myService(EObject self, String arg) {
       // TODO Auto-generated code
      return self;
    }
    
    public EObject initTemporalComponent(EObject self) {
//    	Calendar cal =(Calendar) self.eContainer();
    	TemporalComponent event = (TemporalComponent) self;
    	String uid = UUID.randomUUID().toString();
    	event.setUID(uid);
    	event.setCreated(generateCurrentDate());
    	event.setDtstamp(generateCurrentDate());
		return self;
    	
    }
    
    private Date generateCurrentDate() {
    	Date date = SEProjFactory.eINSTANCE.createDate();
    	LocalDateTime now = LocalDateTime.now();  
    	date.setDay(now.getDayOfMonth());
    	date.setMonth(now.getMonthValue());
    	date.setYear(now.getYear());
    	date.setHour(now.getHour());
    	date.setMinute(now.getMinute());
    	return date;
    }
    
    public boolean isTemporal(EObject self) {
    	return self instanceof TemporalComponent;
    }
    
    public boolean isInstanceOfRRule(EObject self) {
    	return self instanceof RRule;
    }
    
    public boolean isInstanceOfComponent(EObject self) {
    	return self instanceof Component;
    }
    
    public boolean isInstanceVEvent(EObject self) {
    	return self instanceof VEvent;
    }
    
    public void deleteRRule(EObject self) {
    	TemporalComponent comp = (TemporalComponent) self.eContainer();
    	comp.setRrule(null);
    	comp.getExdate().clear(); //on a plus de date a exclure
    }
    
    public void deleteComponent(EObject self) {
    	VCalendar cal = (VCalendar) self.eContainer();
    	cal.getComponent().remove(self);
    }
    

}
