package edu.orangecoastcollege.cs273.occcoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CourseSearchActivity extends AppCompatActivity {

    private DBHelper db;
    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;

    private OfferingListAdapter offeringListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allOfferingsList = db.getAllOfferings();
        filteredOfferingsList = new ArrayList<>(allOfferingsList);
        allInstructorsList = db.getAllInstructors();
        allCoursesList = db.getAllCourses();

        courseTitleEditText = (EditText) findViewById(R.id.courseTitleEditText);


        //////////////////////////////////
        instructorSpinner = (Spinner) findViewById(R.id.instructorSpinner);
        ArrayAdapter<String> instructorSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, getAllInstrutorName());
        instructorSpinner.setAdapter(instructorSpinnerAdapter);
        instructorSpinner.setOnItemSelectedListener(instructorSpinnerListener);
        //////////////////////////


        courseTitleEditText.addTextChangedListener(courseTitleTextWatcher);
        offeringsListView = (ListView) findViewById(R.id.offeringsListView);
        offeringListAdapter =
                new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList);
        offeringsListView.setAdapter(offeringListAdapter);




        /*
       Cursor instructorNamesCursor = db.getInstructorNamesCursor();
        SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_spinner_item,
                        instructorNamesCursor,
                        new String[] {DBHelper.FIELD_LAST_NAME},
                        new int[] {android.R.id.text1}, 0);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorSpinner.setAdapter(cursorAdapter);
        */
    }


    private String[] getAllInstrutorName()
    {
        String [] instructorName = new String[allInstructorsList.size()+1];
        instructorName[0] = "[Select Instructor]";
        for (int i = 1;  i< instructorName.length; i++)
        {
            instructorName[i]= allInstructorsList.get(i-1).getFullName();
        }
        return instructorName;
    }

    public TextWatcher courseTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String instructorName = instructorSpinner.getSelectedItem().toString();
            String input = charSequence.toString().toLowerCase();

            if (input.equals("") && instructorName.equals("[Select Instructor]"))
            {
                // Repopulate the list adapter with all offerings
                offeringListAdapter.clear();
                for(Offering offering : allOfferingsList)
                {
                    offeringListAdapter.add(offering);
                }
            }
            else if(instructorName.equals("[Select Instructor]"))
            {
                offeringListAdapter.clear();
                for(Offering offering : allOfferingsList)
                {
                    //if the course title star with the user input, add it to the list adapter
                    Course course = offering.getCourse();

                    if(course.getTitle().toLowerCase().contains(input))
                    {
                        offeringListAdapter.add(offering);
                    }
                }
            }
            else
            {
                offeringListAdapter.clear();
                for (Offering offering : allOfferingsList) {
                    //if the course title star with the user input, add it to the list adapter
                    Course course = offering.getCourse();
                    Instructor instructor = offering.getInstructor();

                    if (course.getTitle().toLowerCase().contains(input) && instructor.getFullName().equals(instructorName)) {
                        offeringListAdapter.add(offering);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    public AdapterView.OnItemSelectedListener instructorSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selectedInstructorName = adapterView.getItemAtPosition(i).toString();
            offeringListAdapter.clear();
            if(selectedInstructorName.equals("[Select Instructor]"))
            {

                for(Offering offering : allOfferingsList)
                {
                    offeringListAdapter.add(offering);
                }
            }
            else
            {

                for(Offering offering : allOfferingsList)
                {
                    //if the course title star with the user input, add it to the list adapter
                    Instructor instructor = offering.getInstructor();

                    if(instructor.getFullName().equals(selectedInstructorName))
                    {
                        offeringListAdapter.add(offering);
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            adapterView.setSelection(0);
        }
    };
    public void reset(View view)
    {
        courseTitleEditText.setText("");
        instructorSpinner.setSelection(0);
    }
}
