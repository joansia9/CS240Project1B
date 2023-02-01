package passoff;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestDriver {

    public static final String BLUE = "\033[34m";    // BLUE Underlined
    public static final String RED = "\033[31m";    // RED
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        final LauncherDiscoveryRequest request =
                LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectClass(HangmanTest.class))
                        .build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        long testFoundCount = summary.getTestsFoundCount();
        List<Failure> failures = summary.getFailures();

        //Print summary to console
        System.out.print(BLUE + "Passed: " + summary.getTestsSucceededCount() + " of " + testFoundCount + " tests." + ANSI_RESET);
        System.out.println();
        for(Failure failure : failures){ //Print each failure
            StringBuilder message;
            if(failure.getException().getMessage() != null){
                message = new StringBuilder(failure.getException().getMessage()); //get failure info
                int index = message.indexOf(" ==>");
                if(index >= 0){
                    message.delete(index, message.length()); //remove extra failure info
                }
                message.insert(0, RED); //make text red
                message.append(ANSI_RESET); //end red
            }
            else message = new StringBuilder();

            int i;
            for(i = 0; i < failure.getException().getStackTrace().length; i++){ //used to find element on stack trace with Test Code
                if(failure.getException().getStackTrace()[i].toString().contains("Test.java")) //Find the line on stack trace that contains test file
                    break;
            }
            if(i != failure.getException().getStackTrace().length){
                message.append(" - Test File: " + failure.getException().getStackTrace()[i].getFileName()); //add test file that failure originated from to message
                message.append(" Line: "+ failure.getException().getStackTrace()[i].getLineNumber()); //add line number of failure to message
            }
            System.out.println(RED + "  failure" + ANSI_RESET + " - " + failure.getTestIdentifier().getDisplayName() + " - " + message.toString()); //print failure report
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime)/1000.0 + " seconds");
        if(summary.getTestsSucceededCount() != testFoundCount){
            System.exit(1); //for use with driver
        }
    }
}