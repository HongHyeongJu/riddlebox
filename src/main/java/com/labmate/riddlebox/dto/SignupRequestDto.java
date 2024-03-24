package com.labmate.riddlebox.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignupRequestDto {

    @NotBlank(message = "로그인ID 이메일은 필수항목입니다.")
    private String emailHead;

    @NotBlank(message = "로그인ID 이메일은 필수항목입니다.")
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    @Size(min = 8, max = 20)
    private String password1;

    @NotBlank(message = "비밀번호 확인은 필수항목입니다.")
    @Size(min = 8, max = 20)
    private String password2;

    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;

    public SignupRequestDto( String emailHead, String email, String password1,  String password2, String nickname) {
        this.emailHead = emailHead;
        this.email = email;
        this.password1 = password1;
        this.password2 = password2;
        this.nickname = nickname;
    }

      public SignupRequestDto( ) {

    }


}
