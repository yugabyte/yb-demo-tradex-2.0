import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    System.out.println("mickey" + encoder.encode("mickey123"));

    System.out.println("mickey" + encoder.encode("mickey@tradex.com123"));
  }
}
