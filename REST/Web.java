import java.io.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class HTTPRequest {
	RequestType type;
	String resource;
	HTTPHeader headers[];
	
	String getHeaderValue(String key)
		{
		for (int i = 0; i < headers.length; i++)
			{
			if (headers[i].key.equals(key))
				return headers[i].value;
			}
		
		return null;
		}
	}

public class Web implements iWeb{
	
	static int RESPONSE_OK = 200;
	static int RESPONSE_NOT_FOUND = 404;
	static int RESPONSE_SERVER_ERROR = 501;
	
	FormMultipart formParser = new FormMultipart();

	public Web(){
		try {
   			String name = "myserver";
   			iWeb stub = (iWeb) UnicastRemoteObject.exportObject(this, 0);
   			Registry registry = LocateRegistry.createRegistry(1099);
   			registry.rebind(name, stub);
   			System.out.println("Server ready");
  		} catch (Exception e) {
   			System.err.println("Exception:");
   			e.printStackTrace();
  		}
	}
	

	// Sends HTML requests back to the connection
	private void sendResponse(OutputStream output, int responseCode, String contentType, byte content[])
		{
		try {
			output.write(new String("HTTP/1.1 " + responseCode + "\r\n").getBytes());
			output.write("Server: Kit's Server\r\n".getBytes());
			if (content != null) output.write(new String("Content-length: " + content.length + "\r\n").getBytes());
			if (contentType != null) output.write(new String("Content-type: " + contentType + "\r\n").getBytes());
			output.write(new String("Connection: close\r\n").getBytes());
			output.write(new String("\r\n").getBytes());
			
			if (content != null) output.write(content);
			}
			catch (IOException e)
			{
			e.printStackTrace();
			}
		}
	
	// initally loaded page when visiting the web server 
	void page_home(OutputStream output)
		{
			// Should include description of different types of tasks, what they do, then link to either upload or get results

			String response = """
					<html>
					<head></head>
					<body></body>
					</html>
					""";
					
			sendResponse(output, RESPONSE_OK, "text/html", response.getBytes());
		}
	
	// page to upload tasks
	void page_taskUpload(OutputStream output)
		{
			// includes a form to fill out to submit the task wanting to complete, upon submission, should include either link to next page or confirmation message

			String response = """
				<html>
				<head></head>
				<body></body>
				</html>
				""";
		
			sendResponse(output, RESPONSE_OK, "text/html", response.getBytes());
		}

	// page to check on task completion
	void page_statusCheck(OutputStream output)
		{
			// includes list of submitted tasks, and their status of ongoing, pending, completed, completed tasks should contain a link to result download

			String response = """
				<html>
				<head></head>
				<body></body>
				</html>
				""";
		
			sendResponse(output, RESPONSE_OK, "text/html", response.getBytes());
		}

	// page to get results from a specific task
	void page_downloadResults(OutputStream output)
		{
			String response = """
				<html>
				<head></head>
				<body></body>
				</html>
				""";
		
			sendResponse(output, RESPONSE_OK, "text/html", response.getBytes());
		}
	
	//this function maps GET requests onto functions / code which return HTML pages
	void get(HTTPRequest request, OutputStream output)
		{
		if (request.resource.equals("/"))
			page_home(output);
			else if (request.resource.equals("/upload"))
			page_taskUpload(output);
			else if (request.resource.equals("/status"))
			page_statusCheck(output);
			else if (request.resource.equals("/upload"))
			page_downloadResults(output);
			else
			sendResponse(output, RESPONSE_NOT_FOUND, null, null);
		}
	
	//this function maps POST requests onto functions / code which return HTML pages
	void post(HTTPRequest request, byte payload[], OutputStream output)
		{
		if (request.resource.equals("/upload_do"))
			{
			//FormMultipart
			if (request.getHeaderValue("content-type") != null && request.getHeaderValue("content-type").startsWith("multipart/form-data"))
				{
				FormData data = formParser.getFormData(request.getHeaderValue("content-type"), payload);
				
				for (int i = 0; i < data.fields.length; i++)
					{
					System.out.println("field: " + data.fields[i].name);
					
					if (data.fields[i].name.equals("content"))
						{
						System.out.println(" -- filename: " + ((FileFormField) data.fields[i]).filename);
						}
					
					}
				
				sendResponse(output, RESPONSE_OK, "text/html", "<html>File sent, thanks!</html>".getBytes());
				}
				else
				{
				sendResponse(output, RESPONSE_SERVER_ERROR, null, null);
				}
			}
		}
	
}