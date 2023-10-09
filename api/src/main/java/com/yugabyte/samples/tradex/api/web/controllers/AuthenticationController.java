package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.AuthenticationRequest;
import com.yugabyte.samples.tradex.api.domain.business.AuthenticationResponse;
import com.yugabyte.samples.tradex.api.domain.business.SignUpRequest;
import com.yugabyte.samples.tradex.api.domain.business.SignUpResponse;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.events.NewUserCreatedEventPublisher;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.web.ApiException;
import com.yugabyte.samples.tradex.api.web.security.JwtAuthHelper;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthenticationController extends BaseController {

  @Value("${app.mylocation}")
  private String instanceLocation;
  private final NewUserCreatedEventPublisher publisher;
  private final JwtAuthHelper authHelper;
  private final UserService userService;

  @Autowired
  private TradeXDBTypeContext tradeXDBTypeContext;

  public AuthenticationController(UserService userService, ConnectionInfoRepo connectionInfoRepo,
    TradeXDBTypeContext tradeXDBTypeContext, NewUserCreatedEventPublisher publisher,
    JwtAuthHelper authHelper, UserService userService1) {
    super(userService, connectionInfoRepo, tradeXDBTypeContext);
    this.publisher = publisher;
    this.authHelper = authHelper;
    this.userService = userService1;
  }

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  public SignUpResponse signup(@Valid @RequestBody SignUpRequest form) {
    AppUser appUser = createAppUser(form);

    return SignUpResponse.builder()
      .id(appUser.getId()
        .getId())
      .prefRegion(appUser.getId()
        .getPreferredRegion())
      .login(appUser.getEmail())
      .status("SUCCESS")
      .build();
  }

  @PostMapping("/password-reset")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public PasswordResetResponse passwordReset(@Valid @RequestBody PasswordResetRequest request) {
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();
    String login = request.login();
    String newPassword = login + "123";
    final AppUser appUser = userService.findByEmail(dbType, login)
      .orElseThrow();
    String newEncodedPassword = authHelper.encodePassword(newPassword);

    var updatePassKeyResult = userService.updatePasskey(dbType, login, newEncodedPassword, appUser.getId()
      .getPreferredRegion());

    log.info("{}: Password reset successful. Result: {}", login, updatePassKeyResult);

    return new PasswordResetResponse(login, newPassword, true);
  }

  @PostMapping("/sign-in")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public AuthenticationResponse authenticate(@Valid @RequestBody AuthenticationRequest request) {

    String jwt = authHelper.processLoginAndGenerateJwt(request.getLogin(),
      request.getCredentials());
    Optional<AppUser> appUser = userService.findByEmail(tradeXDBTypeContext.getDbType(),
      request.getLogin());

    if (appUser.isEmpty()) {
      log.error("No user with username: {}", request.getLogin());
      throw new UsernameNotFoundException("No user with username " + request.getLogin());
    }

    AppUserId userId = appUser.get()
      .getId();

    return AuthenticationResponse.builder()
      .token(jwt)
      .type("Bearer")
      .status("SUCCESS")
      .id(userId.getId())
      .preferredRegion(userId.getPreferredRegion())
      .build();
  }

  @PostMapping("/sign-out")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @SecurityRequirement(name = "auth-header-bearer")
  public MessageResponse signOut() {
    log.info("Got sign out request");
    return new MessageResponse("Sign Out Successful", true);
  }

  private AppUser createAppUser(SignUpRequest form) {

    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    if (userService.existsByEmail(dbType, form.getEmail())) {
      throw new ApiException("Failed to complete signup", "email", form.getEmail(),
        "Email already in use");
    }

    AppUser newUser = new AppUser();
    BeanUtils.copyProperties(form, newUser);
    newUser.setPasskey(authHelper.encodePassword(form.getPasskey()));
    AppUserId generatedUserId = (AppUserId) userService.createNewUser(dbType, newUser,
        instanceLocation)
      .getData();

    AppUser appUser = userService.findByAppUserId(dbType, generatedUserId)
      .get();
    publisher.publishCustomEvent(appUser, dbType);

    return appUser;
  }

  public record PasswordResetRequest(String login) {

  }

  public record PasswordResetResponse(String login, String password, boolean status) {

  }

  public record MessageResponse(String message, boolean status) {

  }
}
