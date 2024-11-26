# Socket_Programming
 Repo to house the socket programming assignments I completed for Network Security Class

 The following program is designed for a server to allow multiple clients to connect to the server at the same time.

 The five commands that this program implements are LOGIN, LOGOUT, WHO, LOOK, and UPDATE.

 Note: All users share the same address book.

 LOGIN - Identify the user to the remote server
 LOGOUT - Logout from the server
 WHO - List all active users, including the UserID and the users IP address
 LOOK - Look up a name in the book
 UPDATE - Update an existing record in the book and display the updated record

The program was written in three different java files: Client.java, ChildThread.java, and MultiThread.java

There is a Makefile included to compile and run all the files.
