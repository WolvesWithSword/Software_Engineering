package se.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

import sEProj.VCalendar;
import sEProj.generate.PrettyPrintVCalandar;

public class GenerateCalendarSwitch extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		StructuredSelection ss = (StructuredSelection)selection;
		for (Object o : ss) {
			if (o instanceof VCalendar) {
				VCalendar cal = (VCalendar)o;
				treatProgram(cal);
			} else {
				System.err.println("Unknown type : " + o);
			}
		}
		return null;
	}

	private void treatProgram(VCalendar cal) {
		new PrettyPrintVCalandar(System.out).doSwitch(cal);
		
	}

}
