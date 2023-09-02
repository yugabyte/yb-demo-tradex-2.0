import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConnectDBTest {

  @Test
  @Disabled
  public void testConnection() throws Exception {
    var u = "uranus";
    var p = "uranus123#";

    // 172.151.16.27   us-west-2a
    testConnectDB("172.151.16.27", "aws.us-west-1.*,us-west-2.*", u, p);
    // 172.150.22.252  us-west-1a
    testConnectDB("172.150.22.252", "aws.us-west-1.*,us-west-2.*", u, p);

    // 172.161.28.48   us-east-2a
    testConnectDB("172.161.28.48", "aws.us-east-2.*,aws.us-east-1.*", u, p);
    // 172.152.30.209  us-east-1a
    testConnectDB("172.152.30.209", "aws.us-east-2.*,aws.us-east-1.*", u, p);
  }

  public void testConnectDB(String ip, String tpk, String username, String password)
    throws Exception {
    Class.forName("com.yugabyte.Driver");

    String yburl = format(
      "jdbc:yugabytedb://%1$s:5433/uranus?socketTimeout=60&load_balance=true&topology_keys=%2$s",
      ip, tpk);
    Connection conn = DriverManager.getConnection(yburl, username,
      password);
    Statement stmt = conn.createStatement();

    System.out.println("Connected to the YugabyteDB Cluster successfully.");
    stmt.execute("DROP TABLE IF EXISTS employee");
    stmt.execute("""
        CREATE TABLE IF NOT EXISTS employee
        (id int primary key, name varchar, age int, language text)
      """);
    System.out.println("Created table employee");

    String insertStr = "INSERT INTO employee VALUES (1, 'John', 35, 'Java')";
    stmt.execute(insertStr);
    System.out.println("EXEC: " + insertStr);

    ResultSet rs = stmt.executeQuery("select * from employee");
    while (rs.next()) {
      System.out.printf("Query returned: name = %s, age = %s, language = %s\n",
        rs.getString(2), rs.getString(3), rs.getString(4)
      );
    }
  }

}
