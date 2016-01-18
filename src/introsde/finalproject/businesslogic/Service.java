package introsde.finalproject.businesslogic;

import java.util.List;
import java.net.UnknownHostException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface Service {

	@WebMethod(operationName="getUserNotifications")
    @WebResult(name="notifications")
    public List<String> getUserNotifications(@WebParam(name="userId") Long id);
    
    @WebMethod(operationName="updateUserInfo")
    @WebResult(name="result")
    public String updateUserInfo(@WebParam(name="userId") Long id,
                                 @WebParam(name="param") String key,
                                 @WebParam(name="newValue") String value);

    @WebMethod(operationName="updateUserGoal")
    @WebResult(name="result")
    public String updateUserGoal(@WebParam(name="userId") Long id,
                                 @WebParam(name="goal") String goal,
                                 @WebParam(name="newValue") String value);

    @WebMethod(operationName="deleteUserGoal")
    @WebResult(name="result")
    public String deleteUserGoal(@WebParam(name="userId") Long id,
                                 @WebParam(name="goal") String goal);

    @WebMethod(operationName="updateGoalProgress")
    @WebResult(name="result")
    public String updateGoalProgress(@WebParam(name="userId") Long id,
                                     @WebParam(name="goal") String goal,
                                     @WebParam(name="value") String value);

}

