package com.pallotta.andrea.service;

import java.io.StringReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonArray;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.pallotta.andrea.business.BusinessLayer;

import companydata.DataLayer;
import companydata.Department;
import companydata.Employee;
import companydata.Timecard;

/**
 * Root resource (exposed at "CompanyServices" path)
 */
@Path("CompanyServices")
public class CompanyServices {

    @Context
    UriInfo uriInfo;

    DataLayer dl;
    BusinessLayer bl;
    JsonReader reader;
    JsonObject object;
    JsonArray array;
    StringBuilder builder;

    final String COMPANYNAME = "ap4534";
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Welcome to the Company Service API!";
    }

    /**
     * #3 Deletes all Department, Employee and Timecard records in the database for the given company.
     * @param companyName
     * @return Response
     */
    @Path("company")
    @DELETE
    @Produces("application/json")
    public Response deleteCompanyRecords(@QueryParam("company") String companyName) {
        
        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {

            dl = new DataLayer(companyName);

            int rowsDeleted = dl.deleteCompany(companyName);

            if (rowsDeleted > 0) {
                builder.append("{\"success\":\"" + companyName + "'s information deleted with " + rowsDeleted + " rows.\"}");
            } else {
                builder.append("{\"error\":\"Company Name is invalid.\"}");
            }

        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }
    
        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }

    /**
     * #4 Returns the requested Department as a JSON String.
     * @param companyName
     * @param departmentID
     * @return Response
     */
    @Path("department")
    @GET
    @Produces("application/json")
    public Response getDepartment(@QueryParam("company") String companyName, @QueryParam("dept_id") int departmentID) {
        
        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            Department dept = dl.getDepartment(companyName, departmentID);
            builder.append("{\"dept_id\": "   + dept.getId() + ",");
            builder.append("\"company\":\"" + dept.getCompany() + "\",");
            builder.append("\"dept_name\":\"" + dept.getDeptName() + "\",");
            builder.append("\"dept_no\":\"" + dept.getDeptNo() + "\",");
            builder.append("\"location\":\"" + dept.getLocation() + "\"}");

        } catch(Exception e) {
            builder.append("{\"error\":\"DepartmentID does not exist.\"}");
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #5 Returns the requested list of Departments.
     * @param companyName
     * @return Response
     */
    @Path("departments")
    @GET
    @Produces("application/json")
    public Response getDepartments(@QueryParam("company") String companyName) {

        reader = null;
        array = null;
        builder = new StringBuilder("[");
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            List<Department> depts = dl.getAllDepartment(companyName);
            
            for(int i = 0; i < depts.size(); i++) {

                builder.append("{\"dept_id\": " + depts.get(i).getId() + ",");
                builder.append("\"company\":\"" + depts.get(i).getCompany() + "\",");
                builder.append("\"dept_name\":\"" + depts.get(i).getDeptName() + "\",");
                builder.append("\"dept_no\":\"" + depts.get(i).getDeptNo() + "\",");
                
                if (i == depts.size() - 1) {
                    builder.append("\"location\":\"" + depts.get(i).getLocation() + "\"}]");
                } else {
                    builder.append("\"location\":\"" + depts.get(i).getLocation() + "\"},");
                }
            }

            reader = Json.createReader(new StringReader(builder.toString()));
            array = reader.readArray();

        } catch(Exception e) {
            builder.append("{\"error\":\"Company does not have any department.\"}");
        } finally {
            dl.close();
        }

        

        return Response.ok(array).build();
    }


    /**
     * #6 Returns the updated Department as a JSON String.
     * @param jsonInput
     * @return Response
     */
    @Path("department")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateDepartment(String jsonInput) {

        reader = Json.createReader(new StringReader(jsonInput));
        object = reader.readObject();
        
        builder = new StringBuilder();

        // data from jsonInput
        String companyName = object.getString("company");
        int departmentID = object.getInt("dept_id");
        String departmentName = object.getString("dept_name");
        String departmentNo = object.getString("dept_no");
        String location = object.getString("location");

        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            bl = new BusinessLayer(companyName);

            if (bl.deptExists(departmentID) && bl.isDeptNoUnique(departmentNo, departmentID)) {

                Department dept = new Department(departmentID, companyName, departmentName, companyName + "-" + departmentNo, location);
                dl.updateDepartment(dept);
                builder.append("{\"success\":{\"company\":\"" + dept.getCompany() + "\",");
                builder.append("\"dept_id\":" + dept.getId() + ",");
                builder.append("\"dept_name\":\"" + dept.getDeptName() + "\",");
                builder.append("\"dept_no\":\"" + dept.getDeptNo() + "\",");
                builder.append("\"location\":\"" + dept.getLocation() + "\"}}");
            } else {
                builder.append("{\"error\":\"DepartmentID does not exist or DepartmentNo is not unique.\"}");
            }

        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #7 Returns the new Department as a JSON String.
     * @param companyName
     * @param departmentName
     * @param departmentNo
     * @param location
     * @return Response
     */
    @Path("department")
    @POST
    @Produces("application/json")
    public Response insertDepartment(@FormParam("company") String companyName, @FormParam("dept_name") String departmentName, @FormParam("dept_no") String departmentNo, @FormParam("location") String location) {

        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        dl = new DataLayer(companyName);
        bl = new BusinessLayer(companyName);


        try {
            if (bl.isDeptNoUnique(departmentNo)) {
                
                Department dept = dl.insertDepartment(new Department(companyName, departmentName, companyName + "-" + departmentNo, location));
                builder.append("{\"success\":{\"dept_id\":" + dept.getId() + ",");
                builder.append("\"company\":\"" + dept.getCompany() + "\",");
                builder.append("\"dept_name\":\"" + dept.getDeptName() + "\",");
                builder.append("\"dept_no\":\"" + dept.getDeptNo() + "\",");
                builder.append("\"location\":\"" + dept.getLocation() + "\"}}");
            } else {
                builder.append("{\"error\":\"DepartmentNo is not unique.\"}"); 
            }

        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #8 Deletes a department and returns the number of rows deleted.
     * @param companyName
     * @param departmentID
     * @return Response
     */
    @Path("department")
    @DELETE
    @Produces("application/json")
    public Response deleteDepartment(@QueryParam("company") String companyName, @QueryParam("dept_id") int departmentID) {

        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            bl = new BusinessLayer(companyName);

            int rowsDeleted = dl.deleteDepartment(companyName, departmentID);
            if (rowsDeleted > 0) {
                builder.append("{\"success\":\"Department " + departmentID + " from" + companyName + " deleted.\"}");
            } else {
                builder.append("{\"error\":\"DepartmentID is invalid.\"}");
            }
        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }
        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #9 Returns the requested Employee as a JSON String.
     * @param companyName
     * @param employeeID
     * @return Response
     */
    @Path("employee")
    @GET
    @Produces("application/json")
    public Response getEmployee(@QueryParam("company") String companyName, @QueryParam("emp_id") int employeeID) {

        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            bl = new BusinessLayer(companyName);

            if (bl.empIdExists(employeeID)) {

                Employee employee = dl.getEmployee(employeeID);
                builder.append("{\"emp_id\":" + employee.getId() + ",");
                builder.append("\"emp_name\":\"" + employee.getEmpName() + "\",");
                builder.append("\"emp_no\":\"" + employee.getEmpNo() + "\",");
                builder.append("\"hire_date\":\"" + employee.getHireDate() + "\",");
                builder.append("\"job\":\"" + employee.getJob() + "\",");
                builder.append("\"salary\":" + employee.getSalary() + ",");
                builder.append("\"dept_id\":" + employee.getDeptId() + ",");
                builder.append("\"mng_id\":" + employee.getMngId() + "}");

            } else {
                builder.append("{\"error\":\"EmployeeID does not exist.\"}");
            }

        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #10 Returns the requested list of Employees.
     * @param companyName
     * @return Response
     */
    @Path("employees")
    @GET
    @Produces("application/json")
    public Response getEmployees(@QueryParam("company") String companyName) {

        reader = null;
        array = null;
        builder = new StringBuilder("[");
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            List<Employee> employees = dl.getAllEmployee(companyName);
            
            
            for(int i = 0; i < employees.size(); i++) {

                builder.append("{\"emp_id\":" + employees.get(i).getId() + ",");
                builder.append("\"emp_name\":\"" + employees.get(i).getEmpName() + "\",");
                builder.append("\"emp_no\":\"" + employees.get(i).getEmpNo() + "\",");
                builder.append("\"hire_date\":\"" + employees.get(i).getHireDate() + "\",");
                builder.append("\"job\":\"" + employees.get(i).getJob() + "\",");
                builder.append("\"salary\":" + employees.get(i).getSalary() + ",");
                builder.append("\"dept_id\":" + employees.get(i).getDeptId() + ",");
                
                
                
                if (i == employees.size() - 1) {
                    builder.append("\"mng_id\":" + employees.get(i).getMngId() + "}]");
                } else {
                    builder.append("\"mng_id\":" + employees.get(i).getMngId() + "},");
                }
            }

            reader = Json.createReader(new StringReader(builder.toString()));
            array = reader.readArray();

        } catch(Exception e) {
            builder.append("{\"error\":\"Company does not have any department.\"}");
        } finally {
            dl.close();
        }

        return Response.ok(array).build();
    }

    /**
     * #11 Returns the new Employee as a JSON String.
     * @param companyName
     * @param employeeName
     * @param employeeNo
     * @param hireDate
     * @param job
     * @param salary
     * @param departmentID
     * @param mngID
     * @return Response
     */
    @Path("employee")
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public Response insertEmployee(
      @FormParam("company") String companyName,
      @FormParam("emp_name") String employeeName,
      @FormParam("emp_no") String employeeNo,
      @FormParam("hire_date") Date hireDate,
      @FormParam("job") String job,
      @FormParam("salary") double salary,
      @FormParam("dept_id") int departmentID,
      @FormParam("mng_id") int mngID) {


        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        dl = new DataLayer(companyName);
        bl = new BusinessLayer(companyName);

        try {
            if (bl.deptExists(departmentID) && (bl.mngIdExists(mngID) || mngID == 0) &&
                bl.validateWeekday(hireDate) && bl.isDateInThePast(hireDate) && 
                bl.isEmpNoUnique(companyName, companyName + "-" + employeeNo)) {

                Employee employee = dl.insertEmployee(new Employee(employeeName, companyName + "-" + employeeNo, hireDate, job, salary, departmentID, mngID));
                builder.append("{\"success\":{\"emp_id\":"+ employee.getId() + ",");
                builder.append("\"emp_name\":\"" + employee.getEmpName() + "\",");
                builder.append("\"emp_no\":\"" + employee.getEmpNo() + "\",");
                builder.append("\"hire_date\":\"" + employee.getHireDate() + "\",");
                builder.append("\"job\":\"" + employee.getJob() + "\",");
                builder.append("\"salary\":" + employee.getSalary() + ",");
                builder.append("\"dept_id\":" + employee.getDeptId() + ",");
                builder.append("\"mng_id\":" + employee.getMngId() + "}}");
                
            } else {
                builder.append("{\"error\":\"One or more form parameters is invalid.\"}");
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #12 Returns the updated Employee as a JSON String.
     * @param jsonInput
     * @return Response
     */
    @Path("employee")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateEmployee(String jsonInput) {

        reader = Json.createReader(new StringReader(jsonInput));
        object = reader.readObject();
        
        builder = new StringBuilder();

        // data from jsonInput
        String companyName = object.getString("company");
        int employeeID = object.getInt("emp_id");
        String employeeName = object.getString("emp_name");
        String employeeNo = object.getString("emp_no");
        Date hireDate = Date.valueOf(object.getString("hire_date")); 
        String job = object.getString("job");
        Double salary = Double.parseDouble(object.getString("salary"));
        int departmentID = object.getInt("dept_id");
        int mngID = object.getInt("mng_id");

        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        dl = new DataLayer(companyName);
        bl = new BusinessLayer(companyName);

        try {

            if (bl.deptExists(departmentID) && (bl.mngIdExists(mngID) || mngID == 0) &&
                bl.validateWeekday(hireDate) && bl.isDateInThePast(hireDate) && 
                bl.isEmpNoUnique(companyName, companyName + "-" + employeeNo, employeeID) && bl.empIdExists(employeeID)) {

                Employee employee = dl.updateEmployee(new Employee(employeeID, employeeName, companyName + "-" + employeeNo, hireDate, job, salary, departmentID, mngID));
                builder.append("{\"success\":{\"emp_id\":"+ employee.getId() + ",");
                builder.append("\"emp_name\":\"" + employee.getEmpName() + "\",");
                builder.append("\"emp_no\":\"" + employee.getEmpNo() + "\",");
                builder.append("\"hire_date\":\"" + employee.getHireDate() + "\",");
                builder.append("\"job\":\"" + employee.getJob() + "\",");
                builder.append("\"salary\":" + employee.getSalary() + ",");
                builder.append("\"dept_id\":" + employee.getDeptId() + ",");
                builder.append("\"mng_id\":" + employee.getMngId() + "}}");

            } else {
                builder.append("{\"error\":\"One or more form parameters is invalid.\"}");
            }

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #13 Returns the that the employee deleted.
     * @param companyName
     * @param employeeID
     * @return Response
     */
    @Path("employee")
    @DELETE
    @Produces("application/json")
    public Response deleteEmployee(@QueryParam("company") String companyName, @QueryParam("emp_id") int employeeID) {
        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            bl = new BusinessLayer(companyName);

            int rowsDeleted = dl.deleteEmployee(employeeID);
            if (rowsDeleted > 0) {
                builder.append("{\"success\":\"Employee " + employeeID + " deleted.\"}");
            } else {
                builder.append("{\"error\":\"EmployeeID is invalid.\"}");
            } 

        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }

    /**
     * #14 Returns the requested Timecard as a JSON String
     * @param companyName
     * @param timecardID
     * @return Response
     */
    @Path("timecard")
    @GET
    @Produces("application/json")
    public Response getTimecard(@QueryParam("company") String companyName, @QueryParam("timecard_id") int timecardID) {

        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            bl = new BusinessLayer(companyName);

            if (bl.timecardIdExists(timecardID)) {

                Timecard timecard = dl.getTimecard(timecardID);
                builder.append("{\"timecard\":{\"timecard_id\":" + timecard.getId() + ",");
                builder.append("\"start_time\":\"" + timecard.getStartTime() + "\",");
                builder.append("\"end_time\":\"" + timecard.getEndTime() + "\",");
                builder.append("\"emp_id\":" + timecard.getEmpId() + "}");

            } else {
                builder.append("{\"error\":\"TimecardID does not exist.\"}");
            }
        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #15 Returns the requested list of Timecards.
     * @param companyName
     * @param employeeID
     * @return Response
     */
    @Path("timecards")
    @GET
    @Produces("application/json")
    public Response getTimecards(@QueryParam("company") String companyName, @QueryParam("emp_id") int employeeID) {

        builder = new StringBuilder("[");
        reader = null;
        array = null;
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            List<Timecard> timecards = dl.getAllTimecard(employeeID);
            
            for(int i = 0; i < timecards.size(); i++) {

                builder.append("{\"timecard_id\":" + timecards.get(i).getId() + ",");
                builder.append("\"start_time\":\"" + timecards.get(i).getStartTime() + "\",");
                builder.append("\"end_time\":\"" + timecards.get(i).getEndTime() + "\",");
                
                if (i == timecards.size() - 1) {
                    builder.append("\"emp_id\":" + timecards.get(i).getEmpId() + "}]");
                } else {
                    builder.append("\"emp_id\":" + timecards.get(i).getEmpId() + "},");
                }
            }

            reader = Json.createReader(new StringReader(builder.toString()));
            array = reader.readArray();

        } catch(Exception e) {
            builder.append("{\"error\":\"Company does not have any department.\"}");
        } finally {
            dl.close();
        }

        return Response.ok(array).build();
    }


    /**
     * #16 Returns the new Timecard as a JSON String.
     * @param companyName
     * @param employeeID
     * @param startTime
     * @param endTime
     * @return Response
     */
    @Path("timecard")
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public Response insertTimecard(
        @FormParam("company") String companyName,
        @FormParam("emp_id") int employeeID,
        @FormParam("start_time") String startTime,
        @FormParam("end_time") String endTime) {


        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        dl = new DataLayer(companyName);
        bl = new BusinessLayer(companyName);

        try {

            if (bl.empIdExists(employeeID) && 
                bl.isStartTimeValid(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime)) && 
                bl.isEndTimeValid(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime)) &&
                bl.validateWeekday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime)) &&
                bl.validateWeekday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime)) && 
                bl.isTimeInRange(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime)) &&
                bl.isTimeInRange(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime)) &&
                bl.isTimeStampUnique(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime), employeeID)) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timecard timecard = dl.insertTimecard(new Timecard(
                    new Timestamp(dateFormat.parse(startTime).getTime()),
                    new Timestamp(dateFormat.parse(endTime).getTime()),
                    employeeID
                ));

                builder.append("{\"success\":{\"timecard_id\":"+ timecard.getId() + ",");
                builder.append("\"start_time\":\"" + dateFormat.format(timecard.getStartTime()) + "\",");
                builder.append("\"end_time\":\"" + dateFormat.format(timecard.getEndTime()) + "\",");
                builder.append("\"emp_id\":" + timecard.getEmpId() + "}}");
                
            } else {
                builder.append("{\"error\":\"One or more form parameters is invalid.\"}");
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }


    /**
     * #18 Returns the number of rows deleted..
     * @param companyName
     * @param timecardID
     * @return Response
     */
    @Path("timecard")
    @DELETE
    @Produces("application/json")
    public Response deleteTimecard(@QueryParam("company") String companyName, @QueryParam("emp_id") int timecardID) {
        builder = new StringBuilder();
        if (!companyName.equals(COMPANYNAME)) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }

        try {
            dl = new DataLayer(companyName);
            bl = new BusinessLayer(companyName);

            int rowsDeleted = dl.deleteTimecard(timecardID);
            if (rowsDeleted > 0) {
                builder.append("{\"success\":\"Timecard " + timecardID + " deleted.\"}");
            } else {
                builder.append("{\"error\":\"TimecardID is invalid.\"}");
            } 

        } catch(Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            dl.close();
        }

        reader = Json.createReader(new StringReader(builder.toString()));
        object = reader.readObject();

        return Response.ok(object).build();
    }
}

