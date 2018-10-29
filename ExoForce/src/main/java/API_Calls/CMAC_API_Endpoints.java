package API_Calls;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import TestingFunctions.Helper_Functions;

public class CMAC_API_Endpoints{
	
	public static String CreateProject_API(String URL, String OAuth_Token, String organizationUUID, String applicationUUID, String projectName, String latype, String laversion, String latimeStamp){
		String Request = "";
		
		try{
			HttpPost httppost = new HttpPost(URL);
			
			httppost.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);

			JSONObject MainBody = new JSONObject()
					.put("orgUUID", organizationUUID)
					.put("applicationUUID", applicationUUID)
					.put("latimeStamp", latimeStamp) 
					.put("latype", latype) 
					.put("laversion", laversion) 
					.put("projectName", projectName) ;
						
			Request = MainBody.toString();
			StringEntity params = new StringEntity(Request);
			httppost.setEntity(params);
			
			String Response = General_API_Calls.HTTPCall(httppost, Request);
			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}
	
	public static String DeleteProject_API(String URL, String OAuth_Token, String applicationUUID){
		String Request = "";
		try{
			URL = URL.replace("{applicationUUID}", applicationUUID);
			HttpDelete  httpdel = new HttpDelete (URL);
			
			httpdel.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
			
			String Response = General_API_Calls.HTTPCall(httpdel, Request);

			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}
	
	public static String RetrieveProject_API(String URL, String OAuth_Token, String UUID){
		String Request = "";
		
		try{
			//The url should look like the below format.
			//{domain}/cmac/v3/projects/{UUID}     //the UUID could be with organization or applicaiton
			URL = URL.replace("{UUID}", UUID);
			HttpGet httpget = new HttpGet(URL);
			
			httpget.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);

			String Response = General_API_Calls.HTTPCall(httpget, Request);
			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}
	
	public static String UpdateProject_API(String URL, String OAuth_Token, String organizationUUID, String applicationUUID, String latimeStamp, String latype, String laversion, String projectName){
		String Request = "";
		
		try{
			HttpPut httpput = new HttpPut (URL);
			
			httpput.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
			
			JSONObject MainBody = new JSONObject()
					.put("orgUUID", organizationUUID)
					.put("applicationUUID", applicationUUID)
					.put("projectName", projectName)
					.put("latimeStamp", latimeStamp)
					.put("latype", latype)
					.put("laversion", laversion);
			
			Request = MainBody.toString();
			StringEntity params = new StringEntity(Request);
			httpput.setEntity(params);
			
			String Response = General_API_Calls.HTTPCall(httpput, Request);

			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}

	//not yet validated
	public static String CreateResource_API(String URL, String OAuth_Token, String applicationUUID, String endpointUUIDs, String isCertified){
		String Request = "";
		
		try{
			HttpPost httppost = new HttpPost(URL);
			
			httppost.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
			
			
			JSONObject MainBody = new JSONObject()
					.put("applicationUUID", applicationUUID)
					.put("endpointUUIDs", endpointUUIDs) 
					.put("isCertified", isCertified)    //not sure on this as the ICD lists as string but example shows boolean.
					;
						
			Request = MainBody.toString();
			StringEntity params = new StringEntity(Request);
			httppost.setEntity(params);
			
			String Response = General_API_Calls.HTTPCall(httppost, Request);
			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}

	//not yet updated
	public static String DeleteResource_API(String URL, String OAuth_Token, String applicationUUID){
		String Request = "";
		try{
			URL = URL.replace("{applicationUUID}", applicationUUID);
			HttpDelete  httpdel = new HttpDelete (URL);
			
			httpdel.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
			
			String Response = General_API_Calls.HTTPCall(httpdel, Request);

			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}

	//not yet updated
	public static String RetrieveResource_API(String URL, String OAuth_Token, String UUID){
		String Request = "";
		
		try{
			URL = URL.replace("{UUID}", UUID);
			HttpGet httpget = new HttpGet(URL);
			httpget.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);

			String Response = General_API_Calls.HTTPCall(httpget, Request);
			Helper_Functions.PrintOut("RetrieveProjectSummary Response : " + Response, true);
			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}

	//not yet updated
	public static String UpdateResource_API(String URL, String OAuth_Token, String applicationUUID, String endpointUUIDs, String isCertified){
		String Request = "";
		
		try{
			HttpPut httpput = new HttpPut (URL);
			
			httpput.addHeader("Content-Type", "application/json");
			//httppost.addHeader("Authorization", "Bearer " + OAuth_Token);
		
			JSONObject MainBody = new JSONObject()
					.put("applicationUUID", applicationUUID)
					.put("endpointUUIDs", endpointUUIDs)
					.put("isCertified", isCertified);
			
			Request = MainBody.toString();
			StringEntity params = new StringEntity(Request);
			httpput.setEntity(params);
			
			String Response = General_API_Calls.HTTPCall(httpput, Request);

			return Response;
		}catch (Exception e){
			e.printStackTrace();
			return e.toString();
		}
		//Sample request:
		
		//Sample response:

	}



}
