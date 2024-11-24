//RegisterRequest.java
package health.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {

    @NonNull
    private String username;

    @NonNull
    private String password;
}
