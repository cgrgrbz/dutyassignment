# Duty Optimizer Web Service 

[TÜRKÇE](https://github.com/cgrgrbz/dutyassignment/blob/master/OKUBENI.md)

This is a duty assignment optimization web service, gets the input Duty and Employee lists by excel, and solves/assigns the employees to the duties starting from the given date by the give day length.

It is a maven project, powered by spring boot. And OptaPlanner used for the optimization purposes. It also uses h2 in-memory database for testing purposes. 

## The problem we are going to automatize/optimize.

Given the list of duties and the employees, the application solves/assigns employees for the duties considering different circumstances (criteria). 

Excel templates for the Duty and Employee lists are linked below.
 ([DutyList Template](https://github.com/cgrgrbz/dutyassignment/blob/master/src/main/resources/DutyList.xlsx)) 
 ([EmployeeList Template](https://github.com/cgrgrbz/dutyassignment/blob/master/src/main/resources/EmployeeList.xlsx)) 

**Project structure (/src/main/java/com/cagrigurbuz/kayseriulasim/dutyassignment/):**
.
├── controller                          				# Controllers
├── domain                              				# Domain POJOs for the problem
├── repository                          				# JPA Repositories
├── service                             					# Implementations for the controllers
├── solver                              					# Solver related POJOs
├── utils                               					# IO Utils for import/export
├── DataImporter.java                   			# Data Importer during the PostConstruct
├── DutyAssignmentApplication.java      
├── SwaggerConf.java                    		# SwaggerUI configurations
└── README.md

## Domain of the problem

![Class Diagram](https://raw.githubusercontent.com/cgrgrbz/dutyassignment/gh-pages/Class%20Diagram.png)

**We have:**
|Domain| description |
|--|--|
| Duty | A duty, which has a decision variable on it (an Employee) to be assigned. |
| Employee | A employee, which is available to assign for a duty |
| Schedule | A list of the duties, the roster. |

Each duty has an decision variable, an employee, to be assigned during the solving time. 

A Schedule, only and only one, instantiated during the solving time. It has a list of duties, a startDate, and a endDate. 
 
 
## Constraints of the problem
Using Drools - "resources\com\cagrigurbuz\kayseriulasim\dutyassignment\DutyAssignmentRules.drl" 

|Rule|Type|Description|
|--|--|--|
|Assign every duty|Medium||
|One duty per day|Hard||
|Assign employee from same region|Medium||
|Break between non-consecutive shifts is at least 12 hours|Medium||
|fair dutyName count|Soft||
|fair dutyType count|Soft||
|Consecutive day duty TYPE should be the same|Soft||
|Consecutive week duty TYPE should NOT be EVENING|Medium||
|Consecutive week duty should NOT be the same NAME|Medium||
|Maximum Consecutive Six Assigned Days|Hard||


## Web Service API Endpoints


| root | endpoint | type |  description |
|--|--|--|--|
| duty | / | GET | Get a list of all duties |
| duty | /add | GET |Add a new duty |
| duty | /import | POST |Import duties from an Excel file |
| duty | /current | GET |Get the duties only from the current schedule period, which is determined by starting solver. |
| duty | /current/excel | GET |Export the duties only from the current schedule period to excel. |
| employee | / | GET | Get a list of all employees |
| employee | /{employeeCode} | GET | Get an employee by employeeCode |
| employee | /add | POST | Add a new employee |
| employee | /import | POST | Import employees from an Excel file |
| solver | /solve | POST | Solve the schedule |
| solver | /terminate| POST | Terminate the solver |

 

Assuming the application on localhost:8080

- The Swagger UI is enabled at [/swagger-ui.html](localhost:8080/swagger-ui.html)
- The H2 Console is enabled at [/h2-console](localhost:8080//h2-console)


## HOW TO RUN?

By default applications comes with Swagger UI and H2 console enabled at 8080 port as mentioned above.

> **Clone this repo and import it by your IDE and run it**, or you may run it by directly maven commands on console,
> **Go to _Swagger UI_ to use the API with an easy way**, or use your own way to POST and GET the end points,
> **Import your EmployeeList excel file _/employee/import_**
> **Import your DutyList excel file _/duty/import_**
>  **Request a post with two parameters, startingDate and dayLenght**
>  **After the termination time spent, determined on [application.properties](https://github.com/cgrgrbz/dutyassignment/blob/master/src/main/resources/application.properties), you may use duty and points to get a list of all duties or only the current duty list by its enpoint.**

You may also use H2-Console to query the domain for your special condition.

**TODO**
- 
- .

**BUGS**
- .
- .

> by [CagriGurbuz](https://cagrigurbuz.com/).
