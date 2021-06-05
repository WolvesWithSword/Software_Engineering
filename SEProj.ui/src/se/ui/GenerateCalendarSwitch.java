package se.ui;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.testing.util.ParseHelper;
import sEProj.VCalendar;
import sEProj.generate.PrettyPrintVCalandar;
import seproj.CalStandaloneSetup;

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
			} 
			else if(o instanceof IFile) {
				IResource ressource  = (IResource) ss.getFirstElement();
				File file = new File(ressource.getLocationURI().getPath().toString());
				try {
					String content = readFromInputStream(new FileInputStream(file));
					Injector injector = new CalStandaloneSetup().createInjectorAndDoEMFRegistration();
					ParseCalandar parser = injector.getInstance(ParseCalandar.class);
					VCalendar cal = parser.doGetModel(content);
					treatProgram(cal);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				System.err.println("Unknown type : " + o);
			}
		}
		return null;
	}

	private void treatProgram(VCalendar cal) {
		new PrettyPrintVCalandar(System.out).doSwitch(cal);
		
	}
	
	private String readFromInputStream(InputStream inputStream)
	  throws IOException {
	    StringBuilder resultStringBuilder = new StringBuilder();
	    try (BufferedReader br
	      = new BufferedReader(new InputStreamReader(inputStream))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            resultStringBuilder.append(line).append("\n");
	        }
	    }
	  return resultStringBuilder.toString();
	}
	
	private static class ParseCalandar{
	    @Inject ParseHelper<VCalendar> parserHelper;
	    
	    public VCalendar doGetModel(String content) throws Exception {
	        return parserHelper.parse(content);
	    }
	}

}
