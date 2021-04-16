package com.pallotta.andrea.business;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import companydata.DataLayer;
import companydata.Timecard;

public class BusinessLayer {
    
    String companyName;
    DataLayer dl;

    /**
     * Parameterized constructor
     * @param companyName
     */
    public BusinessLayer(String companyName) {
        this.companyName = companyName;
        this.dl = new DataLayer(companyName);
    }

    /**
     * Check if department exists.
     * @param deptId
     * @return boolean
     */
    public boolean deptExists(int deptId) {
        return dl.getDepartment(this.companyName, deptId) == null ? false : true;
    }

    /**
     * Check if mngId exists.
     * @param mngId
     * @return boolean
     */
    public boolean mngIdExists(int mngId) {
        return dl.getEmployee(mngId) == null ? false : true;
    }

    /**
     * Check if employee id exists.
     * @param empId
     * @return boolean
     */
    public boolean empIdExists(int empId) {
        return dl.getEmployee(empId) == null ? false : true;
    }

    /**
     * Check if timecard id exists.
     * @param timecardId
     * @return boolean
     */
    public boolean timecardIdExists(int timecardId) {
        return dl.getTimecard(timecardId) == null ? false : true;
    }

    /**
     * Check if a department number exists.
     * @param deptNo
     * @return boolean
     */
    public boolean isDeptNoUnique(String deptNo) {
        return dl.getAllDepartment(this.companyName)
                    .stream()
                    .noneMatch(dept -> dept.getDeptNo().equalsIgnoreCase(deptNo));
    }

    /**
     * Check if a department number and department id exist.
     * @param deptNo
     * @return boolean
     */
    public boolean isDeptNoUnique(String deptNo, int deptId) {
        return dl.getAllDepartment(this.companyName)
                    .stream()
                    .noneMatch(dept -> (dept.getDeptNo().equalsIgnoreCase(deptNo) && dept.getId() != deptId));
    }

    /**
     * Check if an employee number exists.
     * @param companyName
     * @param empNo
     * @return boolean
     */
    public boolean isEmpNoUnique(String companyName, String empNo) {
        return dl.getAllEmployee(this.companyName)
                    .stream()
                    .noneMatch(emp -> emp.getEmpNo().equalsIgnoreCase(empNo));
    }

    /**
     * Check if an employee number and employer id exist.
     * @param companyName
     * @param empNo
     * @return boolean
     */
    public boolean isEmpNoUnique(String companyName, String empNo, int empId) {
        return dl.getAllEmployee(this.companyName)
                    .stream()
                    .noneMatch(emp -> emp.getEmpNo().equalsIgnoreCase(empNo) && emp.getId() != empId);
    }

    /**
     * Check if day is Satuday or Sunday.
     * @param date
     * @return boolean
     */
    public boolean validateWeekday(Date date) {
        return Stream.of("saturday", "sunday")
                    .noneMatch(new SimpleDateFormat("EEEE").format(date)::equalsIgnoreCase);
    }
    

    /**
     * Check if date is in the past (valid).
     * @param date
     * @return boolean
     */
    public boolean isDateInThePast(Date date) {
        return date.before(new Date());
    }

    /**
     * Check if date and time are equals to the
     * current date (if Monday)
     * If not, set date equals to the Monday prior.
     * @param date
     * @return boolean
     */
    public boolean isStartTimeValid(Date date) {
        try {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dayDf = new SimpleDateFormat("EEEE");
        
            if (Stream.of("monday").anyMatch(dayDf.format(new Date())::equalsIgnoreCase) && 
                df.format(date).equals(df.format(new Date()))) {
                    return true;
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DATE, -1);
            }

            if (df.format(calendar.getTime()).equals(df.format(date))) {
                return true;
            }

        } catch(Exception e) {
            return false;
        }
        return false;
    }

    /**
     * endDate must be a valid date and time at least 1 hour grater than the startDate
     * endDate must be on the same day as the startDate
     * @param startDate
     * @param endDate
     * @return boolean
     */
    public boolean isEndTimeValid(Date startDate, Date endDate) {
        Instant startInst = startDate.toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant endInst = endDate.toInstant().truncatedTo(ChronoUnit.DAYS);
        long hourInMills = 1000*60*60;
        if (startInst.equals(endInst) && (endDate.getTime() - startDate.getTime()) >= hourInMills) {
            return true;
        }

        return false;
    }

    /**
     * Check if time is in range within two values
     * @param date
     * @return boolean
     */
    public boolean isTimeInRange(Date date) {

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        LocalTime startTime = LocalTime.parse("08:00:00");
        LocalTime endTime = LocalTime.parse("18:00:00");
        LocalTime timeForDate = LocalTime.parse(df.format(date));

        if (timeForDate.compareTo(startTime) >= 0 && timeForDate.compareTo(endTime) <= 0) {
            return true;
        }

        return false;
    }

    /**
     * Check that employee does not already have a startDate for the day.
     * @param date
     * @param empId
     * @return boolean
     */
    public boolean isTimeStampUnique(Date date, int empId) {
        List<Timecard> timecards = dl.getAllTimecard(empId);
        Instant instantDate = date.toInstant().truncatedTo(ChronoUnit.DAYS);
        for (Timecard timecard : timecards) {

            Instant instantFromDB = timecard.getStartTime().toInstant().truncatedTo(ChronoUnit.DAYS);
            if (instantDate.equals(instantFromDB)) {
                return false;
            }              
        }
        return true;
    }
  
}
