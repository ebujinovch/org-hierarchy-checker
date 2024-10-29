# OrgHierarchyChecker

OrgHierarchyChecker is a Java-based command-line tool designed for analyzing an organizational
hierarchy structure within a large company. Given employee data in a CSV file format, this
application loads the information, validates the hierarchy, and reports on potential structural and
salary-based improvements.

## Project Goals

The project was created to:

1. **Load and Validate Employee Hierarchy**: Parse employee data from a CSV file, validate the
   structure, and establish manager-employee relationships.
2. **Identify Hierarchical Issues**: Detect issues in the reporting structure, such as invalid
   manager references or overly long chains between employees and the CEO.
3. **Analyze Manager Salaries**: Identify managers who are underpaid or overpaid based on the
   salaries of their direct reports.
4. **Provide Structured Reporting**: Output findings in a clear format, facilitating organizational
   improvements.

## Requirements

### Business Requirements

The application must output the following 3 reports:

- which managers earn less than they should, and by how much
- which managers earn more than they should, and by how much
- which employees have a reporting line which is too long, and by how much

Every manager should earn at least 20% more than the average salary of its direct subordinates, but
no more than 50% more than that average.

The outliers must be visible in the report.

The reporting line of all employees consisting of more than 4 managers between the employee and the
CEO are considered too long and should be reported.

### Technical Requirements

- **Java SE (17 or higher)** for running the application.
- **JUnit** for testing.
- **Maven** as the build and dependency management tool.

### Input File Structure

The CSV input file must follow this structure:

```csv
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
300,Alice,Hasacat,50000,124
305,Brett,Hardleaf,34000,300
```

Each line should include:

- **Id**: Unique identifier for each employee.
- **firstName** and **lastName**: Names of the employee.
- **salary**: Annual salary of the employee as an integer.
- **managerId**: (Optional) ID of the employee's manager. The CEO will not have a manager ID.

## Project Structure

### Simplified Component Overview

- **OrgHierarchyChecker**: Main entry point for the application. It loads the CSV data, performs
  analysis, and prints results to the console.
- **OrgHierarchyLoader**: Responsible for loading and parsing the CSV file to initialize
  an `Organization` object, containing the list of employees.
- **OrgHierarchyAnalyzerService**: Contains methods for hierarchy validation and salary analysis,
  checking each employee’s reporting line and identifying managers who are underpaid or overpaid.
- **Model Classes**:
    - **Employee**: Represents an individual employee with properties like ID, name, salary, and
      manager reference.
    - **Organization**: A collection of `Employee` objects that makes up the company’s hierarchy,
      allowing for easy employee retrieval and reporting line calculations.

## Usage

### Running the Application

1. **Build**: Use Maven to build the project:
   ```bash
   mvn clean install
   ```
2. **Run**: Run the application, specifying the path to the CSV file if desired:
   ```bash
   java -jar target/org-hierarchy-checker.jar [path-to-your-csv-file]
   ```
   If no file path is provided, the application defaults to using `org-hierarchy-example-1.csv`.

### Output Format

Upon execution, the application provides analysis results in the following format:

```
Organization Structure:
- Employee details and hierarchy as parsed from the CSV.

Too long reporting lines:  
[Employee] reports to [Reporting Line]

Underpaid managers:  
[Manager] earns less than intended by [Amount]

Overpaid managers:  
[Manager] earns more than intended by [Amount]
```

Example:
```
Organization{employees=Employee{id=305, firstName='Brett', lastName='Hardleaf', salary=34000, managerId=300},
Employee{id=123, firstName='Joe', lastName='Doe', salary=60000, managerId=null},
Employee{id=124, firstName='Martin', lastName='Chekov', salary=45000, managerId=123},
Employee{id=300, firstName='Alice', lastName='Hasacat', salary=50000, managerId=124},
Employee{id=125, firstName='Bob', lastName='Ronstad', salary=47000, managerId=123}}
Too long reporting lines: 
	
Underpaid managers: 
	Employee{id=124, firstName='Martin', lastName='Chekov', salary=45000, managerId=123} earns less than intended by 15000.00
Overpaid managers: 
	

```