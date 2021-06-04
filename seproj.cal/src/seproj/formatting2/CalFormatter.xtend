/*
 * generated by Xtext 2.22.0
 */
package seproj.formatting2

import com.google.inject.Inject
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument
import sEProj.VCalendar
import sEProj.VEvent
import seproj.services.CalGrammarAccess

class CalFormatter extends AbstractFormatter2 {
	
	@Inject extension CalGrammarAccess

	def dispatch void format(VCalendar vCalendar, extension IFormattableDocument document) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (component : vCalendar.component) {
			component.format
		}
	}

	def dispatch void format(VEvent vEvent, extension IFormattableDocument document) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		vEvent.dtstamp.format
		vEvent.dtstart.format
		vEvent.dtend.format
		vEvent.created.format
		vEvent.last_modified.format
		vEvent.rrule.format
	}
	
	// TODO: implement for VTODO, VJournal
}
