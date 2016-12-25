import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexStage extends Stage<IndexStage> {

    @ExpectedScenarioState(required = true)
    WebDriver webDriver;

    @ExpectedScenarioState(required = true)
    CurrentStep currentStep;

    IndexPage indexPage;

    @BeforeStage
    void setup() {
        indexPage = PageFactory.initElements(webDriver, IndexPage.class);
    }


    public void the_initial_page() {
        webDriver.navigate().to(IndexStage.class.getClassLoader().getResource("index.html"));
        takeScreenShot();
    }

    public void the_title_is(@Quoted String title) {
        assertThat(indexPage.title.getText()).isEqualTo(title);
    }

    public void clicking_the_ClickMe_button() {
        indexPage.clickMeBtn.click();
    }

    @AfterStage
    public void takeScreenShot() {
        String base64 = ( (TakesScreenshot) webDriver ).getScreenshotAs( OutputType.BASE64 );
        currentStep.addAttachment( Attachment.fromBase64( base64, MediaType.PNG ).withTitle( "Screenshot" ) );
    }
}
