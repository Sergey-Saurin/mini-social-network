package network.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetPasswordRequest {
    private String token;
    private PasswordRequest password;
}
