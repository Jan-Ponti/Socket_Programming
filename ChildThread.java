/* 
 * ChildThread.java
 */


import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class ChildThread extends Thread {

	// Vector to keep track of all connected client handlers
    static  Vector<ChildThread> handlers = new Vector<ChildThread>(20);
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
	private String userId = null;

	// Address with sample list of user information
	private static final String[][] addressList = {
		{"1001", "Jas", "Horveen", "313-224-5478"},
		{"1002", "John", "Smith", "313-679-6899"},
		{"1003", "Jane", "Doe", "313-287-4412"}
	};

	// Constructor
    public ChildThread(Socket socket) throws IOException {
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

	// Method to handle all communication
    public void run() {
		String line;
		synchronized(handlers) {
	    	// add the new client in Vector class
	    	handlers.addElement(this);
		}

		try {
			// Read and process commands from the client
	    	while ((line = in.readLine()) != null) {
				System.out.println(line);
				String Response = processCommand(line);
				// Broadcast it to everyone!  You will change this.  
				// Most commands do not need to broadcast
				for(int i = 0; i < handlers.size(); i++) {	
		    		synchronized(handlers) {
						ChildThread handler =
			    		(ChildThread)handlers.elementAt(i);
						if (handler != this) {
			    			handler.out.println(line);
			    			handler.out.flush();
						}
		    		}
				}
			}
		} catch(IOException ioe) {
	    	ioe.printStackTrace();
		} finally {
			// Resource Management
	    	try {
				in.close();
				out.close();
				socket.close();
	    	} catch(IOException ioe) {
				ioe.printStackTrace();
	    	} finally {
				synchronized(handlers) {
		    		handlers.removeElement(this);
				}
	    	}
		}
	}

	// Process the command and return the response
	private String processCommand(String command) {
		String[] steps = command.split(" ");
		String cmd = steps[0];

		switch (cmd) {
			// Login command
			case "LOGIN":
				if (steps.length == 3) {
					String user = steps[1];
					String passwd = steps[2];
					if (login(user, passwd)) {
						return "200 - OK";
					} else {
						return "410 - Wrong UserID or Password";
					}
				} else {
					return "400 - Invalid Command Format";
				}
			// Logout command
			case "LOGOUT":
				userId = null;
				return "200 - OK";
			// Who command
			case "WHO":
				return listActiveUsers();
			// Look command
			case "LOOK":
				if (steps.length == 3) {
					return lookUp(steps[1], steps[2]);
				} else {
					return "400 - Invalid Command Format";
				}
			// Update command
			case "UPDATE":
				if (steps.length == 4 && userId != null) {
					return updateRecords(steps[1], steps[2], steps[3]);
				} else if (userId == null) {
					return "401 - You are not currently logged in, login first";
				} else {
					return "400 - Invalid Command First";
				}
			default:
				return "500 - Unknown Command";
		}
	}

	// Validate user login credentials
	private boolean login(String user, String passwd) {
		if ("john".equals(user) && "john01".equals(passwd)) {
			return true;
		} else if ("jane".equals(user) && "jane01".equals(passwd)) {
			return true;
		} else if ("admin".equals(user) && "admin01".equals(passwd)) {
			return true;
		} else if ("root".equals(user) && "root01".equals(passwd)) {
			return true;
		} else {
			return false;
		}
	}

	// List all active users 
	private String listActiveUsers() {
		StringBuilder sb = new StringBuilder("200 - OK\nThe list of active users:\n");
		synchronized (handlers) {
			for (ChildThread handler : handlers) {
				if (handler.userId != null) {
					sb.append(handler.userId).append("\t").append(handler.socket.getInetAddress()).append("\n");
				}
			}
		}
		return sb.toString();
	}

	// Look up the user information based on the provided field
	private String lookUp(String field, String value) {
		int fieldIndex;
		switch(field) {
			case "1":
				fieldIndex = 1;
				break;
			case "2":
				fieldIndex = 2;
				break;
			case "3":
				fieldIndex = 3;
				break;
			default:
				return "400 Invalid Command Format";
		}

		StringBuilder sb = new StringBuilder();
		boolean found = false;
		for (String[] entry : addressList) {
			if (entry[fieldIndex].equalsIgnoreCase(value)) {
				sb.append("200 OK\n").append(String.join(" ", entry)).append("\n");
				found = true;
			}
		}
		if (!found) {
			sb.append("404 - Your search did not match any records");
		}
		return sb.toString();
	}

	// Update user records based on provided fields
	private String updateRecords(String id, String field, String newValue) {
		int fieldIndex;
		switch (field) {
			case "1":
				fieldIndex = 1;
				break;
			case "2":
				fieldIndex = 2;
				break;
			case "3":
				fieldIndex = 3;
				break;
			default:
				return "400 - Invalid Command Format";
		}

		for (String[] entry : addressList) {
			if (entry[0].equals(id)) {
				entry[fieldIndex] = newValue;
				return "200 OK\nRecord" + id + " updated\n" + String.join(" ", entry);
			}
		}
		
		return "403 - The Record ID does not exist";
	}
}

