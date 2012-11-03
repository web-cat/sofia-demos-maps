package sofia.demos.maps;

import java.util.ArrayList;
import java.util.List;

import sofia.app.ListScreen;
import android.app.Activity;

// -------------------------------------------------------------------------
/**
 * A launcher for the other demos in the project. The demos are presented in a
 * full screen list view.
 *
 * @author  Tony Allevato
 * @version 2011.12.04
 */
public class DemoLauncher extends ListScreen<DemoClass>
{
	//~ Fields ................................................................

	private static final List<DemoClass> demoList;
	static 
	{
		demoList = new ArrayList<DemoClass>();
		demoList.add(new DemoClass(MarkerDemo.class));
	}


	//~ Methods ...............................................................

	// ----------------------------------------------------------
	public void initialize()
	{
		addAll(demoList);
	}


	// ----------------------------------------------------------
	public void listViewItemClicked(DemoClass demoToLaunch)
	{
		presentScreen(demoToLaunch.getType());
	}
}


//-------------------------------------------------------------------------
/**
 * Wraps an activity class with a nice toString method so that it can be
 * displayed in the list view.
 */
class DemoClass
{
	//~ Fields ................................................................

	private Class<? extends Activity> type;

	
	//~ Constructors ..........................................................
	
	// ----------------------------------------------------------
	public DemoClass(Class<? extends Activity> type)
	{
		this.type = type;
	}


	//~ Methods ...............................................................

	// ----------------------------------------------------------
	public Class<? extends Activity> getType()
	{
		return type;
	}


	// ----------------------------------------------------------
	public String toString()
	{
		return type.getSimpleName();
	}
}
