import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.UUID;

public class LogEvent implements RequestHandler<SNSEvent, Object> {

  private DynamoDB dynamoDB;
  private String dynamoDBTableName = "csye6225";
  private String from = "noreply@csye6225-spring2018-shuklake.me";
  private String subject = "Reset Password Link";
  private String htmlBody = "<h1>csye6225-spring2018 Password reset link</h1>"
          + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
          + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
          + "AWS SDK for Java</a>";
  private String textBody;

  public Object handleRequest(SNSEvent request, Context context) {

    String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
    context.getLogger().log("Invocation started: " + timeStamp);
    context.getLogger().log("1: " + (request == null));
    context.getLogger().log("2: " + (request.getRecords().size()));
    context.getLogger().log(request.getRecords().get(0).getSNS().getMessage());
    timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
    context.getLogger().log("Invocation completed: " + timeStamp);

    String emailID = request.getRecords().get(0).getSNS().getMessage();
    String token = UUID.randomUUID().toString();
    this.initDynamoDbClient();
    Item existingUser = this.dynamoDB.getTable(dynamoDBTableName).getItem("id", emailID);
    if(existingUser == null){
      long now = Instant.now().getEpochSecond();
      this.dynamoDB.getTable(dynamoDBTableName)
              .putItem(new PutItemSpec().withItem(new Item()
              .withString("id", emailID)
              .withString("token", token)
              .withLong("TTL", now+1200)));
      textBody = "https://csye6225-spring2018.com/reset?email=" + emailID + "&token=" + token;
      AmazonSimpleEmailService simpleEmailService = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
      String emailBody = htmlBody + "<p>" + textBody + "</p>";
      SendEmailRequest sendEmailRequest = new SendEmailRequest()
              .withDestination(new Destination().withToAddresses(emailID))
              .withMessage(new Message()
                      .withBody(new Body()
                              .withHtml(new Content().withCharset("UTF-8").withData(emailBody))
                              .withText(new Content().withCharset("UTF-8").withData(textBody)))
                      .withSubject(new Content().withCharset("UTF-8").withData(subject)))
              .withSource(from);
      simpleEmailService.sendEmail(sendEmailRequest);

      System.out.println("Email sent successfully send to: "+ emailID);
    }else{
      AmazonSimpleEmailService simpleEmailService = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
      String emailBody = htmlBody + "<p>Password reset link already sent to this user</p>";
      SendEmailRequest sendEmailRequest = new SendEmailRequest()
              .withDestination(new Destination().withToAddresses(emailID))
              .withMessage(new Message()
                      .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(emailBody)))
                      .withSubject(new Content().withCharset("UTF-8").withData(subject)))
              .withSource(from);
      simpleEmailService.sendEmail(sendEmailRequest);
      System.out.println("Email sent successfully!");
    }
    return null;
  }

  private void initDynamoDbClient() {
    AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    this.dynamoDB = new DynamoDB(dynamoDBClient);
  }

}

