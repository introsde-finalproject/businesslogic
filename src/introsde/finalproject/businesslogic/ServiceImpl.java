package introsde.finalproject.businesslogic;

import introsde.finalproject.storage.StorageService;
import introsde.finalproject.localdatabase.User;
import introsde.finalproject.localdatabase.Goal;
import introsde.finalproject.localdatabase.GoalProgress;
import introsde.finalproject.localdatabase.Measure;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import javax.jws.WebService;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;

@WebService(endpointInterface = "introsde.finalproject.businesslogic.Service", serviceName="BusinessLogicService")
public class ServiceImpl implements Service {

    private static final StorageService storageService = new StorageService();

    @Override
    public List<String> getUserNotifications(Long id) {
        User user = storageService.getServiceImplPort().getUser(id);

        List<String> result = new ArrayList<>();
        for (Goal goal : user.getGoals()) {
            if(goal.getGoalValue() > 0) {
                Double dayProgress = 0.0;
                for (GoalProgress goalProgress : goal.getGoalProgresses()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    if (goalProgress.getGoalProgressDate().equals(sdf.format(new Date()))) {
                        dayProgress += goalProgress.getGoalProgressValue();
                    }
                }

                if (dayProgress < goal.getGoalValue()) {
                    if (goal.getGoalType().equals("steps")) {
                        result.add(String.format("It is %d steps left to achieve the goal", (int)(goal.getGoalValue() - dayProgress)));
                    } else if (goal.getGoalType().equals("sleep")) {
                        result.add(String.format("You have to sleep %.2f more hours to achieve goal", goal.getGoalValue() - dayProgress));
                    } else if (goal.getGoalType().equals("water")) {
                        result.add(String.format("It is %.2f liters left to drink to achieve the goal", goal.getGoalValue() - dayProgress));
                    }    
                } else {
                    result.add(String.format("Congratulation! You have achieved your %s goal! Current progress is %.1f", goal.getGoalType(), dayProgress));
                }
            }
        }

        result.add("\n" + storageService.getServiceImplPort().getRandomQuote());
        return result;
    }
    
    @Override
    public String updateUserInfo(Long id, String key, String value) {
        User user = storageService.getServiceImplPort().getUser(id);
        if (key.equals("firstname")) {
            user.setFirstname(value);
            storageService.getServiceImplPort().saveUser(user);
            return "Firstname was successfully changed";
        } else if (key.equals("lastname")) {
            user.setLastname(value);
            storageService.getServiceImplPort().saveUser(user);
            return "Last name was successfully changed";
        } else if (key.equals("birthdate")) {
            user.setBirthdate(value);
            storageService.getServiceImplPort().saveUser(user);
            return "Birthdate was successfully changed";
        } else if (key.equals("weight") || key.equals("height")) {
            Measure m = new Measure();
            m.setMeasureType(key);
            m.setMeasureValue(value);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            m.setDateRegistered(sdf.format(new Date()));

            List<Measure> currentHealth = user.getCurrentHealth();
            List<Measure> healthHistory = user.getHealthHistory();
            Measure oldMeasure = null;
            for(Measure elem : currentHealth) {
                if(elem.getMeasureType().equals(m.getMeasureType())) {
                    oldMeasure = elem;
                }
            }
            if(oldMeasure!=null) {
                healthHistory.add(oldMeasure);
                currentHealth.remove(oldMeasure);
            }
            currentHealth.add(m);

            storageService.getServiceImplPort().saveUser(user);
            return "Measure \"" + key + "\" was successfully changed";
        } else {
            return "Unknown parameter";
        }
    }
    
    @Override
    public String updateUserGoal(Long id, String goal, String value) {
        User user = storageService.getServiceImplPort().getUser(id);
        for (Goal g : user.getGoals()) {
            if (g.getGoalType().equals(goal)) {
                g.setGoalValue(Double.parseDouble(value));
            }
        }
        storageService.getServiceImplPort().saveUser(user);
        return "Goal \"" + goal + "\" was successfully updated";
    }
    
    @Override
    public String deleteUserGoal(Long id, String goal) {
        User user = storageService.getServiceImplPort().getUser(id);
        for (Goal g : user.getGoals()) {
            if (g.getGoalType().equals(goal)) {
                g.setGoalValue(-1.0);
                g.getGoalProgresses().clear();
            }
        }
        storageService.getServiceImplPort().saveUser(user);
        return "Goal \"" + goal + "\" was successfully deleted";
    }
    
    @Override
    public String updateGoalProgress(Long id, String goal, String value) {
        String result = "";
        User user = storageService.getServiceImplPort().getUser(id);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Goal elem : user.getGoals()) {
            if (elem.getGoalType().equals(goal)) {
                GoalProgress newGoalProgress = new GoalProgress();
                newGoalProgress.setGoalProgressValue(Double.parseDouble(value));
                newGoalProgress.setGoalProgressDate(sdf.format(new Date()));
                elem.getGoalProgresses().add(newGoalProgress);

                Double dayProgress = 0.0;
                for (GoalProgress goalProgress : elem.getGoalProgresses()) {
                    if (goalProgress.getGoalProgressDate().equals(sdf.format(new Date()))) {
                        dayProgress += goalProgress.getGoalProgressValue();
                    }
                }

                Double userGoalValue = elem.getGoalValue();
                if (dayProgress < 0.3*userGoalValue) {
                    result = "You did not so well, you should try harder.";
                } else if (dayProgress >= 0.3*userGoalValue && dayProgress < 0.7*userGoalValue) {
                    result = "You are doing good! Keep trying to achieve your goal!";
                } else if (dayProgress >= 0.7*userGoalValue && dayProgress < userGoalValue) {
                    result = "You have almost achieved your goal! Keep working on it!";
                } else if (dayProgress >= userGoalValue && dayProgress < 1.5*userGoalValue) {
                    result = "You have achieved your goal!!!\n"
                             + storageService.getServiceImplPort().getCongratsPicture();
                } else if (dayProgress >= 1.5*userGoalValue) {
                    result = "You have overachieved your goal!!! Maybe you need to update your daily goal?;)\n"
                             + storageService.getServiceImplPort().getCongratsPicture();
                } 
            }
        }
        storageService.getServiceImplPort().saveUser(user);
        return result;
    }

}


