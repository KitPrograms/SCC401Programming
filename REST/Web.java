import java.io.*;

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

public class Web {
	
	static int RESPONSE_OK = 200;
	static int RESPONSE_NOT_FOUND = 404;
	static int RESPONSE_SERVER_ERROR = 501;
	
	FormMultipart formParser = new FormMultipart();
	
	private void sendResponse(OutputStream output, int responseCode, String contentType, byte content[])
		{
		try {
			output.write(new String("HTTP/1.1 " + responseCode + "\r\n").getBytes());
			output.write("Server: Kitten Server\r\n".getBytes());
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
	
	//example of a simple HTML page
	void page_index(OutputStream output)
		{
		sendResponse(output, RESPONSE_OK, "text/html", "<html>Hello!</html>".getBytes());
		}
	
	//example of a form to fill in, which triggers a POST request when the user clicks submit on the form
	void page_upload(OutputStream output)
		{
		String response = "";
		response = response + "<html>";
		response = response + "<body>";
		response = response + "<form action=\"/upload_do\" method=\"POST\" enctype=\"multipart/form-data\">";
		response = response + "<input type=\"text\" name=\"name\" placeholder=\"File name\" required/>";
		response = response + "<input type=\"file\" name=\"content\" required/>";
		response = response + "<input type=\"submit\" name=\"submit\"/>";
		response = response + "</form>";
		response = response + "</body>";
		response = response + "</html>";
		
		sendResponse(output, RESPONSE_OK, "text/html", response.getBytes());
		}
	
	//this function maps GET requests onto functions / code which return HTML pages
	void get(HTTPRequest request, OutputStream output)
		{
		if (request.resource.equals("/"))
			page_index(output);
			else if (request.resource.equals("/upload"))
			page_upload(output);
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