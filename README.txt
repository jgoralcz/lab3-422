This is the lab 3 for SER422 created by Joshua Goralczyk.

- The war is lab3_jgoralcz because, if I recall correctly, you requested that in the docs.

- To begin with I use Tomcat port 8081, feel free to change that in the apidoc.json or properties file if needed.
(You may need to change the build.properties location of your tomcat for instance).

- I use the package edu.asupoly.ser422.lab3 along with folders api, model, and services.

- To begin, please run the mysql_lab3.sql file to get the database and schemas set up (I use mySQL).
- Use `source <path to mysql lab here>` while in mysql to get the database and use it

- I assume that phone numbers are unique, and I have set a 10 char (integer) limit to account for area code.
- If interested, you can look into the rdbm.properties file for my queries.
- You may have to fiddle with the settings to connect to your mySQL in this file as well (such as password and username if yours are different).
Great! We got the database setup.

- an example url for me (change the port): `http://localhost:8081/lab3_jgoralcz/rest/phones/number/480110987`

When implementing the DELETE of phone entries, I thought about using a query parameter instead of a path parameter, but after some searching,
I came to the conclusion that I should use it the same way I GET. In this case, I used the path parameter.