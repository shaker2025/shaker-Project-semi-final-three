package edu.oakland.payroll;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//This is the Database Helper
public class EIHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static String DB_PATH = "/data/data/edu.oakland.payroll/databases/";
    private  static String DB_NAME = "payroll";
    private static final String EMPLOYEE_TABLE = "employeeInfo";
    private static final String employeeName = "employeeName";
    private static final String role = "role";
    private static final String employeeID = "employeeID";
    private static final String hourlyRate = "hourlyRate";
    private static final String SALARY_TABLE = "salaryInfor";
    private static final String payrollID = "payrollID";
    private static final String hoursWorked = "hoursWorked";
    private static final String workedOvertime = "workedOvertime";

    private SQLiteDatabase db;
    private Context dbContext;


    public EIHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.dbContext = context;
    }
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        }else{
            this.getReadableDatabase();
            try{
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database.");
            }
        }
    }
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            //database doesn't exist yet.
            int i = 0;
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null?true:false;
    }
    private void copyDataBase() throws IOException{
        InputStream myInput = dbContext.getAssets().open(DB_NAME+".db");
        String outFileName =  DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    //creates Salary Calculator's ListView
    public boolean updateEmployee(String id, String hoursInput){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d(TAG, "addData: Adding " + id + " to " + DB_NAME + " database");
        values.put(hoursWorked, hoursInput);

        if (Integer.parseInt(hoursInput) > 40){
            values.put(workedOvertime, "Yes");
        }else{
            values.put(workedOvertime, "No");
        }
        long result = db.update(SALARY_TABLE, values, "payrollID = ?", new String[]{id});
        if (result == 0) {
            return false;
        } else {
            return true;
        }
    }

    // Add Employee Activity: adds employee to Employee Information Table
    public boolean addEmployee(String id, String name, String rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d(TAG, "addData: Adding " + name + " to " + DB_NAME + " database");
        values.put(employeeID, id);
        values.put(employeeName, name);
        values.put(hourlyRate, rate);
        values.put(role, "Materials Expert I");

        long result = db.insert(EMPLOYEE_TABLE, null, values);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // Add Employee Activity: adds defaults for Salary Information Table
    public boolean addSalary(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values2 = new ContentValues();
        Log.d(TAG, "addData: Adding " + payrollID + " to " + DB_NAME + " database");
        values2.put(workedOvertime, "No");
        values2.put(hoursWorked, "0");
        values2.put(payrollID, id);
        values2.put(employeeID, id);
        long result2 = db.insert(SALARY_TABLE, null, values2);
        if (result2 == -1) {
            return false;
        } else {
            return true;
        }
    }

    public synchronized void close() {
        if (db != null) db.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
    }
}
