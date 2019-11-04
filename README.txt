Java Refactoring Test Project
=============================

**Please, before starting the test read these instructions carefully.**

Introduction
------------

This is a refactoring testing project used by SAP CX hiring process.

The idea of this exercise is to evaluate your ability to identify poor coding practices and improve the code through the use of best practices.

The main project is a very basic user management application. We are not looking to add any supplementary features, instead we are verifying the following:

* Your knowledge of REST.
* Your knowledge of Maven.
* Your knowledge of Spring.
* Your ability to identify and refactor poor Java code.
* Your ability to identify and fix bugs.
* Your ability to apply proven design principles.
* Your ability to write useful and effective tests.

Feel free to modify whatever you want! :)

Pre-reqs
--------

* To build this project you must install Maven. If you do not have it installed, please refer to the [maven website][1] for assistance.

* For deployment you can use any web container/application server you want. We used tomcat version 8.5.x to validate if the application was starting up correctly.

Instructions
------------

1. At the project root directory, run:
    `$ mvn package`
2. At this point the maven build should run successfully and every test should be green.
3. Put your project on a **private** repository on Github (GIT repo)
4. Create a new branch from your master branch
5. Perform the refactoring you deem necessary in the branch, following what you know to be the best practices. (Feel free to innovate!).
6. Please make sure your code compiles and that all tests are green when you are done.
7. At the end of your work you should push the code into your branch.
8. Create a Pull Request from your branch to the master branch.
9. Add the users "jahlonsou", "aldocuevas" and "bruno.araujo@sap.com" as a collaborators for your private git repo.
10. The final step is to send an email to olivia.louf@sap.com and aldo.cuevas@sap.com  informing them that you finished the test including the Pull Request's URL where the code changes can be found.

Business Requirements
---------------------

* The user's email is a unique identifier and should be handled accordingly.
* A user should have at least 1 role.

Tips
----

* Unit tests != integration tests.
* Spring dependency is provided, feel free to use it.
* Don't be afraid to import additional dependencies if you think you need them.
* Remember that you will have to handle concurrent requests.
* Your final architecture should be portable, extensible and easily maintainable.

Good luck!

[1]: http://maven.apache.org/
[2]: https://bitbucket.org/
[3]: https://bitbucket.org/
[4]: https://git-scm.com/book/en/v2/Getting-Started-Installing-Git
[5]: https://git-scm.com/downloads/guis
