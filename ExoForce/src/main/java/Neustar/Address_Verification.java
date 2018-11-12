import com.TARGUSinfo.WS_GetData.*;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

package Neustar;

/**************************************************************************************************
* An example that sends a query to the TARGUSinfo Web Service interface.
*
* The com.TARGUSinfo.WS_GetData classes were generated from the Apache Axis tool WSDL2Java.
* The Apache Axis library is also required for this code to compile/run.
*/

public class Address_Verification {
	private static final String USERNAME = ""; // set credentials here
	private static final String PASSWORD = "";
	private static final String SERVICEID = "";
	public static void main(String[] args) throws ServiceException, RemoteException {
	oneEID();
	twoEIDs();
	}
	Web Services
	Page 16 of 19
	11/7/12
	Neustar Proprietary & Confidential
	/************************************************************************************************
	* Does a query using one Element ID.
	*
	* @throws ServiceException
	* @throws RemoteException
	*/
	private static void oneEID() throws ServiceException, RemoteException {
	ClientLocator locator = new ClientLocator();
	ClientSoap port = locator.getClientSoap();
	OriginationType orig = new OriginationType(USERNAME, PASSWORD);
	int[] elems = {
	1305,
	};
	ServiceKeyType[] skeys = {
	new ServiceKeyType( 1, "8583145300"),
	};
	printResponse(port.query(orig, SERVICEID, 1, elems, skeys));
	}
	/************************************************************************************************
	* Does a query with two Element IDs.
	*
	* @throws ServiceException
	* @throws RemoteException
	*/
	private static void twoEIDs() throws ServiceException, RemoteException {
	ClientLocator locator = new ClientLocator();
	ClientSoap port = locator.getClientSoap();
	OriginationType orig = new OriginationType(USERNAME, PASSWORD);
	int[] elems = {
	1321,
	3221,
	};
	ServiceKeyType[] skeys = {
	new ServiceKeyType( 1, "8583145300"),
	new ServiceKeyType(1390, "12544 High Bluff Drive"),
	new ServiceKeyType(1391, "San Diego"),
	new ServiceKeyType(1392, "CA"),
	new ServiceKeyType(1393, "92130"),
	new ServiceKeyType(1396, "TARGUS Information Corporation"),
	};
	printResponse(port.query(orig, SERVICEID, 1, elems, skeys));
	}
	Web Services
	Page 17 of 19
	11/7/12
	Neustar Proprietary & Confidential
	/************************************************************************************************
	* print to System.out the contents of the ResponseMsgType
	*
	* @param resp
	*/
	private static void printResponse(ResponseMsgType resp) {
	if (resp.getErrorCode() != 0) {
	System.out.println("error returned: E" + resp.getErrorCode());
	String errorVal = resp.getErrorValue();
	if (errorVal != null && errorVal.length() > 0) {
	System.out.println("more error info: " + errorVal);
	}
	} else {
	ElementResultType[] elemsResults = resp.getResult();
	for (ElementResultType elemResult : elemsResults) {
	System.out.println("result for EID " + elemResult.getId() + ":");
	System.out.println(elemResult.getValue());
	}
	}
	}

}
