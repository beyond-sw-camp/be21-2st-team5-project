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
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService  implements UserDetailsService{

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailFeignClient emailFeignClient;

    private UserResponseDTO userResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil,
                       RedisTemplate<String, Object> redisTemplate, EmailFeignClient emailFeignClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.emailFeignClient = emailFeignClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
    }

   //회원가입
    @Transactional
    public User signup(String username, String password, String nickname){
        if(userRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("이미 존재함");
        }

        User user= User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .role(UserRole.USER)
                .build();

        emailFeignClient.sendSignupVerificationMail(
                new SignupVerificationMailRequest(
                        user.getUsername(),
                        user.getNickname()

                )
        );

        return userRepository.save(user);
    }

    //로그인
    public String[] login(String username,String rawPassword){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("잘못된 아이디 혹은 비밀번호"));


        if(!passwordEncoder.matches(rawPassword,user.getPassword())){
            throw new IllegalArgumentException("잘못된 비밀번호");
        }
        String userid= String.valueOf(user.getId());
        String role=user.getRole().name();
        String accessToken=jwtUtil.createAccessToken(username,role,userid);
        String refreshToken=jwtUtil.createRefreshToken(username,role,userid);

        String refreshKey="refresh:"+username;
        //28일간 저장
        redisTemplate.opsForValue().set(refreshKey,refreshToken,28, TimeUnit.DAYS);

        return new String[]{accessToken,refreshToken};
    }

    /* 로그아웃 */
    public void logout(String accessToken, String username) {

        // Refresh Token 삭제
        redisTemplate.delete("refresh:" + username);

        // Access Token 남은 유효시간 계산
        long expiration = jwtUtil.getExpriation(accessToken);

        // Access Token 블랙리스트 등록
        redisTemplate.opsForValue().set(
                "blacklist:" + accessToken,
                "logout",
                expiration,
                TimeUnit.MILLISECONDS
        );
    }



    /*닉네임 변경*/
    @Transactional
    public UserResponseDTO updateNickname(String username,ProfileUpdateDTO profileUpdateDTO){
      String nickname=profileUpdateDTO.getNickname();
      Optional<User> user=userRepository.findByUsername(username);
      user.get().setNickname(nickname);
      return userResponseDTO(user.orElse(null));
    }

    /*비밀번호 변경*/
    //기존 비밀번호 검증이 필요하지 않을 거 같아서 빼긴했는데...
    public UserResponseDTO updatePassword(String username, PasswordUpdateDTO passwordUpdateDTO){
        User user=userRepository.findByUsername(username).orElseThrow();
        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        return userResponseDTO(user);
    }

//    @Transactional
//    public UserProfileResponse UpdateUsername(String username,String newUsername) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다"));
//
//        return UserProfileResponse.from(user);
//    }

    @Transactional
    public UserProfileResponse getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 username의 유저를 찾을 수 없습니다. username=" + username));

        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse getProfileById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userId의 유저를 찾을 수 없습니다. userId=" + userId));

        return UserProfileResponse.from(user);
    }


}
