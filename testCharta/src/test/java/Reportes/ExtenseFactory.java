package Reportes;

import com.aventstack.extentreports.ExtentReports;

public class ExtenseFactory {

     public static ExtentReports getInstance() {
          ExtentReports extent = new ExtentReports();
          extent.setSystemInfo("RESTassured", "5.1.1");
          extent.setSystemInfo("OS", "Windows");
          return extent;
     }
}