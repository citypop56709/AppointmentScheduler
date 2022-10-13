# Appointment Scheduler
Software 2 -C195 Project.

18 April 2022

Author: Ayun Daywhea

Contact Information

Email: adaywhe@my.wgu.edu

Version: 1.2

Appointment scheduler system application for WGU Software 2.

Scenario:

You are working for a software company that has been contracted to develop a GUI-based scheduling desktop application. The contract is with a global consulting organization that conducts business in multiple languages and has main offices in Phoenix, Arizona; White Plains, New York; Montreal, Canada; and London, England. The consulting organization has provided a MySQL database that the application must pull data from. The database is used for other systems, so its structure cannot be modified.

The organization outlined specific business requirements that must be met as part of the application. From these requirements, a system analyst at your company created solution statements for you to implement in developing the application. These statements are listed in the requirements section.

Your company acquires Country and First-Level-Division data from a third party that is updated once per year. These tables are prepopulated with read-only data. Please use the attachment “Locale Codes for Region and Language” to review division data. Your company also supplies a list of contacts, which are prepopulated in the Contacts table; however, administrative functions such as adding users are beyond the scope of the application and done by your company’s IT support staff. Your application should be organized logically using one or more design patterns and generously commented using Javadoc so your code can be read and maintained by other programmers.

You are working for a small manufacturing organization that has outgrown its current inventory system. Members of the organization have been using a spreadsheet program to manually enter inventory additions, deletions, and other data from a paper-based system but would now like you to develop a more sophisticated inventory program.

You have been provided with a mock-up of the user interface to use in the design and development of the system (see the attached “Software 1 GUI Mock-Up”) and a class diagram to assist you in your work (see the attached “UML Class Diagram”). A systems analyst created the solution statements outlined in the requirements section of this task based on the business requirements. You will use these solution statements to develop your application.

Implementation:

This program uses the Model-View-Controller software design pattern to form the basis of data sharing between the different controllers. It also uses CSS to style each controller, and the program uses styling to alert the users of errors.
The program was built using Java 17.0.2, JavaFX 17.0.2 and it usedMariaDB Version 2.6.2 for the database connector. For the submission the program was switched over to MySQL 8.0.2.5 for the database connection.
It was written using IntelliJ IDEA 2021.3.2 Ultimate Edition, and I used DataGrip 2021.3.4 to manage my local copy of the database.

How to run the program:
You can run the exe in the applications main folder, or run the jar. Make sure that you're connected to the internet to access the database.

Additional Report From A3f:
For an additional report I did a page that displayed the customers by country. This way if you have a great number of more customers in one country vs. another you can allocate your resources appropriately.

More details are available at my video: <a href='https://www.youtube.com/watch?v=zJrFXg5N2UY&t=4s'>here</a>
