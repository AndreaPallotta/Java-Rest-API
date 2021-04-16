package com.pallotta.andrea.business;

import java.sql.Date;

import com.pallotta.andrea.service.CompanyServices;

import companydata.DataLayer;

public class Test {
    
    public static void main(String[] args) {
        CompanyServices cs = new CompanyServices();
        BusinessLayer bl = new BusinessLayer("ap4534");
        DataLayer dl = new DataLayer("ap4534");
        String json;

        // System.out.println(bl.deptExists(15));
        // System.out.println(dl.deleteDepartment("ap4534", 15));
        // System.out.println(cs.getIt());
        // System.out.println("******************\n");

        // TODO: FIX THIS FUNCTION
        // System.out.println(cs.deleteCompanyRecords("ap4534"));
        // System.out.println("******************\n");

        // System.out.println(cs.getDepartment("ap4534", 21));
        // System.out.println(cs.getDepartment("ap4534", 21).getEntity());
        // System.out.println("******************\n");

        // System.out.println(cs.getDepartments("ap4534"));
        // System.out.println(cs.getDepartments("ap4534").getEntity());
        // System.out.println("******************\n");

        // json = "{\"company\":\"ap4534\",\"dept_id\":3,\"dept_name\":\"IT\",\"dept_no\":\"d43356\",\"location\":\"rochester\"}";
        // System.out.println(cs.updateDepartment(json));
        // System.out.println(cs.updateDepartment(json).getEntity());
        // System.out.println("******************\n");

        // System.out.println(cs.insertDepartment("ap4534", "mystery", "d123", "buffalo"));
        // System.out.println(cs.insertDepartment("ap4534", "mystery", "d123", "buffalo").getEntity());
        // System.out.println("******************\n");

        // System.out.println(cs.deleteDepartment("ap4534", 40).getEntity());
        // System.out.println("******************\n");
        
        // System.out.println(cs.getEmployee("ap4534", 1));
        // System.out.println(cs.getEmployee("ap4534", 1).getEntity());
        // System.out.println("******************\n");

        // System.out.println(cs.getEmployees("ap4534"));
        // System.out.println(cs.getEmployees("ap4534").getEntity());
        // System.out.println("******************\n");
    
        //System.out.println(cs.insertEmployee("ap4534", "french", "e1b", Date.valueOf("2018-06-16"), "programmer", 5000.0, 1, 2));
        // System.out.println(cs.insertEmployee("ap4534", "french", "e1b", Date.valueOf("2021-04-07"), "programmer", 5000.0, 1, 2).getEntity());
        // System.out.println("******************\n");

        // TODO: FIX THIS FUNCTION
        // json = "{\"company\":\"ap4534\",\"emp_id\":15,\"emp_name\":\"french\",\"emp_no\":\"e1b\",\"hire_date\":\"2018-06-16\",\"job\":\"programmer\",\"salary\":5000.0,\"dept_id\":1,\"mng_id\":2}";
        // System.out.println(cs.updateEmployee(json));
        // System.out.println(cs.updateEmployee(json).getEntity());
        // System.out.println("******************\n");

        // System.out.println(cs.deleteEmployee("ap4534", 28).getEntity());
        // System.out.println("******************\n");

        // System.out.println(cs.getTimecard("ap4534", 3));
        // System.out.println(cs.getTimecard("ap4534", 3).getEntity());
        // System.out.println("******************\n");

        // // TODO: FIX THIS FUNCTION
        // System.out.println(cs.getTimecards("ap4534", 20));
        // System.out.println(cs.getTimecards("ap4534", 20).getEntity());
        // System.out.println("******************\n");

    }
}
