package com.ohgiraffers.secondbackend.user.service;

import com.ohgiraffers.secondbackend.user.client.EmailFeignClient;
import com.ohgiraffers.secondbackend.user.client.dto.SignupVerificationMailRequest;
import com.ohgiraffers.secondbackend.user.dto.request.PasswordUpdateDTO;
import com.ohgiraffers.secondbackend.user.dto.request.ProfileUpdateDTO;
import com.ohgiraffers.secondbackend.user.dto.response.UserProfileResponse;
import com.ohgiraffers.secondbackend.user.dto.response.UserResponseDTO;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.entity.UserRole;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import com.ohgiraffers.secondbackend.user.util.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JWTUtil jwtUtil;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    EmailFeignClient emailFeignClient;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @InjectMocks
    UserService userService;

    // RedisTemplate 기본 stubbing
    void stubRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // =========================
    // loadUserByUsername
    // =========================

    @DisplayName("loadUserByUsername - 유저를 찾으면 UserDetails 반환")
    @Test
    void loadUserByUsername_success() {
        String username = "user1";

        User user = User.builder()
                .username(username)
                .password("encodedPw")
                .nickname("닉네임")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @DisplayName("loadUserByUsername - 없으면 UsernameNotFoundException")
    @Test
    void loadUserByUsername_notFound() {
        String username = "no_user";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username));
    }

    // =========================
    // signup
    // =========================

    @DisplayName("signup - username 중복 없으면 성공, 비밀번호 인코딩 + 메일 전송 + 저장")
    @Test
    void signup_success() {
        String username = "test@example.com";
        String rawPassword = "pw1234";
        String encodedPassword = "ENC_PW";
        String nickname = "닉네임";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        // save 시 그대로 user 반환
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = userService.signup(username, rawPassword, nickname);

        // then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(nickname, result.getNickname());
        assertEquals(UserRole.USER, result.getRole());
        assertEquals(encodedPassword, result.getPassword());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userRepository, times(1)).save(any(User.class));

        // 메일 요청 payload 확인
        ArgumentCaptor<SignupVerificationMailRequest> mailCaptor =
                ArgumentCaptor.forClass(SignupVerificationMailRequest.class);

        verify(emailFeignClient, times(1))
                .sendSignupVerificationMail(mailCaptor.capture());

        SignupVerificationMailRequest mailReq = mailCaptor.getValue();
        // record SignupVerificationMailRequest(String username, String nickname)
        assertEquals(username, mailReq.username());
        assertEquals(nickname, mailReq.nickname());
    }

    @DisplayName("signup - username 중복이면 IllegalArgumentException")
    @Test
    void signup_duplicateUsername_throws() {
        String username = "dup@example.com";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(mock(User.class)));

        assertThrows(IllegalArgumentException.class,
                () -> userService.signup(username, "pw", "닉"));
        verify(userRepository, never()).save(any());
        verify(emailFeignClient, never()).sendSignupVerificationMail(any());
    }

    // =========================
    // login
    // =========================

    @DisplayName("login - 아이디/비밀번호가 맞으면 토큰 2개 반환 + refresh 저장")
    @Test
    void login_success() {
        stubRedis();

        String username = "user1";
        String rawPassword = "pw";
        String encodedPw = "ENC_PW";

        User user = User.builder()
                .username(username)
                .password(encodedPw)
                .nickname("닉네임")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPw))
                .thenReturn(true);

        when(jwtUtil.createAccessToken(anyString(), anyString(), anyString()))
                .thenReturn("ACCESS_TOKEN");
        when(jwtUtil.createRefreshToken(anyString(), anyString(), anyString()))
                .thenReturn("REFRESH_TOKEN");

        // when
        String[] tokens = userService.login(username, rawPassword);

        // then
        assertNotNull(tokens);
        assertEquals(2, tokens.length);
        assertEquals("ACCESS_TOKEN", tokens[0]);
        assertEquals("REFRESH_TOKEN", tokens[1]);

        String expectedKey = "refresh:" + username;
        verify(valueOperations, times(1))
                .set(eq(expectedKey), eq("REFRESH_TOKEN"), eq(28L), eq(TimeUnit.DAYS));
    }

    @DisplayName("login - 존재하지 않는 username이면 UsernameNotFoundException")
    @Test
    void login_userNotFound_throws() {
        String username = "no_user";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.login(username, "pw"));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @DisplayName("login - 비밀번호 틀리면 IllegalArgumentException")
    @Test
    void login_wrongPassword_throws() {
        String username = "user1";

        User user = User.builder()
                .username(username)
                .password("ENC_PW")
                .nickname("닉네임")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENC_PW"))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.login(username, "wrong"));

        verify(jwtUtil, never()).createAccessToken(anyString(), anyString(), anyString());
        verify(jwtUtil, never()).createRefreshToken(anyString(), anyString(), anyString());
    }

    // =========================
    // logout
    // =========================

    @DisplayName("logout - refresh 삭제 & access token 블랙리스트 등록")
    @Test
    void logout_success() {
        // given
        String username = "user1";
        String accessToken = "access-token-example";
        String refreshKey = "refresh:" + username;
        String blacklistKey = "blacklist:" + accessToken;

        long expiration = 10000L; // 예시 TTL

        // jwtUtil.getExpiration(accessToken) 호출 시 expiration 반환하도록 설정
        when(jwtUtil.getExpriation(accessToken)).thenReturn(expiration);

        // Redis opsForValue Mock 설정
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        // when
        userService.logout(accessToken, username);

        // then — Refresh 삭제 확인
        verify(redisTemplate, times(1)).delete(refreshKey);

        // then — Access Token 블랙리스트 저장 확인
        verify(valueOps, times(1)).set(
                eq(blacklistKey),
                eq("logout"),
                eq(expiration),
                eq(TimeUnit.MILLISECONDS)
        );
    }


    // =========================
    // updateNickname
    // =========================

    @DisplayName("updateNickname - 닉네임 변경 성공")
    @Test
    void updateNickname_success() {
        String username = "user1";
        String oldNickname = "old";
        String newNickname = "newNick";

        User user = User.builder()
                .username(username)
                .password("ENC_PW")
                .nickname(oldNickname)
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        // ProfileUpdateDTO가 Lombok @Getter,@Setter + NoArgsConstructor라고 가정
        ProfileUpdateDTO dto = new ProfileUpdateDTO();
        dto.setNickname(newNickname);

        // when
        UserResponseDTO result = userService.updateNickname(username, dto);

        // then
        assertEquals(newNickname, user.getNickname());
        assertEquals(newNickname, result.getNickname());
        verify(userRepository, times(1)).findByUsername(username);
    }

    // =========================
    // updatePassword
    // =========================

    @DisplayName("updatePassword - 비밀번호 변경 성공")
    @Test
    void updatePassword_success() {
        String username = "user1";
        String oldEncPw = "ENC_OLD";
        String newRawPw = "NEW_PW";
        String newEncPw = "ENC_NEW";

        User user = User.builder()
                .username(username)
                .password(oldEncPw)
                .nickname("닉네임")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newRawPw))
                .thenReturn(newEncPw);

        // PasswordUpdateDTO: oldPassword, newPassword
        PasswordUpdateDTO dto = new PasswordUpdateDTO();
        dto.setOldPassword("이전비번(현재 검증 안함)");
        dto.setNewPassword(newRawPw);

        // when
        UserResponseDTO result = userService.updatePassword(username, dto);

        // then
        assertEquals(newEncPw, user.getPassword());
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getNickname(), result.getNickname());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(newRawPw);
    }

    // =========================
    // getProfileByUsername
    // =========================

    @DisplayName("getProfileByUsername - 유저를 찾으면 UserProfileResponse 반환")
    @Test
    void getProfileByUsername_success() {
        String username = "user1";

        User user = User.builder()
                .username(username)
                .password("ENC")
                .nickname("닉네임")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        UserProfileResponse result = userService.getProfileByUsername(username);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getNickname(), result.getNickName());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @DisplayName("getProfileByUsername - 없으면 IllegalArgumentException")
    @Test
    void getProfileByUsername_notFound_throws() {
        String username = "no_user";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getProfileByUsername(username));
    }

    // =========================
    // getProfileById
    // =========================

    @DisplayName("getProfileById - 유저를 찾으면 UserProfileResponse 반환")
    @Test
    void getProfileById_success() {
        long userId = 1L;

        User user = User.builder()
                .username("user1")
                .password("ENC")
                .nickname("닉네임")
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserProfileResponse result = userService.getProfileById(userId);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getNickname(), result.getNickName());
        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("getProfileById - 없으면 IllegalArgumentException")
    @Test
    void getProfileById_notFound_throws() {
        long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getProfileById(userId));
    }
}
