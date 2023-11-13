package com.example.security.oauth;

import com.example.security.auth.PrincipalDetails;
import com.example.security.model.User;
import com.example.security.oauth.provider.GoogleUserInfo;
import com.example.security.oauth.provider.NaverUserInfo;
import com.example.security.oauth.provider.OAuth2UserInfo;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    //구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest.getClientRegistration = " + userRequest.getClientRegistration());   //어떤 oauth로 로그인했는지 확인
        System.out.println("userRequest.getAccessToken = " + userRequest.getAccessToken());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        //oauth 로그인 성공 -> Access Token 요청 => userRequest
        // 토큰을 통해 회원 프로필을 받아옴(loadUser() 함수)
        // System.out.println("super.loadUser(userRequest).getAttributes = " + super.loadUser(userRequest).getAttributes());
        System.out.println("getAttributes = " + oAuth2User.getAttributes());

        //회원가입 진행
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId(); //google의 사용자 고유 키
        String username = provider + "_" + providerId;  // google_17837483984457
        String email = oAuth2UserInfo.getEmail();
        String password = bCryptPasswordEncoder.encode("password");
        String role = "ROLE_USER";

        //회원가입 여부 검사
        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            userEntity = User.builder()
                    .email(email)
                    .username(username)
                    .password(password)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
