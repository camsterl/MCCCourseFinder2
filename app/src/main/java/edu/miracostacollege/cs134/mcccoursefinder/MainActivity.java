package edu.miracostacollege.cs134.mcccoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.miracostacollege.cs134.mcccoursefinder.model.Course;
import edu.miracostacollege.cs134.mcccoursefinder.model.DBHelper;
import edu.miracostacollege.cs134.mcccoursefinder.model.Instructor;
import edu.miracostacollege.cs134.mcccoursefinder.model.Offering;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private static final String TAG = "MCC Course Finder";

    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;
    private OfferingListAdapter offeringsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allCoursesList = db.getAllCourses();
        allInstructorsList = db.getAllInstructors();
        allOfferingsList = db.getAllOfferings();
        //make a copy of filtered
        filteredOfferingsList = new ArrayList<>(allOfferingsList);

        //TODO (1): Construct instructorSpinnerAdapter using the method getInstructorNames()
        //TODO: to populate the spinner.
        instructorSpinner = findViewById(R.id.instructorSpinner);

        final ArrayAdapter<String> instructorSpinnerAdapater = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, getInstructorname());
        instructorSpinner.setAdapter(instructorSpinnerAdapater);
        instructorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedInstructorsName = String.valueOf(parent.getItemAtPosition(position));
                //check if selected instructor is selcet is=nstrucotr
                if(position > 0)
                {
                    //clear listadapter first then filter
                    offeringsListAdapter.clear();
                    for(Offering offering : allOfferingsList)
                    {
                        Instructor instructor = offering.getInstructor();
                        if(instructor.getFullName().equalsIgnoreCase(selectedInstructorsName))
                        {
                            offeringsListAdapter.add(offering);
                        }
                    }
                }
                else
                {
                    offeringsListAdapter.clear();
                    offeringsListAdapter.addAll(allOfferingsList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //wire up listview
        offeringsListView = findViewById(R.id.offeringsListView);
        offeringsListAdapter = new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList);

        //connect listview to listadapter
        offeringsListView.setAdapter(offeringsListAdapter);

        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String searchText = s.toString();
                offeringsListAdapter.clear();
                //loop thru offferings
                if (searchText.isEmpty()) {
                    offeringsListAdapter.addAll(allOfferingsList);
                    return;
                }
                for (Offering offering : allOfferingsList) {
                    if (offering.getCourse().getTitle().contains(searchText)) {
                        offeringsListAdapter.add(offering);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void reset(View view)
    {
        offeringsListAdapter.clear();
        offeringsListAdapter.addAll(allOfferingsList);

    }

    //TODO (2): Create a method getInstructorNames that returns a String[] containing the entry
    //TODO: "[SELECT INSTRUCTOR]" at position 0, followed by all the full instructor names in the
    //TODO: allInstructorsList
    private String[] getInstructorname()
    {
        String[] instructorName = new String[allInstructorsList.size() + 1];
        instructorName[0] = "[Select Instructor}";
        for(int i = 0; i< allInstructorsList.size(); i++)
        {
            instructorName[i+1] = allInstructorsList.get(i).getFullName();
        }
        return instructorName;
    }



    //TODO (3): Create a void method named reset that sets the test of the edit text back to an
    //TODO: empty string, sets the selection of the Spinner to 0 and clears out the offeringListAdapter,
    //TODO: then rebuild it with the allOfferingsList



    //TODO (4): Create a TextWatcher named courseTitleTextWatcher that will implement the onTextChanged method.
    //TODO: In this method, set the selection of the instructorSpinner to 0, then
    //TODO: Clear the offeringListAdapter
    //TODO: If the entry is an empty String "", the offeringListAdapter should addAll from the allOfferingsList
    //TODO: Else, the offeringListAdapter should add each Offering whose course title starts with the entry.



    //TODO (5): Create an AdapterView.OnItemSelectedListener named instructorSpinnerListener and implement
    //TODO: the onItemSelected method to do the following:
    //TODO: If the selectedInstructorName != "[Select Instructor]", clear the offeringListAdapter,
    //TODO: then rebuild it with every Offering that has an instructor whose full name equals the one selected.
}
