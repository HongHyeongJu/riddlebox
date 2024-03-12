package com.labmate.riddlebox.security.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OAuthTokenKakao {
    private String access_token;
    private String token_type;
    private String id_token;
    private int expires_in;
    private String scope;
    private String refresh_token;


}
