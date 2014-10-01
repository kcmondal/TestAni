import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jdom.Document;
import org.jdom.Element;

public class GetGroupsClient {

  public static void main(String args[]) {
    // Create request xml**
    Element request = new Element("request");
    // Create PostMethod specifying service url**
    String serviceUrl = "http://localhost:8080/geonetwork/srv/en/xml.group.list";
    PostMethod post = new PostMethod(serviceUrl);

    try {
      String postData = Xml.getString(new Document(request));

      // Set post data, mime-type and encoding**
      post.setRequestEntity(new StringRequestEntity(postData, "application/xml", "UTF8"));

      // Send request**
      HttpClient httpclient = new HttpClient();
      int result = httpclient.executeMethod(post);

      // Display status code**
      System.out.println("Response status code: " + result);

      // Display response**
      System.out.println("Response body: ");
      System.out.println(post.getResponseBodyAsString());

    } catch (Exception ex) {
      ex.printStackTrace();

    } finally {
      // Release current connection to the connection pool
      // once you are done**
      post.releaseConnection();
    }
  }
}