package network.controller;

import lombok.extern.slf4j.Slf4j;
import network.api.request.NotificationsRequest;
import network.api.request.SetPasswordRequest;
import network.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import network.api.request.RegisterRequest;
import network.api.request.SetEmailRequest;
import network.api.response.CommonResponse;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse> personRegister(@RequestBody RegisterRequest registerRequest){
        log.trace("/api/v1/account/register");

        String message = accountService.registerNewUser(registerRequest);
        return new ResponseEntity<>(new CommonResponse(message), HttpStatus.OK);
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<CommonResponse> passwordRecovery(@RequestBody String email){
        log.trace("/api/v1/account/password/recovery");

        String message = "";
        boolean mailIsSend = accountService.sendEmailToPerson(email);
        if (!mailIsSend){
            message = String.format("Mail was not send to %s", email);
        }
        return new ResponseEntity<>(new CommonResponse(message), HttpStatus.OK);
    }

    @PutMapping("/password/set")
    public ResponseEntity<CommonResponse> passwordChange(@RequestBody SetPasswordRequest setPasswordRequest){
        log.trace("/api/v1/account/password/set");

        String message = accountService.changePersonPassword(setPasswordRequest.getToken(), setPasswordRequest.getPassword().getPassword());
        return new ResponseEntity<>(new CommonResponse(message), HttpStatus.OK);
    }

    @PutMapping("/email")
    public ResponseEntity<CommonResponse> emailChange(@RequestBody SetEmailRequest email){
        log.trace("/api/v1/account/email");

        String message = accountService.changePersonEmail(email.getEmail());
        return new ResponseEntity<>(new CommonResponse(message), HttpStatus.OK);
    }

    @PutMapping("/notifications")
    public ResponseEntity<CommonResponse> accountNotifications(@RequestBody NotificationsRequest notificationsRequest){
        log.trace("/api/v1/account/notifications");
        String message = accountService
                .saveNotificationSetting(notificationsRequest.getNotificationTypeCode(),notificationsRequest.getEnable());
        return new ResponseEntity<>(new CommonResponse(message), HttpStatus.OK);
    }

}
